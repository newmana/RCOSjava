package org.rcosjava.messaging.postoffices.animator;

import java.lang.reflect.*;
import java.util.*;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.PostOffice;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.util.FIFOQueue;

/**
 * Provide message handling centre of operations. Variation on PostOffice -
 * Messages are CC'ed to the AnimatorOffice where they are distributed to
 * various Animators (which are actually MessageHandlers).
 * <P>
 * <DT> <B>History:</B>
 * <DD> 17/03/96 MOD for Animator to send to all </DD>
 * <DD> 20/05/97 Changed message system </DD>
 * <DD> 05/05/98 Removed sendToAll (now does this by default) </DD>
 * <DD> 01/04/98 Modified to use TreeMap. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @created 24th January 1996
 * @version 1.00 $Date$
 */
public class AnimatorOffice extends PostOffice
{
  /**
   * Description of the Field
   */
  private OSOffice theOSPostOffice;

  /**
   * The local messages to be sent.
   */
  private FIFOQueue localMessages = new FIFOQueue(5, 1);

  /**
   * The message to be sent to post offices.
   */
  private FIFOQueue postOfficeMessages = new FIFOQueue(5, 1);

  /**
   * List of other post offices to send it to.
   */
  private ArrayList myPostOffices = postOffices;

  /**
   * Attach animator to another post office.
   *
   * @param newId the string identifier to register the post office to the OS
   *      post office.
   * @param newOSPostOffice Description of Parameter
   */
  public AnimatorOffice(String newId, OSOffice newOSPostOffice)
  {
    super(newId, null);

    theOSPostOffice = newOSPostOffice;

    // Register OSPostOffice with Animator Office
    this.addPostOffice(theOSPostOffice);
    // Register the Animator with the OSPostOffice
    theOSPostOffice.addPostOffice(this);

    LocalMessageSender internalSender = new LocalMessageSender();

    internalSender.setName("AnimatorLocalOfficeThread");
    internalSender.start();

    PostOfficeMessageSender poSender = new PostOfficeMessageSender();

    poSender.setName("AnimatorPOOfficeThread");
    poSender.start();
  }

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   */
  public void sendMessage(MessageAdapter message)
  {
    //Send to all other registered post offices by adding it to the list.
    //The send thread should move it along.
    postOfficeMessages.add(message);

    //Send to locally registered components by adding it to the list
    localMessages.add(message);
  }

  /**
   * Send a message to all registered post office and to all locally registered
   * components.
   *
   * @param message the message to send.
   */
  public void sendMessage(UniversalMessageAdapter message)
  {
    sendMessage((MessageAdapter) message);
  }

  /**
   * Sends messages to all conpontents registered to this post office.
   *
   * @param message the message to send.
   */
  public void sendMessage(AnimatorMessageAdapter message)
  {
    sendMessage((MessageAdapter) message);
  }

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   */
  public void localSendMessage(MessageAdapter message)
  {
    localMessages.add(message);
  }

  /**
   * Send a message to only registered object of local post office.
   *
   * @param message Message to send.
   */
  public void localSendMessage(AnimatorMessageAdapter message)
  {
    localSendMessage((MessageAdapter) message);
  }

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   */
  public void sendToPostOffices(MessageAdapter message)
  {
    postOfficeMessages.add(message);
  }

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   */
  public void processMessage(MessageAdapter message)
  {
    try
    {
      message.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error Processing Message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  private class PostOfficeMessageSender extends Thread
  {
    /**
     * Main processing method for the PostOfficeMessageSender object
     */
    public void run()
    {
      while (true)
      {
        try
        {
          Thread.sleep(5);
        }
        catch (java.lang.InterruptedException ie)
        {
        }
        synchronized (postOfficeMessages)
        {
          if (AnimatorOffice.this.postOfficeMessages.size() > 0)
          {
            PostOffice tmpPostOffice;
            //Retrieve first message off the blocks
            MessageAdapter message = (MessageAdapter)
                AnimatorOffice.this.postOfficeMessages.retrieveCurrent();

            if (!myPostOffices.isEmpty())
            {
              int count;

              for (count = 0; count < myPostOffices.size(); count++)
              {
                tmpPostOffice = getPostOffice(count);
                if (message.forPostOffice(tmpPostOffice))
                {
                  tmpPostOffice.localSendMessage(message);
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  private class LocalMessageSender extends Thread
  {
    /**
     * Main processing method for the LocalMessageSender object
     */
    public void run()
    {
      while (true)
      {
        try
        {
          Thread.sleep(5);
        }
        catch (java.lang.InterruptedException ie)
        {
        }
        synchronized (localMessages)
        {
          //Retrieve first message off the blocks
          if (AnimatorOffice.this.localMessages.size() > 0)
          {
            MessageAdapter message = (MessageAdapter)
                AnimatorOffice.this.localMessages.retrieveCurrent();

            if (message.forPostOffice(AnimatorOffice.this))
            {
              //Go through the hashtable returning all the handlers
              //registered.  Send the message to all of them.

              Iterator tmpIter = AnimatorOffice.this.getHandlers().values().iterator();

              synchronized (AnimatorOffice.this.getHandlers())
              {
                while (tmpIter.hasNext())
                {
                  AnimatorMessageHandler theDestination = (AnimatorMessageHandler)
                      tmpIter.next();

                  //Send the message to the destination
                  try
                  {
                    Class[] classes = {message.getClass().getSuperclass()};
                    Method method = theDestination.getClass().getMethod(
                        "processMessage", classes);

                    Object[] args = {message};

                    method.invoke(theDestination, args);
                  }
                  catch (Exception e)
                  {
                    e.printStackTrace();
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
