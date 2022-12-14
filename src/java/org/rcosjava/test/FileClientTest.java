package org.rcosjava.test;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.pll2.FileClient;

/**
 * Tests the functionality of the file client in reading/writing various files.
 *
 * @author Andrew Newman (created 28 April 2002)
 */
public class FileClientTest extends TestCase
{
  /**
   * Description of the Field
   */
  private FileClient myClient;

  /**
   * Constructor for the FileClientTest object
   *
   * @param name Description of Parameter
   */
  public FileClientTest(String name)
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

    suite.addTest(new FileClientTest("testGetDirs"));
    suite.addTest(new FileClientTest("testGetFiles"));
    suite.addTest(new FileClientTest("testPutFiles"));
    suite.addTest(new FileClientTest("testStatFiles"));
    return suite;
  }

  /**
   * The main program for the FileClientTest class
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
    myClient = new FileClient("localhost", 4242);
    myClient.openConnection();
  }

  /**
   * The teardown method for JUnit
   */
  public void tearDown()
  {
    myClient.closeConnection();
  }

  /**
   * A unit test for JUnit
   */
  public void testGetDirs()
  {
    assertNotNull("Getting directory listing", myClient.getExeDir());
    assertNotNull("Getting directory listing", myClient.getExeDir(java.io.File.separator));
    assertEquals("That passing / is equal to noargs", myClient.getExeDir().length,
        myClient.getExeDir(java.io.File.separator).length);
  }

  /**
   * A unit test for JUnit
   */
  public void testGetFiles()
  {
//    myClient.getRecFile();
    //Doesn't test whether the data is valid just the right size
    assertEquals("Length of the data returned is not correct", myClient.getExeFile("/NUMBERS.PCD").getSegmentSize(),
        288);
    assertEquals("Length of the data returned is not correct", myClient.getExeFile("/FRED.PCD").getSegmentSize(),
        1024);
  }

  /**
   * A unit test for JUnit
   */
  public void testPutFiles()
  {
    try
    {
      //myClient.writeRecFile("/test.xml", new InstructionExecution(new OSMessageRecorder(),
      //  new Memory()));
      myClient.writeRecFile("/test.xml", "hello");
      System.out.println("[" + myClient.getRecFile("/test.xml") + "]");
    }
    catch (Exception e)
    {
    }
  }

  /**
   * A unit test for JUnit
   */
  public void testStatFiles()
  {
//    myClient.getRecFile();
    assertEquals("Getting known file of length 288", myClient.statExeFile("/NUMBERS.PCD"),
        288);
    assertEquals("Getting a file that doesn't exist", myClient.statExeFile("/FRED.PCD"),
        0);
  }
}
