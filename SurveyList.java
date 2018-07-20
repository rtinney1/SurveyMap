import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Vector;
import java.io.*;

public class SurveyList extends DefaultListModel<SurveyCall> implements DataManager
{
	public SurveyList()
	{
	}

	public SurveyList(DataInputStream dis)
	{
		int numCalls;
		SurveyCall call;

		try
		{
			numCalls = dis.readInt();

			for(int i = 0; i < numCalls; i++)
			{

				call = new SurveyCall(dis);
				addElement(call);
			}
		}
		catch(IOException ioe)
		{
			System.out.println("Error in SurveyList Constructor");
		}
	}

	void store(DataOutputStream dos)
	{
		int numCalls;

		try
		{
			numCalls = this.size();

			dos.writeInt(numCalls);

			for(int i = 0; i < numCalls; i++)
			{
				getElementAt(i).store(dos);
				//System.out.println("hello");
			}
		}
		catch(IOException ioe)
		{
			System.out.println("Error in SurveyList store()");
		}
	}

	public void add(SurveyCall call)
	{
		addElement(call);
	}

	public void replace(SurveyCall call, int indexOfCall)
	{
		System.out.println("In Replace of SurveyList: " + indexOfCall);
		removeElementAt(indexOfCall);
		System.out.println("Original Call removed");
		insertElementAt(call, indexOfCall);
		System.out.println("Inserted new call");
	}
}