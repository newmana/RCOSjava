package net.sourceforge.rcosjava.software.animator.support.mtgos;

/**
 * A chain is a simple linked list which contains an MTGO object and another
 * chain.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1997
 */
public class Chain
{
  public MTGO object;
  public Chain nextChain;

  public Chain (MTGO newObject, Chain newNextChain)
  {
    object = newObject;
    nextChain = newNextChain;
  }
}
