package org.rcosjava.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.ipc.*;

/**
 * Test the use of the IPC Manager Animator Test.
 *
 * @author administrator
 * @created 28 April 2002
 */
public class IPCManagerAnimatorTest extends TestCase
{
  /**
   * Constructor for the ProcessQueueTest object
   *
   * @param name Description of Parameter
   */
  public IPCManagerAnimatorTest(String name)
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

    suite.addTest(new IPCManagerAnimatorTest("testSemaphores"));
    return suite;
  }

  /**
   * The main program for the IPCManagerAnimatorTest class
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
  public void testSemaphores()
  {
    try
    {
      // Setup animator for testing.
      OSOffice osOffice = new OSOffice("test");
      AnimatorOffice animatorOffice = new AnimatorOffice("test2", osOffice);
      IPCManagerAnimator animator = new IPCManagerAnimator(animatorOffice,
          new ImageIcon[] { new ImageIcon() } );
      Frame f = new Frame("Test IPC Manager");
      f.setSize(600,300);
      f.setVisible(true);
      f.setBackground(Color.black);
      f.add(animator.getPanel());
      animator.setupLayout(f);
      f.invalidate();
      f.validate();

      HashMap semaphoreMap;
      Semaphore semaphore;
      java.util.List attached;
      java.util.List waiting;

      // Test creation
      animator.semaphoreCreated("test", 1, 1);
      semaphoreMap = animator.getSemaphoreMap();
      semaphore = (Semaphore) semaphoreMap.get("test");
      assert("Creator PID is 1", semaphore.getCreatorId() == 1);
      assert("Name is 'test'", semaphore.getName().equals("test"));
      assert("Value is 1", semaphore.getValue() == 1);

      // Test waiting
      animator.semaphoreWaiting("test", 1, 1);

      semaphoreMap = animator.getSemaphoreMap();
      semaphore = (Semaphore) semaphoreMap.get("test");
      attached = semaphore.getAttachedProcesses();
      waiting = semaphore.getWaitingProcesses();
      assert("Creator PID is 1", semaphore.getCreatorId() == 1);
      assert("Name is 'test'", semaphore.getName().equals("test"));
      assert("Value expected was 1 but got " + semaphore.getValue(),
          semaphore.getValue() == 1);
      assert("Attached processes should be one ", attached.size() == 1);
      assert("Waiting process should be one was " + waiting.size(),
          waiting.size() == 1);

      // Test open
      animator.semaphoreOpened("test", 2, 1);

      semaphoreMap = animator.getSemaphoreMap();
      semaphore = (Semaphore) semaphoreMap.get("test");
      attached = semaphore.getAttachedProcesses();
      waiting = semaphore.getWaitingProcesses();
      assert("Creator PID is 1", semaphore.getCreatorId() == 1);
      assert("Name is 'test'", semaphore.getName().equals("test"));
      assert("Value expected was 1 but got " + semaphore.getValue(),
          semaphore.getValue() == 1);
      assert("Attached processes should be two ", attached.size() == 2);
      assert("Waiting process should be one was " + waiting.size(),
          waiting.size() == 1);

      // Test signal
      animator.semaphoreSignalled("test", 2, 0, 1);

      semaphoreMap = animator.getSemaphoreMap();
      semaphore = (Semaphore) semaphoreMap.get("test");
      attached = semaphore.getAttachedProcesses();
      waiting = semaphore.getWaitingProcesses();
      assert("Creator PID is 1", semaphore.getCreatorId() == 1);
      assert("Name is 'test'", semaphore.getName().equals("test"));
      assert("Value expected was 0 but got " + semaphore.getValue(),
          semaphore.getValue() == 0);
      assert("Attached processes should be two", attached.size() == 2);
      assert("Waiting process should be zero was " + waiting.size(),
          waiting.size() == 0);

      // Test close
      animator.semaphoreClosed("test", 2, 1);
      semaphoreMap = animator.getSemaphoreMap();
      semaphore = (Semaphore) semaphoreMap.get("test");
      attached = semaphore.getAttachedProcesses();
      assert("Attached processes should be one", attached.size() == 1);

      animator.semaphoreClosed("test", 1, 1);
      semaphoreMap = animator.getSemaphoreMap();
      semaphore = (Semaphore) semaphoreMap.get("test");
      assertNull("Should've removed test ", semaphore);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
