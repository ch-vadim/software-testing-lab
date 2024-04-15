package org.testing.tasks;

import org.testing.system.Processor;

public class ExtendedTask extends Task{
    private int ticksBeforeWaiting;
    private int waitingTicks;
    public ExtendedTask(long id, int ticks, Priority priority, int ticksBeforeWaiting, int waitingTicks) {
        super(id, ticks, priority);
        this.ticksBeforeWaiting = ticksBeforeWaiting;
        this.waitingTicks = waitingTicks;
    }

    @Override
    public void _wait() {
        doTransition(Transition.WAIT);
    }

    @Override
    public void release() {
        doTransition(Transition.RELEASE);
    }

    @Override
    public void run() {
        while (!isCompleted) {
            Processor.waitTick();
            if (_state.equals(TaskState.RUNNING)) {
                if (ticks>0) {
                    ticks--;
                    ticksBeforeWaiting--;
                    if (ticksBeforeWaiting == 0) {
                        ticksBeforeWaiting--;
                        _wait();
                    }
                    if (ticks==0) {
                        terminate();
                        isCompleted = true;
                        this.interrupt();
                        return;
                    }
                }
            } else if (_state.equals(TaskState.WAITING)) {
                if (waitingTicks>0) {
                    waitingTicks--;
                    if (waitingTicks == 0) {
                        release();
                    }
                }
            }

        }
    }

    @Override
    public String toString() {
        return "ExtendedTask{" +
                "id=" + id +
                ", priority=" + priority +
                ", ticks=" + ticks +
                ", state=" + _state +
                ", ticksBeforeWaiting=" + ticksBeforeWaiting +
                ", waitingTicks=" + waitingTicks +
                '}';
    }
}
