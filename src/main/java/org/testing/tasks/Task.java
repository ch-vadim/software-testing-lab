package org.testing.tasks;

import org.testing.exceptions.IllegalTaskStateException;
import org.testing.system.Processor;

public class Task implements Runnable, Comparable<Task>{
    protected final long id;

    protected Priority priority;
    protected int ticks;
    protected State state;

    public Task(long id, int ticks, Priority priority) {
        this.id = id;
        this.ticks = ticks;
        this.priority = priority;
        state = State.SUSPENDED;
    }

    public State getState() {
        return state;
    }
    public int getTicks() {
        return ticks;
    }
    public Priority getPriority() {
        return priority;
    }

    public void activate() {
        doTransition(Transition.ACTIVATE);
    }

    public void start() {
        doTransition(Transition.START);
    }

    public void preempt() {
        doTransition(Transition.PREEMPT);
    }

    public void terminate() {
        doTransition(Transition.TERMINATE);
    }

    public void _wait() {
        throw new RuntimeException();
    }
    public void release() {
        throw new RuntimeException();
    }

    protected void doTransition(Transition transition) {
        if (!state.equals(transition.prevState))
            throw new IllegalTaskStateException(this+" with transition"+transition);
        this.state = transition.nextState;

    }

    @Override
    public void run() {
        while (true) {
            if (state.equals(State.RUNNING)) {
                Processor.waitTick();
                if (ticks>0) {
                    ticks--;
                    if (ticks==0) {
                        terminate();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int compareTo(Task o) {
        return this.priority.compareTo(o.priority);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", priority=" + priority +
                ", ticks=" + ticks +
                ", state=" + state +
                '}';
    }
}
