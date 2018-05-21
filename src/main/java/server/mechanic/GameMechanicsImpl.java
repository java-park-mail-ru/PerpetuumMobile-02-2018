package server.mechanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import server.mechanic.game.GameSession;
import server.mechanic.services.event.client.ClientEvent;
import server.mechanic.services.event.client.ClientEventService;
import server.mechanic.services.GameSessionService;
import server.model.User;
import server.services.UserService;
import server.websocket.RemotePointService;


import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class GameMechanicsImpl implements GameMechanics {
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMechanicsImpl.class);

    @NotNull
    private final UserService userService;

    @NotNull
    private final ClientEventService clientEventService;

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private final GameSessionService gameSessionService;

    @NotNull
    private ConcurrentLinkedQueue<Integer> waiters = new ConcurrentLinkedQueue<>();

    @NotNull
    private Set<Integer> usersForRemove = new CopyOnWriteArraySet<>();

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public GameMechanicsImpl(@NotNull UserService userService,
                             @NotNull RemotePointService remotePointService,
                             @NotNull GameSessionService gameSessionService,
                             @NotNull ClientEventService clientEventService) {
        this.userService = userService;
        this.remotePointService = remotePointService;
        this.gameSessionService = gameSessionService;
        this.clientEventService = clientEventService;
    }

    @Override
    public void addClientEvent(@NotNull Integer userId, @NotNull ClientEvent clientEvent) {
        tasks.add(() -> clientEventService.pushClientEvent(userId, clientEvent));
    }

    @Override
    public void addUser(@NotNull Integer userId) {
        if (gameSessionService.isPlaying(userId)) {
            return;
        }
        waiters.add(userId);
        if (LOGGER.isDebugEnabled()) {
            final User user = userService.getUserById(userId);
            LOGGER.debug(String.format("User %s added to the waiting list", user.getLogin()));
        }
    }

    @Override
    public void removeUser(@NotNull Integer userId) {
        if (waiters.contains(userId)) {
            waiters.remove(userId);
            return;
        }
        if (!gameSessionService.isPlaying(userId)) {
            return;
        }
        usersForRemove.add(userId);
        LOGGER.debug("User deleted from waiting list: " + userId);
    }


    private void tryStartGames() {
        final Set<User> matchedPlayers = new LinkedHashSet<>();

        while (waiters.size() >= 2 || waiters.size() >= 1 && matchedPlayers.size() >= 1) {
            final Integer candidate = waiters.poll();
            // for sure not null, cause we the only one consumer
            //noinspection ConstantConditions
            if (!insureCandidate(candidate)) {
                continue;
            }
            matchedPlayers.add(userService.getUserById(candidate));
            if (matchedPlayers.size() == 2) {
                final Iterator<User> iterator = matchedPlayers.iterator();
                gameSessionService.startGame(iterator.next(), iterator.next());
                matchedPlayers.clear();
            }
        }
        matchedPlayers.stream().map(User::getId).forEach(waiters::add);
    }

    private boolean insureCandidate(@NotNull Integer candidate) {
        return remotePointService.isConnected(candidate)
                && userService.getUserById(candidate) != null;
    }

    @Override
    public void gmStep(int threadCount) {
        while (!tasks.isEmpty()) {
            final Runnable nextTask = tasks.poll();
            if (nextTask != null) {
                try {
                    nextTask.run();
                } catch (RuntimeException ex) {
                    LOGGER.error("Can't handle game task", ex);
                }
            }
        }


        for (GameSession session : gameSessionService.getSessions()) {
            Integer sessionMod = session.getSessionId() % threadCount;
            Integer curThreadMod = (int) Thread.currentThread().getId() % threadCount;
            if (sessionMod.equals(curThreadMod)) {
                clientEventService.processEventsFor(session);
            }
        }

        final List<GameSession> sessionsToTerminate = new ArrayList<>();
        final List<GameSession> sessionsToFinish = new ArrayList<>();

        for (GameSession session : gameSessionService.getSessions()) {
            Integer sessionMod = session.getSessionId() % threadCount;
            Integer curThreadMod = (int) Thread.currentThread().getId() % threadCount;
            if (!sessionMod.equals(curThreadMod)) {
                continue;
            }

            if (session.tryFinishGame()) {
                sessionsToFinish.add(session);
                continue;
            }

            List<Integer> usersCloseGame = new ArrayList<>();
            Integer firstUserId = session.getFirst().getUserId();
            Integer secondUserId = session.getSecond().getUserId();
            if (usersForRemove.contains(firstUserId)) {
                usersCloseGame.add(firstUserId);
                usersForRemove.remove(firstUserId);
            }
            if (usersForRemove.contains(secondUserId)) {
                usersCloseGame.add(secondUserId);
                usersForRemove.remove(secondUserId);
            }


            if (usersCloseGame.contains(firstUserId)) {
                session.tryFinishGameClose(firstUserId);
                sessionsToFinish.add(session);
                continue;
            }

            if (usersCloseGame.contains(secondUserId)) {
                session.tryFinishGameClose(secondUserId);
                sessionsToFinish.add(session);
                continue;
            }


            if (!gameSessionService.checkHealthStateAny(session)) {
                sessionsToTerminate.add(session);
                continue;
            }

            if (!gameSessionService.checkHealthState(session)) {
                Integer userId;
                if (remotePointService.isConnected(firstUserId)) {
                    userId = secondUserId;
                } else {
                    userId = firstUserId;
                }
                session.tryFinishGameClose(userId);
                sessionsToFinish.add(session);
                continue;
            }
            clientEventService.resetForGameSession(session);
        }

        for (GameSession session : sessionsToTerminate) {
            clientEventService.resetForGameSession(session);
            gameSessionService.forceTerminate(session, true);
        }
        for (GameSession session : sessionsToFinish) {
            clientEventService.resetForGameSession(session);
            gameSessionService.forceTerminate(session, false);
        }

        tryStartGames();
        clientEventService.resetGarbage(gameSessionService);
    }
}
