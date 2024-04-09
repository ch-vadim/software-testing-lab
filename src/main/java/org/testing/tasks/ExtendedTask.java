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
        while (true) {
            if (state.equals(State.RUNNING)) {
                Processor.waitTick();
                if (ticks>0) {
                    ticks--;
                    ticksBeforeWaiting--;
                    if (ticksBeforeWaiting == 0) {
                        _wait();
                    }
                    if (ticks==0) {
                        terminate();
                        break;
                    }
                }
            } else if (state.equals(State.WAITING)) {
                Processor.waitTick();
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
                ", state=" + state +
                ", ticksBeforeWaiting=" + ticksBeforeWaiting +
                ", waitingTicks=" + waitingTicks +
                '}';
    }
}
