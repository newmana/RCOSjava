/*
 * Copyright (c) 1998 by Groupe Bull. All Rights Reserved
 * TaskCounter.java
 * $Id$
 */
package fr.dyade.koala.util;

/**
 * This interface is used to follow a current task.
 *
 * @version $Revision$
 * @author  Philippe Le Hégaret
 */
public interface TaskCounter {

    /**
     * This method returns the total length of the task.
     */    
    long getMaximumValue();

    /**
     * This method returns the current position of the task.
     */    
    long getValue();
}
