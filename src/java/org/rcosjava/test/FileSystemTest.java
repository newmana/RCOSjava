package org.rcosjava.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.filesystem.*;
import org.rcosjava.software.filesystem.cpm14.*;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class FileSystemTest extends TestCase
{
  /**
   * Constructor for the CPUTest object
   *
   * @param name Description of Parameter
   */
  public FileSystemTest(String name)
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

    suite.addTest(new FileSystemTest("testCPM14"));
    return suite;
  }

  /**
   * The main program for the CPUTest class
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
   * Tests the CPM14 file system implementation.
   */
  public void testCPM14()
  {
    try
    {
      FileSystem fs = new CPM14FileSystem();

      // Mount a new drive.
      String mountPoint = "C";
      String deviceName = "DISK1";
      fs.mount(mountPoint, deviceName);

      // Get file system request.
      int requestId = 0;

      // Allocate a new file called "C:test.pas"
      FileSystemReturnData data = fs.allocate(requestId, "C:test.pas");
      int fileId = data.getReturnValue();

      // Assume allocation is predictable
      assertEquals("Request Id should be equal", 0, requestId);
      assertEquals("File Id should be equal", 0, fileId);

      // Increment the requestId each call.
      requestId++;

      // Create a new directory entry for the file and sets the file length
      // to zero.
      data = fs.create(requestId, fileId);

      // Ensure that the file was created without error.
      assertEquals("Request Id should be equal", 1, requestId);
      assertEquals("File Id should be equal", 0, fileId);

      // Ensure that the first directory entry has been created correctly.
      String dump = fs.dumpDirectoryEntry(0, 0);
      System.out.println(dump);
      assertEquals("Directory request should have one file",
          "0 t e s t         p a s 0 0 0 0 \n0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 " +
          "\n", dump);

      dump = fs.dumpDirectoryEntry(0, 0);
      System.out.println(dump);

      // Write to the file system some bytes.
      for (int counter = 0; counter < 4000; counter++)
      {
        // (byte) 'b'
        // fs.write();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
