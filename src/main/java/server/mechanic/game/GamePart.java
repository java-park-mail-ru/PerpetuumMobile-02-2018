package server.mechanic.game;

public interface GamePart {

    default boolean shouldBeSnaped() {
        return true;
    }

//    Snap<? extends GamePart> takeSnap();
}
