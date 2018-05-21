package server.mechanic;

import server.mechanic.services.event.client.ClientEvent;

import javax.validation.constraints.NotNull;

public interface GameMechanics {

    void addClientEvent(@NotNull Integer userId, @NotNull ClientEvent clientSnap);

    void addUser(@NotNull Integer userId);

    void removeUser(@NotNull Integer userId);

    void gmStep(int threadCount);

}
