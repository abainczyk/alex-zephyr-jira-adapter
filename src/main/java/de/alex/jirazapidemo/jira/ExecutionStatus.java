package de.alex.jirazapidemo.jira;

public class ExecutionStatus {
    private boolean active;
    private int inQueue;

    public ExecutionStatus(boolean active, int inQueue) {
        this.active = active;
        this.inQueue = inQueue;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getInQueue() {
        return inQueue;
    }

    public void setInQueue(int inQueue) {
        this.inQueue = inQueue;
    }
}
