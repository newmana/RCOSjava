//*******************************************************************/
// FILE     : Message.java
// PURPOSE  : Basic message class
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96  Created
//            03/07/98  Used double dispatch
//            05/05/98  Removed usage of Destination
//            06/05/98  Added undo functionality 
//*******************************************************************/

package MessageSystem.Messages;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.PostOffices.PostOffice;

public interface Message
{
  public void setValues(SimpleMessageHandler mhNewSource, Object theBody);  
  public void setSource(SimpleMessageHandler mhNewSource);
  public void setBody(Object theBody);
  public SimpleMessageHandler getSource();
  public String getType();
  public Object getBody();
  public void doMessage(PostOffice theElement);
  public void undoMessage();
  public boolean undoableMessage();
  public boolean forPostOffice(PostOffice poMyPostOffice);
}