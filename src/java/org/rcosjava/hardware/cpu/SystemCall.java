package org.rcosjava.hardware.cpu;

import java.io.Serializable;
import java.util.*;

/**
 * System calls are used by the CSP op-code to distguish the type of system
 * call.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 09/07/2001 Created </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 9th of July 2001
 * @see org.rcosjava.hardware.cpu.Instruction
 * @see org.rcosjava.hardware.cpu.CPU
 * @version 1.00 $Date$
 */
public class SystemCall extends WordParameter
{
  /**
   * The name of the system call. The location in the list reflects its value.
   */
  private static HashMap allSystemCallsByName = new HashMap();

  /**
   * The value of the system call. The location in the list reflects its name.
   */
  private static HashMap allSystemCallsByValue = new HashMap();

  /**
   * Operation constant for system call (OPCODE_CSP) CHaracter IN
   */
  public final static SystemCall CHARACTER_IN = new SystemCall((short) 0,
    "CHIN");

  /**
   * Operation constant for system call (OPCODE_CSP) CHaracter OUT
   */
  public final static SystemCall CHARACTER_OUT = new SystemCall((short) 1,
    "CHOUT");

  /**
   * Operation constant for system call (OPCODE_CSP) NUMber IN
   */
  public final static SystemCall NUMBER_IN = new SystemCall((short) 2, "NUMIN");

  /**
   * Operation constant for system call (OPCODE_CSP) NUMber OUT
   */
  public final static SystemCall NUMBER_OUT = new SystemCall((short) 3,
    "NUMOUT");

  /**
   * Operation constant for system call (OPCODE_CSP) HEXadecimal IN
   */
  public final static SystemCall HEXADECIMAL_IN = new SystemCall((short) 4,
    "HEXIN");

  /**
   * Operation constant for system call (OPCODE_CSP) HEXadecimal OUT
   */
  public final static SystemCall HEXADECIMAL_OUT = new SystemCall((short) 5,
    "HEXOUT");

  /**
   * Operation constant for system call (OPCODE_CSP) EXECute
   */
  public final static SystemCall EXECUTE = new SystemCall((short) 6, "EXEC");

  /**
   * Operation constant for system call (OPCODE_CSP) FORK process
   */
  public final static SystemCall FORK = new SystemCall((short) 7, "FORK");

  /**
   * Operation constant for system call (OPCODE_CSP) STRing OUT
   */
  public final static SystemCall STRING_OUT = new SystemCall((short) 8,
    "STROUT");

  /**
   * Operation constant for system call (OPCODE_CSP) SEMaphore CLOSE
   */
  public final static SystemCall SEMAPHORE_CLOSE = new SystemCall((short) 9,
    "SEM_CLOSE");

  /**
   * Operation constant for system call (OPCODE_CSP) SEMaphore CREATE
   */
  public final static SystemCall SEMAPHORE_CREATE = new SystemCall((short) 10,
    "SEM_CREATE");

  /**
   * Operation constant for system call (OPCODE_CSP) SEMaphore OPEN
   */
  public final static SystemCall SEMAPHORE_OPEN = new SystemCall((short) 11,
    "SEM_OPEN");

  /**
   * Operation constant for system call (OPCODE_CSP) SEMaphore SIGNAL
   */
  public final static SystemCall SEMAPHORE_SIGNAL = new SystemCall((short) 12,
    "SEM_SIGNAL");

  /**
   * Operation constant for system call (OPCODE_CSP) SEMaphore WAIT
   */
  public final static SystemCall SEMAPHORE_WAIT = new SystemCall((short) 13,
    "SEM_WAIT");

  /**
   * Operation constant for system call (OPCODE_CSP) SHaRed memory CLOSE
   */
  public final static SystemCall SHARED_MEMORY_CLOSE = new SystemCall(
    (short) 14, "SHR_CLOSE");

  /**
   * Operation constant for system call (OPCODE_CSP) SHaRed memory CREATE
   */
  public final static SystemCall SHARED_MEMORY_CREATE = new SystemCall(
    (short) 15, "SHR_CREATE");

  /**
   * Operation constant for system call (OPCODE_CSP) SHaRed memory OPEN
   */
  public final static SystemCall SHARED_MEMORY_OPEN = new SystemCall(
    (short) 16, "SHR_OPEN");

  /**
   * Operation constant for system call (OPCODE_CSP) SHaRed memory READ
   */
  public final static SystemCall SHARED_MEMORY_READ = new SystemCall(
    (short) 17, "SHR_READ");

  /**
   * Operation constant for system call (OPCODE_CSP) SHaRed memory WRITE
   */
  public final static SystemCall SHARED_MEMORY_WRITE = new SystemCall(
    (short) 18, "SHR_WRITE");

  /**
   * Operation constant for system call (OPCODE_CSP) SHaRed memory SIZE
   */
  public final static SystemCall SHARED_MEMORY_SIZE = new SystemCall(
    (short) 19, "SHR_SIZE");

  /**
   * Operation constant for system call (OPCODE_CSP) File ALLOCation
   */
  public final static SystemCall FILE_ALLOCATE = new SystemCall((short) 20,
    "F_ALLOC");

  /**
   * Operation constant for system call (OPCODE_CSP) File OPEN
   */
  public final static SystemCall FILE_OPEN = new SystemCall((short) 21,
    "F_OPEN");

  /**
   * Operation constant for system call (OPCODE_CSP) File CREATe
   */
  public final static SystemCall FILE_CREATE = new SystemCall((short) 22,
    "F_CREAT");

  /**
   * Operation constant for system call (OPCODE_CSP) File CLOSE
   */
  public final static SystemCall FILE_CLOSE = new SystemCall((short) 23,
    "F_CLOSE");

  /**
   * Operation constant for system call (OPCODE_CSP) File End Of File
   */
  public final static SystemCall FILE_END_OF_FILE = new SystemCall((short) 24,
    "F_EOF");

  /**
   * Operation constant for system call (OPCODE_CSP) File DELete
   */
  public final static SystemCall FILE_DELETE = new SystemCall((short) 25,
    "F_DEL");

  /**
   * Operation constant for system call (OPCODE_CSP) File READ
   */
  public final static SystemCall FILE_READ = new SystemCall((short) 26,
    "F_READ");

  /**
   * Operation constant for system call (OPCODE_CSP) File WRITE
   */
  public final static SystemCall FILE_WRITE = new SystemCall((short) 27,
    "F_WRITE");

  /**
   * The internal value of the system call. Currently valid between 0 and 27.
   */
  private short systemCallValue;

  /**
   * The internal string value of the system call.
   */
  private String systemCallName;

  /**
   * Default constructor creates operator
   *
   * @param newSystemCallValue Description of Parameter
   * @param newSystemCallName Description of Parameter
   */
  protected SystemCall(short newSystemCallValue, String newSystemCallName)
  {
    systemCallValue = newSystemCallValue;
    systemCallName = newSystemCallName;
    allSystemCallsByName.put(systemCallName, this);
    allSystemCallsByValue.put(new Short(systemCallValue), this);
  }

  /**
   * @param systemCallValue Description of Parameter
   * @return the constant collection of operators stored by value
   */
  public static SystemCall getSystemCallsByValue(short systemCallValue)
  {
    SystemCall tmpSystemCall = (SystemCall)
        allSystemCallsByValue.get(new Short(systemCallValue));

    return tmpSystemCall;
  }

  /**
   * @param systemCallString Description of Parameter
   * @return the constant collection of operators stored by name
   */
  public static SystemCall getSystemCallsByName(String systemCallString)
  {
    SystemCall tmpSystemCall = (SystemCall)
        allSystemCallsByName.get(systemCallString);

    return tmpSystemCall;
  }

  /**
   * @return ordinal value of opcode
   */
  public short getValue()
  {
    return systemCallValue;
  }

  /**
   * @return opcode string value
   */
  public String getName()
  {
    return systemCallName;
  }

  /**
   * @return the string value of the system call.
   */
  public String toString()
  {
    return systemCallName;
  }
}
