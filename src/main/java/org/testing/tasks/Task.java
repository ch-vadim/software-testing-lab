package org.testing.tasks;

import org.testing.exceptions.IllegalTaskStateException;
import org.testing.system.Processor;

public class Task extends Thread implements Comparable<Task>{
    protected final long id;

    protected Priority priority;
    protected int ticks;
    protected TaskState _state;
    protected boolean isCompleted = false;

    public Task(long id, int ticks, Priority priority) {
        this.id = id;
        this.ticks = ticks;
        this.priority = priority;
        _state = TaskState.SUSPENDED;
    }

    public TaskState _getState() {
        return _state;
    }

    public Priority _getPriority() {
        return priority;
    }

    public void activate() {
        doTransition(Transition.ACTIVATE);
    }

    public void _start() {
        doTransition(Transition.START);
    }

    public void preempt() {
        doTransition(Transition.PREEMPT);
    }

    public void terminate() {
        doTransition(Transition.TERMINATE);
    }

    public void _wait() {
        throw new IllegalTaskStateException("Primary task can't do wait transition");
    }
    public void release() {
        throw new IllegalTaskStateException("Primary task can't do release transition");
    }

    protected void doTransition(Transition transition) {
        if (!_state.equals(transition.prevState))
            throw new IllegalTaskStateException(this+" with transition"+transition);
        this._state = transition.nextState;

    }

    public boolean isCompleted() { return isCompleted;}

    @Override
    public void run() {
        while (!isCompleted) {
            Processor.waitTick();
            if (_state.equals(TaskState.RUNNING)) {
                if (ticks>0) {
                    ticks--;
                    if (ticks==0) {
                        terminate();
                        isCompleted = true;
                        this.interrupt();
                        return;
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
                ", state=" + _state +
                '}';
    }
}
