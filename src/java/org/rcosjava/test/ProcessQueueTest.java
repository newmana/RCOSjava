package org.rcosjava.test;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.software.process.ProcessQueue;
import org.rcosjava.software.process.RCOSProcess;
import org.rcosjava.software.util.FIFOQueue;

/**
 * Tests the correct insertion and removal of processes.
 *
 * @author Andrew Newman (created 28 April 2002)
 */
public class ProcessQueueTest extends TestCase
{
  /**
   * Description of the Field
   */
  RCOSProcess tmpProcess1;

  /**
   * Description of the Field
   */
  RCOSProcess tmpProcess2;

  /**
   * Description of the Field
   */
  RCOSProcess tmpProcess3;

  /**
   * Description of the Field
   */
  private ProcessQueue testQueue;

  /**
   * Constructor for the ProcessQueueTest object
   *
   * @param name Description of Parameter
   */
  public ProcessQueueTest(String name)
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

    suite.addTest(new ProcessQueueTest("testProcessQueue"));
    suite.addTest(new ProcessQueueTest("testAddingSameProcess"));
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

  //Tests the main memory hardware element.
  //Basically it must allocate and deallocate pages correctly.
  /**
   * A unit test for JUnit
   */
  public void testProcessQueue()
  {
    try
    {
      testQueue = new ProcessQueue(new FIFOQueue(10, 1));
      tmpProcess1 = new RCOSProcess(1, "test.pll", 123, 1, 1);
      tmpProcess2 = new RCOSProcess(2, "test.pll", 123, 1, 1);
      tmpProcess3 = new RCOSProcess(3, "test.pll", 123, 1, 1);
      testQueue.insertProcess(tmpProcess1);
      testQueue.insertProcess(tmpProcess2);
      testQueue.insertProcess(tmpProcess3);

      assertEquals("First process with peekProcess() ", testQueue.peekProcess(),
          tmpProcess3);
      assertEquals("Second process with getProcess() ", testQueue.getProcess(2),
          tmpProcess2);
      assertEquals("Third process with getProcess() ", testQueue.getProcess(3),
          tmpProcess3);

      testQueue.removeProcess(3);
      testQueue.removeProcess(1);

      assertEquals("Attempt to getProcess() 3 when removed ",
          null, testQueue.getProcess(3));
      assertEquals("Attempt to getProcess() 1 when removed ",
          testQueue.getProcess(1), null);
      assertEquals("Attempt to getProcess() 2 when others removed ",
          testQueue.getProcess(2), tmpProcess2);

      testQueue.removeProcess(2);
      testQueue.insertProcess(tmpProcess1);

      assertEquals("Attempt to getProcess() 1 when all removed ",
          testQueue.getProcess(1), tmpProcess1);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Test inserting the same process twice.
   */
  public void testAddingSameProcess()
  {
    try
    {
      testQueue = new ProcessQueue(new FIFOQueue(10, 1));
      tmpProcess1 = new RCOSProcess(1, "test.pll", 123, 1, 1);
      testQueue.insertProcess(tmpProcess1);
      testQueue.removeProcess(1);
      assertTrue("No processes after insertion and removal", testQueue.size() == 0);

      testQueue.insertProcess(tmpProcess1);
      testQueue.insertProcess(tmpProcess1);
      assertTrue("Should only be one process", testQueue.size() == 1);

      testQueue.removeProcess(1);
      assertTrue("No processes left after removal", testQueue.size() == 0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
