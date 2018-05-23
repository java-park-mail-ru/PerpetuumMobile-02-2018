package server.mechanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.time.Clock;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class MechanicsExecutor implements Runnable {
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(MechanicsExecutor.class);
    private static final long STEP_TIME = 50;
    private static final int THREAD_COUNT = 2;

    @NotNull
    private final GameMechanics gameMechanics;

    @NotNull
    private final Clock clock = Clock.systemDefaultZone();

    private final Executor tickExecutor = Executors.newFixedThreadPool(THREAD_COUNT);

    @Autowired
    public MechanicsExecutor(@NotNull GameMechanics gameMechanics) {
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    public void initAfterStartup() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            tickExecutor.execute(this);
        }
    }

    @Override
    public void run() {
        LOGGER.info("Thread " + Thread.currentThread().getId() + " started");
        try {
            mainCycle();
        } finally {
            LOGGER.warn("Mechanic executor terminated");
        }
    }

    private void mainCycle() {
        LOGGER.info("Thread " + Thread.currentThread().getId() + " in main cycle!!!");
        while (true) {
            try {
                final long before = clock.millis();

                gameMechanics.gmStep(THREAD_COUNT);

                final long after = clock.millis();
                try {
                    final long sleepingTime = Math.max(0, STEP_TIME - (after - before));
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    LOGGER.error("Mechanics thread was interrupted", e);
                }

                if (Thread.currentThread().isInterrupted()) {
                    //gameMechanics.reset();
                    return;
                }
                final long afterSleep = clock.millis();
            } catch (RuntimeException e) {
                LOGGER.error("Mechanics executor was reseted due to exception", e);
                //gameMechanics.reset();
            }
        }
    }
}
