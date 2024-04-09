package org.testing.system;

import org.testing.tasks.State;
import org.testing.tasks.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class Scheduler implements Runnable {

    private static final int DEFAULT_MAX_COUNT_READY_TASKS = 10;
    private final BlockingQueue<Task> buffer = new LinkedBlockingQueue<>();
    private final PriorityBlockingQueue<Task> readyTasks = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<Task> waitingTasks =
            new PriorityBlockingQueue<>(DEFAULT_MAX_COUNT_READY_TASKS, Comparator.comparing(Task::getState));

    private final int maxCountReadyTasks;

    private Task executableTask;

    private final List<Task> completableTasks = new ArrayList<>();

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
                    waitingTasks.peek().getState().equals(State.READY)) {
                Task task = waitingTasks.take();
                System.out.println("Task released after waiting: " + task);
                readyTasks.add(task);
            }
        }
    }

    private boolean isCompleted() {
        return executableTask != null && executableTask.getTicks() == 0;
    }

    private boolean isWaiting() {
        return executableTask != null && executableTask.getState().equals(State.WAITING);
    }

    private void checkMorePriority() throws InterruptedException {
        if (executableTask == null) {
            if (!readyTasks.isEmpty()) {
                executableTask = readyTasks.take();
                Thread thread = new Thread(executableTask);
                thread.start();
                executableTask.start();
            }
        } else {
            if (!readyTasks.isEmpty()) {
                Task task = readyTasks.peek();
                if (executableTask.getPriority().compareTo(task.getPriority()) > 0) {
                    readyTasks.take();
                    executableTask.preempt();
                    readyTasks.add(executableTask);
                    System.out.println("Task: " + executableTask + " was replaced by higher priority: " + task);
                    executableTask = task;
                    Thread thread = new Thread(executableTask);
                    thread.start();
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
