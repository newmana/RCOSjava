package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.kernel.Kernel;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class SystemCall extends OSMessageAdapter
{
  /**
   * Constructor for the SystemCall object
   *
   * @param theElement Description of Parameter
   */
  public SystemCall(OSMessageHandler theElement)
  {
    super(theElement);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(Kernel theElement)
  {
    try
    {
      theElement.systemCall();
    }
    catch (Exception e)
    {
      System.err.println("System call error: " + e);
      e.printStackTrace();
    }
  }
}

