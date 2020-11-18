package fr.customentity.advancedbungeequeue.common.actions.all;

import fr.customentity.advancedbungeequeue.common.actions.PlayerAction;

import java.util.UUID;

public class JoinQueueAction extends PlayerAction {

    private String destinationServer;

    public JoinQueueAction(UUID uuid, String destinationServer) {
        super(uuid);
        this.destinationServer = destinationServer;
    }

    public String getDestinationServer() {
        return destinationServer;
    }
}
