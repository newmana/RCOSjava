package net.sourceforge.rcosjava.test;

import net.sourceforge.rcosjava.hardware.cpu.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CPUTest extends TestCase
{
  private Context context1, context2;
  private CPU myCPU;
  private Instruction instruction1, instruction2;
  private Interrupt interrupt1, interrupt2;

  public CPUTest(String name)
  {
    super(name);
  }

  public void setUp()
  {
    context1 = new Context();
    context2 = new Context();
    instruction1 = new Instruction();
    instruction2 = new Instruction();
    interrupt1 = new Interrupt(1, "fred");
    interrupt1 = new Interrupt(1, "fred2");
  }

  public void testContext()
  {
    //Test initial equality
    assertEquals("Equality of created", context1, context2);
  }

  public void testCPU()
  {
  }

  public void testInstruction()
  {
    assertEquals("Equality of null created", instruction1, instruction2);
    assertEquals("Illegal OpCode of null created ", OpCode.ILLEGAL,
      OpCode.getOpCodesByValue(instruction1.getOpCode()));

    instruction1 = new Instruction(OpCode.OPERATION.getValue(),
      (byte) 1, (short) 0);
    assertEquals("Legal Operation 0 ", 0, instruction1.getWordParameter());
    instruction1 = new Instruction(OpCode.OPERATION.getValue(),
      (byte) 1, (short) 22);
    assertEquals("Legal Operation 22 ", 22, instruction1.getWordParameter());
    instruction1 = new Instruction(OpCode.OPERATION.getValue(),
      (byte) 1, (short) 27);
    assertEquals("Illegal Operation 27 ", -1, instruction1.getWordParameter());

    instruction1 = new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
      (byte) 1, (short) 0);
    assertEquals("Legal System Call 0 ", 0, instruction1.getWordParameter());
    instruction1 = new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
      (byte) 1, (short) 27);
    assertEquals("Legal Operation 27 ", 27, instruction1.getWordParameter());
    instruction1 = new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
      (byte) 1, (short) 42);
    assertEquals("Illegal Operation 42 ", -1, instruction1.getWordParameter());

    instruction1 = new Instruction(OpCode.STORE.getValue(), (byte) 1,
      (short) 12);
    instruction2 = new Instruction(OpCode.STORE.getValue(), (byte) 1,
      (short) 12);
    assertEquals("Equality of two created instruction", instruction1,
      instruction2);
    instruction2 = new Instruction(OpCode.CALL.getValue(), (byte) 1,
      (short) 12);
    assert("Equality of two created instruction",
      !instruction1.equals(instruction2));
    instruction2 = new Instruction(OpCode.STORE.getValue(), (byte) 2,
      (short) 12);
    assert("Equality of two created instruction",
      !instruction1.equals(instruction2));
    instruction2 = new Instruction(OpCode.STORE.getValue(), (byte) 1,
      (short) 42);
    assert("Equality of two created instruction",
      !instruction1.equals(instruction2));
  }

  public void testInterrupt()
  {
  }

  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    suite.addTest(new CPUTest("testContext"));
    suite.addTest(new CPUTest("testCPU"));
    suite.addTest(new CPUTest("testInstruction"));
    suite.addTest(new CPUTest("testInterrupt"));
    return suite;
  }

  public static void main (String[] args)
  {
    junit.textui.TestRunner.run(suite());
  }
}