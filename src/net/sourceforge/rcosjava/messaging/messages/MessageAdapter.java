package net.sourceforge.rcosjava.messaging.messages;

import java.lang.Object;
import java.io.Serializable;
import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;

/**
 * An adapter (default implementation) of the message interface.  It prevents
 * messages from having to implement methods that have exactly the same
 * implementation.
 * <P>
 * HISTORY: 03/07/98 Used double dispatch.<BR>
 *          05/05/98 Removed usage of destination.<BR>
 *          06/05/98 Removed usage of string based sender.<BR>
 * <P>
 * @author Andrew Newman
 * @authro Bruce Jamieson
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public abstract class MessageAdapter implements Message
{
  /**
   * Where there message was originally sent from
   */
  private SimpleMessageHandler source;

  /**
   * The contents of the message (must be serializable)
   */
  private Object body = new Object();

  /**
   * Null constructor.  Does nothing.
   */
  public MessageAdapter()
  {
  }

  /**
   * Creates a message without a null message.  The message is the content?
   * Things such as a simple notify message or such.
   *
   * @param newSource sets the source of the message.
   */
  public MessageAdapter(SimpleMessageHandler newSource)
  {
    source = newSource;
    body = null;
  }

  /**
   * Creates a message with a known body (should be serializable) and a
   * known source.
   *
   * @param newSource sets the source of the message.
   * @param newBody the message contents.
   */
  public MessageAdapter(SimpleMessageHandler newSource, Object newBody)
  {
    source = newSource;
    body = newBody;
  }

  /**
   * Resets the values of the message.  This is to enable reuse of an object
   * if required.
   *
   * @param newSource sets the source of the message.
   * @param newBody the message contents.
   */
  public void setValues(SimpleMessageHandler newSource, Object newBody)
  {
    source = newSource;
    body = newBody;
  }

  /**
   * Set a new source of the message retains the current value of the message.
   * To be used to send the same content from different sources (fairly rare).
   *
   * @param newSource sets the source of the message.
   */
  public void setSource(SimpleMessageHandler newSource)
  {
    source = newSource;
  }

  /**
   * Set a new body of the message.  So that more than one message type can
   * be sent to the same source.
   *
   * @param newBody the message contents.
   */
  public void setBody(Object newBody)
  {
    body = newBody;
  }

  /**
   * Returns the source of the message.
   */
  public SimpleMessageHandler getSource()
  {
    return source;
  }

  /**
   * Returns the type (getClass.getName) of the message.  Used to determine
   * the exact classification of the message so it can be cast.
   */
  public String getType()
  {
    return(getClass().getName());
  }

  /**
   * Returns the message contents as an object.  Shouldn't be used to often
   * as casting is required.
   */
  public Object getBody()
  {
    return body;
  }

  /**
   * Generic method.  Does nothing.  For each passed receiver of the message
   * (in this case PostOffice) a doMessage will perform some action on it.
   *
   * @param theElement the object to do work upon.
   */
  public void doMessage(PostOffice theElement)
  {
  }

  /**
   * Generic method.  Does nothing.  For each message there should be the
   * possbility (at least) of going backwards and undoing the message.
   */
  public void undoMessage()
  {
  }

  /**
   * Returns whether the message is undoable (able to call undoMessage with
   * a positive result).  All messages are undoable by default (returns false).
   */
  public boolean undoableMessage()
  {
    //By default messages are not undoable
    return false;
  }

  /**
   * Returns whether the message should be processed by the post office.
   * Currently, all messages are able to be processed (returns true).
   *
   * @param myPostOffice the post office to determine whether I should be
   * processed by it.
   */
  public boolean forPostOffice(PostOffice myPostOffice)
  {
    return true;
  }
}