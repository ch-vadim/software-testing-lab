package org.testing.tasks;

public enum Transition {
    ACTIVATE(TaskState.SUSPENDED, TaskState.READY),
    START(TaskState.READY, TaskState.RUNNING),
    PREEMPT(TaskState.RUNNING, TaskState.READY),
    WAIT(TaskState.RUNNING, TaskState.WAITING),
    RELEASE(TaskState.WAITING, TaskState.READY),
    TERMINATE(TaskState.RUNNING, TaskState.SUSPENDED);
    public final TaskState prevState;
    public final TaskState nextState;

    Transition(TaskState prevState, TaskState nextState) {
        this.prevState = prevState;
        this.nextState = nextState;
    }

}
