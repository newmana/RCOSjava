package org.rcosjava.software.animator.support.mtgos;

/**
 * A chain is a simple linked list which contains an MTGO object and another
 * chain.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1997
 * @version 1.00 $Date$
 */
public class Chain
{
  /**
   * The MTGO object that is this part of the chain.
   */
  private MTGO object;

  /**
   * The next chain object from this one.
   */
  private Chain nextChain;

  /**
   * Create a new chain object by adding the given MTGO and the next chain.  The
   * end of a chain is represented by adding a null chain.
   *
   * @param newObject the current chain object in the chain.
   * @param newNextChain the next chain object.
   */
  public Chain(MTGO newObject, Chain newNextChain)
  {
    object = newObject;
    nextChain = newNextChain;
  }

  /**
   * Returns the MTGO object.
   *
   * @return the MTGO object.
   */
  public MTGO getMTGO()
  {
    return object;
  }

  /**
   * Sets the MTGO object.
   *
   * @param newMTGO the new MTGO object.
   */
  public void setMTGO(MTGO newMTGO)
  {
    object = newMTGO;
  }

  /**
   * Returns the next chain object.
   *
   * @return the next chain object.
   */
  public Chain getNextChain()
  {
    return nextChain;
  }

  /**
   * Sets the next chain object.
   */
  public void setNextChain(Chain newChain)
  {
    nextChain = newChain;
  }
}