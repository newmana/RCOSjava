//*******************************************************************/
// FILE     : Context.java
// PURPOSE  : Implements Context for simple PCODE CPU
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman.
// VERSION  : 1.00
// HISTORY  : 23/03/96 Separate from CPU.java into own
//                     file due to move to packages. DJ
//            04/10/97 Modified for getters and setters.
//                     AN
//
//*******************************************************************/

package Hardware.CPU;

// Context class
// - model the context of current CPU

public class Context implements Cloneable
{
  private Instruction instructionRegister;
  private short programCounter; // address for next instruction to execute
  private short stackPointer;   // top of the stack
  private short basePointer;    // base of the stack for current procedure block

  public Context()
  {
    initialise();
  }

  public void initialise()
  {
    programCounter = 0;
    stackPointer = 0;
    basePointer = 0;
    instructionRegister = new Instruction();
  }

  public short getProgramCounter()
  {
    return programCounter;
  }

  public void incProgramCounter()
  {
    programCounter++;
  }

  public void decProgramCounter()
  {
    programCounter--;
  }

  public void setProgramCounter(short sNewProgramCounter)
  {
    programCounter = sNewProgramCounter;
  }

  public short getStackPointer()
  {
    return stackPointer;
  }

  public void incStackPointer()
  {
    stackPointer++;
  }

  public void decStackPointer()
  {
    stackPointer--;
  }

  public void setStackPointer(short sNewStackPointer)
  {
    stackPointer = sNewStackPointer;
  }

  public short getBasePointer()
  {
    return basePointer;
  }

  public void incBasePointer()
  {
    basePointer++;
  }

  public void decBasePointer()
  {
    basePointer--;
  }

  public void setBasePointer(short sNewBasePointer)
  {
    basePointer = sNewBasePointer;
  }

  public Instruction getInstructionRegister()
  {
    return instructionRegister;
  }

  public void setInstructionRegister(Instruction iNewInstructionRegister)
  {
    instructionRegister = iNewInstructionRegister;
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