//***************************************************************************
// FILE     : AnimatorOffice.java
// PACKAGE  : MessageSystem
// PURPOSE  : Provide message handling centre of operations.
//            Variation on PostOffice - Messages are CC'ed to the
//            AnimatorOffice where they are distributed to
//            various Animators (which are actually MessageHandlers)
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 24/01/96 Created
//            17/03/96 MOD for Animator to send to all
//            20/05/97 Changed message system
//            05/05/98 Removed sendToAll (now does this by default)
//***************************************************************************/

package MessageSystem.PostOffices.Animator;

import MessageSystem.PostOffices.PostOffice;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.MessageHandler;
import java.util.Enumeration;

public class AnimatorOffice extends PostOffice
{
  private OSOffice theOSPostOffice;

  //Attach animator to another post office.
  public AnimatorOffice(String newID, OSOffice newPostOffice)
  {
    id = newID;
    theOSPostOffice = newPostOffice;
    // Register OSPostOffice with Animator Office
    this.addPostOffice(theOSPostOffice);
    // Register the Animator with the OSPostOffice
    theOSPostOffice.addPostOffice(this);
  }

  public void sendMessage(MessageAdapter aMessage)
  {
    //Send to all other registered post offices.
    sendToPostOffices(aMessage);
    //Send to locally registered components.
    localSendMessage(aMessage);
  }

  public void sendMessage(UniversalMessageAdapter aMessage)
  {
    sendMessage((MessageAdapter) aMessage);
  }

  public void localSendMessage(MessageAdapter maMessage)
  {
    if (maMessage.forPostOffice(this))
    {
      //Go through the hashtable returning all the handlers
      //registered.  Send the message to all of them.
			for (Enumeration e = getHandlers().elements(); e.hasMoreElements();)
			{
			  AnimatorMessageHandler theDestination = (AnimatorMessageHandler) e.nextElement();
			  //Send the message to the destination
			  theDestination.processMessage(maMessage);
			}
		}
	}

	public void localSendMessage(AnimatorMessageAdapter maMessage)
  {
		if (maMessage.forPostOffice(this))
		{
			//Go through the hashtable returning all the handlers
			//registered.  Send the message to all of them.
			for (Enumeration e = getHandlers().elements(); e.hasMoreElements();)
			{
			  AnimatorMessageHandler theDestination = (AnimatorMessageHandler) e.nextElement();
			  //Send the message to the destination
			  theDestination.processMessage(maMessage);
			}
		}
  }

	public void sendToPostOffices(MessageAdapter maMessage)
  {
    PostOffice poTmpPostOffice;

    if (!postOffices.isEmpty())
    {
      int iCount;
      for (iCount = 0; iCount < postOffices.size(); iCount++)
      {
        poTmpPostOffice = getPostOffice(iCount);
        if (maMessage.forPostOffice(poTmpPostOffice))
        {
          poTmpPostOffice.localSendMessage(maMessage);
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
