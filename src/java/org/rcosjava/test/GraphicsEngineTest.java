package org.rcosjava.test;
import java.awt.*;
import java.awt.image.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.software.animator.support.mtgos.GraphicsEngine;
import org.rcosjava.software.animator.support.mtgos.MTGO;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class GraphicsEngineTest extends TestCase
{
  /**
   * Description of the Field
   */
  private GraphicsEngine testEngine;
  /**
   * Description of the Field
   */
  private Frame frame = new Frame("Hi");

  /**
   * Constructor for the GraphicsEngineTest object
   *
   * @param name Description of Parameter
   */
  public GraphicsEngineTest(String name)
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

    suite.addTest(new GraphicsEngineTest("testAddingAndRetrieving"));
    return suite;
  }

  /**
   * The main program for the GraphicsEngineTest class
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
    frame.setVisible(true);
    testEngine = new GraphicsEngine();
  }

  /**
   * A unit test for JUnit
   */
  public void testAddingAndRetrieving()
  {
    testEngine.addMTGO(new MTGO(new BufferedImage(1, 1, 1), "test1", false), frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1, 1, 1), "test2", false), frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1, 1, 1), "test3", false), frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1, 1, 1), "test4", false), frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1, 1, 1), "test5", false), frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1, 1, 1), "test6", false), frame);

    try
    {
      testEngine.removeMTGO("test1");
      testEngine.removeMTGO("test4");
      testEngine.removeMTGO("test6");
      assertEquals("Retrieving 2nd object", "test2", testEngine.returnMTGO("test2").getName());
      assertEquals("Retrieving 3rd object", "test3", testEngine.returnMTGO("test3").getName());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
