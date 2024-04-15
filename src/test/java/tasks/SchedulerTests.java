package tasks;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testing.system.Processor;
import org.testing.system.Scheduler;
import org.testing.tasks.ExtendedTask;
import org.testing.tasks.Priority;
import org.testing.tasks.Task;

public class SchedulerTests {

    Scheduler scheduler;
    Processor processor;
    Thread schedulerThread;
    Thread processorThread;

    @Before
    public void init() {
        scheduler = new Scheduler();
        processor = new Processor();
        schedulerThread = new Thread(scheduler);
        processorThread = new Thread(processor);
        schedulerThread.start();
        processorThread.start();
    }

    @After
    public void interruptAllThreads() {
        schedulerThread.interrupt();
        processorThread.interrupt();
    }

    //Проверяем что задача попала в буффер и после тика начала исполняться
    @Test
    public void addTaskTest() throws InterruptedException {
        Task task = new Task(0, 2, Priority.LOW);
        scheduler.addTask(task);
        Assert.assertFalse(scheduler.buffer.isEmpty());
        Processor.waitTick();
        Thread.sleep(300);
        Assert.assertNotNull(scheduler.executableTask);
    }

    //Проверяем, что задачи попадают в очередь
    @Test
    public void fillQueueTest() throws InterruptedException {
        Task task = new Task(0, 2, Priority.LOW);
        Task task2 = new Task(0, 2, Priority.LOW);
        scheduler.addTask(task);
        scheduler.addTask(task2);
        Processor.waitTick();
        Thread.sleep(300);
        Assert.assertFalse(scheduler.readyTasks.isEmpty());
    }


    //Проверяем, что после завершения задачи она попадает в список завершенных
    @Test
    public void testCompleteTask() throws InterruptedException {
        Task task = new Task(0, 1, Priority.LOW);
        scheduler.addTask(task);
        Processor.waitTick();
        Processor.waitTick();
        Thread.sleep(300);
        Assert.assertFalse(scheduler.completableTasks.isEmpty());
    }

    //проверяем что после ухода в состояние waiting задача попадает в соответствующую очередь
    @Test
    public void testWaitingQueue() throws InterruptedException {
        Task task = new ExtendedTask(0, 3, Priority.LOW, 1, 2);
        scheduler.addTask(task);
        Processor.waitTick();
        Processor.waitTick();
        Thread.sleep(300);
        Assert.assertFalse(scheduler.waitingTasks.isEmpty());

    }

    //проверяем что задача заменяется более приоритетной
    @Test
    public void testChangeTaskMorePriority() {
        Task task = new Task(0, 5, Priority.LOW);
        Task task2 = new Task(0, 5, Priority.CRITICAL);
        scheduler.addTask(task);
        Processor.waitTick();
        Processor.waitTick();
        scheduler.addTask(task2);
        Processor.waitTick();
        Processor.waitTick();
        Assert.assertEquals(Priority.CRITICAL, scheduler.executableTask._getPriority());

    }

    //проверяем что после окончания ожидания задача уходит и списка задач с состояним Waiting
    @Test
    public void testReleaseTask() throws InterruptedException {
        Task task = new ExtendedTask(0, 5, Priority.CRITICAL, 1, 3);
        scheduler.addTask(task);
        Processor.waitTick();
        Processor.waitTick();
        Thread.sleep(300);
        Assert.assertFalse(scheduler.waitingTasks.isEmpty());
        Processor.waitTick();
        Processor.waitTick();
        Processor.waitTick();
        Processor.waitTick();
        Assert.assertTrue(scheduler.waitingTasks.isEmpty());
        Assert.assertNotNull(scheduler.executableTask);

    }


}
