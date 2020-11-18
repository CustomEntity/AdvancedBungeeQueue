package fr.customentity.advancedbungeequeue.common.actions;

import java.util.UUID;

public abstract class PlayerAction extends Action<UUID> {

    private UUID uuid;

    public PlayerAction(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Class<UUID> getEntryType() {
        return UUID.class;
    }
}
