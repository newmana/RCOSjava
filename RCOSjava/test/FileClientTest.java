package test;

import pll2.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FileClientTest extends TestCase
{
  private FileClient myClient;

  public FileClientTest(String name)
  {
    super(name);
  }

  public void setUp()
  {
    myClient = new FileClient("localhost", 4242);
    myClient.openConnection();
  }

  public void tearDown()
  {
    myClient.closeConnection();
  }

  public void testGetDirs()
  {
    assertNotNull("Getting directory listing", myClient.getExeDir());
    assertNotNull("Getting directory listing", myClient.getExeDir("/"));
    assertEquals("That passing / is equal to noargs", myClient.getExeDir().length,
      myClient.getExeDir("/").length);
  }

  public void testGetFiles()
  {
//    myClient.getRecFile();
    System.out.println("File: " + myClient.getExeFile("/NUMBERS.PCD"));
    System.out.println("File: " + myClient.getExeFile("/FRED.PCD"));
  }

  public void testStatFiles()
  {
//    myClient.getRecFile();
    assertEquals("Getting known file of length 288", myClient.statExeFile("/NUMBERS.PCD"),
      288);
    assertEquals("Getting a file that doesn't exist", myClient.statExeFile("/FRED.PCD"),
      0);
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    suite.addTest(new FileClientTest("testGetDirs"));
    suite.addTest(new FileClientTest("testGetFiles"));
    suite.addTest(new FileClientTest("testStatFiles"));
    return suite;
  }

  public static void main (String[] args)
  {
    junit.textui.TestRunner.run(suite());
  }
}
