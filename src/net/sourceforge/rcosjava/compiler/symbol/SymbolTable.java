package net.sourceforge.rcosjava.compiler.symbol;

import net.sourceforge.rcosjava.hardware.cpu.*;
import net.sourceforge.rcosjava.software.util.FIFOQueue;
import java.io.*;
import java.net.*;
import java.util.*;

import org.sablecc.simplec.analysis.*;
import org.sablecc.simplec.node.*;
import org.sablecc.simplec.lexer.*;
import org.sablecc.simplec.parser.*;
import org.sablecc.simplec.tool.Version;

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
  private HashMap instructions;

  /**
   * The single instance of the symbol table.
   */
  private static SymbolTable instance = null;

  /**
   * Create the hashmap.
   */
  private SymbolTable()
  {
    instructions = new HashMap();
  }

  /**
   * Returns the instance of the symbol table or creates one if it doesn't exist
   * yet.
   * @return the instance of the symbol table or creates one if it doesn't exist
   *         yet.
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
   * Gets the name of the symbol and saves it to
   */
  public void addSymbol(Symbol newSymbol)
    throws Exception
  {
    if (instructions.containsKey(newSymbol.getName()))
    {
      ArrayList list = (ArrayList) instructions.get(newSymbol.getName());
      if (null == list.get(newSymbol.getLevel()))
      {
        list.add(newSymbol.getLevel(), newSymbol);
      }
      else
      {
        throw new Exception("Symbol already exists");
      }
    }
    else
    {
      ArrayList list = new ArrayList();
      list.add(newSymbol.getLevel(), newSymbol);
      instructions.put(newSymbol.getName(), list);
    }
  }

  /**
   * Get an Array.  Pull the item out of the given level.
   */
  public Array getArray(String name, int level) throws Exception
  {
    return (Array) getSymbol(name, level);
  }

  /**
   * Get a Constant.
   */
  public Constant getConstant(String name, int level) throws Exception
  {
    return (Constant) getSymbol(name, level);
  }

  /**
   * Get a File.
   */
  public File getFile(String name, int level) throws Exception
  {
    return (File) getSymbol(name, level);
  }

  /**
   * Get a Return.
   */
  public Return getReturn(String name, int level) throws Exception
  {
    return (Return) getSymbol(name, level);
  }

  /**
   * Get a Variable.
   */
  public Variable getVariable(String name, int level) throws Exception
  {
    return (Variable) getSymbol(name, level);
  }

  /**
   * Find the symbol if the name exists in the instructions hashmap and if
   * the level matches.
   *
   * @param name the name of the symbol to find.
   * @param level the level to find the symbol at.
   * @exception Exception if the symbol is not found.
   */
  private Symbol getSymbol(String name, int level) throws Exception
  {
    Symbol symbol = null;
    System.out.println("Get Symbol!!");

    if (instructions.containsKey(name))
    {
      ArrayList list = (ArrayList) instructions.get(name);
      System.out.println("Symbol: " + symbol);

      if (null != list.get(level))
      {
        System.out.println("Symbol: " + symbol);
        symbol = (Symbol) list.get(level);
        System.out.println("Symbol: " + symbol);
      }
    }

    System.out.println("Symbol: " + symbol);
    if (symbol == null)
    {
      throw new Exception("Symbol not found");
    }
    return symbol;
  }
}