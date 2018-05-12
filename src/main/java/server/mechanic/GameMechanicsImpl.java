package server.mechanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import server.mechanic.services.GameSessionService;
import server.model.User;
import server.services.UserService;
import server.websocket.RemotePointService;


import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GameMechanicsImpl implements GameMechanics {
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMechanicsImpl.class);

    @NotNull
    private final UserService userService;

//    @NotNull
//    private final ClientSnapshotsService clientSnapshotsService;

//    @NotNull
//    private final ServerSnapshotService serverSnapshotService;

    @NotNull
    private final RemotePointService remotePointService;

//    @NotNull
//    private final PullTheTriggerService pullTheTriggerService;

//    @NotNull
    private final GameSessionService gameSessionService;

//    @NotNull
//    private final MechanicsTimeService timeService;

//    @NotNull
//    private final GameTaskScheduler gameTaskScheduler;

    @NotNull
    private ConcurrentLinkedQueue<Integer> waiters = new ConcurrentLinkedQueue<>();

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public GameMechanicsImpl(@NotNull UserService userService,
                             @NotNull RemotePointService remotePointService,
                             @NotNull GameSessionService gameSessionService) {
        this.userService = userService;
        this.remotePointService = remotePointService;
        this.gameSessionService = gameSessionService;
    }

    @Override
    public void addUser(@NotNull Integer userId) {
        if (gameSessionService.isPlaying(userId)) {
            return;
        }
        LOGGER.info("User wait : " + userId);
        waiters.add(userId);
        if (LOGGER.isDebugEnabled()) {
            final User user = userService.getUserById(userId);
            LOGGER.debug(String.format("User %s added to the waiting list", user.getLogin()));
        }
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
    public void gmStep(long frameTime) {
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

//        for (GameSession session : gameSessionService.getSessions()) {
//            clientSnapshotsService.processSnapshotsFor(session);
//        }

//        gameTaskScheduler.tick();

//        final List<GameSession> sessionsToTerminate = new ArrayList<>();
//        final List<GameSession> sessionsToFinish = new ArrayList<>();
//        for (GameSession session : gameSessionService.getSessions()) {
//            if (session.tryFinishGame()) {
//                sessionsToFinish.add(session);
//                continue;
//            }
//
//            if (!gameSessionService.checkHealthState(session)) {
//                sessionsToTerminate.add(session);
//                continue;
//            }
//
//            try {
//                serverSnapshotService.sendSnapshotsFor(session, frameTime);
//            } catch (RuntimeException ex) {
//                LOGGER.error("Failed to send snapshots, terminating the session", ex);
//                sessionsToTerminate.add(session);
//            }
//            pullTheTriggerService.pullTheTriggers(session);
//        }
//        sessionsToTerminate.forEach(session -> gameSessionService.forceTerminate(session, true));
//        sessionsToFinish.forEach(session -> gameSessionService.forceTerminate(session, false));

        tryStartGames();
//        clientSnapshotsService.reset();
//        timeService.tick(frameTime);
    }

//    @Override
//    public void reset() {
//        for (GameSession session : gameSessionService.getSessions()) {
//            gameSessionService.forceTerminate(session, true);
//        }
//        waiters.forEach(user -> remotePointService.cutDownConnection(user, CloseStatus.SERVER_ERROR));
//        waiters.clear();
//        tasks.clear();
//        clientSnapshotsService.reset();
//        timeService.reset();
//        gameTaskScheduler.reset();
//    }
}
