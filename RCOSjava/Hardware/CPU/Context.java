/**
 * Implements Context for simple PCODE CPU.  Models the context of current CPU.
 *
 * Usage example:
 * <CODE>
 * 	Context newContext = new Context();
 * </CODE>
 *
 * @see Hardware.CPU.Instruction
 * @see Hardware.CPU.Interrupt
 * @version 1.00 $Date$
 * @author Andrew Newman.
 * @author David Jones
 **/

package Hardware.CPU;

public class Context implements Cloneable
{
  /**
   * The currently executing instruction.
   */
  private Instruction instructionRegister;

  /**
   * Address for next instruction to execute
   */
  private short programCounter;

  /**
   * Top of the stack.
   */
  private short stackPointer;

  /**
   * Base of the stack for current procedure block
   */
  private short basePointer;

  /**
   * Null constructor.  Initialises the object.
   * @see initialise
   */
  public Context()
  {
    initialise();
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
   * Returns the value of the program counter.
   */
  public short getProgramCounter()
  {
    return programCounter;
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
   * Sets the program counter to the newly given value.
   *
   * @newProgramCounter the new value to set the program counter to.
   */
  public void setProgramCounter(short newProgramCounter)
  {
    programCounter = newProgramCounter;
  }

  /**
   * Returns the value of the stack pointer.
   */
  public short getStackPointer()
  {
    return stackPointer;
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
   * Sets the stack pointer to the newly given value.
   *
   * @newStackPointer the new value to set the stack pointer to.
   */
  public void setStackPointer(short newStackPointer)
  {
    stackPointer = newStackPointer;
  }

  /**
   * Returns the value of the base pointer.
   */
  public short getBasePointer()
  {
    return basePointer;
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
   * Sets the base pointer to the newly given value.
   *
   * @newBasePointer the new value to set the base pointer to.
   */
  public void setBasePointer(short newBasePointer)
  {
    basePointer = newBasePointer;
  }

  /**
   * Returns a copy (value) of the instruction register.
   */
  public Instruction getInstructionRegister()
  {
    return (Instruction) instructionRegister.clone();
  }

  /**
   * Sets the instructions to the newly given object.
   *
   * @newInstructionRegister the new instruction register object.
   */
  public void setInstructionRegister(Instruction newInstructionRegister)
  {
    instructionRegister = newInstructionRegister;
  }

  public Object clone()
  {
    try
    {
      Context newObject = (Context) super.clone();
      if (this.instructionRegister != null)
        newObject.instructionRegister = (Instruction) this.instructionRegister.clone();
      return newObject;
    }
    catch (java.lang.CloneNotSupportedException e)
    {
    }
    return null;
  }

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

  public String toString()
  {
    return instructionRegister + ", " + programCounter + ", " + stackPointer +
     ", " + basePointer;
  }
}