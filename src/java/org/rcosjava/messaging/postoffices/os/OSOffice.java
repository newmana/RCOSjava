package org.rcosjava.messaging.postoffices.os;

import java.lang.reflect.*;
import java.util.*;

import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.PostOffice;
import org.rcosjava.software.util.FIFOQueue;

import org.apache.log4j.*;

/**
 * Provide a message handling service for OS components.  This should be a
 * thread safe object.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 01/04/98 Modified to use TreeMap. </DD> </DT>
 * <P>
 * @author Bruce Jamieson
 * @author Andrew Newman
 * @created 21 October 2000
 */
public class OSOffice extends PostOffice
{
  /**
   * Serial id.
   */
  private static final long serialVersionUID = -145608613943167252L;

  /**
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(OSOffice.class);

  /**
   * The local messages to be sent.
   */
  private FIFOQueue localMessages = new FIFOQueue(5, 1);

  /**
   * The message to be sent to post offices.
   */
  private FIFOQueue postOfficeMessages = new FIFOQueue(5, 1);

  /**
   * Initalize the OS post office.
   *
   * @param newId To be done
   */
  public OSOffice(String newId)
  {
    super(newId, null);
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
   * Send a message to all registered post offices and to all locally registered
   * components.
   *
   * @param message message to send.
   */
  public void sendMessage(OSMessageAdapter message)
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
   * Send a message to only registered objects of local post office.
   *
   * @param message Message to send.
   */
  public void localSendMessage(OSMessageAdapter message)
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
      log.error("Error Processing Message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * This is called by another thread to deliver messages to any other
   * postoffices and to any other registered handlers.
   */
  public void deliverMessages()
  {
    if (log.isInfoEnabled())
    {
      log.info("Delivering message");
    }
    synchronized (postOfficeMessages)
    {
      while (postOfficeMessages.size() > 0)
      {
        postOfficeDeliverMessage();
      }
    }

    synchronized (localMessages)
    {
      while (localMessages.size() > 0)
      {
        localDeliverMessage();
      }
    }
  }

  /**
   * Deliver one message to all registered post offices.
   */
  private void postOfficeDeliverMessage()
  {
    synchronized (postOfficeMessages)
    {
      if (OSOffice.this.postOfficeMessages.size() > 0)
      {
        PostOffice tmpPostOffice;
        //Retrieve first message off the blocks
        MessageAdapter message = (MessageAdapter)
            OSOffice.this.postOfficeMessages.retrieveCurrent();

        if (!postOffices.isEmpty())
        {
          int count;

          for (count = 0; count < postOffices.size(); count++)
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

  /**
   * Deliver one message to all locally registered handlers.
   */
  private void localDeliverMessage()
  {
    synchronized (localMessages)
    {
      if (localMessages.size() > 0)
      {
        //Retrieve first message off the blocks
        MessageAdapter message = (MessageAdapter) localMessages.retrieveCurrent();
        if (log.isInfoEnabled())
        {
          log.info("Local Delivering message:" + message.getClass());
        }

        TreeMap map = OSOffice.this.getHandlers();

        if (message.forPostOffice(OSOffice.this))
        {
          //Go through the hashtable returning all the handlers
          //registered.  Send the message to all of them.
          Iterator tmpIter = map.values().iterator();

          while (tmpIter.hasNext())
          {
            OSMessageHandler theDestination = (OSMessageHandler)
                tmpIter.next();

            //Send the message to the destination
            try
            {
              Class[] classes = { message.getClass().getSuperclass() };
              Method method = theDestination.getClass().getMethod(
                  "processMessage", classes);
              Object[] args = { message };
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