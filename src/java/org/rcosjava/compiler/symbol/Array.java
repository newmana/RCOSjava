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

  public void handleIntLiteral(StatementCompiler compiler, String varValue)
  {
    short varIntValue = Short.parseShort(varValue.trim());
    compiler.writePCode(OpCode.LITERAL.getValue(), (byte) 0, varIntValue);
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
      handleLoad(compiler, count);
      count++;
    }
    compiler.writePCode(OpCode.LITERAL.getValue(), (byte) 0, (short) (count));
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
        handleStore(compiler, count, OpCode.LITERAL.getValue(), (byte) 0,
          (short) 13);
        handleStore(compiler, count, OpCode.LITERAL.getValue(), (byte) 0,
          (short) 10);
        count = count + 2;
      }
      else
      {
        handleStore(compiler, count, OpCode.LITERAL.getValue(), (byte) 0,
          (short) varValue.charAt(count));
        count++;
      }
    }
  }

  public void handleLoad(StatementCompiler compiler, int index)
  {
    compiler.writePCode(OpCode.LITERAL.getValue(), (byte) 0, (short) index);
    if (Compiler.getLevel() == getLevel())
    {
      compiler.writePCode(OpCode.LOAD_INDEXED.getValue(), (byte) 0,
        getOffset());
    }
    else
    {
      compiler.writePCode(OpCode.LOAD_INDEXED.getValue(),
        (byte) (Compiler.getLevel() - getLevel()), getOffset());
    }
  }

  public void handleLoad(StatementCompiler compiler, Symbol index)
  {
    index.handleLoad(compiler);
    if (Compiler.getLevel() == getLevel())
    {
      compiler.writePCode(OpCode.LOAD_INDEXED.getValue(), (byte) 0,
        getOffset());
    }
    else
    {
      compiler.writePCode(OpCode.LOAD_INDEXED.getValue(),
        (byte) (Compiler.getLevel() - getLevel()), getOffset());
    }
  }

  public void handleStore(StatementCompiler compiler, Symbol index,
    int instruction, byte byteParam, short wordParam)
  {
    index.handleLoad(compiler);
    compiler.writePCode(instruction, byteParam, wordParam);

    if (Compiler.getLevel() == getLevel())
    {
      compiler.writePCode(OpCode.STORE_INDEXED.getValue(), (byte) 0,
        getOffset());
    }
    else
    {
      compiler.writePCode(OpCode.STORE_INDEXED.getValue(),
        (byte) (Compiler.getLevel() - getLevel()), getOffset());
    }
  }

  public void handleStore(StatementCompiler compiler, int index,
    int instruction, byte byteParam, short wordParam)
  {
    compiler.writePCode(OpCode.LITERAL.getValue(), (byte) 0, (short) index);
    compiler.writePCode(instruction, byteParam, wordParam);
    if (Compiler.getLevel() == getLevel())
    {
      compiler.writePCode(OpCode.STORE_INDEXED.getValue(), (byte) 0,
        getOffset());
    }
    else
    {
      compiler.writePCode(OpCode.STORE_INDEXED.getValue(),
        (byte) (Compiler.getLevel() - getLevel()), getOffset());
    }
  }
}