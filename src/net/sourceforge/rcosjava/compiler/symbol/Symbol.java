package net.sourceforge.rcosjava.compiler.symbol;

import net.sourceforge.rcosjava.hardware.cpu.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.sablecc.simplec.analysis.*;
import org.sablecc.simplec.node.*;
import org.sablecc.simplec.lexer.*;
import org.sablecc.simplec.parser.*;

/**
 * A symbol is a constant, variable, array, function, return or file.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public abstract class Symbol
{
  /**
   * The name of the symbol in the symbol table.
   */
  protected String name;

  /**
   * The level that the variable is located in the stack.
   */
  protected short level;

  /**
   * Offset that the variable is located in.
   */
  protected short offset;

  /**
   * Returns the name of the symbol.
   *
   * @return the name of the symbol.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the level (starting at 0) that the variable is located.
   *
   * @return the level (starting at 0) that the variable is located.
   */
  public short getLevel()
  {
    return level;
  }

  /**
   * Returns the location of the variable in the stack.
   *
   * @return the location of the variable in the stack.
   */
  public short getOffset()
  {
    return offset;
  }

  /**
   * Sets the location of the variable in the stack.
   *
   * @param newOffset the offset in the stack of the variable.
   */
  public void setOffset(short newOffset)
  {
    offset = newOffset;
  }
}