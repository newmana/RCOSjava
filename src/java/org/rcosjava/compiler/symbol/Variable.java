package org.rcosjava.compiler.symbol;

import org.rcosjava.hardware.cpu.*;
import org.rcosjava.compiler.Compiler;
import org.rcosjava.compiler.StatementCompiler;
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
    size = 1;
  }

  public void handleStore(StatementCompiler compiler)
  {
    OpCode storeOpCode = OpCode.STORE;
    // Store variable at the variables location
    if (Compiler.getLevel() == getLevel())
    {
      compiler.writePCode(new Instruction(storeOpCode.getValue(), (byte) 0,
        getOffset()));
    }
    else
    {
      compiler.writePCode(new Instruction(storeOpCode.getValue(),
        (byte) getLevel(), getOffset()));
    }
  }

  public void handleLoad(StatementCompiler compiler)
  {
    OpCode loadOpCode = null;
    loadOpCode = OpCode.LOAD;

    if (Compiler.getLevel() == getLevel())
    {
      compiler.writePCode(new Instruction(loadOpCode.getValue(), (byte) 0,
        getOffset()));
    }
    else
    {
      compiler.writePCode(new Instruction(loadOpCode.getValue(),
        (byte) getLevel(), getOffset()));
    }
  }

  public void handleCharLiteral(StatementCompiler compiler, String varValue)
  {
    compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
      (short) varValue.charAt(1)));
  }

  public void handleStringLiteral(StatementCompiler compiler, String varValue)
  {
    byte byteParam = 0;

    int varStrLength = varValue.length()-1;
    //emit each element in the string
    int count = 1;
    while(count < varStrLength)
    {
      // Check if it's a \n otherwise ignore
      if ((varValue.charAt(count) == '\\') &&
          (varValue.charAt(count + 1) == 'n'))
      {
        compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
          (short) 13));
        compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
          (short) 10));
        count = count + 2;
      }
      else
      {
        compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
          (short) varValue.charAt(count)));
        count++;
      }
    }
    //emit store a required pos
    compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), byteParam,
      (short) (count-1)));
  }

  public void handleIntLiteral(StatementCompiler compiler,
    String varValue)
  {
    short varIntValue = Short.parseShort(varValue.trim());
    compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
      varIntValue));
  }
}