package tasks;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testing.exceptions.IllegalTaskStateException;
import org.testing.tasks.Priority;
import org.testing.tasks.TaskState;
import org.testing.tasks.Task;

public class TaskTests {

    Task task;

    @Before
    public void initTask() {
        task = new Task(0, 10, Priority.LOW);
    }

    @Test
    public void testInitialState() {
        Assert.assertEquals(TaskState.SUSPENDED, task._getState());
    }

    @Test
    public void testActivate() {
        task.activate();

        Assert.assertEquals(TaskState.READY, task._getState());
    }

    @Test
    public void testStart() {
        task.activate();
        task._start();

        Assert.assertEquals(TaskState.RUNNING, task._getState());
    }

    @Test
    public void testPreempt() {
        task.activate();
        task._start();
        task.preempt();

        Assert.assertEquals(TaskState.READY, task._getState());
    }

    @Test
    public void testTerminate() {
        task.activate();
        task._start();
        task.terminate();

        Assert.assertEquals(TaskState.SUSPENDED, task._getState());
    }

    @Test
    public void testIllegalTransitionsFromSuspendedState() {
        Assert.assertThrows(IllegalTaskStateException.class, () -> task._start());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task.preempt());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task.terminate());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task._wait());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task.release());
    }

    @Test
    public void testIllegalTransitionsFromReadyState() {
        task.activate();

        Assert.assertThrows(IllegalTaskStateException.class, () -> task.activate());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task.preempt());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task.terminate());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task._wait());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task.release());
    }

    @Test
    public void testIllegalTransitionsFromRunningState() {
        task.activate();
        task._start();

        Assert.assertThrows(IllegalTaskStateException.class, () -> task.activate());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task._start());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task._wait());
        Assert.assertThrows(IllegalTaskStateException.class, () -> task.release());
    }

}
