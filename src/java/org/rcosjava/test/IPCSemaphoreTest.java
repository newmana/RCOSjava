package org.rcosjava.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.software.ipc.Semaphore;

/**
 * Test the use of the IPC's semaphore class.
 *
 * @author Andrew Newman (created 28 April 2002)
 */
public class IPCSemaphoreTest extends TestCase
{
  /**
   * Constructor for the IPCSemaphoreTest object
   *
   * @param name Description of Parameter
   */
  public IPCSemaphoreTest(String name)
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

    suite.addTest(new IPCSemaphoreTest("testSimpleUsage"));
    return suite;
  }

  /**
   * The main program for the IPCSemaphoreTest class
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
   * Test the full process of semaphore.
   */
  public void testSimpleUsage()
  {
    try
    {
      int result;
      Semaphore testSem = new Semaphore("test", 1, 1, 0);

      // PID 1, Wait - returns -1 for wait, non-zero for available.
      result = testSem.wait(1);
      assertTrue("Should be able to wait after creating", result == -1);

      testSem.open(2);

      // Returns the PID of the waiting process, should be one.
      result = testSem.signal();
      assertTrue("The waiting semaphore should now be PID 1", result == 1);

      // Close returns how many processes are still using it.
      result = testSem.close(1);
      assertTrue("Should still have one connected", result == 1);

      result = testSem.close(2);
      assertTrue("Should still have one connected", result == 0);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
