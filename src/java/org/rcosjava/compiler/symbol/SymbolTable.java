package org.rcosjava.compiler.symbol;

import org.rcosjava.hardware.cpu.*;
import org.rcosjava.compiler.Compiler;
import org.rcosjava.software.util.FIFOQueue;
import java.io.*;
import java.net.*;
import java.util.*;

import org.sablecc.simplec.analysis.*;
import org.sablecc.simplec.node.*;
import org.sablecc.simplec.lexer.*;
import org.sablecc.simplec.parser.*;

/**
 * A symbol table which holds details for each variable.  Each variable contains
 * an array of symbols which represents the order in which they were inserted.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class SymbolTable
{
  /**
   * Hashmap containing the symbols.
   */
  private HashMap symbols;

  /**
   * The single instance of the symbol table.
   */
  private static SymbolTable instance = null;

  /**
   * Location offset of variable in memory.
   */
  private short symbolIndex;

  /**
   * Create the hashmap.
   */
  private SymbolTable()
  {
    symbols = new HashMap();
  }

  /**
   * Returns the instance of the symbol table or creates one if it doesn't exist
   * yet.
   * @return the instance of the symbol table or creates one if it doesn't exist
   *  yet.
   */
  public static SymbolTable getInstance()
  {
    if (null == instance)
    {
      instance = new SymbolTable();
    }
    return instance;
  }

  /**
   * Adds a new symbolto the symbol table.
   *
   * @param newSymbol the symbol to add.
   */
  public void addSymbol(Symbol newSymbol)
    throws ParserException, IOException, LexerException
  {
    if (symbols.containsKey(newSymbol.getName()))
    {
      HashMap symbolMap = (HashMap) symbols.get(newSymbol.getName());

      // Create a new symbol if the current level doesn't have the required
      // variable.
      if (null == symbolMap.get(new Short(newSymbol.getLevel())))
      {
        short newOffset = (short) (symbolIndex + symbolMap.size());
        newSymbol.setOffset(newOffset);
        symbolMap.put(new Short(newSymbol.getLevel()), newSymbol);
        symbolIndex += newSymbol.getSize();
      }
      else
      {
        throw new org.sablecc.simplec.parser.ParserException(
          Compiler.getLexer().peek().getLine(),
          Compiler.getLexer().peek().getPos(),
          "Duplicate declaration of " + newSymbol.getName());
      }
    }
    else
    {
      HashMap symbolMap = new HashMap();
      newSymbol.setOffset(((short) (symbolIndex)));
      symbolMap.put(new Short(newSymbol.getLevel()), newSymbol);
      symbols.put(newSymbol.getName(), symbolMap);
      symbolIndex += newSymbol.getSize();
    }
  }

  /**
   * Get an Array.  Pull the item out of the given level.
   */
  public Array getArray(String name, short level) throws Exception
  {
    // Assume array is left of left square bracket.

    String arrayName = name.substring(0, name.indexOf("[")).trim();
    return (Array) getSymbol(arrayName, level);
  }

  /**
   * Get a Constant.
   */
  public Constant getConstant(String name, short level) throws Exception
  {
    return (Constant) getSymbol(name, level);
  }

  /**
   * Get a File.
   */
  public File getFile(String name, short level) throws Exception
  {
    return (File) getSymbol(name, level);
  }

  /**
   * Get a Return.
   */
  public Return getReturn(String name, short level) throws Exception
  {
    return (Return) getSymbol(name, level);
  }

  /**
   * Get a Variable.
   */
  public Variable getVariable(String name, short level) throws Exception
  {
    System.out.println("Get var: " + name);
    System.out.println("Get var: " + level);
    return (Variable) getSymbol(name, level);
  }

  /**
   * Find the symbol if the name exists in the symbols hashmap and if
   * the level matches.
   *
   * @param name the name of the symbol to find.
   * @param level the level to find the symbol at.
   * @exception Exception if the symbol is not found.
   */
  public Symbol getSymbol(String name, short level) throws Exception
  {
    Symbol symbol = null;

    if (symbols.containsKey(name))
    {
      HashMap symbolMap = (HashMap) symbols.get(name);

      // Search from the highest to the lowest level for it in the symbol table.
      short counter = level;
      while (counter >= 0)
      {
        if (null != symbolMap.get(new Short(counter)))
        {
          symbol = (Symbol) symbolMap.get(new Short(counter));
          break;
        }
        counter--;
      }
    }

    if (symbol == null)
    {
      throw new Exception("Symbol not found: " + name);
    }
    return symbol;
  }

  /**
   * Returns the total variable size (in bytes).
   *
   * @return the total variable size (in bytes).
   */
  public short getVariableSize()
  {
    return symbolIndex;
  }
}