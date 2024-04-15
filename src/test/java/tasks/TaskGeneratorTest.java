package tasks;

import org.junit.Assert;
import org.junit.Test;
import org.testing.system.TaskGenerator;
import org.testing.tasks.Task;

import java.util.HashSet;
import java.util.Set;

public class TaskGeneratorTest {

    //проверяем, что генератор генерируют различные задачи
    @Test
    public void testTaskGenerator() {
        int NUM_TASKS = 5;
        Set<Task> generatedTasks = new HashSet<>();
        for (int i =0; i<NUM_TASKS; i++) {
            generatedTasks.add(TaskGenerator.generateTask());
        }
        Assert.assertEquals(NUM_TASKS, generatedTasks.size());
    }
}
