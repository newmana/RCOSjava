package net.sourceforge.rcosjava.messaging.messages;

import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;

/**
 * A simple method that does not override any of the messages default
 * properties.  Post offices receiving this message knows that the sender
 * of the message wishes to register with it.
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public class RegisterDevice extends MessageAdapter
{
}

