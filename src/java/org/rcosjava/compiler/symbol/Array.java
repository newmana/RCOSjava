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
 * An array symbol is a char[] or int[].
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class Array extends Symbol
{
  private short arraySize;
  private String varValue;

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

  public void handleCharLiteral(StatementCompiler compiler, String newVarValue)
  {
    varValue = newVarValue;
  }

  public void handleStringLiteral(StatementCompiler compiler,
    String newVarValue)
  {
    varValue = newVarValue.substring(1, newVarValue.length()-1);
  }

  public void handleLoad(StatementCompiler compiler)
  {
    int varStrLength = varValue.length();
    int count = 0;
    while(count < varStrLength)
    {
      writeLoad(compiler, count);
      count++;
    }
    compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
      (short) (count)));
  }

  public void handleStore(StatementCompiler compiler)
  {
    int varStrLength = varValue.length();
    int count = 0;
    Instruction tmpInst;

    while(count < varStrLength)
    {
      // Check if it's a \n otherwise ignore
      if ((varValue.charAt(count) == '\\') &&
          (varValue.charAt(count + 1) == 'n'))
      {
        tmpInst = new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
          (short) 13);
        writeStore(compiler, count, tmpInst);
        tmpInst = new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
          (short) 10);
        writeStore(compiler, count, tmpInst);
        count = count + 2;
      }
      else
      {
        tmpInst = new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
          (short) varValue.charAt(count));
        writeStore(compiler, count, tmpInst);
        count++;
      }
    }
  }

  private void writeLoad(StatementCompiler compiler, int index)
  {
    compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
      (short) index));
    compiler.writePCode(new Instruction(OpCode.LOAD_INDEXED.getValue(),
      (byte) 0, getOffset()));
  }

  private void writeStore(StatementCompiler compiler, int index,
    Instruction newInstruction)
  {
    compiler.writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
      (short) index));
    compiler.writePCode(newInstruction);
    compiler.writePCode(new Instruction(OpCode.STORE_INDEXED.getValue(),
      (byte) 0, getOffset()));
  }
}