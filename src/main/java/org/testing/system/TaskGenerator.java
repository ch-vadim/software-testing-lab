package org.testing.system;

import org.testing.tasks.ExtendedTask;
import org.testing.tasks.Priority;
import org.testing.tasks.Task;


import java.util.Random;

public class TaskGenerator {
    private static final Random RANDOM = new Random();
    private static final Priority[] PRIORITIES = Priority.values();
    private static long counter = 0;

    private TaskGenerator(){}
    public static Task generateTask() {
        int ticks = RANDOM.nextInt(5,10);
        Priority priority = PRIORITIES[RANDOM.nextInt(PRIORITIES.length)];
        if (RANDOM.nextBoolean()) return new ExtendedTask(++counter, ticks, priority, ticks-RANDOM.nextInt(1,ticks-1), RANDOM.nextInt(1,10));
        return new Task(++counter, ticks, priority);

    }
}
