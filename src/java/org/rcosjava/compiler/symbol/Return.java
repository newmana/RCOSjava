package org.rcosjava.compiler.symbol;

import org.rcosjava.hardware.cpu.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.sablecc.simplec.analysis.*;
import org.sablecc.simplec.node.*;
import org.sablecc.simplec.lexer.*;
import org.sablecc.simplec.parser.*;

/**
 * A return symbol is the type retuned by the return statmenet in a function.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class Return extends Symbol
{
  public Return(String newName, short newLevel, short newOffset)
  {
    name = newName;
    level = newLevel;
    offset = newOffset;
  }
}