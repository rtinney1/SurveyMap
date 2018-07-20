import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.event.*;
import java.util.Vector;
import java.io.*;

public class MyDialogBox extends JDialog implements ActionListener
{
	JButton          saveAndCloseButton;
	JButton          saveButton;
	JButton          cancelButton;
	JRadioButton     northRButton;
	JRadioButton     southRButton;
	JRadioButton     eastRButton;
	JRadioButton     westRButton;
	JTextField       degreeTF;
	JTextField       minutesTF;
	JTextField       distanceTF;
	JTextField       commentsTF;
	IntInputVerifier degreeVerifier;
	IntInputVerifier minutesVerifier;
	DoubleInputVerifier distanceVerifier;
	DataManager      list;
	int              indexOfCall;
	MainFrame        mainFrame;

	public MyDialogBox(MainFrame mframe, DataManager dlm)
	{
		super(mframe, "Add Survey Call", ModalityType.APPLICATION_MODAL);

		JPanel      buttonPanel;

		list = dlm;

		mainFrame = mframe;

		saveAndCloseButton = new JButton("Save and Close");
		saveAndCloseButton.addActionListener(this);
		saveAndCloseButton.setActionCommand("SAVECLOSE");

		saveButton = new JButton("Add");
		saveButton.addActionListener(this);
		saveButton.setActionCommand("ADD");

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("CANCEL");
		cancelButton.setVerifyInputWhenFocusTarget(false);

		buttonPanel = new JPanel();
		buttonPanel.add(saveButton);
		buttonPanel.add(saveAndCloseButton);
		buttonPanel.add(cancelButton);

		construct();

		northRButton.setSelected(true);
		eastRButton.setSelected(true);

		this.add(buttonPanel, BorderLayout.SOUTH);

		this.setSize(400, 300);
		this.setVisible(true);
	}

	public MyDialogBox(MainFrame mframe, DataManager dlm, int callIndex, SurveyCall call)
	{
		super(mframe, "Edit Survey Call", ModalityType.APPLICATION_MODAL);

		JPanel      buttonPanel;
		SurveyCall  newCall;
		char        initDirection;
		int         degree;
		int         minutes;
		char        turntoDirection;
		double      distance;
		String      comment;

		list = dlm;
		newCall = call;
		initDirection = newCall.getInitDirection();
		degree = newCall.getDegree();
		minutes = newCall.getMinutes();
		turntoDirection = newCall.getTurnToDirection();
		distance = newCall.getDistance();
		comment = newCall.getComment();

		mainFrame = mframe;

		indexOfCall = callIndex;

		System.out.println("In Constructor of MyDialogBox: " + indexOfCall);

		saveButton = new JButton("Save");
		saveButton.addActionListener(this);
		saveButton.setActionCommand("SAVE");

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("CANCEL");
		cancelButton.setVerifyInputWhenFocusTarget(false);

		buttonPanel = new JPanel();
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);

		construct();

		if (initDirection == 'N')
			northRButton.setSelected(true);
		else
			southRButton.setSelected(true);

		degreeTF.setText("" + degree);

		minutesTF.setText("" + minutes);

		if (turntoDirection == 'E')
			eastRButton.setSelected(true);
		else
			westRButton.setSelected(true);

		distanceTF.setText("" + distance);

		commentsTF.setText(comment);

		this.add(buttonPanel, BorderLayout.SOUTH);

		this.setSize(400, 300);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals("ADD"))
		{
			SurveyCall call;
			call = this.addCall();
			if(call != null)
				list.add(call);
			mainFrame.fileChanged = true;
			mainFrame.plotPanel.repaint();
			mainFrame.printJMI.setEnabled(true);
		}
		else if(ae.getActionCommand().equals("SAVECLOSE"))
		{
			SurveyCall call;
			call = this.addCall();
			if( call != null)
			{
				list.add(call);
				this.dispose();
				mainFrame.fileChanged = true;
				mainFrame.plotPanel.repaint();
				mainFrame.printJMI.setEnabled(true);
			}
		}
		else if(ae.getActionCommand().equals("SAVE"))
		{
			System.out.println("In save in MyDialogBox");
			SurveyCall call;
			call = this.addCall();
			if(call != null)
			{
				System.out.println("In ActionPerformed of MyDialogBox: " + indexOfCall);
				list.replace(call, indexOfCall);
				this.dispose();
				//mainFrame.changedFile();
				mainFrame.fileChanged = true;
				mainFrame.plotPanel.repaint();
			}
		}
		else if(ae.getActionCommand().equals("CANCEL"))
			this.dispose();
	}

	void construct()
	{
		ButtonGroup initRButtonGroup;
		ButtonGroup turnToRButtonGroup;
		JLabel      initDirectionLBL;
		JLabel      degreeLBL;
		JLabel      minutesLBL;
		JLabel      turnToLBL;
		JLabel      distanceLBL;
		JLabel      chainsLBL;
		JLabel      commentsLBL;
		JLabel      blank1LBL;
		JLabel      blank2LBL;
		JPanel      nsPanel;
		JPanel      ewPanel;
		JPanel      everythingElsePanel;
		GroupLayout groupLO;

		initDirectionLBL = new JLabel("Look");

		northRButton = new JRadioButton("North");

		southRButton = new JRadioButton("South");

		nsPanel = new JPanel();
		nsPanel.add(northRButton);
		nsPanel.add(southRButton);

		initRButtonGroup = new ButtonGroup();
		initRButtonGroup.add(northRButton);
		initRButtonGroup.add(southRButton);

		degreeLBL = new JLabel("Turn(angle)");

		degreeVerifier = new IntInputVerifier(0, 91);

		degreeTF = new JTextField();
		degreeTF.setInputVerifier(degreeVerifier);

		degreeTF.setVerifyInputWhenFocusTarget(false);

		minutesLBL = new JLabel("Turn(minutes)");

		minutesVerifier = new IntInputVerifier(0, 60);

		minutesTF = new JTextField();
		minutesTF.setInputVerifier(minutesVerifier);

		turnToLBL = new JLabel("Towards");

		eastRButton = new JRadioButton("East");

		westRButton = new JRadioButton("West");

		ewPanel = new JPanel();
		ewPanel.add(eastRButton);
		ewPanel.add(westRButton);

		turnToRButtonGroup = new ButtonGroup();
		turnToRButtonGroup.add(eastRButton);
		turnToRButtonGroup.add(westRButton);

		distanceLBL = new JLabel("Walk");

		distanceVerifier = new DoubleInputVerifier(0, 999999999);

		distanceTF = new JTextField();
		distanceTF.setInputVerifier(distanceVerifier);

		chainsLBL = new JLabel("in chains");

		commentsLBL = new JLabel("Comments");

		commentsTF = new JTextField();

		everythingElsePanel = new JPanel();
		groupLO = new GroupLayout(everythingElsePanel);
		everythingElsePanel.setLayout(groupLO);

		GroupLayout.SequentialGroup hGroup = groupLO.createSequentialGroup();

		hGroup.addGroup(groupLO.createParallelGroup().
			addComponent(initDirectionLBL).addComponent(degreeLBL).
			addComponent(minutesLBL).addComponent(turnToLBL).
			addComponent(distanceLBL).addComponent(commentsLBL));
		hGroup.addGroup(groupLO.createParallelGroup().
			addComponent(nsPanel).addComponent(degreeTF).
			addComponent(minutesTF).addComponent(ewPanel).
			addComponent(distanceTF).addComponent(commentsTF));
		hGroup.addGroup(groupLO.createParallelGroup().
			addComponent(chainsLBL));
		groupLO.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = groupLO.createSequentialGroup();

		vGroup.addGroup(groupLO.createParallelGroup(GroupLayout.Alignment.BASELINE).
			addComponent(initDirectionLBL).addComponent(nsPanel));
		vGroup.addGroup(groupLO.createParallelGroup(GroupLayout.Alignment.BASELINE).
			addComponent(degreeLBL).addComponent(degreeTF));
		vGroup.addGroup(groupLO.createParallelGroup(GroupLayout.Alignment.BASELINE).
			addComponent(minutesLBL).addComponent(minutesTF));
		vGroup.addGroup(groupLO.createParallelGroup(GroupLayout.Alignment.BASELINE).
			addComponent(turnToLBL).addComponent(ewPanel));
		vGroup.addGroup(groupLO.createParallelGroup(GroupLayout.Alignment.BASELINE).
			addComponent(distanceLBL).addComponent(distanceTF).
			addComponent(chainsLBL));
		vGroup.addGroup(groupLO.createParallelGroup(GroupLayout.Alignment.BASELINE).
			addComponent(commentsLBL).addComponent(commentsTF));
		groupLO.setVerticalGroup(vGroup);

		everythingElsePanel.add(initDirectionLBL);
		everythingElsePanel.add(nsPanel);

		everythingElsePanel.add(degreeLBL);
		everythingElsePanel.add(degreeTF);

		everythingElsePanel.add(minutesLBL);
		everythingElsePanel.add(minutesTF);

		everythingElsePanel.add(turnToLBL);
		everythingElsePanel.add(ewPanel);

		everythingElsePanel.add(distanceLBL);
		everythingElsePanel.add(distanceTF);
		everythingElsePanel.add(chainsLBL);

		everythingElsePanel.add(commentsLBL);
		everythingElsePanel.add(commentsTF);

		this.add(everythingElsePanel, BorderLayout.CENTER);
		pack();
		setUpDialogBox();
		degreeTF.requestFocus();
	}

	SurveyCall addCall()
	{
		SurveyCall call;
		char       initDirection;
		int        degree;
		int        minutes;
		char       turntoDirection;
		double     distance;
		String     comment;
		String     tempString;

		initDirection = 'A';
		turntoDirection = 'B';

		if (northRButton.isSelected())
			initDirection = 'N';
		else if (southRButton.isSelected())
			initDirection = 'S';

		if(eastRButton.isSelected())
			turntoDirection = 'E';
		else if (westRButton.isSelected())
			turntoDirection = 'W';

		comment = commentsTF.getText().trim();

		try
		{
			tempString = degreeTF.getText().trim();
			degree = Integer.parseInt(tempString);

			tempString = minutesTF.getText().trim();
			minutes = Integer.parseInt(tempString);

			tempString = distanceTF.getText().trim();
			distance = Double.parseDouble(tempString);

			call = new SurveyCall(initDirection, degree, minutes, turntoDirection, distance, comment);

			northRButton.setSelected(true);
			degreeTF.setText("");
			minutesTF.setText("");
			eastRButton.setSelected(true);
			distanceTF.setText("");
			commentsTF.setText("");

			degreeTF.requestFocus();

			System.out.println("created call");
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(null, "Some Fields Were Left Blank", "Error", JOptionPane.ERROR_MESSAGE);
			call = null;
		}

		return call;
	}//end addCall()

	private void setUpDialogBox()
	{
		Toolkit tk;
	    Dimension d;

	    tk = Toolkit.getDefaultToolkit();
	    d = tk.getScreenSize();
	    setSize(d.width/2, d.height/2);
	    setLocation(d.width/4, d.width/5);

	    //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    //setTitle("Test");

	    //this.pack();

	    //setVisible(true);
    }//end setupMainFrame()
}

