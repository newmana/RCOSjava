package net.sourceforge.rcosjava.compiler.symbol;

import net.sourceforge.rcosjava.hardware.cpu.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.sablecc.simplec.analysis.*;
import org.sablecc.simplec.node.*;
import org.sablecc.simplec.lexer.*;
import org.sablecc.simplec.parser.*;
import org.sablecc.simplec.tool.Version;

/**
 * A function symbol is a call to a function.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class Function extends Symbol
{
  private int noParameters;

  public Function(String newName, int newLevel, short newOffset,
      int newNoParameters)
  {
    name = newName;
    level = newLevel;
    offset = newOffset;
    noParameters = newNoParameters;
  }
}