package net.sourceforge.rcosjava.messaging.messages;

import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;

/**
 * The basic message object. Each real message will follow these requirements. A
 * message is sent by an object to a post office. The post office then sends the
 * message to each of the registered object. The message that is sent will
 * actually contain a message for each receiving object. This will be called by
 * the receiving object and the action performed.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 03/07/1998 Used double dispatch.<BR>
 * </DD>
 * <DD> 05/05/1998 Removed usage of Destination.<BR>
 * </DD>
 * <DD> 06/05/1998 Added undo functionality.<BR>
 * </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public interface Message
{
  /**
   * The default construct of a message. The message must have at least a source
   * and an object to store in it. These can either be overridden or extended.
   *
   * @param newSource the object that sent the message.
   * @param newBody the body (values) stored in the object.
   */
  public void setValues(SimpleMessageHandler newSource, Object newBody);

  /**
   * The source of the object that sent the message.
   *
   * @param newSource the new sender.
   */
  public void setSource(SimpleMessageHandler newSource);

  /**
   * The body (the contents of the message) of the message.
   *
   * @param newBody the new body.
   */
  public void setBody(Object newBody);

  /**
   * Returns the sender of the message.
   *
   * @return The Source value
   */
  public SimpleMessageHandler getSource();

  /**
   * The classification of the message. Can be used to determine the processing
   * of the message by the post office.
   *
   * @return The Type value
   */
  public String getType();

  /**
   * Returns the body of the message.
   *
   * @return The Body value
   */
  public Object getBody();

  /**
   * Perform so action upon the post office receiving this object. This is the
   * only default receiver of the message object. This is to enable the ability
   * of initialization messages to occur (like AddHandler and RegisterDevice).
   *
   * @param theElement the post office object that is to have the work performed
   *      on.
   */
  public void doMessage(PostOffice theElement);

  /**
   * The method called when a message is to be undone. Should throw an exception
   * if the message is no undoable.
   */
  public void undoMessage();

  /**
   * This is to be used by the post office to determine whether undoMessage
   * should be called.
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage();

  /**
   * Determines whether the object is for a particular post office. Each message
   * can originate from a certain point. But the destination is determined by
   * checking with the message what type of post office can send the message.
   *
   * @param postOffice the post office to check against.
   * @return Description of the Returned Value
   */
  public boolean forPostOffice(PostOffice postOffice);
}
