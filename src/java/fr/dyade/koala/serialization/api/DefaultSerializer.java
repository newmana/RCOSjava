/* Copyright (c) 1998 by Groupe Bull. All Rights Reserved */
/* $Id$ */
/* Author: Thierry-Kormann@sophia.inria.fr  */

package fr.dyade.koala.serialization.api;

import java.io.IOException;
import fr.dyade.koala.serialization.*;

/**
 * Enables the XML serialization of classes that does nothing that calling the
 * <code>defaultReadObject</code> and the <code>defaultWriteObject</code>
 * methods for the java serialization.
 *
 * @author Thierry.Kormann@sophia.inria.fr
 */
public class DefaultSerializer {

  public static void readObject(GeneratorInputStream s)
      throws ClassNotFoundException, IOException {

    s.defaultReadObject();
  }
}