package fr.customentity.advancedbungeequeue.bungee.data;

public class Priority {

    private String name;
    private int priority;

    public Priority(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }
}
