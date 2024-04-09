package org.testing.tasks;

public enum Transition {
    ACTIVATE(State.SUSPENDED, State.READY),
    START(State.READY, State.RUNNING),
    PREEMPT(State.RUNNING, State.READY),
    WAIT(State.RUNNING, State.WAITING),
    RELEASE(State.WAITING, State.READY),
    TERMINATE(State.RUNNING, State.SUSPENDED);
    public final State prevState;
    public final State nextState;

    Transition(State prevState, State nextState) {
        this.prevState = prevState;
        this.nextState = nextState;
    }

    public State getPrevState() {
        return prevState;
    }

    public State getNextState() {
        return nextState;
    }
}
