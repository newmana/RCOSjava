package net.sourceforge.rcosjava.compiler;

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
 * Provides a compiler of a simple C like grammar with certain extensions..
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class Compiler
{
  public static void main(String args[])
  {
    System.out.println(Version.banner());

    if(args.length != 1)
    {
      System.out.println("usage:");
      System.out.println("  java Compiler filename");
      System.exit(1);
    }

    try
    {
      Lexer lexer = new Lexer(new PushbackReader(new BufferedReader(
        new FileReader(args[0])), 1024));

      Parser parser = new Parser(lexer);

      Node ast = parser.parse();

      ast.apply(new FunctionCompiler());

      System.out.println(ast);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}