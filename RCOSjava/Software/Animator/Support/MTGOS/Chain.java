//**************************************************************************/
// FILE     : Chain.java
// PACKAGE  : Animator.MTGOS
// PURPOSE  : This is the basic engine which uses the MTGO objects.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/01/97  First created.
//
//**************************************************************************/

package Software.Animator.Support.MTGOS;

public class Chain
{
  public MTGO mob;
  public Chain rest;
  
  public Chain (MTGO mob, Chain rest)
  {
    this.mob = mob;
    this.rest = rest;
  }
}
