//**************************************************************/
// FILE     : MessageAdapter.java
// PURPOSE  : Basic message class
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96  Created
//            03/07/98  Used double dispatch
//            05/05/98  Removed usage of destination
//            06/05/98  Removed usage of string based sender.
//**************************************************************/

package MessageSystem.Messages;

import java.lang.Object;
import java.io.Serializable;
import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.PostOffices.PostOffice;

public abstract class MessageAdapter implements Message, Serializable
{
  private SimpleMessageHandler mhSource;
  private Object oBody = new Object();

  public MessageAdapter()
  {
    mhSource = null;
    oBody = null;
  }

  public MessageAdapter(SimpleMessageHandler mhNewSource)
  {
    mhSource = mhNewSource;
    oBody = null;
  }

  public MessageAdapter(SimpleMessageHandler mhNewSource, Object oNewBody)
  {
    mhSource = mhNewSource;
    oBody = oNewBody;
  }

  public void setValues(SimpleMessageHandler mhNewSource, Object oNewBody)
  {
    mhSource = mhNewSource;
    oBody = oNewBody;
  }

  public void setSource(SimpleMessageHandler mhNewSource)
  {
    mhSource = mhNewSource;
  }

  public void setBody(Object oNewBody)
  {
    oBody = oNewBody;
  }

  public SimpleMessageHandler getSource()
  {
    return mhSource;
  }

  public String getType()
  {
    return(getClass().getName());
  }

  public Object getBody()
  {
    return oBody;
  }

  public void doMessage(PostOffice theElement)
  {
  }

  public void undoMessage()
  {
  }

  public boolean undoableMessage()
  {
    //By default messages are not undoable
    return false;
  }

  public boolean forPostOffice(PostOffice poMyPostOffice)
  {
    return true;
  }
}