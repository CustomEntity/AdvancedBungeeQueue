package fr.customentity.advancedbungeequeue.common.actions.all;

import fr.customentity.advancedbungeequeue.common.QueueResult;
import fr.customentity.advancedbungeequeue.common.actions.PlayerAction;

import java.io.Serializable;
import java.util.UUID;

public class ConfirmConnectionAction extends PlayerAction {

    private QueueResult queueResult;

    public ConfirmConnectionAction(UUID uuid, QueueResult queueResult) {
        super(uuid);
        this.queueResult = queueResult;
    }

    public QueueResult getQueueResult() {
        return queueResult;
    }
}
