/*
 * Modified by Philippe Le Hegaret 98/06/08
 *
 * @(#)ObjectStreamConstants.java	1.11 98/01/30
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 */

package fr.dyade.koala.serialization;

/**
 *
 * @author  unascribed
 * @version 1.11, 01/30/98
 */
public interface ObjectStreamConstants {
    final static short STREAM_MAGIC = (short)0xaced;
    final static short STREAM_VERSION = 5;

    /* Each item in the stream is preceded by a tag
     */
    final static byte TC_BASE = 0x70;
    final static byte TC_NULL = 	(byte)0x70; // Null object reference
    final static byte TC_REFERENCE =	(byte)0x71; // Reference to prev object
    final static byte TC_CLASSDESC = 	(byte)0x72; // Class Descriptor
    final static byte TC_OBJECT = 	(byte)0x73; // new object
    final static byte TC_STRING = 	(byte)0x74; // new String
    final static byte TC_ARRAY = 	(byte)0x75; // new Array
    final static byte TC_CLASS = 	(byte)0x76; // Reference to Class 
    final static byte TC_BLOCKDATA = 	(byte)0x77; // Block of optional data
    final static byte TC_ENDBLOCKDATA =	(byte)0x78; // End of optional data
    final static byte TC_RESET = 	(byte)0x79; // Reset stream context
    final static byte TC_BLOCKDATALONG= (byte)0x7A; // long block data
    final static byte TC_EXCEPTION = 	(byte)0x7B; // exception during write
    final static byte TC_MAX = 		(byte)0x7B;

    /* First wire handle to be assigned. */
    final static int baseWireHandle = 0x7e0000;

    /* The null reference. */
    final static int SC_NULLREFERENCE = baseWireHandle - 1;

    /* Flag bits for ObjectStreamClasses in Stream. */
    final static byte SC_WRITE_METHOD = 0x01;
    final static byte SC_BLOCK_DATA   = 0x01; // jdk1.2
    final static byte SC_SERIALIZABLE = 0x02;
    final static byte SC_EXTERNALIZABLE = 0x04;


    /* prim_typecode */
    final static byte BYTE_TYPE    = (byte) 'B';
    final static byte CHAR_TYPE    = (byte) 'C';
    final static byte DOUBLE_TYPE  = (byte) 'D';
    final static byte FLOAT_TYPE   = (byte) 'F';
    final static byte INT_TYPE     = (byte) 'I';
    final static byte LONG_TYPE    = (byte) 'J';
    final static byte SHORT_TYPE   = (byte) 'S';
    final static byte BOOLEAN_TYPE = (byte) 'Z';

    /* obj_typecode */
    final static byte ARRAY_TYPE   = (byte) '[';
    final static byte OBJECT_TYPE  = (byte) 'L';
}
