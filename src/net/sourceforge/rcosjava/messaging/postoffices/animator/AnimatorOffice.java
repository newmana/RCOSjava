package net.sourceforge.rcosjava.messaging.postoffices.animator;

import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import java.util.Iterator;
import java.util.SortedMap;

/**
 * Provide message handling centre of operations.  Variation on PostOffice -
 * Messages are CC'ed to the AnimatorOffice where they are distributed to
 * various Animators (which are actually MessageHandlers)
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 17/03/96 MOD for Animator to send to all
 * </DD><DD>
 * 20/05/97 Changed message system
 * </DD><DD>
 * 05/05/98 Removed sendToAll (now does this by default)
 * </DD><DD>
 * 01/04/98 Modified to use TreeMap.
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 24th January 1996
 */
public class AnimatorOffice extends PostOffice
{
  private OSOffice theOSPostOffice;

  /**
   * Attach animator to another post office.
   *
   * @param newId the string identifier to register the post office to the
   * OS post office.
   * @param OSOffice the post office to register to for universal messages
   * to pass between.
   */
  public AnimatorOffice(String newId, OSOffice newPostOffice)
  {
    id = newId;
    theOSPostOffice = newPostOffice;
    // Register OSPostOffice with Animator Office
    this.addPostOffice(theOSPostOffice);
    // Register the Animator with the OSPostOffice
    theOSPostOffice.addPostOffice(this);
  }

  public void sendMessage(MessageAdapter message)
  {
    //Send to all other registered post offices.
    sendToPostOffices(message);
    //Send to locally registered components.
    localSendMessage(message);
  }

  /**
   * Send a message to all registered post office and to all locally registered
   * components.
   */
  public void sendMessage(UniversalMessageAdapter aMessage)
  {
    sendMessage((MessageAdapter) aMessage);
  }

  /**
   * To be done
   *
   * @param message To be done
   */
  public void sendMessage(AnimatorMessageAdapter message)
  {
    sendMessage((MessageAdapter) message);
  }

  public void localSendMessage(MessageAdapter message)
  {
    if (message.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.

     Iterator tmpIter = this.getHandlers().values().iterator();

      synchronized (this.getHandlers())
      {
        while(tmpIter.hasNext())
        {
          AnimatorMessageHandler theDestination = (AnimatorMessageHandler)
            tmpIter.next();
          //Send the message to the destination
          theDestination.processMessage(message);
        }
      }
    }
  }

 /**
   * Send a message to only registered object of local post office.
   *
   * @param message Message to send.
   */
  public void localSendMessage(AnimatorMessageAdapter message)
  {
    //System.out.println("AO Sending: " + message);
    if (message.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.
      Iterator tmpIter = this.getHandlers().values().iterator();

      synchronized (this.getHandlers())
      {
        while(tmpIter.hasNext())
        {
          AnimatorMessageHandler theDestination = (AnimatorMessageHandler)
            tmpIter.next();
          //Send the message to the destination
          theDestination.processMessage(message);
        }
      }
    }
  }

  public void sendToPostOffices(MessageAdapter message)
  {
    PostOffice poTmpPostOffice;

    if (!postOffices.isEmpty())
    {
      int iCount;
      for (iCount = 0; iCount < postOffices.size(); iCount++)
      {
        poTmpPostOffice = getPostOffice(iCount);
        if (message.forPostOffice(poTmpPostOffice))
        {
          poTmpPostOffice.localSendMessage(message);
        }
      }
    }
  }

  public void processMessage(MessageAdapter maMsg)
  {
    try
    {
      maMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error Processing Message: "+e.getMessage());
      e.printStackTrace();
    }
  }
}
