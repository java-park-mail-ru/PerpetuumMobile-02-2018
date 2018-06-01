package server.mechanic;

import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;

@Service
public class MechanicsExecutor {

    private static final Integer THREAD_NUM = 2;

    private final GameMechanics gameMechanics;

    public MechanicsExecutor(GameMechanics gameMechanics) {
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    public void initAfterStartup() {
        for (int iter = 0; iter < THREAD_NUM; iter++) {
            MechanicMain main = new MechanicMain(iter, THREAD_NUM, gameMechanics);
            Thread thread = new Thread(main);
            thread.start();
        }
    }


}