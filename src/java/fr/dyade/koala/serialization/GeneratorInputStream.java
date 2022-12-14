/*
* Copyright (c) 1998 by Groupe Bull. All Rights Reserved
* GeneratorInputStream.java
* $Id$
*/
package fr.dyade.koala.serialization;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.io.EOFException;
import java.io.NotActiveException;
import java.io.StreamCorruptedException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import java.net.URL;

/**
 * @version $Revision$
 * @author  Philippe Le H?garet
 */
public final class GeneratorInputStream extends InputStream
    implements ObjectStreamConstants {

  private static final boolean KoalaDebug = false;


  /**
   * this is a fake object.
   * Only String and ClassDescription are constructed. Otherwise readObject()
   * returns this fake object.
   */
  public static final Object fakeObject = new Object();

// the input stream
  InputStream in;

  private BufferedReader dis;

// the class description of the current object
  private ClassDescription currentClassDescription;

// manage all handles
  private Object[] handles = new Object[100];
  private int currentHandle = baseWireHandle;

// true if we are in a block data
  private boolean inBlockDataMode;
// the length of the block data
  private int count;

// java.io.Serializable
  private static Class serializableClass;
// java.io.Externalizable
  private static Class externalizableClass;

  private static Hashtable fakeMethod;
  private Object[] argsMethod = new Object[1];

  private ObjectOutputHandler objectOutputHandler;

  private boolean hide = false;

  /**
   * Creates an ObjectInputStream that reads from the specified InputStream.
   * The stream header containing the magic number and version number are
   * read from the stream and verified. This method will block until the
   * corresponding ObjectOutputStream has written and flushed the header.
   *
   * @param in  an input stream created by java.io.ObjectOutputStream
   * @exception StreamCorruptedException The version or magic number
   *                                     are incorrect.
   * @exception IOException An exception occurred in the underlying stream.
   * @see java.io.ObjectOutputStream
   */
  public GeneratorInputStream(InputStream in)
      throws IOException, StreamCorruptedException {

    this.in = new BufferedInputStream(in);

// Create a DataInputStream used to read primitive types.
    this.dis = new BufferedReader(
        new InputStreamReader(this));

    readHeader();
    argsMethod[0] = this;
  }

  /**
   * Assign an handler for all objects.
   */
  public void setObjectOuputHandler(ObjectOutputHandler arg) {
    objectOutputHandler = arg;
  }

  /**
   * Returns the current object handler.
   */
  ObjectOutputHandler getObjectOutputHandler() {
    return objectOutputHandler;
  }

  /**
   * The readStreamHeader method is provided to allow subclasses to
   * read and verify their own stream headers. It reads and
   * verifies the magic number and version number.
   */
  void readHeader() throws IOException, StreamCorruptedException {
    if (KoalaDebug) {
      System.err.println("read Header");
    }

    short magic = (short)((readEOF() << 8) + (readEOF() << 0));
    short version = (short)((readEOF() << 8) + (readEOF() << 0));
    if (magic != STREAM_MAGIC) {
      throw new StreamCorruptedException("InputStream does not contain"
          + " a serialized object");
    }
    if (version != STREAM_VERSION) {
      throw new StreamCorruptedException("Version Mismatch, Expected " +
          STREAM_VERSION + " and got " +
          version);
    }
  }

  /**
   * Reads the non-static and non-transient fields of the current class
   * from this stream.  This may only be called from the readObject method
   * of the class being deserialized. It will throw the NotActiveException
   * if it is called otherwise.
   *
   * @exception ClassNotFoundException if the class of a serialized
   *              object could not be found.
   * @exception IOException        if an I/O error occurs.
   * @exception NotActiveException if the stream is not currently reading
   *              objects.
   */
  public final void defaultReadObject()
      throws IOException, ClassNotFoundException, NotActiveException {
    if (KoalaDebug) {
      System.err.println("defaultReadObject() ");
    }

    boolean prev = setBlockData(false);
    try {
      if (currentClassDescription == null) {
        throw new NotActiveException("defaultReadObject");
      }
      if (currentClassDescription.fieldsQuick != null) {
        _readValues(currentClassDescription);
      }
    } finally {
      if (KoalaDebug) {
        System.err.println("end defaultReadObject() ");
      }
      setBlockData(prev);
    }
  }

  /**
   * Reads an object from the InputStream.
   * <p> The class of the object, the signature of the class, and the values
   * of the non-transient and non-static fields of the class and all of its
   * supertypes are read.  Default deserializing for a class can be overriden
   * using the writeObject and readObject methods.  Objects referenced by
   * this object are read transitively so that a complete equivalent graph of
   * objects is reconstructed by readObject.
   * <p> The root object is completly restored when all of its fields and the
   * objects it references are completely restored.  At this point the object
   * validation callbacks are executed in order based on their registered
   * priorities. The callbacks are registered by objects (in the readObject
   * special methods) as they are individually restored.
   * <p> Exceptions are thrown for problems with the InputStream and for
   * classes that should not be deserialized.  All exceptions are fatal to
   * the InputStream and leave it in an indeterminate state; it is up to the
   * caller to ignore or recover the stream state.
   *
   * @exception ClassNotFoundException The class is not readable.
   * @exception StreamCorruptedException Control information in the
   *     stream is inconsistent.
   * @exception BlockDataException Primitive data was found in the
   * stream instead of objects.
   * @exception IOException Any of the usual Input/Output related exceptions.
   * @return an object (may be null)
   */
  public Object readObject()
      throws BlockDataException, ClassNotFoundException, IOException {
    return readObjectInternal(null);
  }

  private Object readObjectInternal(Field field)
      throws BlockDataException, ClassNotFoundException, IOException {
    if (KoalaDebug) {
      System.err.println("enter readObject() count=" + count
                         + " dataMode=" + inBlockDataMode);
    }

    if (inBlockDataMode) {
      if (count == 0) {
        refill();
      }
      if (count > 0) {
        throw new BlockDataException(count);
      }
    }

    peekCode();

    boolean prevBlockDataMode = setBlockData(false);
    byte c = readCode();

    try {
      while (c == TC_RESET) {
        resetContext();
        c = peekCode();
      }

      switch (c) {
        case TC_OBJECT:
          newObject(field);
          break;
        case TC_CLASS:
          newClass(field);
          break;
        case TC_ARRAY:
          newArray(field);
          break;
        case TC_STRING:
          String result = newString();
          if (!hide) {
            getObjectOutputHandler().write(result, false, field);
          }
          return result;
        case TC_CLASSDESC:
          newClassDesc();
          break;
        case TC_REFERENCE:
          return prevObject(field);
        case TC_BLOCKDATA:
          throw new BlockDataException(readEOF());
        case TC_BLOCKDATALONG:
          throw new BlockDataException(((readEOF() << 24)
                                        + (readEOF() << 16)
                                        + (readEOF() << 8)
                                        + (readEOF() << 0)));
                                      case TC_ENDBLOCKDATA:
                                        if (inBlockDataMode) {
                                          pushbackCode(c);
                                          throw new
                                              StreamCorruptedException("end of block data unexpected");
                                        }
                                        throw new BlockDataException();
                                      case TC_EXCEPTION:
                                        throw new WriteAbortedException("Writing aborted by exception",
                                            newException());
                                          case TC_NULL:
                                            if (KoalaDebug) {
                                              System.err.println("null reference");
                                            }
                                            if (!hide) {
                                              getObjectOutputHandler().writeNullReference(field);
                                            }
                                            return null;
                                          default:
                                            pushbackCode(c);
                                          throw new StreamCorruptedException("Invalid byte "
                                              + Integer.toString((int) c,
                                              16));
      }
    } finally {
      setBlockData(prevBlockDataMode);

      if (KoalaDebug) {
        System.err.println("end read object");
      }
    }
    return fakeObject;
  }

  /**
   * Creates a new array instance from the input stream
   */
  void newArray(Field field) throws ClassNotFoundException, IOException {
    if (KoalaDebug) {
      System.err.println("enter newArray");
    }
    ClassDescription _class = classDesc();
    int size = ((readEOF() << 24) + (readEOF() << 16)
                + (readEOF() << 8) + (readEOF() << 0));
    Type type = null;

    try {
      type = _class.getType().getElementType();
    } catch (Exception e) {
      e.printStackTrace();
    }
// assign the handle before reading all values
// assign the new handle _after_ reading his class description
    int handle = assignHandle(fakeObject);

    if (KoalaDebug) {
      System.err.println("read array values");
    }

    getObjectOutputHandler().writeStartArray(handle, _class,
        size, false, field);
    switch (type.getTypeDefinition()) {
      case Type.BYTE:
        for (int i = 0; i < size; i++) {
          getObjectOutputHandler().write((byte) readEOF(), false, null);
        }
        break;
      case Type.CHAR:
        for (int i = 0; i < size; i++) {
          getObjectOutputHandler().write((char)((readEOF() << 8)
          + (readEOF() << 0)),
          false, null);
        }
        break;
      case Type.DOUBLE:
        for (int i = 0; i < size; i++) {
          int i1 = ((readEOF() << 24) + (readEOF() << 16)
          + (readEOF() << 8) + (readEOF() << 0));
          int i2 = ((readEOF() << 24) + (readEOF() << 16)
                    + (readEOF() << 8) + (readEOF() << 0));
          double d = Double.longBitsToDouble(((long) i1 << 32)
              + (i2 & 0xFFFFFFFFL));
          getObjectOutputHandler().write(d, false, null);
        }
        break;
      case Type.FLOAT:
        for (int i = 0; i < size; i++) {
          int i1 = ((readEOF() << 24) + (readEOF() << 16)
          + (readEOF() << 8) + (readEOF() << 0));
          getObjectOutputHandler().write(Float.intBitsToFloat(i1),
              false, null);
        }
        break;
      case Type.INT:
        for (int i = 0; i < size; i++) {
          getObjectOutputHandler().write(((readEOF() << 24)
          + (readEOF() << 16)
          + (readEOF() << 8)
          + (readEOF() << 0)),
          false, null);
        }
        break;
      case Type.LONG:
        for (int i = 0; i < size; i++) {
          int i1 = ((readEOF() << 24) + (readEOF() << 16)
          + (readEOF() << 8) + (readEOF() << 0));
          int i2 = ((readEOF() << 24) + (readEOF() << 16)
                    + (readEOF() << 8) + (readEOF() << 0));
          getObjectOutputHandler().write((((long) i1 << 32)
              + (i2 & 0xFFFFFFFFL)),
              false, null);
        }
        break;
      case Type.SHORT:
        for (int i = 0; i < size; i++) {
          getObjectOutputHandler().write((short)((readEOF() << 8)
          + (readEOF() << 0)),
          false, field);
        }
        break;
      case Type.BOOLEAN:
        for (int i = 0; i < size; i++) {
          getObjectOutputHandler().write((readEOF() != 0), false, null);
        }
        break;
      case Type.STRING:
      case Type.ARRAY:
      case Type.OBJECT:
        for (int i = 0; i < size; i++) {
          try {
            readObject();
          } catch (StreamCorruptedException e) {
            if (KoalaDebug) {
              e.printStackTrace();
            }
            throw new IOException("error while reading " + type);
          }
        }
        break;
      default:
        throw new StreamCorruptedException("Invalid type");
    }
    getObjectOutputHandler().writeEndArray(handle, _class, field);
  }

  /**
   * Creates a new exception from the input stream.
   * @@SEEME
   */
  Object newException() throws ClassNotFoundException, IOException {
    byte code = peekCode();

    resetContext();

    readObject();

    resetContext();
    return null;
  }

  /**
   * Reads a new class description.
   */
  ClassDescription newClassDesc()
      throws ClassNotFoundException, IOException {
    ClassDescription nc;
    if (KoalaDebug) {
      System.err.println("enter newClassDesc");
    }
    String name = readUTFInternal().replace('/','.');

    if (KoalaDebug) {
      System.err.println("class name is " + name);
    }

    nc = new ClassDescription(name);
// assign a new handle to this class description
    assignHandle(nc);

    try {
      if (name.charAt(0) == '[') {
        nc.setType(TypeFactory.createTypeInternal(name));
      } else {
        nc.setType(TypeFactory.createObject(name));
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new IOException(e.toString());
    }

// read the serial version UID
    int i1 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    int i2 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    nc.setSerialVersionUID((((long) i1 << 32) + (i2 & 0xFFFFFFFFL)));

    if (KoalaDebug) {
      System.err.println("serial version is " + nc.getSerialVersionUID());
    }

// read flags (write method ? serializable ? externalizable ?)
    nc.setFlag(classDescFlags());

// read all fields description
    nc.setFields(fields());

// Is there any extra annotation for this class description
    classAnnotation();

// read the class description of his super class
    nc.setSuperClass(classDesc());

    if (KoalaDebug) {
      System.err.println("leave newClassDesc " + nc);
    }

    getObjectOutputHandler().writeClassDescription(nc);

    if (nc.hasWriteMethod()) {
      getMethod(nc, "readObject");
    }
    return nc;
  }

  /**
   * Reads a class annotation from the input stream
   */
  void classAnnotation() throws ClassNotFoundException, IOException {
    if (KoalaDebug) {
      System.err.println("enter classAnnotation");
    }

    try {
      while (true) {
        try {
          readObject();
        } catch (BlockDataException e) {
          if (!e.eof) {
            int i = e.length;
            blockdata(i);
          } else {
            throw e;
          }
        }
      }
    } catch (BlockDataException e) {
    }
  }

  /**
   * Reads all class flags from the input stream
   */
  byte classDescFlags() throws IOException {
    if (KoalaDebug) {
      System.err.println("enter classDescFlags");
    }

    byte c = (byte) readEOF();

    if (KoalaDebug) {
      System.err.println("flag = " + c);
    }

    switch (c) {
      case SC_WRITE_METHOD:
        if (KoalaDebug) {
          System.err.println("flag = SC_WRITE_METHOD");
        }
      case SC_SERIALIZABLE:
        if (KoalaDebug) {
          System.err.println("flag = SC_SERIALIZABLE");
        }
      case SC_EXTERNALIZABLE:
        if (KoalaDebug) {
          System.err.println("flag = SC_EXTERNALIZABLE");
        }
      case (SC_WRITE_METHOD | SC_SERIALIZABLE):
        if (KoalaDebug) {
          System.err.println("flag = SC_SERIALIZABLE + SC_WRITE_METHOD");
        }
      case (SC_BLOCK_DATA | SC_EXTERNALIZABLE):
        if (KoalaDebug) {
          System.err.println("flag = SC_EXTERNALIZABLE + SC_BLOCK_DATA");
        }
        return c;
      default:
        throw new StreamCorruptedException("invalid classDescInfo " + c);
    }
  }

  Field fieldDesc() throws ClassNotFoundException, IOException {
    Type type;
    String name;

    if (KoalaDebug) {
      System.err.println("enter fieldDesc");
    }

    byte code = (byte) readEOF();

    name = readUTFInternal();
    switch (code) {
      case BYTE_TYPE:
        type = TypeFactory.createByte();
        break;
      case CHAR_TYPE:
        type = TypeFactory.createChar();
        break;
      case DOUBLE_TYPE:
        type = TypeFactory.createDouble();
        break;
      case FLOAT_TYPE:
        type = TypeFactory.createFloat();
        break;
      case INT_TYPE:
        type = TypeFactory.createInt();
        break;
      case LONG_TYPE:
        type = TypeFactory.createLong();
        break;
      case SHORT_TYPE:
        type = TypeFactory.createShort();
        break;
      case BOOLEAN_TYPE:
        type = TypeFactory.createBoolean();
        break;
      case ARRAY_TYPE:
      case OBJECT_TYPE:
        try {
          hide = true;
          Object className = readObject();
          if ((className != null) && (className instanceof String)) {
            try {
              type =
                  TypeFactory.createTypeInternal(((String) className)
                  .replace('/','.'));
            } catch (Exception e) {
              throw new StreamCorruptedException("Attempt to find a type "
                  + className);
            }
          } else {
            throw new StreamCorruptedException("Attempt to find a new string "
                + className);
          }
        } finally {
          hide = false;
        }
        break;
      default:
        throw new StreamCorruptedException("invalid type");
    }

    return new Field(type, name);
  }

  Field[] fields() throws ClassNotFoundException, IOException {
    int count = ((readEOF() << 8) + (readEOF() << 0));
    int i = 0;
    Field[] fields = new Field[count];

    while (i < count) {
      fields[i] = fieldDesc();
      if (KoalaDebug) {
        System.err.println("read field " + fields[i].getName());
      }
      i++;

    }
    if (KoalaDebug) {
      System.err.println("read " + count + " fields");
    }

    return fields;
  }

  /**
   * Reads a reference to an another handle
   */
  Object prevObject(Field field) throws IOException {
    int handle;
    if (KoalaDebug) {
      System.err.println("enter prevObject");
    }
// read the handle
    handle = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0)) - baseWireHandle;

// get the object in my array
    Object ret = handles[handle];

    if (ret == null) {
// doesn't exist ! oups !
      throw new StreamCorruptedException("empty reference " + handle);
    }
    if (!hide) {
      if (ret == fakeObject) {
        getObjectOutputHandler().writeReference(handle, field);
      } else if (ret instanceof String) {
        getObjectOutputHandler().write((String) ret, false, field);
      }
    }
    if (KoalaDebug) {
      System.err.println("return with " + ret);
    }
// Yeah ! return it !
    return ret;
  }

  /**
   * Reads a class description
   *
   * Can be :
   *  - a new class description
   *  - a null reference
   *  - a reference to an another class description
   */
  ClassDescription classDesc() throws ClassNotFoundException, IOException {
    int c = read();

    if (KoalaDebug) {
      System.err.println("enter classDesc");
    }

    switch (c) {
      case TC_CLASSDESC:
// a new one !
        return newClassDesc();
      case TC_REFERENCE:
// an old one !
        Object obj = prevObject(null);
        if ((obj != null) && (obj instanceof ClassDescription)) {
// Yeah ! return it !
          return (ClassDescription) obj;
        } else {
// oups ! what's the matter ?
          throw new StreamCorruptedException("attempt to find a class description "
              + obj);
        }
      case TC_NULL:
// ok, probably a null super class
        return null;
      default:
        throw new StreamCorruptedException("Invalid byte "
        + Integer.toHexString((int) c));
    }

  }

  /**
   * Reads a new class
   */
  void newClass(Field field) throws ClassNotFoundException, IOException {
    if (KoalaDebug) {
      System.err.println("enter newClass");
    }

// read his class description
    ClassDescription _class = classDesc();

// assign his handle _after_ reading his class description
    int id = assignHandle(fakeObject);
    getObjectOutputHandler().writeStartObjectClass(id, _class,
        false, field);
    getObjectOutputHandler().writeEndObjectClass(id, _class, field);
  }

  /**
   * Creates a new String
   */
  String newString() throws IOException {
    if (KoalaDebug) {
      System.err.println("enter newString");
    }
// read the string
    String data = readUTFInternal();
// assign his new handle
    assignHandle(data);

// return it
    if (KoalaDebug) {
      System.err.println("string is " + data);
    }

    return data;
  }

  /**
   * Reads a block data
   */
  void blockdata(int size) throws IOException {
    if (KoalaDebug) {
      System.err.println("enter blockdata " + size);
    }

// if the size comes from an int, it may be negative
    if (size < 0) {
      new StreamCorruptedException("negative size in blockdata");
    }

// all byte data
    byte[] data = new byte[size];

// read the block
    readFullyInternal(data, 0, size);

// return it (there is no handle for a block data)
// @@FIXME
  }

  /**
   * Creates a new object
   */
  void newObject(Field field) throws ClassNotFoundException, IOException {
    if (KoalaDebug) {
      System.err.println("enter newObject");
    }
// read his class description
    ClassDescription desc = classDesc();

// assign the new handle _after_ reading his class description
    int id = assignHandle(fakeObject);

    getObjectOutputHandler().writeStartObject(id, desc, false, field);

// read all data for the object
    readObjectData(desc);

    getObjectOutputHandler().writeEndObject(id, desc, field);
  }

  /**
   * Reads data inside an object
   */
  private void readObjectData(ClassDescription desc)
      throws ClassNotFoundException, IOException {
    if (KoalaDebug) {
      System.err.println("enter readObjectData for " + desc);
    }
    ClassDescription _super = desc.getSuperClass();
    if (_super != null) {
// read the data for his super class
// read super data
      getObjectOutputHandler().writeStartSuper(_super);
      readObjectData(_super);
      getObjectOutputHandler().writeEndSuper(_super);
    }

    if (desc.hasWriteMethod()) {
      if (desc.method == null) {
        try {
/*
System.err.println("** WARNING ** attempt to find readObject in "
+ desc.getName());
*/
// normal serialization
          boolean prev = setBlockData(true);
          ClassDescription previous = currentClassDescription;
          currentClassDescription = desc;
          try {
            boolean prev2 = setBlockData(false);
            try {
              if (currentClassDescription.fieldsQuick != null) {
                _readValues(currentClassDescription);
              }
            } finally {
              setBlockData(prev2);
            }
          } finally {
            setBlockData(prev);
            byte code = peekCode();
            if (code == TC_ENDBLOCKDATA) {
              readCode();
            } else {
              skipData();
            }
            currentClassDescription = previous;
          }
        } catch (StreamCorruptedException e) {
          throw new StreamCorruptedException("attempt to find "
              + "readObject in"
              + desc.getName());
        }
      } else {
        boolean prev = setBlockData(true);
        ClassDescription previous = currentClassDescription;

        count = 0;
        currentClassDescription = desc;

        try {
          if (KoalaDebug) {
            System.err.println("invoke " + desc.method);
          }
          desc.method.invoke(null, argsMethod);
        } catch (InvocationTargetException e) {
          e.getTargetException().printStackTrace();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          setBlockData(prev);
          byte code = peekCode();
          if (code == TC_ENDBLOCKDATA) {
            readCode();
          } else {
            skipData();
          }
          currentClassDescription = previous;
        }
      }
    } else {
// normal serialization
      ClassDescription prev = currentClassDescription;
      currentClassDescription = desc;
      try {
        if (currentClassDescription.fieldsQuick != null) {
          _readValues(currentClassDescription);
        }
      } finally {
        currentClassDescription = prev;
      }
    }

    if (KoalaDebug) {
      System.err.println("leave readObjectData for " + desc);
    }
  }

  private void skipData()
      throws ClassNotFoundException, IOException {
    if (KoalaDebug) {
      System.err.println("Enter in skip data");
    }
    try {
      while (true) {
        try {
// ignore object
          readObject();
        } catch (BlockDataException e) {
          if (!e.eof) {
            byte[] skips = new byte[e.length];
// ignore blockdata
            readFullyInternal(skips, 0, e.length);
// @@SEEME
            getObjectOutputHandler().writeRow(skips, 0, e.length);
          } else {
            throw e;
          }
        }
      }
    } catch (BlockDataException e) {
      if (inBlockDataMode) {
        readCode(); // skip TC_ENDBLOCKDATA
      }
    }

    if (KoalaDebug) {
      System.err.println("Leave skip data");
    }
  }

  /**
   * Reads all values according to an array of field description
   */
  private void _readValues(ClassDescription _class)
      throws ClassNotFoundException, IOException {
    int[] qfields = _class.fieldsQuick;
    Field[] fields = _class.fields;
// read the size of value (or the number of values ? @@SEEME)

    if (KoalaDebug) {
      System.err.println("enter _readValues " + qfields.length);
    }
    int length = qfields.length;
    for (int i = 0; i < length; i++) {
      switch (qfields[i]) {
      case Type.BYTE:
        getObjectOutputHandler().write((byte) readEOF(), false,
        fields[i]);
        break;
      case Type.CHAR:
        getObjectOutputHandler().write((char)((readEOF() << 8)
            + (readEOF() << 0)),
            false, fields[i]);
        break;
      case Type.DOUBLE:
      {
        int i1 = ((readEOF() << 24) + (readEOF() << 16)
                  + (readEOF() << 8) + (readEOF() << 0));
        int i2 = ((readEOF() << 24) + (readEOF() << 16)
                  + (readEOF() << 8) + (readEOF() << 0));
        double d = Double.longBitsToDouble(((long) i1 << 32)
            + (i2 & 0xFFFFFFFFL));

        getObjectOutputHandler().write(d, false, fields[i]);
      }
      break;
    case Type.FLOAT:
    {
      int i1 = ((readEOF() << 24) + (readEOF() << 16)
                + (readEOF() << 8) + (readEOF() << 0));
      getObjectOutputHandler().write(Float.intBitsToFloat(i1),
                                     false, fields[i]);
    }
    break;
  case Type.INT:
    getObjectOutputHandler().write(((readEOF() << 24)
                                    + (readEOF() << 16)
                                    + (readEOF() << 8)
                                    + (readEOF() << 0)),
                                    false, fields[i]);
    break;
  case Type.LONG:
  {
    int i1 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    int i2 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    getObjectOutputHandler().write((((long) i1 << 32)
                                    + (i2 & 0xFFFFFFFFL)),
                                    false, fields[i]);
  }
  break;
case Type.SHORT:
  int ch1 = readEOF();
  int ch2 = readEOF();
  getObjectOutputHandler().write((short)((ch1 << 8) + (ch2 << 0)),
                                 false, fields[i]);
  break;
case Type.BOOLEAN:
  getObjectOutputHandler().write((readEOF() != 0),
                                 false, fields[i]);
  break;
case Type.STRING:
  readObjectInternal(fields[i]);
  break;
case Type.ARRAY:
  readObjectInternal(fields[i]);
  break;
case Type.OBJECT:
  readObjectInternal(fields[i]);
  break;
default:
  throw new StreamCorruptedException("invalid type");
    }
    }
  }

  /**
   * Returns the class description which has specified name.
   *
   * @param  name the name of the class
   * @return a class description or null otherwise
   */
  public ClassDescription getClassDescription(String name) {
    int size = currentHandle - baseWireHandle;
    int i = 0;

    while (i < size) {
      if (handles[i] instanceof ClassDescription
          && (((ClassDescription) handles[i]).getName().equals(name))) {
        return (ClassDescription) handles[i];
      }
      i++;
    }
    return null;
  }

  /**
   * Gets the class description for a specified class.
   *
   * @param  c the class
   * @return a class description or null otherwise
   */
  public ClassDescription getClassDescription(Class c) throws IOException {
    throw new IOException("unimplemented method");
  }

  private byte currCode = 0;

/*
  * Peek at the next control code in the stream.
  * If the code has not been peeked at yet, read it from the stream.
*/
  private byte peekCode() throws IOException, StreamCorruptedException{
    while (currCode == 0) {

      int newcode = in.read();	// Read byte from the underlying stream

      if (newcode < 0) {
        throw new EOFException("Expecting code");
      }

      currCode = (byte)newcode;
      if ((currCode < TC_BASE) || (currCode > TC_MAX)) {
        if (KoalaDebug) {
          try {
            for (int i = 0 ; i < 10 ; i++) {
              System.err.println(Integer.toString(in.read(), 16));
            }
            } catch (Exception e) {}
        }
        throw new StreamCorruptedException("Type code out of range, is "
            + currCode);
      }

/*
      * Handles reset as a hidden code and reset the stream.
*/
      if (currCode == TC_RESET) {
/* @@FIXME
if (recursionDepth != 0 ||
currentObject != null ||
currentClassDesc != null) {
throw new StreamCorruptedException("Illegal stream state for reset");
}
*/
/* Reset the stream, and repeat the peek at the next code
*/
        resetContext();
        currCode = 0;
      }
    }
    return currCode;
  }

/*
  * Returns the next control code in the stream.
  * peekCode gets the next code.  readCode just consumes it.
*/
  private byte readCode()
      throws IOException, StreamCorruptedException {
    byte tc = peekCode();
    currCode = 0;
    return tc;
  }

/*
  * Puts back the specified code to be peeked at next time.
*/
  private void pushbackCode(byte code) {
    currCode = code;
  }

  private void resetContext() {
    System.err.println("** WARNING ** reset context not yet implemented");
    System.err.println("**            stream may be corrupted");

    for (int i = 0; i < (currentHandle - baseWireHandle); i++) {
      handles[i] = null;
    }
    currentHandle = baseWireHandle;
  }

/*
  * Expect the next thing in the stream is a datablock, If its a
  * datablock, extract the count of bytes to allow.  If data is not
  * available set the count to zero.  On error or EOF, set count to -1.
*/
  private void refill() throws IOException {
    count = -1;		/*  No more data to read, EOF */
    byte code;

    try {
      code = peekCode();
      if (KoalaDebug) {
        System.err.println("refill " + Integer.toString(code, 16));
      }
    } catch (EOFException e) {
      e.printStackTrace();
      return;
    }
    if (code == TC_BLOCKDATA) {
      if (KoalaDebug) {
        System.err.println("This is a blockdata");
      }
      code = readCode();			/* Consume the code */
      int c = in.read();
      if (c < 0) {
        throw new StreamCorruptedException("EOF expecting count");
      }
      count = c & 0xff;
    } else if (code == TC_BLOCKDATALONG) {
      if (KoalaDebug) {
        System.err.println("This is a long blockdata");
      }
      code = readCode();			/* Consume the code */
      int b3 = in.read();
      int b2 = in.read();
      int b1 = in.read();
      int b0 = in.read();
      if ((b3 | b2 | b1 | b0) < 0) {
        throw new StreamCorruptedException("EOF expecting count");
      }
      int c = (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
/*
      * The 32 bit integer size in the long block data format is
      * signed (unlike like the normal block data format), and
      * negative values are invalid.
*/
      if (c < 0) {
        throw new StreamCorruptedException("Negative block data size");
      }
      count = c;
    }
  }

/*
  * Sets the blockdata buffering mode.
  * If it is being set to false after being true there must
  * be no pending data. If count > 0 a corrupted exception is thrown.
*/
  private boolean setBlockData(boolean mode) throws IOException {
    if (KoalaDebug) {
      System.err.println(inBlockDataMode + " setBlockData(" + mode + ")");
    }
    if (inBlockDataMode == mode) {
      return mode;
    }
    if (inBlockDataMode && count > 0) {
      throw new StreamCorruptedException("Unread " + count
          + " bytes");
    }

/* Set count to allow reading or not */
    count =  mode ? 0 : -1;

    inBlockDataMode = mode;
    return !mode;
  }

  private final void checkSize(int size) {
    int oldl = handles.length;
    if (size >= oldl) {
      Object[] old = handles;
      handles = new Object[size + size];
      System.arraycopy(old, 0, handles, 0, oldl);
    }
  }

  private final int assignHandle(Object obj) {
    int id = currentHandle++ - baseWireHandle;
    checkSize(id);
    if (obj instanceof ClassDescription) {
      ((ClassDescription) obj).setId(id);
    }
    handles[id] = obj;
    return id;
  }

  private final int reserveHandle() {
    return (currentHandle++ - baseWireHandle);
  }

  /**
   * Reads a byte of data. This method will block if no input is
   * available.
   * @return  the byte read, or -1 if the end of the
   *          stream is reached.
   * @exception IOException If an I/O error has occurred.
   */
  public int read() throws IOException {
    int data;

    if (inBlockDataMode) {
      while (count == 0) {
        refill();
      }
      if (count < 0) {
        return -1;			/* EOF */
      }
      data = in.read();
      if (data >= 0) {
        count--;
      }
    } else {
      data = in.read();		/* read directly from input stream */
    }
    return data;
  }

  /**
   * Reads a byte of data. This method will block if no input is
   * available.
   * @return  the byte read, or EOFException if the end of the
   *          stream is reached.
   * @exception IOException If an I/O error has occurred.
   */
  private int readEOF() throws IOException {
    int data;

    if (inBlockDataMode) {
      while (count == 0) {
        refill();
      }
      if (count < 0) {
        throw new EOFException();
      }
      data = in.read();
      if (data >= 0) {
        count--;
      }
    } else {
      data = in.read();		/* read directly from input stream */
    }
    if (data == -1) {
      throw new EOFException();
    }
    return data;
  }

  /**
   * Reads into an array of bytes.  This method will
   * block until some input is available.
   * @param b	the buffer into which the data is read
   * @param off the start offset of the data
   * @param len the maximum number of bytes read
   * @return  the actual number of bytes read, -1 is
   * 		returned when the end of the stream is reached.
   * @exception IOException If an I/O error has occurred.
   * @since     JDK1.1
   */
  public int read(byte[] data, int offset, int length) throws IOException {
    int v;
    int i;

    if (length < 0) {
      throw new IndexOutOfBoundsException();
    }

    if (inBlockDataMode) {
      while (count == 0) {
        refill();
      }
      if (count < 0) {
        return -1;
      }
      int l = Math.min(length, count);
      i = in.read(data, offset, l);
      if (i > 0) {
        count -= i;
      }
      return i;			/* return number of bytes read */
    } else {
/* read directly from input stream */
      return in.read(data, offset, length);
    }
  }

  /**
   * Closes the input stream. Must be called
   * to release any resources associated with
   * the stream.
   * @exception IOException If an I/O error has occurred.
   */
  public void close() throws IOException {
    in.close();
  }

  /**
   * Reads in a boolean.
   * @return the boolean read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public boolean readBoolean() throws IOException {
    boolean b = (readEOF() != 0);
    getObjectOutputHandler().write(b, true, null);
    return b;
  }

  /**
   * Reads an 8 bit byte.
   * @return the 8 bit byte read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public byte readByte() throws IOException  {
    byte b = (byte) readEOF();
    getObjectOutputHandler().write(b, true, null);
    return b;
  }

  /**
   * Reads an unsigned 8 bit byte.
   * @return the 8 bit byte read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public int readUnsignedByte()  throws IOException {
    int i = readEOF();
    getObjectOutputHandler().write((byte) i, true, null);
    return i;
  }

  /**
   * Reads a 16 bit short.
   * @return the 16 bit short read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public short readShort()  throws IOException {
    short s = (short)((readEOF() << 8) + (readEOF() << 0));
    getObjectOutputHandler().write(s, true, null);
    return s;
  }

  /**
   * Reads an unsigned 16 bit short.
   * @return the 16 bit short read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public int readUnsignedShort() throws IOException {
    int i = readEOF();
    getObjectOutputHandler().write((short) i, true, null);
    return i;
  }

  /**
   * Reads a 16 bit char.
   * @return the 16 bit char read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public char readChar()  throws IOException {
    char c = (char)((readEOF() << 8) + (readEOF() << 0));
    getObjectOutputHandler().write(c, true, null);
    return c;
  }

  /**
   * Reads a 32 bit int.
   * @return the 32 bit integer read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public int readInt()  throws IOException {
    int i = ((readEOF() << 24) + (readEOF() << 16)
             + (readEOF() << 8) + (readEOF() << 0));
    getObjectOutputHandler().write(i, true, null);
    return i;
  }

  /**
   * Reads a 64 bit long.
   * @return the read 64 bit long.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public long readLong()  throws IOException {
    int i1 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    int i2 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    long l = (((long) i1 << 32) + (i2 & 0xFFFFFFFFL));
    getObjectOutputHandler().write(l, true, null);
    return l;
  }


  /**
   * Reads a 32 bit float.
   * @return the 32 bit float read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public float readFloat() throws IOException {
    int i1 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    float f = Float.intBitsToFloat(i1);
    getObjectOutputHandler().write(f, true, null);
    return f;
  }

  /**
   * Reads a 64 bit double.
   * @return the 64 bit double read.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public double readDouble() throws IOException {
    int i1 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    int i2 = ((readEOF() << 24) + (readEOF() << 16)
              + (readEOF() << 8) + (readEOF() << 0));
    double d = Double.longBitsToDouble(((long) i1 << 32)
                                       + (i2 & 0xFFFFFFFFL));
    getObjectOutputHandler().write(d, true, null);
    return d;
  }

  private char lineBuffer[];

  /**
   * Reads in a line that has been terminated by a \n, \r,
   * \r\n or EOF.
   * @return a String copy of the line.
   */
  public String readLine() throws IOException {
    System.err.println( "warning, this function is not handle yet!");
    return dis.readLine();
  }

  /**
   * Reads bytes, blocking until all bytes are read.
   * @param data the buffer into which the data is read
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public void readFully(byte[] data) throws IOException {
    readFullyInternal(data, 0, data.length);
    getObjectOutputHandler().writeRow(data, 0, data.length);
  }

  /**
   * Reads bytes, blocking until all bytes are read.
   * @param data the buffer into which the data is read
   * @param off the start offset of the data
   * @param len the maximum number of bytes to read
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  private void readFullyInternal(byte[] data, int offset, int len)
      throws IOException {
    if (len < 0) {
      throw new IndexOutOfBoundsException();
    }

    int n = 0;
    while (n < len) {
      int count = read(data, offset + n, len - n);
      if (count < 0)
        throw new EOFException();
      n += count;
    }
  }

  /**
   * Skips bytes, block until all bytes are skipped.
   * @param n the number of bytes to be skipped
   * @return  the actual number of bytes skipped.
   * @exception EOFException If end of file is reached.
   * @exception IOException If other I/O error has occurred.
   */
  public int skipBytes(int n) throws IOException {
    for (int i = 0 ; i < n ; i += (int) skip(n - i));
    return n;
  }

  /**
   * Reads a UTF format String.
   * @return the String.
   */
  private String readUTFInternal() throws IOException {
    int utflen = (readEOF() << 8) + (readEOF() << 0);
    char str[] = new char[utflen];
    int count = 0;
    int strlen = 0;
    while (count < utflen) {
      int c = readEOF();
      int char2, char3;
      switch (c >> 4) {
        case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
// 0xxxxxxx
          count++;
          str[strlen++] = (char) c;
          break;
        case 12: case 13:
// 110x xxxx   10xx xxxx
          count += 2;
          if (count > utflen) {
            throw new UTFDataFormatException();
          }
          char2 = readEOF();
          if ((char2 & 0xC0) != 0x80) {
            throw new UTFDataFormatException();
          }
          str[strlen++] = (char)(((c & 0x1F) << 6) | (char2 & 0x3F));
          break;
        case 14:
// 1110 xxxx  10xx xxxx  10xx xxxx
          count += 3;
          if (count > utflen) {
            throw new UTFDataFormatException();
          }
          char2 = readEOF();
          char3 = readEOF();
          if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
            throw new UTFDataFormatException();
          }
          str[strlen++] = (char)(((c & 0x0F) << 12) |
                                 ((char2 & 0x3F) << 6) |
                                 ((char3 & 0x3F) << 0));
          break;
        default:
// 10xx xxxx,  1111 xxxx
          throw new UTFDataFormatException();
      }
    }
    return new String(str, 0, strlen);
  }

  private final void getMethod(ClassDescription desc,
                               String name)
      throws ClassNotFoundException {
    int i = 0;
    Class cl = null;
    Method m = (Method) fakeMethod.get(desc.getName());

    if (m != null) {
      desc.method = m;
      return;
    }

    cl = Class.forName(desc.getName());
    if (cl == null) {
      throw new ClassNotFoundException(desc.getName());
    }

    try {
      Properties tmp = new Properties();
      URL url = new URL(fr.dyade.koala.xml.koml.KOMLConstants.XML_PROPERTIES);
      InputStream f = url.openStream();
      tmp.load(f);
      f.close();
      GeneratorInputStream.addMethods(tmp);

      m = (Method) fakeMethod.get(desc.getName());
      if (m != null) {
        desc.method = m;
        return;
      }
    } catch (Exception e) {

      e.printStackTrace();
      if (KoalaDebug) {
        System.err.println(GeneratorInputStream.class
                           + ": couldn't load properties ");
        e.printStackTrace();
      }
    }


    try {
      m = cl.getMethod("readObject", params);
      fakeMethod.put(desc.getName(), m);
      desc.method = m;
    } catch (Exception e) {
    }
    return;
  }

  private static Class[] params;

  /**
   * Add methods from properties.
   */
  public static void addMethods(Properties properties) {
    for (Enumeration e = properties.keys(); e.hasMoreElements();) {
      String key = (String) e.nextElement();
      String value = (String) properties.get(key);

      try {
        Class cl = Class.forName(value.trim());
        fakeMethod.put(key, cl.getMethod("readObject", params));
      } catch (Exception ex) {
        System.err.println(ex.toString());
      }
    }
  }

  static {
    params = new Class[1];
    params[0] = GeneratorInputStream.class;

    try {
      serializableClass = Class.forName("java.io.Serializable");
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      externalizableClass =
          Class.forName("java.io.Externalizable");
    } catch (Exception e) {
      e.printStackTrace();
    }

    fakeMethod = new Hashtable();

    try {
      Class c = Class.forName("fr.dyade.koala.serialization.api.DefaultSerializer");
      Method m = c.getMethod("readObject", params);
      fakeMethod.put("java.awt.Menu", m);
      fakeMethod.put("java.awt.Font", m);
      fakeMethod.put("java.awt.Frame", m);
      fakeMethod.put("java.net.URL", m);
      fakeMethod.put("java.text.DecimalFormat", m);
      fakeMethod.put("java.text.SimpleDateFormat", m);

      c = Class.forName("fr.dyade.koala.serialization.api.AWTListenerSerializer");
      m = c.getMethod("readObject", params);
      fakeMethod.put("java.awt.Scrollbar", m);
      fakeMethod.put("java.awt.Container", m);
      fakeMethod.put("java.awt.List", m);
      fakeMethod.put("java.awt.TextField", m);
      fakeMethod.put("java.awt.TextComponent", m);
      fakeMethod.put("java.awt.Component", m);
      fakeMethod.put("java.awt.Choice", m);
      fakeMethod.put("java.awt.Button", m);
      fakeMethod.put("java.awt.CheckboxMenuItem", m);
      fakeMethod.put("java.awt.Window", m);
      fakeMethod.put("java.awt.Checkbox", m);
      fakeMethod.put("java.awt.MenuItem", m);

      c = Class.forName("fr.dyade.koala.serialization.api.ChangeSupportListenerSerializer");
      m = c.getMethod("readObject", params);
      fakeMethod.put("java.beans.VetoableChangeSupport", m);
      fakeMethod.put("java.beans.PropertyChangeSupport", m);

      c = Class.forName("fr.dyade.koala.serialization.api.FileSerializer");
      m = c.getMethod("readObject", params);
      fakeMethod.put("java.io.File", m);

      c = Class.forName("fr.dyade.koala.serialization.api.DateSerializer");
      m = c.getMethod("readObject", params);
      fakeMethod.put("java.util.Date", m);

      c = Class.forName("fr.dyade.koala.serialization.api.SimpleTimeZoneSerializer");
      m = c.getMethod("readObject", params);
      fakeMethod.put("java.util.SimpleTimeZone", m);

      c = Class.forName("fr.dyade.koala.serialization.api.HashtableSerializer");
      m = c.getMethod("readObject", params);
      fakeMethod.put("java.util.Hashtable", m);
    } catch (Exception e) {
    }

/*
try {
url = GeneratorInputStream.class.getResource("XML.properties");
InputStream f = url.openStream();
tmp.load(f);
f.close();
GeneratorInputStream.addMethods(tmp);
} catch (Exception e) {
System.err.println(GeneratorInputStream.class
+ ": couldn't load properties ");
e.printStackTrace();
}
*/
  }

}