package org.testing;

import org.testing.system.Processor;
import org.testing.system.Scheduler;
import org.testing.system.TaskGenerator;
import org.testing.tasks.Task;


public class Main {
    private static final int COUNT_TASKS = 50;
    public static void main(String[] args) throws InterruptedException {
        Scheduler scheduler = new Scheduler();
        Processor processor = new Processor();
        Thread schedulerThread = new Thread(scheduler);
        Thread processorThread = new Thread(processor);
        schedulerThread.start();
        processorThread.start();
        Thread.sleep(1000);
        for (int i =0; i<COUNT_TASKS;i++) {
            Task newTask = TaskGenerator.generateTask();
            scheduler.addTask(newTask);
            Thread.sleep(1500);
        }

    }
}