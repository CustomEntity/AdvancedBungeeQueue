package fr.customentity.advancedbungeequeue.common.actions;

import java.io.Serializable;

public abstract class Action<T> implements Serializable {

    public abstract Class<T> getEntryType();

}
