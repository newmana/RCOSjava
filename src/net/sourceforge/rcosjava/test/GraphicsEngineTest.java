package net.sourceforge.rcosjava.test;

import net.sourceforge.rcosjava.software.animator.support.*;
import net.sourceforge.rcosjava.software.animator.support.mtgos.*;
import net.sourceforge.rcosjava.software.animator.support.positions.*;
import java.awt.*;
import java.awt.image.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GraphicsEngineTest extends TestCase
{
  private GraphicsEngine testEngine;
  private Frame frame = new Frame("Hi");

  public GraphicsEngineTest(String name)
  {
    super(name);
  }

  public void setUp()
  {
    frame.setVisible(true);
    testEngine = new GraphicsEngine(frame);
  }


  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    suite.addTest(new GraphicsEngineTest("testAddingAndRetrieving"));
    return suite;
  }

  public void testAddingAndRetrieving()
  {
    testEngine.addMTGO(new MTGO(new BufferedImage(1,1,1),"test1",false),frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1,1,1),"test2",false),frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1,1,1),"test3",false),frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1,1,1),"test4",false),frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1,1,1),"test5",false),frame);
    testEngine.addMTGO(new MTGO(new BufferedImage(1,1,1),"test6",false),frame);

    try
    {
      testEngine.removeMTGO("test1");
      testEngine.removeMTGO("test4");
      testEngine.removeMTGO("test6");
      assertEquals("Retrieving 2nd object", "test2", testEngine.returnMTGO("test2").text);
      assertEquals("Retrieving 3rd object", "test3", testEngine.returnMTGO("test3").text);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
  {
    junit.textui.TestRunner.run(suite());
  }
}
