package server.mechanic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;

public class MechanicMain implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(MechanicMain.class);
    private static final long STEP_TIME = 50;
    private Integer threadNum;
    private Integer threadCount;

    private final GameMechanics gameMechanics;
    private final Clock clock = Clock.systemDefaultZone();


    public MechanicMain(Integer threadNum, Integer threadCount, GameMechanics gameMechanics) {
        this.gameMechanics = gameMechanics;
        this.threadNum = threadNum;
        this.threadCount = threadCount;
    }

    @Override
    public void run() {
        try {
            mainCycle();
        } finally {
            LOGGER.warn("Mechanic executor terminated");
        }
    }

    private void mainCycle() {
        LOGGER.info("Game thread started:" + threadNum);
        long lastFrameMillis = STEP_TIME;
        while (true) {
            try {
                final long before = clock.millis();

                gameMechanics.gmStep(threadNum, threadCount);

                final long after = clock.millis();
                try {
                    final long sleepingTime = Math.max(0, STEP_TIME - (after - before));
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    LOGGER.error("Mechanics thread was interrupted", e);
                }

                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                final long afterSleep = clock.millis();
                lastFrameMillis = afterSleep - before;
            } catch (RuntimeException e) {
                LOGGER.error("Mechanics executor was reseted due to exception", e);
            }
        }
    }

}
