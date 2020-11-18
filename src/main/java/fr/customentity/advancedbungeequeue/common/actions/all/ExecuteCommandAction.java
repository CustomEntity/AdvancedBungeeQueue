package fr.customentity.advancedbungeequeue.common.actions.all;

import fr.customentity.advancedbungeequeue.common.actions.PlayerAction;

import java.util.UUID;

public class ExecuteCommandAction extends PlayerAction {

    private String command;

    public ExecuteCommandAction(UUID uuid, String command) {
        super(uuid);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
