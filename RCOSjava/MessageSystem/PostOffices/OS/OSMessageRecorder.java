package MessageSystem.PostOffices.OS;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.PostOffice;
import MessageSystem.Messages.AddHandler;
import pll2.*;

public class OSMessageRecorder extends OSMessageHandler
{
  public static int counter;
  private FileClient myClient;

  public OSMessageRecorder()
  {
  }

  public OSMessageRecorder(String newID, OSOffice mhNewPostOffice)
  {
    super(newID, mhNewPostOffice);
  }

  public void processMessage(OSMessageAdapter mMessage)
  {
    System.out.println("OS Got OS Message: " + mMessage);
    myClient = new FileClient("localhost", 4242);
    myClient.openConnection();
    try
    {
      myClient.writeRecFile("/test.xml" + counter++, mMessage);
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }

  public void processMessage(UniversalMessageAdapter mMessage)
  {
    System.out.println("OS Got Universal Message: " + mMessage);
    myClient = new FileClient("localhost", 4242);
    myClient.openConnection();
    try
    {
      myClient.writeRecFile("/test.xml" + counter++, mMessage);
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }
}
