package org.rcosjava.software.animator.filesystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.*;

import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.animator.support.RCOSRectangle;
import org.rcosjava.software.filesystem.FileSystemManager;
import org.rcosjava.software.filesystem.FileSystemData;
import org.rcosjava.software.filesystem.msdos.MSDOSFile;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSFATException;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSDirectoryException;
import org.rcosjava.software.util.Data;
import org.rcosjava.software.util.DataInvalidaException;
import org.rcosjava.software.util.HorarioInvalidoException;
import org.rcosjava.software.util.Horario;

/**
 * A graphical representation of the a file system.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 18/09/2002 Originally created by Danielly. </DD>
 * <DD> 24/07/2003 Finally imported as a panel into new RCOSjava AN. </DD> </DT>
 * <P>
 * @author Danielly Karine da Silva Cruz
 * @author Andrew Newman
 * @created 18 September 2002
 * @version 1.00 $Date$
 */
public class FileSystemPanel extends RCOSPanel
{
  private JTextField textFieldPath = new JTextField();
  private JTextField textFieldDirectoryName = new JTextField(8);
  private JTextField textFieldFileName = new JTextField(8);
  private JTextField textFieldExtensionName = new JTextField(3);

  private FileSystemManager fileSystemManager;
  private JTextArea textArea = new JTextArea(15,30);
  private JTextArea textAreaDir = new JTextArea(1,7);

  private ArrayList blocks = new ArrayList();
  private int blockIndex = 1;

  private static final Color UNALLOCATED_COLOUR = Color.gray;
  private static final Color READ_COLOUR  = Color.blue;
  private static final Color WRITTEN_COLOUR = Color.red;
  private static final Color DELETED_COLOUR = Color.green;

  /**
   * Default block font size (Courier, Plain, 11).
   */
  protected transient static final Font blockFont = new Font("Courier",
      Font.PLAIN, 11);

  /**
   * Constructor for the FileSystemFrame object
   *
   * @param images Description of Parameter
   */
  public FileSystemPanel(FileSystemManager theFileSystemManager)
  {
    super();
    fileSystemManager = theFileSystemManager;
  }

  /**
   * Description of the Method
   */
  public void setupLayout(Component c)
  {
    setLayout(new BorderLayout());
    setBackground(defaultBgColour);
    setForeground(defaultFgColour);

    JPanel panelClose = new JPanel();
    JLabel tmpLabel;

    JPanel textFieldPathPanel = new JPanel();
    JPanel textFieldDirectoryNamePanel = new JPanel();
    JPanel textFieldFileNamePanel = new JPanel();
    JPanel textFieldExtensionNamePanel = new JPanel();

    JPanel dirButtonPanel = new JPanel();
    JPanel writeDirButtonPanel = new JPanel();
    JPanel writeFileButtonPanel = new JPanel();
    JPanel readFileButtonPanel = new JPanel();
    JPanel msdosExplorerTextareaPanel = new JPanel();
    JPanel deleteFileButtonPanel = new JPanel();
    JPanel deleteDirButtonPanel = new JPanel();

    JPanel mainPanel = new JPanel();
    mainPanel.setBackground(defaultBgColour);
    mainPanel.setForeground(defaultFgColour);

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;
    constraints.fill = GridBagConstraints.BOTH;
    constraints.insets = new Insets(3, 3, 3, 3);
    constraints.anchor = GridBagConstraints.CENTER;
    mainPanel.setLayout(gridBag);

//    constraints.gridwidth = GridBagConstraints.REMAINDER;
//    tmpLabel = new JLabel("RCOS File System");
//    gridBag.setConstraints(tmpLabel, constraints);
//    mainPanel.add(tmpLabel);
//
//    constraints.gridwidth = GridBagConstraints.RELATIVE;
//    tmpLabel = new JLabel("Type your text here:");
//    gridBag.setConstraints(tmpLabel, constraints);
//    mainPanel.add(tmpLabel);
//
//    constraints.gridwidth = GridBagConstraints.REMAINDER;
//    tmpLabel = new JLabel("Fields of the File System: ");
//    gridBag.setConstraints(tmpLabel, constraints);
//    mainPanel.add(tmpLabel);
//
//    constraints.gridwidth = 1;
//
//    JPanel panel1 = new JPanel();
//
//    panel1.setLayout(new GridLayout(1, 1));
//
//    textArea.setBackground(textBoxColour);
//    textArea.setForeground(defaultFgColour);
//
//    panel1.add(textArea);
//    gridBag.setConstraints(panel1, constraints);
//    mainPanel.add(panel1);
//
//    JPanel panel2 = new JPanel();
//
//    textFieldPath.setBackground(textBoxColour);
//    textFieldDirectoryName.setBackground(textBoxColour);
//    textFieldFileName.setBackground(textBoxColour);
//    textFieldExtensionName.setBackground(textBoxColour);
//
//    textFieldPath.setBounds(3,3,10,1);
//    textFieldPathPanel.setLayout(null);
//    textFieldPathPanel.add(textFieldPath);
//
//    textFieldDirectoryName.setBounds(3,3,10,1);
//    textFieldDirectoryNamePanel.setLayout(null);
//    textFieldDirectoryNamePanel.add(textFieldDirectoryName);
//
//    textFieldFileName.setBounds(1,1,10,1);
//    textFieldFileNamePanel.setLayout(null);
//    textFieldFileNamePanel.add(textFieldFileName);
//
//    textFieldExtensionName.setBounds(1,1,10,1);
//    textFieldExtensionNamePanel.setLayout(null);
//    textFieldExtensionNamePanel.add(textFieldExtensionName);
//
//    JButton dirButton = new JButton ("Dir");
//    dirButtonPanel.add(dirButton);
//    dirButton.addMouseListener(new DirFileSystem());
//
//    JButton writeDirButton = new JButton("Write Directory");
//    writeDirButtonPanel.add(writeDirButton);
//    writeDirButton.addMouseListener(new WriteDirSystem());
//
//    JButton writeFileButton = new JButton("Write File");
//    writeFileButtonPanel.add(writeFileButton);
//    writeFileButton.addMouseListener(new WriteFileSystem());
//
//    JButton readFileButton = new JButton("Read File");
//    readFileButtonPanel.add(readFileButton);
//    readFileButton.addMouseListener(new ReadFileSystem());
//
//    JButton deleteFileButton = new JButton("Delete File");
//    deleteFileButtonPanel.add(deleteFileButton);
//    deleteFileButton.addMouseListener(new DeleteFileSystem());
//
//    JButton deleteDirButton = new JButton("Delete Directory");
//    deleteDirButtonPanel.add(deleteDirButton);
//    deleteDirButton.addMouseListener(new DeleteDirSystem());
//
//    panel2.setLayout(new GridLayout(5, 4));
//    panel2.add(new JLabel("Path:              "));
//    panel2.add(new JLabel("Directory Name:    "));
//    panel2.add(new JLabel("File Name:         "));
//    panel2.add(new JLabel("Extension Name:    "));
//
//    panel2.add(textFieldPath);
//    panel2.add(textFieldDirectoryName);
//    panel2.add(textFieldFileName);
//    panel2.add(textFieldExtensionName);
//
//    panel2.add(dirButtonPanel);
//    panel2.add(writeDirButtonPanel);
//    panel2.add(writeFileButtonPanel);
//    panel2.add(readFileButtonPanel);
//
//    panel2.add(deleteDirButtonPanel);
//    panel2.add(deleteFileButtonPanel);
//
//    textAreaDir.setBackground(textBoxColour);
//    textAreaDir.setForeground(defaultFgColour);
//    msdosExplorerTextareaPanel.setLayout(null);
//    textAreaDir.setBounds(3,3,30,2);
//    msdosExplorerTextareaPanel.add(textAreaDir);
//
//    panel2.add(new JLabel("RCOS Explorer:    "));
//    panel2.add(textAreaDir);
//    constraints.gridwidth = GridBagConstraints.REMAINDER;
//    gridBag.setConstraints(panel2, constraints);
//    mainPanel.add(panel2);
//
//    constraints.gridwidth = 1;

    JPanel browserPanel = new JPanel();
    browserPanel.setBackground(defaultBgColour);
    browserPanel.setForeground(defaultFgColour);

    // Text Explorer
    TitledBorder browserTitle = BorderFactory.createTitledBorder(
        "File System Browser");
    browserTitle.setTitleColor(defaultFgColour);
    browserPanel.setBorder(BorderFactory.createCompoundBorder(
        browserTitle, BorderFactory.createEmptyBorder(3,3,3,3)));

    browserPanel.add(new JLabel("Hellooooooooooooooooooooooo"));

    constraints.gridheight = 2;
    constraints.gridwidth = 1;
    gridBag.setConstraints(browserPanel, constraints);
    mainPanel.add(browserPanel);

    // Create Block Area
    JPanel blockPanel = new JPanel();
    GridLayout gridLayoutFAT = new GridLayout(10, 10, 1, 1);
    blockPanel.setLayout(gridLayoutFAT);
    blockPanel.setBackground(defaultBgColour);
    blockPanel.setForeground(defaultFgColour);

    // Create border around panel
    TitledBorder blocksTitle = BorderFactory.createTitledBorder("Blocks");
    blocksTitle.setTitleColor(defaultFgColour);
    blockPanel.setBorder(BorderFactory.createCompoundBorder(
        blocksTitle, BorderFactory.createEmptyBorder(3,3,3,3)));

    // Create the 100 blocks.
    for (int i = 0; i < 100; i++)
    {
      // Create new label for block
      JPanel tmpPanel = new JPanel(new BorderLayout());
      tmpPanel.setBackground(UNALLOCATED_COLOUR);
      JLabel label = new JLabel("" + (i+1));
      label.setBackground(UNALLOCATED_COLOUR);
      label.setForeground(Color.white);
      label.setFont(blockFont);
      label.setVerticalAlignment(JLabel.CENTER);
      label.setVerticalTextPosition(JLabel.CENTER);
      label.setHorizontalAlignment(JLabel.CENTER);
      label.setHorizontalTextPosition(JLabel.CENTER);

      // Add to blocks.
      blocks.add(i, label);

      // Add the label to the panel
      tmpPanel.add(label, BorderLayout.CENTER);
      blockPanel.add(tmpPanel);
    }

    blockPanel.invalidate();

    constraints.gridheight = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(blockPanel, constraints);
    mainPanel.add(blockPanel);

    // Create Key Area
    RCOSRectangle unallocBox;
    RCOSRectangle readBox;
    RCOSRectangle writeBox;
    RCOSRectangle deleteBox;

    unallocBox = new RCOSRectangle(0, 0, 20, 20, UNALLOCATED_COLOUR,
        Color.white);
    readBox = new RCOSRectangle(0, 0, 20, 20, READ_COLOUR, Color.white);
    writeBox = new RCOSRectangle(0, 0, 20, 20, WRITTEN_COLOUR, Color.white);
    deleteBox = new RCOSRectangle(0, 0, 20, 20, DELETED_COLOUR, Color.white);

    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 0;
    constraints.weightx = 0;

    // Create panel
    JPanel keyPanel = new JPanel();
    keyPanel.setBackground(defaultBgColour);
    keyPanel.setForeground(defaultFgColour);

    // Create border around panel
    TitledBorder optionsTitle = BorderFactory.createTitledBorder("Key");
    optionsTitle.setTitleColor(defaultFgColour);
    keyPanel.setBorder(BorderFactory.createCompoundBorder(
        optionsTitle, BorderFactory.createEmptyBorder(3,3,3,3)));
    keyPanel.setLayout(gridBag);

    gridBag.setConstraints(unallocBox, constraints);
    keyPanel.add(unallocBox);

    tmpLabel = new JLabel(" Unallocated ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    keyPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(readBox, constraints);
    keyPanel.add(readBox);

    tmpLabel = new JLabel(" Read ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    keyPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(writeBox, constraints);
    keyPanel.add(writeBox);

    tmpLabel = new JLabel(" Wrote ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    keyPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(deleteBox, constraints);
    keyPanel.add(deleteBox);

    tmpLabel = new JLabel(" Deleted ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    keyPanel.add(tmpLabel);

    // Add key panel
    constraints.gridheight = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(keyPanel, constraints);
    mainPanel.add(keyPanel);

    add(mainPanel, BorderLayout.CENTER);
    repaint();
  }

  class CloseFileSystem extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      fileSystemManager.recordeSystemFile();
      setVisible(false);
    }
  }

  class DirFileSystem extends MouseAdapter{

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e){
        // pegar o path e o nome do diretorio

        System.out.println("dentro do evento q trata do click");
        String path = textFieldPath.getText().toUpperCase();
        System.out.println("path: "+path);
        String directoryName = textFieldDirectoryName.getText().toUpperCase();
        System.out.println("directoryName: "+directoryName);

        MSDOSFile msdosFile = null;
        String retorno = "";

        try{
           msdosFile = getNewMSDOSFile(directoryName,"", false);
        }catch(HorarioInvalidoException hiex){
           System.out.println("hiex: "+hiex.getMessage());
           retorno = hiex.getMessage();
        }catch(DataInvalidaException diex){
           System.out.println("diex: "+diex.getMessage());
           retorno = diex.getMessage();
        }

        FileSystemData fileSystemData = null;
        try{
           fileSystemData = fileSystemManager.read(1, msdosFile, path);
        }catch(MSDOSFATException fatex){
           System.out.println("fatex: "+fatex.getMessage());
           retorno = fatex.getMessage();
        }catch(MSDOSDirectoryException direx){
           System.out.println("direx: "+direx.getMessage());
           retorno = direx.getMessage();
        }
        if(fileSystemData!=null){
          retorno = fileSystemData.getReturnValue();
        }


        if(fileSystemData!=null){
           retorno = fileSystemData.getReturnValue();
           // Seta blocos
           setBlocks(fileSystemData, Color.blue);
        }

        System.out.println("retorno: "+retorno);
        textAreaDir.setText(retorno);

//      myProcessManager.sendRunMessage();
    }

  }

  class ReadFileSystem extends MouseAdapter{

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e){
        // pegar o path e o nome do diretorio

        System.out.println("dentro do evento de ler arquivo");
        String path = textFieldPath.getText().toUpperCase();
        System.out.println("path: "+path);
        String fileName = textFieldFileName.getText().toUpperCase();
        System.out.println("fileName: "+fileName);

        String extensionName = textFieldExtensionName.getText().toUpperCase();
        System.out.println("extensionName: "+extensionName);

        MSDOSFile msdosFile = null;
        String retorno = "";

        try{
           msdosFile = getNewMSDOSFile(fileName,extensionName, true);
        }catch(HorarioInvalidoException hiex){
           System.out.println("hiex: "+hiex.getMessage());
           retorno = hiex.getMessage();
        }catch(DataInvalidaException diex){
           System.out.println("diex: "+diex.getMessage());
           retorno = diex.getMessage();
        }

        FileSystemData fileSystemData = null;
        try{
           fileSystemData = fileSystemManager.read(1, msdosFile, path);
        }catch(MSDOSFATException fatex){
           System.out.println("fatex: "+fatex.getMessage());
           retorno = fatex.getMessage();
        }catch(MSDOSDirectoryException direx){
           System.out.println("direx: "+direx.getMessage());
           retorno = direx.getMessage();
        }

//        Button button;
        if(fileSystemData!=null){
           retorno = fileSystemData.getReturnValue();
           setBlocks(fileSystemData, Color.blue);
//           int[] entries = fileSystemData.getEntriesFileFromFAT();
//           int aux = 0;
//           System.out.println("entries.length: "+entries.length);
//           for (int i = 0; i < entries.length; i++) {
//             System.out.println("i: "+i);
//             aux = entries[i];
//             System.out.println("aux: "+aux);
//             if (aux!=-1){ // significa lendo da raiz
//               button = (Button)buttons.elementAt(aux);
//               button.setBackground(Color.blue);
//               System.out.println("button: "+button.getLabel());
//             }
//           }
        }
        System.out.println("retorno: "+retorno);
        textArea.setText(retorno);

//      myProcessManager.sendRunMessage();
    }
  }

  class DeleteDirSystem extends MouseAdapter{

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e){
        // pegar o path e o nome do diretorio

        System.out.println("dentro do evento q trata do click");
        String path = textFieldPath.getText().toUpperCase();
        System.out.println("path: "+path);
        String directoryName = textFieldDirectoryName.getText().toUpperCase();
        System.out.println("directoryName: "+directoryName);

        MSDOSFile msdosFile = null;
        String retorno = "";

        try{
           msdosFile = getNewMSDOSFile(directoryName,"", false);
        }catch(HorarioInvalidoException hiex){
           System.out.println("hiex: "+hiex.getMessage());
           retorno = hiex.getMessage();
        }catch(DataInvalidaException diex){
           System.out.println("diex: "+diex.getMessage());
           retorno = diex.getMessage();
        }

        FileSystemData fileSystemData = null;
        try{
           fileSystemData = fileSystemManager.delete(1, msdosFile, path);
        }catch(MSDOSFATException fatex){
           System.out.println("fatex: "+fatex.getMessage());
           retorno = fatex.getMessage();
        }catch(MSDOSDirectoryException direx){
           System.out.println("direx: "+direx.getMessage());
           retorno = direx.getMessage();
        }
        if(fileSystemData!=null){
          retorno = fileSystemData.getReturnValue();
        }


        if(fileSystemData!=null){
           retorno = fileSystemData.getReturnValue();
           // Seta blocos
           setBlocks(fileSystemData, Color.green);
        }

        System.out.println("retorno: "+retorno);
        textAreaDir.setText(retorno);
        textAreaDir.setText("Directory "+msdosFile.getNameFile()+" deleted with success");

//      myProcessManager.sendRunMessage();
    }

  }

  class DeleteFileSystem extends MouseAdapter{

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e){
        // pegar o path e o nome do diretorio

        System.out.println("dentro do evento de ler arquivo");
        String path = textFieldPath.getText().toUpperCase();
        System.out.println("path: "+path);
        String fileName = textFieldFileName.getText().toUpperCase();
        System.out.println("fileName: "+fileName);

        String extensionName = textFieldExtensionName.getText().toUpperCase();
        System.out.println("extensionName: "+extensionName);

        MSDOSFile msdosFile = null;
        String retorno = "";

        try{
           msdosFile = getNewMSDOSFile(fileName,extensionName, true);
        }catch(HorarioInvalidoException hiex){
           System.out.println("hiex: "+hiex.getMessage());
           retorno = hiex.getMessage();
        }catch(DataInvalidaException diex){
           System.out.println("diex: "+diex.getMessage());
           retorno = diex.getMessage();
        }

        FileSystemData fileSystemData = null;
        try{
           fileSystemData = fileSystemManager.delete(1, msdosFile, path);
        }catch(MSDOSFATException fatex){
           System.out.println("fatex: "+fatex.getMessage());
           retorno = fatex.getMessage();
        }catch(MSDOSDirectoryException direx){
           System.out.println("direx: "+direx.getMessage());
           retorno = direx.getMessage();
        }

//        Button button;
        if(fileSystemData!=null){
           retorno = fileSystemData.getReturnValue();
           setBlocks(fileSystemData, Color.green);
//           int[] entries = fileSystemData.getEntriesFileFromFAT();
//           int aux = 0;
//           System.out.println("entries.length: "+entries.length);
//           for (int i = 0; i < entries.length; i++) {
//             System.out.println("i: "+i);
//             aux = entries[i];
//             System.out.println("aux: "+aux);
//             if (aux!=-1){ // significa lendo da raiz
//               button = (Button)buttons.elementAt(aux);
//               button.setBackground(Color.blue);
//               System.out.println("button: "+button.getLabel());
//             }
//           }
        }
        System.out.println("retorno: "+retorno);
        textArea.setText(retorno);
        textAreaDir.setText("File "+msdosFile.getNameFile()+" deleted with success");

//      myProcessManager.sendRunMessage();
    }
  }

  class WriteDirSystem extends MouseAdapter{

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e){
        // pegar o path e o nome do diretorio

        System.out.println("dentro do evento de escrever diretorio");
        String path = textFieldPath.getText().toUpperCase();
        System.out.println("path: "+path);
        String directoryName = textFieldDirectoryName.getText().toUpperCase();
        System.out.println("directoryName: "+directoryName);

        MSDOSFile msdosFile = null;
        String retorno = "";

        try{
           msdosFile = getNewMSDOSFile(directoryName,"", false);
        }catch(HorarioInvalidoException hiex){
           System.out.println("hiex: "+hiex.getMessage());
           retorno = hiex.getMessage();
        }catch(DataInvalidaException diex){
           System.out.println("diex: "+diex.getMessage());
           retorno = diex.getMessage();
        }

        FileSystemData fileSystemData = null;
        try{
           fileSystemData = fileSystemManager.write(1, msdosFile, null, path);
        }catch(MSDOSFATException fatex){
           System.out.println("fatex: "+fatex.getMessage());
           retorno = fatex.getMessage();
        }catch(MSDOSDirectoryException direx){
           System.out.println("direx: "+direx.getMessage());
           retorno = direx.getMessage();
        }

//        Button button;
        if(fileSystemData!=null){
           retorno = fileSystemData.getReturnValue();
           setBlocks(fileSystemData, Color.red);
//           int[] entries = fileSystemData.getEntriesFileFromFAT();
//           int aux = 0;
//           System.out.println("entries.length: "+entries.length);
//           for (int i = 0; i < entries.length; i++) {
//             System.out.println("i: "+i);
//             aux = entries[i];
//             System.out.println("aux: "+aux);
//             if (aux!=-1){ // significa lendo da raiz
//               button = (Button)buttons.elementAt(aux);
//               button.setBackground(Color.red);
//               System.out.println("button: "+button.getLabel());
//             }
//           }
        }

        retorno = "Directory "+msdosFile.getNameFile()+" written with success";
        System.out.println("retorno: "+retorno);
//        System.out.println("Day of Date from the file: "+msdosFile.getLastModificationDate().getDay());
//        System.out.println("Month of Date from the file: "+msdosFile.getLastModificationDate().getMonth());
//        System.out.println("Year of Date from the file: "+msdosFile.getLastModificationDate().getYear());
        textArea.setText(retorno);

//      myProcessManager.sendRunMessage();
    }
  }

  class WriteFileSystem extends MouseAdapter{

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e){
        // pegar o path e o nome do diretorio

        System.out.println("dentro do evento de ler arquivo");
        String path = textFieldPath.getText().toUpperCase();
        System.out.println("path: "+path);
        String fileName = textFieldFileName.getText().toUpperCase();
        System.out.println("directoryName: "+fileName);

        String extensionName = textFieldExtensionName.getText().toUpperCase();
        System.out.println("extensionName: "+extensionName);

        MSDOSFile msdosFile = null;
        String retorno = "";

        try{
           msdosFile = getNewMSDOSFile(fileName,extensionName, true);
        }catch(HorarioInvalidoException hiex){
           System.out.println("hiex: "+hiex.getMessage());
           retorno = hiex.getMessage();
        }catch(DataInvalidaException diex){
           System.out.println("diex: "+diex.getMessage());
           retorno = diex.getMessage();
        }

        String data = textArea.getText();
        FileSystemData fileSystemData = null;
        try{
           fileSystemData = fileSystemManager.write(1, msdosFile, data, path);
        }catch(MSDOSFATException fatex){
           System.out.println("fatex: "+fatex.getMessage());
           retorno = fatex.getMessage();
        }catch(MSDOSDirectoryException direx){
           System.out.println("direx: "+direx.getMessage());
           retorno = direx.getMessage();
        }

//        Button button;
        if(fileSystemData!=null){
           retorno = fileSystemData.getReturnValue();
           setBlocks(fileSystemData, Color.red);
//           int[] entries = fileSystemData.getEntriesFileFromFAT();
//           int aux = 0;
//           System.out.println("entries.length: "+entries.length);
//           for (int i = 0; i < entries.length; i++) {
//             System.out.println("i: "+i);
//             aux = entries[i];
//             System.out.println("aux: "+aux);
//             if (aux!=-1){ // significa lendo da raiz
//               button = (Button)buttons.elementAt(aux);
//               button.setBackground(Color.red);
//               System.out.println("button: "+button.getLabel());
//             }
//           }
        }

        retorno = "File "+msdosFile.getNameFile()+" written with success";
        System.out.println("retorno: "+retorno);
//        System.out.println("Date do file: "+msdosFile.getLastModificationDate().getDay());
        textArea.setText(retorno);

//      myProcessManager.sendRunMessage();
    }
  }

  public MSDOSFile getNewMSDOSFile(String nomeArquivo, String extensao, boolean arquivo)
     throws HorarioInvalidoException, DataInvalidaException{
            java.util.Date dt = new java.util.Date(System.currentTimeMillis());
            Calendar calendario = new GregorianCalendar();
            calendario.setTime(dt);
            int ano = calendario.get(calendario.YEAR);
            int mes = calendario.get(calendario.MONTH);
            int dia = calendario.get(calendario.DAY_OF_MONTH);
            String dataAtual = Data.format(new Data( dia, mes+1, ano),1);

     MSDOSFile msdosFile = new MSDOSFile(nomeArquivo,extensao, arquivo, false, false,
                               false, false, dataAtual,
                               new Horario("00","37","18"), -1, -1, -1);
     return msdosFile;
  }

  public void setBlocks(FileSystemData fileSystemData, Color color)
  {
    boolean flag = false;
    JLabel label = null;
    int[] entries = fileSystemData.getEntriesFileFromFAT();
    int aux = 0;
    System.out.println("entries.length: "+entries.length);
    aux = entries[0];
    if (aux!=-1)
    {
      // significa lendo algo diferente da raiz, q estah no diretory
      // e nao nos blocos do disco
      for (int i = 1; i <= 100; i++)
      {
        System.out.println("i: "+i);
        System.out.println("aux: "+aux);
        flag = false;

        for(int j=0; j<entries.length; j++)
        {
          if (i==entries[j])
          {
          flag = true;
        }
        }
        label = (JLabel) blocks.get(i);
        if(flag)
        {
          label.setBackground(color);
        }
        else
        {
          label.setBackground(Color.lightGray);
        }
      }
    }
  }
}
