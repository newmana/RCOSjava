//***************************************************************************
// FILE     : MessageHandler.java
// PACKAGE  : MessageSystem
// PURPOSE  : Abstract class to provide base class for message
//            handlers.
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 24/01/96 Created
//            17/03/96 Modified to include LocalSendMessage to allow
//                     broadcasting of messages to just local post office.
//
//***************************************************************************/

package MessageSystem.PostOffices;

import java.util.*;
import MessageSystem.Messages.MessageAdapter;

public interface MessageHandler
{
  public void setID(String newID);
  public String getID();
  public void localSendMessage(MessageAdapter message);
  public void sendMessage(MessageAdapter message);
  public void addHandler(String newID, MessageHandler newHandler);
  public MessageHandler getHandler(String handlerToGet);
  public Hashtable getHandlers();
  public Enumeration getKeysHandlers();
  public void removeHandler(String oldID);
  public void clearHandlers();
  public void processMessage(MessageAdapter message);
}