package MessageSystem.PostOffices.Universal;

import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.PostOffices.Animator.AnimatorMessageRecorder;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.PostOffices.OS.OSMessageRecorder;

import pll2.FileClient;

/**
 * This is a simple contains a OS Message Recorder and a Animator Message
 * Recorder.  It will record all messages and the relay them to the file system
 * to be saved.  This will enable the system to read these files and reconstruct
 * what occurred.
 * <P>
 * <DT><B>Usage example:</B><DD>
 * <CODE>
 *      To be done.
 * </CODE>
 * </DD></DT>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd January 2001
 * @see MessageSystem.PostOffices.OS.OSMessageRecorder
 * @see MessageSystem.PostOffices.Animator.AnimatorMessageRecorder
 **/
 public class UniversalMessageRecorder
{
  private OSMessageRecorder osRecorder;
  private AnimatorMessageRecorder animatorRecorder;
  private static int counter;
  private FileClient myClient;

  /**
   * Null constructor.  Does nothing.
   */
  public UniversalMessageRecorder()
  {
  }

  /**
   * Accepts the OS Office and Animator Office to register with.
   *
   * @param newID the id to register with the offices to.
   * @param newOSPostOffice the new operating system post office to register
   * to.
   * @param newAnimatorOffice the new animator post office to register to.
   */
  public UniversalMessageRecorder(String newId, OSOffice newOSPostOffice,
    AnimatorOffice newAnimatorPostOffice)
  {
    osRecorder = new OSMessageRecorder(newId, newOSPostOffice, this);
    animatorRecorder = new AnimatorMessageRecorder(newId, newAnimatorPostOffice,
      this);
  }

  public void processAnimatorMessage(AnimatorMessageAdapter newMessage)
  {
    System.out.println("Uni Got Animator Message: " + newMessage);
    myClient = new FileClient("localhost", 4242);
    myClient.openConnection();
    try
    {
      myClient.writeRecFile("/test" + (counter++) + ".xml", newMessage);
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }

  public void processOSMessage(OSMessageAdapter newMessage)
  {
    System.out.println("Uni Got OS Message: " + newMessage);
    myClient = new FileClient("localhost", 4242);
    myClient.openConnection();
    try
    {
      myClient.writeRecFile("/test.xml" + counter++, newMessage);
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }

  public void processOSUniversalMessage(
    UniversalMessageAdapter newMessage)
  {
    System.out.println("Uni Got OS Universal Message: " + newMessage);
    myClient = new FileClient("localhost", 4242);
    myClient.openConnection();
    try
    {
      myClient.writeRecFile("/test" + (counter++) + ".xml", newMessage);
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }

  public void processAnimatorUniversalMessage(
    UniversalMessageAdapter newMessage)
  {
    System.out.println("Uni Got Animator Uni Message: " + newMessage);
    myClient = new FileClient("localhost", 4242);
    myClient.openConnection();
    try
    {
      myClient.writeRecFile("/test" + (counter++) + ".xml", newMessage);
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }
}

