package org.rcosjava.messaging.postoffices.animator;

import java.lang.reflect.*;
import java.util.*;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.PostOffice;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.util.FIFOQueue;

import org.apache.log4j.*;

/**
 * Provide a message handling service for all animators.  This one differs to
 * the OSOffice in that it has two internal threads that handle delivery to
 * any other registered Post Offices and to locally registered handlers.
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
   * Serial id.
   */
  private static final long serialVersionUID = 1157444033773059874L;

  /**
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(AnimatorOffice.class);

  /**
   * My peer post office, os post office.
   */
  private OSOffice osPostOffice;

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
   * The thread that handles sending messages to locally subscribed handlers.
   */
  private LocalMessageSender internalSender;

  /**
   * The thread that handles sending message to other post offices.
   */
  private PostOfficeMessageSender poSender;

  /**
   * Initialize the animator and attach it to the OS post office.
   *
   * @param newId the string identifier to register the post office to the OS
   *      post office.
   * @param newOSPostOffice Description of Parameter
   */
  public AnimatorOffice(String newId, OSOffice newOSPostOffice)
  {
    super(newId, null);

    osPostOffice = newOSPostOffice;

    // Register OSPostOffice with Animator Office
    this.addPostOffice(osPostOffice);
    // Register the Animator with the OSPostOffice
    osPostOffice.addPostOffice(this);

    internalSender = new LocalMessageSender();
    internalSender.setName("AnimatorLocalOfficeThread");

    poSender = new PostOfficeMessageSender();
    poSender.setName("AnimatorPOOfficeThread");
  }

  /**
   * Indicate that the post office should start sending messages.  Starts the
   * internal thread for sending messages to locally subscribed handlers as well
   * as the internal thread for sending messages to other post offices.
   */
  public void startSending()
  {
    internalSender.start();
    poSender.start();
  }

  /**
   * Send a message to all registered post offices and to all locally registered
   * components.
   *
   * @param message message to send.
   */
  public void sendMessage(MessageAdapter message)
  {
    //Send to all other registered post offices by adding it to the list.
    //The send thread should move it along.
    sendToPostOffices(message);

    //Send to locally registered components by adding it to the list
    localSendMessage(message);
  }

  /**
   * Send a message to all registered post offices and to all locally registered
   * components.
   *
   * @param message message to send.
   */
  public void sendMessage(UniversalMessageAdapter message)
  {
    sendMessage((MessageAdapter) message);
  }

  /**
   * Sends a message to only the locally registered components.
   *
   * @param message message to send.
   */
  public void sendMessage(AnimatorMessageAdapter message)
  {
    localSendMessage((MessageAdapter) message);
  }

  /**
   * Send a message to only registered objects of local post office.
   *
   * @param message Message to send.
   */
  public void localSendMessage(MessageAdapter message)
  {
    synchronized (localMessages)
    {
      localMessages.add(message);
    }
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
   * Send message only to other post offices.
   *
   * @param message Message to send to all post offices.
   */
  public void sendToPostOffices(MessageAdapter message)
  {
    synchronized (postOfficeMessages)
    {
      postOfficeMessages.add(message);
    }
  }

  /**
   * Process a sent message.
   *
   * @param message Message received.
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
   * A simple class responsible for merely adding a message to each of the
   * OS Offices registered.
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
          log.error("Interrupted while sleeping", ie);
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
   * The class responsible for processing the message for each of the registered
   * handlers.
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
          log.error("Interrupted while sleeping", ie);
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
                  log.error("An error occurred: " + e);
                }
              }
            }
          }
        }
      }
    }
  }
}