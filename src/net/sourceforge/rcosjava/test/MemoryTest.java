package net.sourceforge.rcosjava.test;

import net.sourceforge.rcosjava.hardware.memory.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MemoryTest extends TestCase
{
  private MainMemory testMem;
  private Memory memBlock1, memBlock2;

  public MemoryTest(String name)
  {
    super(name);
  }

  public void setUp()
  {
    testMem = new MainMemory(20);
    memBlock1 = new Memory();
    memBlock2 = new Memory();
  }

  //Tests the main memory hardware element.
  //Basically it must allocate and deallocate pages correctly.
  public void testMainMemory()
  {
    try
    {
      int initialSize, sizeBefore, firstFreeBefore, count;
      initialSize = testMem.getFreeUnits();
      //Memory entries start at zero
      assertEquals("First free not 0", testMem.findFirstFree(), 0);
      //Allocating memory block 0, next one should be 1
      testMem.allocateMemory(0);
      assertEquals("First free after allocation was not 1", testMem.findFirstFree(), 1);
      //Allocating memory block 0 again, number of units should be the same as is
      //the first free unit
      sizeBefore = testMem.getFreeUnits();
      firstFreeBefore = testMem.findFirstFree();
      testMem.allocateMemory(0);
      assertEquals("Size after reallocation of same block not the same", testMem.getFreeUnits(),
        sizeBefore);
      assertEquals("First free after same allocation was not 1", firstFreeBefore, 1);
      //Deallocating already free memory should have no effect.
      testMem.freeMemory(1);
      assertEquals("Deallocating already freed memory", testMem.findFirstFree(), 1);
      //Free the allocated memory, the first should be 0
      testMem.freeMemory(0);
      assertEquals("Deallocating allocated memory", testMem.findFirstFree(), 0);
      //Allocate/deallocate all.
      sizeBefore = testMem.getFreeUnits();
      firstFreeBefore = testMem.findFirstFree();
      for (count = 0; count < 20; count++)
        testMem.allocateMemory(count);
      assertEquals("First free after allocation was not -1", testMem.findFirstFree(), -1);
      for (count = 0; count < 20; count++)
        testMem.freeMemory(count);
      assertEquals("Size change after mass de/allocation", testMem.getFreeUnits(), sizeBefore);
      assertEquals("First free after mass de/allocation", testMem.findFirstFree(), firstFreeBefore);
      //Test that the number of Units remains the same
      assertEquals("Total Units differ from start to end", testMem.getFreeUnits(), initialSize);
    }
    catch (NoFreeMemoryException e)
    {
    }
  }

  //Test memory unit.  Basically, a collection of memory which is controlled
  //by the MainMemory.  Must store whether allocated, written and read correctly.
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
    assert("Equality of differing segment sizes", !memBlock1.equals(memBlock2));
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
    assert("Inequality of contents of memory (bigger 1st)", !memBlock1.equals(memBlock2));
    //Test inequalty with contents the different second one with more
    memBlock1 = new Memory("Fred will test");
    memBlock1 = new Memory("Fred will tests");
    assert("Inequality of contents of memory (bigger 2nd)", !memBlock1.equals(memBlock2));
    //Test inequalty with contents are same length but different contents
    memBlock1 = new Memory("Fred will tests");
    memBlock1 = new Memory("Fred will tesss");
    assert("Inequality of contents of memory (differing)", !memBlock1.equals(memBlock2));
    //Test equality with writes
    memBlock1 = new Memory(10);
    memBlock2 = new Memory(10);
    memBlock1.write(new byte[] {1,1,1,1});
    memBlock2.write(new byte[] {1,1,1,1});
    assert("Equality of contents of memory (byte write same, same length)", memBlock1.equals(memBlock2));
    memBlock1 = new Memory(10);
    memBlock2 = new Memory(10);
    memBlock1.write(new byte[] {1,1,0,1});
    memBlock2.write(new byte[] {1,1,1,1});
    assert("Equality of contents of memory (byte write different, same length)", !memBlock1.equals(memBlock2));
    memBlock1 = new Memory(10);
    memBlock2 = new Memory(10);
    memBlock1.write(new byte[] {1,1,1,1,0,1});
    memBlock2.write(new byte[] {1,1,1,1});
    assert("Equality of contents of memory (byte write different, different length 1)", !memBlock1.equals(memBlock2));
    memBlock1 = new Memory(10);
    memBlock2 = new Memory(10);
    memBlock1.write(new byte[] {1,1,1,1});
    memBlock2.write(new byte[] {1,1,1,1,0,1});
    assert("Equality of contents of memory (byte write different, different length 2)", !memBlock1.equals(memBlock2));
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    suite.addTest(new MemoryTest("testMemory"));
    suite.addTest(new MemoryTest("testMainMemory"));
    return suite;
  }

  public static void main (String[] args)
  {
    junit.textui.TestRunner.run(suite());
  }
}