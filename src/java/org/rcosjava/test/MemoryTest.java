package org.rcosjava.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.hardware.memory.MainMemory;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.hardware.memory.NoFreeMemoryException;

/**
 * Tests reading and writing of memory blocks.
 *
 * @author Andrew Newman (created 28 April 2002).
 */
public class MemoryTest extends TestCase
{
  /**
   * Description of the Field
   */
  private MainMemory testMem;
  /**
   * Description of the Field
   */
  private Memory memBlock1, memBlock2;

  /**
   * Constructor for the MemoryTest object
   *
   * @param name Description of Parameter
   */
  public MemoryTest(String name)
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

    suite.addTest(new MemoryTest("testMemory"));
    suite.addTest(new MemoryTest("testMainMemory"));
    return suite;
  }

  /**
   * The main program for the MemoryTest class
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
    testMem = new MainMemory(20);
    memBlock1 = new Memory();
    memBlock2 = new Memory();
  }

  //Tests the main memory hardware element.
  //Basically it must allocate and deallocate pages correctly.
  /**
   * A unit test for JUnit
   */
  public void testMainMemory()
  {
    try
    {
      int initialSize;
      int sizeBefore;
      int firstFreeBefore;
      int count;

      initialSize = testMem.getFreeUnits();
      //Memory entries start at zero
      assertEquals("First free not 0", testMem.findFirstFree(), 0);
      //Allocating memory block 0, next one should be 1
      testMem.allocateMemory(testMem.findFirstFree());
      assertEquals("First free after allocation was not 1", testMem.findFirstFree(), 1);
      //Allocating memory block 0 again, number of units should be the same as is
      //the first free unit
      sizeBefore = testMem.getFreeUnits();
      firstFreeBefore = testMem.findFirstFree();
      testMem.allocateMemory(0);
      assertEquals("Size after reallocation of same block not the same", testMem.getFreeUnits(),
          sizeBefore);
      assertEquals("First free after same allocation was 1", firstFreeBefore, 1);
      testMem.allocateMemory(firstFreeBefore);
      assertEquals("First free should now be 2", testMem.findFirstFree(), 2);
      //Deallocating already free memory should have no effect.
      testMem.freeMemory(1);
      assertEquals("Deallocating already freed memory", testMem.findFirstFree(), 1);
      //Free the allocated memory, the first should be 0
      testMem.freeMemory(0);
      assertEquals("Deallocating allocated memory", testMem.findFirstFree(), 0);
      //Allocate/deallocate all.
      sizeBefore = testMem.getFreeUnits();
      firstFreeBefore = testMem.findFirstFree();
      for (count = 0; count < 19; count++)
      {
        testMem.allocateMemory(count);
      }
      for (count = 0; count < 19; count++)
      {
        testMem.freeMemory(count);
      }
      assertEquals("Size change after mass de/allocation", testMem.getFreeUnits(), sizeBefore);
      assertEquals("First free after mass de/allocation", testMem.findFirstFree(), firstFreeBefore);

      //Test find first free works
      for (count = 0; count < 19; count++)
      {
        testMem.allocateMemory(testMem.findFirstFree());
      }
      assertEquals("After 20 find firsts should be none left", testMem.getFreeUnits(), 1);
      for (count = 0; count < 19; count++)
      {
        testMem.freeMemory(count);
      }

      //Test that the number of Units remains the same
      assertEquals("Total Units differ from start to end", testMem.getFreeUnits(), initialSize);
    }
    catch (NoFreeMemoryException e)
    {
      e.printStackTrace();
    }
  }

  //Test memory unit.  Basically, a collection of memory which is controlled
  //by the MainMemory.  Must store whether allocated, written and read correctly.
  /**
   * A unit test for JUnit
   */
  public void testMemory()
  {
    //Test equality
    assertEquals("Equality of created", memBlock1, memBlock2);
    //Test allocation and eqaulity
    memBlock1.setAllocated();
    memBlock2.setAllocated();
    assertEquals("Equality of allocated", memBlock1, memBlock2);
    //Test segment size and equality
    memBlock1 = new Memory(90210);
    assertTrue("Equality of differing segment sizes", !memBlock1.equals(memBlock2));
    //Test segment size is correct
    //Test equality of with segment sizes
    memBlock1 = new Memory(100);
    memBlock1.setAllocated();
    memBlock2 = new Memory(100);
    memBlock2.setAllocated();
    assertEquals("Arbitary Segment Size", 100, memBlock1.getSegmentSize());
    assertEquals("Equality of segment sizes", memBlock1, memBlock2);
    //Test equalty with contents the same
    memBlock1.write("Fred will test".getBytes());
    memBlock2.write("Fred will test".getBytes());
    assertEquals("Arbitary Segment Size", 100, memBlock1.getSegmentSize());
    assertEquals("Equality of contents of memory", memBlock1, memBlock2);
    //Test inequalty with contents the different first one with more
    memBlock1 = new Memory("Fred will tests");
    memBlock1 = new Memory("Fred will test");
    assertTrue("Inequality of contents of memory (bigger 1st)", !memBlock1.equals(memBlock2));
    //Test inequalty with contents the different second one with more
    memBlock1 = new Memory("Fred will test");
    memBlock1 = new Memory("Fred will tests");
    assertTrue("Inequality of contents of memory (bigger 2nd)", !memBlock1.equals(memBlock2));
    //Test inequalty with contents are same length but different contents
    memBlock1 = new Memory("Fred will tests");
    memBlock1 = new Memory("Fred will tesss");
    assertTrue("Inequality of contents of memory (differing)", !memBlock1.equals(memBlock2));
    //Test equality with writes
    memBlock1 = new Memory(10);
    memBlock2 = new Memory(10);
    memBlock1.write(new byte[]{1, 1, 1, 1});
    memBlock2.write(new byte[]{1, 1, 1, 1});
    assertTrue("Equality of contents of memory (byte write same, same length)", memBlock1.equals(memBlock2));
    memBlock1 = new Memory(10);
    memBlock2 = new Memory(10);
    memBlock1.write(new byte[]{1, 1, 0, 1});
    memBlock2.write(new byte[]{1, 1, 1, 1});
    assertTrue("Equality of contents of memory (byte write different, same length)", !memBlock1.equals(memBlock2));
    memBlock1 = new Memory(10);
    memBlock2 = new Memory(10);
    memBlock1.write(new byte[]{1, 1, 1, 1, 0, 1});
    memBlock2.write(new byte[]{1, 1, 1, 1});
    assertTrue("Equality of contents of memory (byte write different, different length 1)", !memBlock1.equals(memBlock2));
    memBlock1 = new Memory(10);
    memBlock2 = new Memory(10);
    memBlock1.write(new byte[]{1, 1, 1, 1});
    memBlock2.write(new byte[]{1, 1, 1, 1, 0, 1});
    assertTrue("Equality of contents of memory (byte write different, different length 2)", !memBlock1.equals(memBlock2));
  }
}
