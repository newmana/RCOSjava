package org.rcosjava.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rcosjava.hardware.cpu.CPU;
import org.rcosjava.hardware.cpu.Context;
import org.rcosjava.hardware.cpu.Instruction;
import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.hardware.cpu.OpCode;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class CPUTest extends TestCase
{
  /**
   * Description of the Field
   */
  private Context context1, context2;

  /**
   * Description of the Field
   */
  private Instruction instruction1, instruction2;

  /**
   * Constructor for the CPUTest object
   *
   * @param name Description of Parameter
   */
  public CPUTest(String name)
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

    suite.addTest(new CPUTest("testContext"));
    suite.addTest(new CPUTest("testCPU"));
    suite.addTest(new CPUTest("testInstruction"));
    suite.addTest(new CPUTest("testInterrupt"));
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
    context1 = new Context();
    context2 = new Context();
    instruction1 = new Instruction();
    instruction2 = new Instruction();
  }

  /**
   * A unit test for JUnit
   */
  public void testContext()
  {
    //Test initial equality
    assertEquals("Equality of created", context1, context2);
  }

  /**
   * A unit test for JUnit
   */
  public void testCPU()
  {
  }

  /**
   * A unit test for JUnit
   */
  public void testInstruction()
  {
    assertEquals("Equality of null created", instruction1, instruction2);
    assertEquals("Illegal OpCode of null created ", OpCode.ILLEGAL,
        instruction1.getOpCode());

    instruction1 = Instruction.OPR_INSTRUCTION;
    instruction1.setByteParameter((byte) 1);
    instruction1.setWordParameter((short) 0);
    assertEquals("Legal Operation 0 ", 0, instruction1.getWordParameter());
    instruction1 = Instruction.OPR_INSTRUCTION;
    instruction1.setByteParameter((byte) 1);
    instruction1.setWordParameter((short) 22);
    assertEquals("Legal Operation 22 ", 22, instruction1.getWordParameter());
    instruction1 = Instruction.OPR_INSTRUCTION;
    instruction1.setByteParameter((byte) 1);
    instruction1.setWordParameter((short) 27);
    assertEquals("Illegal Operation 27 ", -1, instruction1.getWordParameter());

    instruction1 = Instruction.CSP_INSTRUCTION;
    instruction1.setByteParameter((byte) 1);
    instruction1.setWordParameter((short) 0);
    assertEquals("Legal System Call 0 ", 0, instruction1.getWordParameter());
    instruction1 = Instruction.CSP_INSTRUCTION;
    instruction1.setByteParameter((byte) 1);
    instruction1.setWordParameter((short) 27);
    assertEquals("Legal Operation 27 ", 27, instruction1.getWordParameter());
    instruction1 = Instruction.CSP_INSTRUCTION;
    instruction1.setByteParameter((byte) 1);
    instruction1.setWordParameter((short) 42);
    assertEquals("Illegal Operation 42 ", -1, instruction1.getWordParameter());

    instruction1 = (Instruction) Instruction.STO_INSTRUCTION.clone();
    instruction1.setByteParameter((byte) 1);
    instruction1.setWordParameter((short) 12);
    instruction2 = (Instruction) Instruction.STO_INSTRUCTION.clone();
    instruction2.setByteParameter((byte) 1);
    instruction2.setWordParameter((short) 12);
    assertEquals("Two instruction should be equal", instruction1,
        instruction2);
    instruction2 = (Instruction) Instruction.CAL_INSTRUCTION.clone();
    instruction2.setByteParameter((byte) 1);
    instruction2.setWordParameter((short) 12);
    assertTrue("Two instructions should be equal (2)",
        !instruction1.equals(instruction2));
    instruction2 = (Instruction) Instruction.STO_INSTRUCTION.clone();
    instruction2.setByteParameter((byte) 2);
    instruction2.setWordParameter((short) 12);
    assertTrue("Two instructions should be unequal",
        !instruction1.equals(instruction2));
    instruction2 = (Instruction) Instruction.STO_INSTRUCTION.clone();
    instruction2.setByteParameter((byte) 1);
    instruction2.setWordParameter((short) 42);
    assertTrue("Two instructions should be uneqal (2)",
        !instruction1.equals(instruction2));
  }

  /**
   * A unit test for JUnit
   */
  public void testInterrupt()
  {
  }
}
