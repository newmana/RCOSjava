package net.sourceforge.rcosjava.messaging.postoffices.os;

import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.software.util.FIFOQueue;

import java.lang.reflect.*;
import java.util.*;

/**
 * Provide message handling centre of operations.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 01/04/98 Modified to use TreeMap.
 * </DD></DT>
 * <P>
 * @author Bruce Jamieson
 * @author Andrew Newman
 * @created 21 October 2000
 */
public class OSOffice extends PostOffice
{
  /**
   * The local messages to be sent.
   */
  private FIFOQueue localMessages = new FIFOQueue(5,1);

  /**
   * The message to be sent to post offices.
   */
  private FIFOQueue postOfficeMessages = new FIFOQueue(5,1);

  private Vector myPostOffices = postOffices;

  /**
   * To be done
   *
   * @param newId To be done
   */
  public OSOffice(String newId)
  {
    id = newId;

//    LocalMessageSender internalSender = new LocalMessageSender();
//    internalSender.setName("OSOfficeLocalThread");
//    internalSender.start();
    PostOfficeMessageSender poSender = new PostOfficeMessageSender();
    poSender.setName("OSOfficePOThread");
    poSender.start();
  }

  public void sendMessage(MessageAdapter message)
  {
    //Send to all other registered post offices by adding it to the list.
    //The send thread should move it along.
    postOfficeMessages.add(message);

    //Send to locally registered components by adding it to the list
    localSendMessage(message);
  }

  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   */
  public void sendMessage(UniversalMessageAdapter message)
  {
    sendMessage((MessageAdapter) message);
  }

  /**
   * Sends messages to all conpontents registered to this post office.  Calls
   * localSendMessage.
   *
   * @param message the message to send.
   */
  public void sendMessage(OSMessageAdapter message)
  {
    localSendMessage((MessageAdapter) message);
  }

  public void localSendMessage(MessageAdapter message)
  {
//    localMessages.add(message);
    if (message.forPostOffice(OSOffice.this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.

      Iterator tmpIter = OSOffice.this.getHandlers().values().iterator();

      synchronized (OSOffice.this.getHandlers())
      {
        while(tmpIter.hasNext())
        {
          OSMessageHandler theDestination = (OSMessageHandler)
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

/*    if (message.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.

      Iterator tmpIter = this.getHandlers().values().iterator();

      synchronized (this.getHandlers())
      {
        while(tmpIter.hasNext())
        {
          OSMessageHandler theDestination = (OSMessageHandler) tmpIter.next();
          //Send the message to the destination
          theDestination.processMessage(message);
        }
      }
    }*/

  /**
   * To be done
   *
   * @param message To be done
   */
  public void localSendMessage(OSMessageAdapter message)
  {
    localSendMessage((MessageAdapter) message);

/*    if (message.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.

      Iterator tmpIter = this.getHandlers().values().iterator();

      synchronized (this.getHandlers())
      {
        while(tmpIter.hasNext())
        {
          OSMessageHandler theDestination = (OSMessageHandler) tmpIter.next();
          //Send the message to the destination
          theDestination.processMessage(message);
        }
      }
    }*/
  }

  public void sendToPostOffices(MessageAdapter message)
  {
    postOfficeMessages.add(message);
/*    PostOffice tmpPostOffice;

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
    }*/
  }

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

  private class PostOfficeMessageSender extends Thread
  {
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
        synchronized(postOfficeMessages)
        {
          if (OSOffice.this.postOfficeMessages.size() > 0)
          {

            PostOffice tmpPostOffice;
            //Retrieve first message off the blocks
            MessageAdapter message = (MessageAdapter)
              OSOffice.this.postOfficeMessages.retrieveCurrent();

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

  private class LocalMessageSender extends Thread
  {
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
        synchronized(localMessages)
        {
          if (OSOffice.this.localMessages.size() > 0)
          {

            //Retrieve first message off the blocks
            MessageAdapter message = (MessageAdapter)
              OSOffice.this.localMessages.retrieveCurrent();

            //System.out.println("OS got message: " + message);
            if (message.forPostOffice(OSOffice.this))
            {
              //Go through the hashtable returning all the handlers
              //registered.  Send the message to all of them.

             Iterator tmpIter = OSOffice.this.getHandlers().values().iterator();

              synchronized (OSOffice.this.getHandlers())
              {
                while(tmpIter.hasNext())
                {
                  OSMessageHandler theDestination = (OSMessageHandler)
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
