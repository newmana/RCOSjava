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
 * An array symbol is a char[] or int[].
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class Array extends Symbol
{
  private short arraySize;

  public Array(String newName, short newLevel, short newOffset,
     short newArraySize)
  {
    name = newName;
    level = newLevel;
    offset = newOffset;
    arraySize = newArraySize;

    // This is a separate variable incase it needs to change in the future
    size = arraySize;
  }
}