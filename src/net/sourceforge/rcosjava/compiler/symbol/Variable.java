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
 * A variable symbol is simple construct such as an int or a char.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class Variable extends Symbol
{
  public Variable(String newName, short newLevel, short newOffset)
  {
    name = newName;
    level = newLevel;
    offset = newOffset;
  }
}