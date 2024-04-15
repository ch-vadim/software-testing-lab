package org.testing.system;

import org.testing.tasks.TaskState;
import org.testing.tasks.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Scheduler implements Runnable {

    private static final int DEFAULT_MAX_COUNT_READY_TASKS = 10;
    public final BlockingQueue<Task> buffer = new LinkedBlockingQueue<>();
    public final PriorityBlockingQueue<Task> readyTasks = new PriorityBlockingQueue<>();
    public final PriorityBlockingQueue<Task> waitingTasks =
            new PriorityBlockingQueue<>(DEFAULT_MAX_COUNT_READY_TASKS, Comparator.comparing(Task::_getState));

    private final int maxCountReadyTasks;

    public Task executableTask;

    public final List<Task> completableTasks = new ArrayList<>();

    public Scheduler() {
        this(DEFAULT_MAX_COUNT_READY_TASKS);
    }

    public Scheduler(int maxCountReadyTasks) {
        this.maxCountReadyTasks = maxCountReadyTasks;
    }

    public void addTask(Task task) {
        synchronized(this) {
            buffer.add(task);
        }
    }

    private void fillQueue() throws InterruptedException {
        synchronized (this) {
            while (buffer.size() != 0 &&
                    readyTasks.size() + waitingTasks.size() < maxCountReadyTasks - 1) {
                Task task = buffer.take();
                task.activate();
                readyTasks.add(task);
                System.out.println("Added to queue: "+task);
                System.out.println("Current queue size: " + (readyTasks.size() + waitingTasks.size()));
            }
            while (waitingTasks.size() != 0 &&
                    waitingTasks.peek()._getState().equals(TaskState.READY)) {
                Task task = waitingTasks.take();
                System.out.println("Task released after waiting: " + task);
                readyTasks.add(task);
            }
        }
    }

    private boolean isCompleted() {
        return executableTask != null && executableTask.isCompleted();
    }

    private boolean isWaiting() {
        return executableTask != null && executableTask._getState().equals(TaskState.WAITING);
    }

    private void checkMorePriority() throws InterruptedException {
        if (executableTask == null) {
            if (!readyTasks.isEmpty()) {
                executableTask = readyTasks.take();
                if (executableTask.getState().equals(Thread.State.NEW))
                    executableTask.start();
                executableTask._start();
            }
        } else {
            if (!readyTasks.isEmpty()) {
                Task task = readyTasks.peek();
                if (executableTask._getPriority().compareTo(task._getPriority()) > 0) {
                    readyTasks.take();
                    executableTask.preempt();
                    readyTasks.add(executableTask);
                    System.out.println("Task: " + executableTask + " was replaced by higher priority: " + task);
                    executableTask = task;
                    executableTask._start();
                    if (executableTask.getState().equals(Thread.State.NEW))
                        executableTask.start();
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Processor.waitTick();
                Thread.sleep(100);
                fillQueue();
                if (isCompleted()) {
                    completableTasks.add(executableTask);
                    System.out.println("Task was complete: "+executableTask);
                    executableTask = null;
                }
                if (isWaiting()) {
                    waitingTasks.add(executableTask);
                    System.out.println("Task start waiting: " + executableTask);
                    executableTask = null;
                }
                checkMorePriority();
                System.out.println("Current task: " + executableTask);
                System.out.println("Completed tasks size = "+ completableTasks.size());
                System.out.println();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
