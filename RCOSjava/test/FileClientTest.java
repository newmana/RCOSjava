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
    myClient.getExeFile("/NUMBERS.PCD");
    myClient.getExeFile("/FRED.PCD");
  }

  public void testStatFiles()
  {
//    myClient.getRecFile();
    myClient.statExeFile("/NUMBERS.PCD");
    myClient.statExeFile("/FRED.PCD");
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    suite.addTest(new FileClientTest("testGetDirs"));
    suite.addTest(new FileClientTest("testGetFiles"));
    suite.addTest(new FileClientTest("testGetFiles"));
    return suite;
  }

  public static void main (String[] args)
  {
    junit.textui.TestRunner.run(suite());
  }
}
