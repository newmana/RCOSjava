package org.rcosjava.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.RCOS;
import org.rcosjava.messaging.postoffices.os.*;
import org.rcosjava.messaging.postoffices.universal.*;
import org.rcosjava.messaging.messages.os.AddDiskRequest;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.software.disk.*;
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
  private FileSystem fs;

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
      OSOffice postOffice = new OSOffice(RCOS.OS_POST_OFFICE_ID);
      fs = new CPM14FileSystem(postOffice);
      SimpleMessageHandler handler = new SimpleMessageHandler(postOffice);

      // String to hold dump information.
      String dump;

      // Mount a new drive.
      String mountPoint = "C";
      String deviceName = "DISK1";
      fs.mount(mountPoint, deviceName);

      // Get file system request.
      int requestId = 0;

      // Allocate a new file called "C:test.pas"
      FileSystemReturnData data = fs.allocate(requestId, "C:test.pas");
      int fileId = data.getReturnValue();

      // Ensure allocated correctly.
      dump = fs.dumpFIDEntry(fileId);
      assertEquals("The current file id entry should have the correct " +
        "filename and mode", "C:test.pas,0", dump);

      // Assume allocation is predictable
      assertEquals("Request Id should be equal", 0, requestId);
      assertEquals("File Id should be equal", 0, fileId);

      // Increment the requestId each call.
      requestId++;

      // Create a new directory entry for the file and sets the file length
      // to zero.
      data = fs.create(requestId, fileId);

      // Ensure created correctly.
      dump = fs.dumpFIDEntry(fileId);
      assertEquals("The current file id entry should have the correct " +
        "filename and mode", "C:test.pas,2", dump);

      // Ensure that the file was created without error.
      assertEquals("Request Id should be equal", 1, requestId);
      assertEquals("File Id should be equal", 0, fileId);

      // Ensure that the first directory entry has been created correctly.
      dump = fs.dumpDirectoryEntry(0, 0);
      assertEquals("Directory request should have one file",
          "0 t e s t         p a s 0 0 0 0 \n0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 " +
          "\n", dump);

      dump = fs.dumpFIDEntry(fileId);
      assertEquals("The current file id entry should have the correct " +
        "filename and mode", "C:test.pas,2", dump);

      int index = 1;

      // Write to the file system some bytes.
      for (int counter = 0; counter < 4000; counter++)
      {
        requestId++;
        fs.write(requestId, fileId, (byte) 'b');

        // Make sure we deliver the message to the listeners.
        postOffice.deliverMessages();
      }

      // Check the entry now has the correct number of records and data blocks
      // allocated.
      dump = fs.dumpDirectoryEntry(0, 0);
      assertEquals("Directory request should have one file, with 4 allocated " +
        "sectors", "0 t e s t         p a s 0 0 0 31 \n2 3 4 5 0 0 0 0 0 0 0 " +
        "0 0 0 0 0 \n", dump);

      dump = fs.dumpFIDEntry(fileId);
      assertEquals("The current file id entry should have the correct " +
        "filename and mode", "C:test.pas,2", dump);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public class SimpleMessageHandler extends OSMessageRecorder
  {
    public SimpleMessageHandler(OSOffice newPostOffice)
    {
      super("SimpleMessageHandler", newPostOffice, null);
    }

    public void processMessage(OSMessageAdapter newMessage)
    {
      if (newMessage.getType() == "org.rcosjava.messaging.messages.os.AddDiskRequest")
      {
        AddDiskRequest msg = (AddDiskRequest) newMessage;
        fs.flush(msg.getDiskRequest());
      }
    }
  }
}
