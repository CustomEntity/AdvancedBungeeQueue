package fr.customentity.advancedbungeequeue.common.actions;

import java.util.UUID;

public abstract class PlayerAction extends Action<UUID> {

    private String uuid;

    public PlayerAction(UUID uuid) {
        this.uuid = uuid.toString();
    }

    public UUID getSenderUniqueId() {
        return UUID.fromString(uuid);
    }

    @Override
    public Class<UUID> getEntryType() {
        return UUID.class;
    }
}
