package org.rcosjava.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.messaging.messages.universal.NewProcess;
import org.rcosjava.software.process.ProgramManager;
import org.rcosjava.software.process.ProcessQueue;
import org.rcosjava.software.process.RCOSProcess;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.util.FIFOQueue;

/**
 * Test the use of the process scheduler
 *
 * @author Andrew Newman (created 28 April 2002)
 */
public class ProcessSchedulerTest extends TestCase
{
  /**
   * Test process
   */
  RCOSProcess tmpProcess1;

  /**
   * Test process scheduler
   */
  private ProcessScheduler scheduler;

  /**
   * Constructor for the ProcessQueueTest object
   *
   * @param name Description of Parameter
   */
  public ProcessSchedulerTest(String name)
  {
    super(name);
  }

  /**
   * A unit test suite for JUnit
   *
   * @return The test suite
   */
  public static Test suite()
  {
    TestSuite suite = new TestSuite();

    suite.addTest(new ProcessSchedulerTest("testBasicProcessScheduling"));
    suite.addTest(new ProcessSchedulerTest("testBlockingProcessScheduling"));
    suite.addTest(new ProcessSchedulerTest("testKillingProcesses"));
    return suite;
  }

  /**
   * The main program for the ProcessQueueTest class
   *
   * @param args The command line arguments
   */
  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(suite());
  }

  /**
   * The JUnit setup method
   */
  public void setUp()
  {
  }

  /**
   * Test the basic life cycle of a process.
   */
  public void testBasicProcessScheduling()
  {
    try
    {
      scheduler = new ProcessScheduler(new OSOffice("test"));

      // Create new process
      scheduler.newProcess(new NewProcess(scheduler, "test.pll", new Memory(),
          10));
      tmpProcess1 = (RCOSProcess) scheduler.getAllProcesses().get(new Integer(1));

      // Schedule any processes running
      scheduler.schedule();

      // Zombie process created.
      scheduler.zombieCreated(tmpProcess1);

      assertTrue("Should have process in zombie created queue",
          scheduler.getZombieCreatedQueue().getProcess(1).equals(tmpProcess1));

      // Zombie process allocated a terminal
      scheduler.processAllocatedTerminal(tmpProcess1, "Terminal1");

      // Zombie process moved to read queue
      scheduler.zombieToReady(tmpProcess1);

      assertTrue("Should have process in ready queue",
          scheduler.getReadyQueue().getProcess(1).equals(tmpProcess1));
      assertTrue("Zombie queue should be 0",
          scheduler.getZombieCreatedQueue().size() == 0);

      // Schedule any processes running
      scheduler.schedule();

      // Should've moved it to the executing queue
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Process should be executing",
          scheduler.getExecutingProcess().equals(tmpProcess1));

      // Process has finished executing (quantum expired)
      scheduler.runningToReady(tmpProcess1);
      assertTrue("Executing queue should be empty",
          !scheduler.runningProcess());
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Process should be in ready queue",
          scheduler.getReadyQueue().getProcess(1).equals(tmpProcess1));

      // Null process
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingQueue().size() == 0);

      // Schedule
      scheduler.schedule();
      scheduler.schedule();
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Process should be executing",
          scheduler.getExecutingProcess().equals(tmpProcess1));

      // Process finished
      scheduler.processFinished(tmpProcess1);
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Blocked queue should be empty",
          scheduler.getBlockedQueue().size() == 0);
      assertTrue("Zombie Created queue should be empty",
          scheduler.getZombieCreatedQueue().size() == 0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Test the life cycle of a process with blocking.
   */
  public void testBlockingProcessScheduling()
  {
    try
    {
      scheduler = new ProcessScheduler(new OSOffice("test"));

      // Create new process
      scheduler.newProcess(new NewProcess(scheduler, "test.pll", new Memory(),
          10));
      tmpProcess1 = (RCOSProcess) scheduler.getAllProcesses().get(new Integer(1));

      // Schedule any processes running
      scheduler.schedule();

      // Zombie process created.
      scheduler.zombieCreated(tmpProcess1);

      assertTrue("Should have process in zombie created queue",
          scheduler.getZombieCreatedQueue().getProcess(1).equals(tmpProcess1));

      // Zombie process allocated a terminal
      scheduler.processAllocatedTerminal(tmpProcess1, "Terminal1");

      // Zombie process moved to read queue
      scheduler.zombieToReady(tmpProcess1);

      assertTrue("Should have process in ready queue",
          scheduler.getReadyQueue().getProcess(1).equals(tmpProcess1));
      assertTrue("Zombie queue should be 0",
          scheduler.getZombieCreatedQueue().size() == 0);

      // Schedule any processes running
      scheduler.schedule();

      // Should've moved it to the executing queue
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Process should be executing",
          scheduler.getExecutingProcess().equals(tmpProcess1));

      // Process has finished executing (quantum expired)
      scheduler.runningToReady(tmpProcess1);
      assertTrue("Executing queue should be empty",
          !scheduler.runningProcess());
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Process should be in ready queue",
          scheduler.getReadyQueue().getProcess(1).equals(tmpProcess1));

      // Null process
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingQueue().size() == 0);

      // Schedule
      scheduler.schedule();
      scheduler.schedule();
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Process should be executing",
          scheduler.getExecutingProcess().equals(tmpProcess1));

      // Running to blocked
      scheduler.runningToBlocked(tmpProcess1);
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Executing queue should be 0 in size",
          scheduler.getExecutingQueue().size() == 0);
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Blocked queue size should be 1",
          scheduler.getBlockedQueue().size() == 1);
      assertTrue("Process should be in blocked queue",
          scheduler.getBlockedQueue().getProcess(1).equals(tmpProcess1));

      // Null process
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingQueue().size() == 0);

      // Blocked to Ready
      scheduler.blockedToReady(tmpProcess1);
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Executing queue should be 0 in size",
          scheduler.getExecutingQueue().size() == 0);
      assertTrue("Ready queue should be be 1",
          scheduler.getReadyQueue().size() == 1);
      assertTrue("Process should be in ready queue",
          scheduler.getReadyQueue().getProcess(1).equals(tmpProcess1));
      assertTrue("Blocked queue size should be empty",
          scheduler.getBlockedQueue().size() == 0);

      // Schedule
      scheduler.schedule();
      scheduler.schedule();
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Process should be executing",
          scheduler.getExecutingProcess().equals(tmpProcess1));

      // Process finished
      scheduler.processFinished(tmpProcess1);
      assertTrue("Executing queue should be empty",
          scheduler.getExecutingProcess() == null);
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Blocked queue should be empty",
          scheduler.getBlockedQueue().size() == 0);
      assertTrue("Zombie Created queue should be empty",
          scheduler.getZombieCreatedQueue().size() == 0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Test killing the processes from various parts of their life cycle.
   */
  public void testKillingProcesses()
  {
    try
    {
      scheduler = new ProcessScheduler(new OSOffice("test"));

      // Create new process
      scheduler.newProcess(new NewProcess(scheduler, "test.pll", new Memory(),
          10));
      tmpProcess1 = (RCOSProcess) scheduler.getAllProcesses().get(new Integer(1));

      // Schedule any processes running
      scheduler.schedule();

      // Zombie process created.
      scheduler.zombieCreated(tmpProcess1);

      assertTrue("Should have process in zombie created queue",
          scheduler.getZombieCreatedQueue().getProcess(1).equals(tmpProcess1));

      // Zombie process allocated a terminal
      scheduler.processAllocatedTerminal(tmpProcess1, "Terminal1");

      // Zombie process moved to read queue
      scheduler.zombieToReady(tmpProcess1);

      assertTrue("Should have process in ready queue",
          scheduler.getReadyQueue().getProcess(1).equals(tmpProcess1));
      assertTrue("Zombie queue should be 0",
          scheduler.getZombieCreatedQueue().size() == 0);

      // Schedule any processes running
      scheduler.schedule();

      // Should've moved it to the executing queue
      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
      assertTrue("Process should be executing",
          scheduler.getExecutingProcess().equals(tmpProcess1));

      // Process has finished executing (quantum expired)
      scheduler.runningToReady(tmpProcess1);

      // Kill the executing process
      scheduler.killProcess(new RCOSProcess(1, ""));

      assertTrue("Ready queue should be empty",
          scheduler.getReadyQueue().size() == 0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
