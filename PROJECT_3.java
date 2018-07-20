import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.print.*;
import java.io.*;
import java.lang.String;
//import javax.swing.GroupLayout;

public class PROJECT_3
{
	public static void main (String args[])
	{
		new MainFrame();
	}
}//end main class

class MainFrame extends JFrame implements ActionListener, ListSelectionListener, DropTargetListener
{
	PlotPanel           plotPanel;
	JScrollPane         listScrollPane;
	MyDialogBox         surveyDetailDialog;
	JButton             addButton;
	JButton             editButton;
	JButton             deleteButton;
	JRadioButton        rawDataRB;
	JRadioButton        angleRB;
	JRadioButton        coordRB;
	JList               surveyList;
	SurveyList          listBox;
	int[]               indexOfCallArray;
	int                 indexOfCall;
	boolean             fileChanged;
	boolean             fileOpened;
	boolean             saveEnabled;
	JFileChooser        fileChooser;
	File                inputFL;
	File                outputFL;
	DropTarget          dropTarget;
	JMenuItem           saveJMI;
	JMenuItem           editJMI;
	JMenuItem           deleteJMI;
	JMenuItem           printJMI;
	DataInputStream     dis;
	DataOutputStream    dos;


	public MainFrame()
	{
		Container       maincp;
		JLabel          displayLBL;
		SurveyCall      call;
		JPanel          listPanel;
		JPanel          buttonPanel;
		JPanel          rbPanel;
		ButtonGroup     displayModeBG;
		//GroupLayout     groupLO;

		fileChanged = false;
		fileOpened = false;
		saveEnabled = false;

		fileChooser = new JFileChooser(".");

		addButton = new JButton("Add");
		addButton.addActionListener(this);
		addButton.setActionCommand("ADD");

		editButton = new JButton("Edit");
		editButton.addActionListener(this);
		editButton.setEnabled(false);
		editButton.setActionCommand("EDIT");

		deleteButton = new JButton("Delete");
		deleteButton.addActionListener(this);
		deleteButton.setEnabled(false);
		deleteButton.setActionCommand("DELETE");

		displayLBL = new JLabel("Set Display Mode");

		rawDataRB = new JRadioButton("Raw Data");
		rawDataRB.addActionListener(this);
		rawDataRB.setActionCommand("RAWDATA");
		rawDataRB.setSelected(true);

		angleRB = new JRadioButton("Angle and Vector");
		angleRB.addActionListener(this);
		angleRB.setActionCommand("ANGLE");

		coordRB = new JRadioButton("Final Cooridinates");
		coordRB.addActionListener(this);
		coordRB.setActionCommand("COORD");

		displayModeBG = new ButtonGroup();
		displayModeBG.add(rawDataRB);
		displayModeBG.add(angleRB);
		displayModeBG.add(coordRB);

		dropTarget = new DropTarget(listScrollPane, this);

		System.out.println("Starting");

		listBox = new SurveyList();

		surveyList = new JList(listBox);
		plotPanel = new PlotPanel(surveyList, this);

		surveyList.setFont(new Font("Courier", Font.PLAIN, 12));
			//surveyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		surveyList.addListSelectionListener(this);

		listScrollPane = new JScrollPane(surveyList);

		dropTarget = new DropTarget(this, this);

		editJMI = newItem("Edit", "EDIT", this, KeyEvent.VK_E, KeyEvent.VK_E, "Edit existing call");
		editJMI.setEnabled(false);
		saveJMI = newItem("Save", "SAVE", this, KeyEvent.VK_S, KeyEvent.VK_S, "Save current file");
		saveJMI.setEnabled(false);
		deleteJMI = newItem("Delete", "DELETE", this, KeyEvent.VK_D, KeyEvent.VK_D, "Delete call");
		deleteJMI.setEnabled(false);
		printJMI = newItem("Print", "PRINT", this, KeyEvent.VK_P, KeyEvent.VK_P, "Print Plot");
		printJMI.setEnabled(false);

		setJMenuBar(newMenuBar());

		rbPanel = new JPanel();
		/*groupLO = new GroupLayout(rbPanel);
		rbPanel.setLayout(groupLO);

		GroupLayout.SequentialGroup hGroup = groupLO.createSequentialGroup();

		hGroup.addGroup(groupLO.createParallelGroup().
			addComponent(rawDataRB).addComponent(angleRB).
			addComponent(coordRB));
		groupLO.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = groupLO.createSequentialGroup();

		vGroup.addGroup(groupLO.createParallelGroup(GroupLayout.Alignment.BASELINE).
			addComponent(displayLBL).addComponent(angleRB));
		groupLO.setVerticalGroup(vGroup);*/

		rbPanel.add(displayLBL);
		rbPanel.add(rawDataRB);
		rbPanel.add(angleRB);
		rbPanel.add(coordRB);

		listPanel = new JPanel(new BorderLayout());
		buttonPanel = new JPanel();
		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);

		listPanel.add(rbPanel, BorderLayout.NORTH);
		listPanel.add(listScrollPane, BorderLayout.CENTER);
		listPanel.add(buttonPanel, BorderLayout.SOUTH);

		maincp = getContentPane();
		maincp.add(listPanel, BorderLayout.EAST);
		maincp.add(plotPanel, BorderLayout.CENTER);

		setUpMainFrame();
	}//end constructor

	public void valueChanged(ListSelectionEvent lse)
	{
		indexOfCallArray = surveyList.getSelectedIndices();

		if(indexOfCallArray == null)
		{
			System.out.println("*********************************");
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			plotPanel.editMI.setEnabled(false);
			plotPanel.deleteMI.setEnabled(false);
			deleteJMI.setEnabled(false);
			editJMI.setEnabled(false);
		}
		else if(indexOfCallArray.length > 1)
		{
			editButton.setEnabled(false);
			deleteButton.setEnabled(true);
			plotPanel.editMI.setEnabled(false);
			plotPanel.deleteMI.setEnabled(true);
			deleteJMI.setEnabled(true);
			editJMI.setEnabled(false);
			plotPanel.repaint();
		}
		else
		{
			editButton.setEnabled(true);
			deleteButton.setEnabled(true);
			plotPanel.editMI.setEnabled(true);
			plotPanel.deleteMI.setEnabled(true);
			deleteJMI.setEnabled(true);
			editJMI.setEnabled(true);
			//System.out.println("I'm at line 141" + indexOfCallArray[0]);
			indexOfCall = surveyList.getSelectedIndex();
			plotPanel.repaint();
		}

		if(indexOfCall == -1)
		{
			System.out.println("*********************************");
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			deleteJMI.setEnabled(false);
			editJMI.setEnabled(false);
			plotPanel.repaint();
		}
		System.out.println("In ValueChanged in MainFrame: " + indexOfCall);
	}//end valueChanged

	private JMenuBar newMenuBar()
	{
		JMenuBar menuBar;
		JMenu    subMenu;

		menuBar = new JMenuBar();

		subMenu = new JMenu("File");

		subMenu.add(newItem("New...", "NEW", this, KeyEvent.VK_N, KeyEvent.VK_N, "Create a new file."));
		subMenu.add(newItem("Open...", "OPEN", this, KeyEvent.VK_O, KeyEvent.VK_O, "Open an Existing File."));
		subMenu.add(saveJMI);
		subMenu.add(newItem("Save As", "SAVEAS", this, KeyEvent.VK_W, KeyEvent.VK_W, "Create new save file"));
		subMenu.add(printJMI);

		menuBar.add(subMenu);
		subMenu = new JMenu("Edit");

		subMenu.add(newItem("Add", "ADD", this, KeyEvent.VK_A, KeyEvent.VK_A, "Add new call"));
		subMenu.add(editJMI);
		subMenu.add(deleteJMI);

		menuBar.add(subMenu);

		return menuBar;
	}//end newBar

	private JMenuItem newItem(String label, String actionCommand, ActionListener menuListener, int mnemonic, int keyEvent, String toolTip)
	{
		JMenuItem jmt;

		jmt = new JMenuItem(label, mnemonic);
		jmt.setAccelerator(KeyStroke.getKeyStroke(keyEvent, ActionEvent.ALT_MASK));
		jmt.getAccessibleContext().setAccessibleDescription(toolTip);
		jmt.setActionCommand(actionCommand);
		jmt.addActionListener(menuListener);

		return jmt;
	}//end newItem

	public void changedFile()
	{
		if(fileChanged)
			fileChanged = false;
		else
			fileChanged = true;
	}

	public void dragEnter(DropTargetDragEvent dtde)
	{}

	public void dragExit(DropTargetEvent dtde)
	{}

	public void dragOver(DropTargetDragEvent dtde)
	{}

	public void dropActionChanged(DropTargetDragEvent dtde)
	{}

	public void drop(DropTargetDropEvent dtde)
	{
		int              returnVal;

		if(fileChanged)
		{
			returnVal = JOptionPane.showConfirmDialog(null, "Would You Like To Save Changes?", "", JOptionPane.YES_NO_CANCEL_OPTION);
			if(returnVal == JOptionPane.YES_OPTION)
			{
				try
				{
					if(fileOpened)
					{
						dos = new DataOutputStream(new FileOutputStream(inputFL));
						listBox.store(dos);
					}
					else if(saveEnabled)
					{
						listBox.store(dos);
					}
					else
					{
						saveDialog();
					}

					dropAction(dtde);
					fileChanged = false;
					editButton.setEnabled(false);
					plotPanel.editMI.setEnabled(false);
					plotPanel.deleteMI.setEnabled(false);
					deleteButton.setEnabled(false);
					deleteJMI.setEnabled(false);
					editJMI.setEnabled(false);
					plotPanel.repaint();
				}//end try
				catch(FileNotFoundException fnfe)
				{
					System.out.println("Error in saveDialog: FileNotFound");
				}
			}//end if(JOptionPane ~)
			else if(returnVal == JOptionPane.NO_OPTION)
			{
				dropAction(dtde);
				fileChanged = false;
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
				plotPanel.editMI.setEnabled(false);
				plotPanel.deleteMI.setEnabled(false);
				deleteJMI.setEnabled(false);
				editJMI.setEnabled(false);
				plotPanel.repaint();
			}
		}//end if(fileChanged)
		else
			dropAction(dtde);
	}//end drop

	private void dropAction(DropTargetDropEvent dtde)
	{
		java.util.List<File> fileList;
		Transferable         transferableData;
		DataInputStream      dis;

		transferableData = dtde.getTransferable();

		try
		{
			if(transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrop(DnDConstants.ACTION_COPY);

				fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));

				if(fileList.size() == 1)
				{
					dis = new DataInputStream(new FileInputStream(fileList.get(0)));

					listBox = new SurveyList(dis);

					surveyList.setModel(listBox);
					listScrollPane.repaint();
					fileOpened = true;
					saveJMI.setEnabled(true);
					printJMI.setEnabled(true);
				}
				else
					JOptionPane.showMessageDialog(null, "Cannot open more than one file at a time", "Error", JOptionPane.ERROR_MESSAGE);

			}
			else
				JOptionPane.showMessageDialog(null, "Cannot open more than one file at a time", "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(UnsupportedFlavorException ufe)
		{
			System.out.println("File list flavor not supported");
		}
		catch(IOException ioe)
		{
			System.out.println("I/O Exception");
		}
	}

	public void addCall()
	{
		surveyDetailDialog = new MyDialogBox(this, listBox);
	}

	public void editCall(int n)
	{
		surveyDetailDialog = new MyDialogBox(this, listBox, n, listBox.getElementAt(n));
	}

	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getActionCommand().equals("ADD"))
		{
			addCall();
			//surveyDetailDialog.createDialog(this, "Add new calls");
		}//end ADD
		else if (ae.getActionCommand().equals("EDIT"))
		{
			editCall(indexOfCall);
		}//end EDIT
		else if (ae.getActionCommand().equals("DELETE"))
		{
			deleteCall();
		}//end DELETE
		else if (ae.getActionCommand().equals("NEW"))
		{
			newFile();
		}//end NEW
		else if (ae.getActionCommand().equals("OPEN"))
		{
			openFile();
		}//end OPEN
		else if (ae.getActionCommand().equals("SAVE"))
		{
			try
			{
				if(fileOpened)
					dos = new DataOutputStream(new FileOutputStream(inputFL));

				listBox.store(dos);

				fileChanged = false;
			}
			catch(FileNotFoundException fnfe)
			{
				System.out.println("Error in saveDialog: FileNotFound");
			}
		}//end SAVE
		else if (ae.getActionCommand().equals("SAVEAS"))
		{
			saveDialog();

			saveJMI.setEnabled(true);
			saveEnabled = true;
			fileOpened = false;

			fileChanged = false;
		}//end SAVEAS
		else if(ae.getActionCommand().equals("PRINT"))
		{
			PrinterJob pj;
			PageFormat pageFormat;

			try
			{
				pj = PrinterJob.getPrinterJob();
				pageFormat = pj.pageDialog(pj.defaultPage());
				pj.setPrintable(plotPanel, pageFormat);

				if(pj.printDialog())
				{
					System.out.println("Printing...");
					pj.print();
				}
			}
			catch(PrinterException pe)
			{
				System.out.println("Error with printing...");
			}
		}
		else if (ae.getActionCommand().equals("RAWDATA"))
		{
			SurveyCall.setModel(SurveyCall.RAW_DATA_MODEL);
			listScrollPane.repaint();
		}
		else if (ae.getActionCommand().equals("ANGLE"))
		{
			SurveyCall.setModel(SurveyCall.ANGLE_VECTOR_MODEL);
			listScrollPane.repaint();
		}
		else if (ae.getActionCommand().equals("COORD"))
		{
			SurveyCall.setModel(SurveyCall.FINAL_COORD_MODEL);
			listScrollPane.repaint();
		}
	}//end actionPerformed

	public void deleteCall()
	{
		if(indexOfCallArray.length > 1)
		{
			for(int i = indexOfCallArray.length - 1; i > -1; i--)
			{
				indexOfCall = indexOfCallArray[i];
				listBox.removeElementAt(indexOfCall);
			}
			if(indexOfCallArray.length == listBox.getSize())
				printJMI.setEnabled(false);
		}
		else
			listBox.removeElementAt(indexOfCall);

		fileChanged = true;

		listScrollPane.repaint();
		plotPanel.repaint();
	}

	private void openFile()
	{
		int returnVal;

		if(fileChanged)
		{
			returnVal = JOptionPane.showConfirmDialog(null, "Would You Like To Save Changes?", "", JOptionPane.YES_NO_CANCEL_OPTION);
			if(returnVal == JOptionPane.YES_OPTION)
			{
				try
				{
					if(fileOpened)
					{
						dos = new DataOutputStream(new FileOutputStream(inputFL));
						listBox.store(dos);
					}
					else if(saveEnabled)
					{
						listBox.store(dos);
					}
					else
					{
						saveDialog();
					}

					openDialog();
					fileChanged = false;
					editButton.setEnabled(false);
					deleteButton.setEnabled(false);
					plotPanel.editMI.setEnabled(false);
					plotPanel.deleteMI.setEnabled(false);
					deleteJMI.setEnabled(false);
					editJMI.setEnabled(false);
					printJMI.setEnabled(true);
				}//end try
				catch(FileNotFoundException fnfe)
				{
					System.out.println("Error in saveDialog: FileNotFound");
				}
			}//end if(JOptionPane ~)
			else if(returnVal == JOptionPane.NO_OPTION)
			{
				fileChanged = false;
				openDialog();
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
				plotPanel.editMI.setEnabled(false);
				printJMI.setEnabled(true);
				plotPanel.deleteMI.setEnabled(false);
				deleteJMI.setEnabled(false);
				editJMI.setEnabled(false);
			}
		}//end if(fileChanged)
		else
		{
			openDialog();
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			plotPanel.editMI.setEnabled(false);
			plotPanel.deleteMI.setEnabled(false);
			printJMI.setEnabled(true);
			deleteJMI.setEnabled(false);
			editJMI.setEnabled(false);
		}
	}

	private void saveDialog()
	{
		int returnVal;
		String file;

		returnVal = fileChooser.showSaveDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			System.out.println("You chose to save to this file: " + fileChooser.getSelectedFile().getName());
			file = fileChooser.getSelectedFile().getName();

			if(!file.endsWith(".bin"))
				file += ".bin";

			outputFL = new File(file);

			try
			{
				dos = new DataOutputStream(new FileOutputStream(outputFL));

				listBox.store(dos);
			}
			catch(FileNotFoundException fnfe)
			{
				System.out.println("Error in saveDialog: FileNotFound");
			}
		}
	}

	private void openDialog()
	{
		int returnVal;

		returnVal = fileChooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			System.out.println("You chose to open this file: " + fileChooser.getSelectedFile().getName());
			inputFL = fileChooser.getSelectedFile();

			try
			{
				dis = new DataInputStream(new FileInputStream(inputFL));

				listBox = new SurveyList(dis);

				surveyList.setModel(listBox);
				listScrollPane.repaint();

				fileOpened = true;
				saveJMI.setEnabled(true);
				plotPanel.repaint();
			}
			catch(FileNotFoundException fnfe)
			{
				System.out.println("Error in saveDialog: FileNotFound");
			}
		}
	}

	private void newFile()
	{
		int returnVal;

		if(fileChanged)
		{
			returnVal = JOptionPane.showConfirmDialog(null, "Would You Like To Save Changes?", "", JOptionPane.YES_NO_CANCEL_OPTION);
			if(returnVal == JOptionPane.YES_OPTION)
			{
				try
				{
					if(fileOpened)
					{
						dos = new DataOutputStream(new FileOutputStream(inputFL));
						listBox.store(dos);
					}
					else if(saveEnabled)
					{
						listBox.store(dos);
					}
					else
					{
						saveDialog();
					}

					fileOpened = false;
					saveEnabled = false;
					fileChanged = false;
					saveJMI.setEnabled(false);
					editButton.setEnabled(false);
					deleteButton.setEnabled(false);
					plotPanel.editMI.setEnabled(false);
					plotPanel.deleteMI.setEnabled(false);
					deleteJMI.setEnabled(false);
					editJMI.setEnabled(false);
					printJMI.setEnabled(false);

					System.out.println("Creating new list; chose to save");
					listBox.clear();

					listScrollPane.repaint();
					plotPanel.repaint();
				}//end try
				catch(FileNotFoundException fnfe)
				{
					System.out.println("Error in saveDialog: FileNotFound");
				}
			}//end if(~)
			else if(returnVal == JOptionPane.NO_OPTION)
			{
				fileOpened = false;
				saveEnabled = false;
				fileChanged = false;
				saveJMI.setEnabled(false);
				editButton.setEnabled(false);
				deleteButton.setEnabled(false);
				plotPanel.editMI.setEnabled(false);
				plotPanel.deleteMI.setEnabled(false);
				deleteJMI.setEnabled(false);
				editJMI.setEnabled(false);

				System.out.println("Creating new list; chose not to save");
				listBox.clear();

				listScrollPane.repaint();
			}//end else if
		}//end if(fileChanged)
		else
		{
			fileOpened = false;
			saveEnabled = false;
			fileChanged = false;
			saveJMI.setEnabled(false);
			editButton.setEnabled(false);
			deleteButton.setEnabled(false);
			plotPanel.editMI.setEnabled(false);
			plotPanel.deleteMI.setEnabled(false);
			deleteJMI.setEnabled(false);
			editJMI.setEnabled(false);

			System.out.println("Creating new list; current list either empty or already saved");
			listBox.clear();

			listScrollPane.repaint();
		}
	}

	private void setUpMainFrame()
	{
		Toolkit tk;
	    Dimension d;

	    tk = Toolkit.getDefaultToolkit();
	    d = tk.getScreenSize();
	    setSize(d.width/2, d.height/2);
	    setLocation(d.width/4, 0);

	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    setTitle("Test");

	    //this.pack();

	    setVisible(true);
    }//end setupMainFrame()
}