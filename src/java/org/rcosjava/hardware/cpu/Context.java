package org.rcosjava.hardware.cpu;

import java.io.Serializable;

/**
 * Implements Context for simple PCODE CPU. Models the context of current CPU.
 * <P>
 * <DT> <B>Usage Example:</B>
 * <DD> <CODE>
 * 	Context newContext = new Context();
 * </CODE> </DD> </DT>
 * <P>
 * Creates a new context with the Instruction Register, Program Counter, Stack
 * Pointer and Base Pointer all at 0.
 * <P>
 * @author Andrew Newman.
 * @author David Jones
 * @created 21st January 1996
 * @see org.rcosjava.hardware.cpu.CPU
 * @see org.rcosjava.software.kernel.Kernel
 * @version 1.00 $Date$
 */
public class Context implements Cloneable, Serializable
{
  /**
   * The currently executing instruction.
   */
  private Instruction instructionRegister;

  /**
   * Address for next instruction to execute
   */
  private short programCounter = -1;

  /**
   * Top of the stack.
   */
  private short stackPointer = -1;

  /**
   * Base of the stack for current procedure block
   */
  private short basePointer = -1;

  /**
   * Null constructor. Initialises the object.
   */
  public Context()
  {
    initialise();
  }

  /**
   * Sets the program counter to the newly given value.
   *
   * @param newProgramCounter The new ProgramCounter value
   * @newProgramCounter the new value to set the program counter to.
   */
  public void setProgramCounter(short newProgramCounter)
  {
    programCounter = newProgramCounter;
  }

  /**
   * Sets the stack pointer to the newly given value.
   *
   * @param newStackPointer The new StackPointer value
   * @newStackPointer the new value to set the stack pointer to.
   */
  public void setStackPointer(short newStackPointer)
  {
    stackPointer = newStackPointer;
  }

  /**
   * Sets the base pointer to the newly given value.
   *
   * @param newBasePointer The new BasePointer value
   * @newBasePointer the new value to set the base pointer to.
   */
  public void setBasePointer(short newBasePointer)
  {
    basePointer = newBasePointer;
  }

  /**
   * Sets the instructions to the newly given object.
   *
   * @param newInstructionRegister The new InstructionRegister value
   * @newInstructionRegister the new instruction register object.
   */
  public void setInstructionRegister(Instruction newInstructionRegister)
  {
    instructionRegister = newInstructionRegister;
  }

  /**
   * Returns the value of the program counter.
   *
   * @return The ProgramCounter value
   */
  public short getProgramCounter()
  {
    return programCounter;
  }

  /**
   * Returns the value of the stack pointer.
   *
   * @return The StackPointer value
   */
  public short getStackPointer()
  {
    return stackPointer;
  }

  /**
   * Returns the value of the base pointer.
   *
   * @return The BasePointer value
   */
  public short getBasePointer()
  {
    return basePointer;
  }

  /**
   * Returns a copy (value) of the instruction register.
   *
   * @return The InstructionRegister value
   */
  public Instruction getInstructionRegister()
  {
    return instructionRegister;
  }

  /**
   * Initialises the program counter, stack pointer and base pointer to 0.
   * Creates a new instruction register.
   */
  public void initialise()
  {
    programCounter = 0;
    stackPointer = 0;
    basePointer = 0;
    instructionRegister = new Instruction();
  }

  /**
   * Increments the program counter by one.
   */
  public void incProgramCounter()
  {
    programCounter++;
  }

  /**
   * Decrements the program counter by one.
   */
  public void decProgramCounter()
  {
    programCounter--;
  }

  /**
   * Increments the stack pointer by one.
   */
  public void incStackPointer()
  {
    stackPointer++;
  }

  /**
   * Decrements the stack pointer by one.
   */
  public void decStackPointer()
  {
    stackPointer--;
  }

  /**
   * Increments the base pointer by one.
   */
  public void incBasePointer()
  {
    basePointer++;
  }

  /**
   * Decrements the base pointer by one.
   */
  public void decBasePointer()
  {
    basePointer--;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public Object clone()
  {
    Context newObject = (Context) new Context();

    if (instructionRegister != null)
    {
      newObject.instructionRegister = (Instruction) instructionRegister.clone();
    }
    newObject.setBasePointer(this.getBasePointer());
    newObject.setInstructionRegister(this.getInstructionRegister());
    newObject.setProgramCounter(this.getProgramCounter());
    newObject.setStackPointer(this.getStackPointer());
    return newObject;
  }

  /**
   * Returns the hash code of the object.
   *
   * @return the hash code of the object.
   */
  public int hashCode()
  {
    return instructionRegister.hashCode() ^ basePointer ^ programCounter ^
        stackPointer;
  }

  /**
   * Returns equal if the passed object is the same type, and the instruction
   * register, program counter, stack point and base pointer are equal.
   *
   * @param obj object to test for equality.
   * @return true if the objects are equal.
   */
  public boolean equals(Object obj)
  {
    if (obj != null && (obj.getClass().equals(this.getClass())))
    {
      Context con = (Context) obj;

      if ((instructionRegister.equals(con.instructionRegister)) &&
          (programCounter == con.programCounter) &&
          (stackPointer == con.stackPointer) &&
          (basePointer == con.basePointer))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the string value of the context in the form: Instruction Register,
   * Program Counter, Stack Pointer, Base Pointer.
   *
   * @return Description of the Returned Value
   */
  public String toString()
  {
    return instructionRegister + ", " + programCounter + ", " + stackPointer +
        ", " + basePointer;
  }
}
