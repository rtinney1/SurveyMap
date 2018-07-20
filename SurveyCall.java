import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.Random;
import java.util.Formatter;

public class SurveyCall implements Cloneable
{
	char   initDirection;
	int    degree;
	int    minutes;
	char   turntoDirection;
	double distance;
	String comment;
	String outputString;
	double angleDegrees;
	double angleRadians;
	double deltaX;
	double deltaY;
	static public int   displayModel = 0;
	final static int    RAW_DATA_MODEL = 0;
	final static int    ANGLE_VECTOR_MODEL = 1;
	final static int    FINAL_COORD_MODEL = 2;

	public SurveyCall()
	{
	}

	public SurveyCall(char id, int de, int m, char ttd, double di, String comm)
	{
		initDirection = id;
		degree = de;
		minutes = m;
		turntoDirection = ttd;
		distance = di;
		comment = comm;
	}

	public SurveyCall(DataInputStream dis)
	{
		try
		{
			initDirection = dis.readChar();
			degree = dis.readInt();
			minutes = dis.readInt();
			turntoDirection = dis.readChar();
			distance = dis.readDouble();
			comment = dis.readUTF();
			angleDegrees = this.getAngleInDegrees();
		}
		catch (IOException ie)
		{
			System.out.println("Error Creating Call");
		}
	}

	public void store(DataOutputStream dos)
	{
		try
		{
			dos.writeChar(initDirection);
			dos.writeInt(degree);
			dos.writeInt(minutes);
			dos.writeChar(turntoDirection);
			dos.writeDouble(distance);
			dos.writeUTF(comment);
			System.out.println("Working");
		}
		catch (Exception e)
		{
			System.out.println("Error storing Call");
		}
	}

	public Double getAngleInDegrees()
	{
		if(initDirection == 'S')
		{
			if(turntoDirection == 'E')
				angleDegrees = 270 + degree + (minutes/60);
			else if (turntoDirection == 'W')
				angleDegrees = 270 - degree + (minutes/60);
		}
		else if(initDirection == 'N')
		{
			if(turntoDirection == 'E')
				angleDegrees = 90 - degree + (minutes/60);
			else if(turntoDirection == 'W')
				angleDegrees = 90 + degree + (minutes/60);
		}
		else
			angleDegrees = 0;

		return angleDegrees;
	}

	public Double getAngleInRadians()
	{
		double aD;

		aD = getAngleInDegrees();

		angleRadians = aD * (2 * Math.PI)/360;

		return angleRadians;
	}

	@Override
	public String toString()
	{
		if(displayModel == RAW_DATA_MODEL)
			outputString = String.format("%c %3d %3d %c %5.1f %s", initDirection, degree, minutes, turntoDirection, distance, comment);
		else if(displayModel == ANGLE_VECTOR_MODEL)
			outputString = "" + getAngleInDegrees() + " " + distance;
			//String.format("%4f %3f", getAngleInDegrees(), distance);
		else if(displayModel == FINAL_COORD_MODEL)
			outputString = /*"(" + getDoubleX() + " , " + getDoubleY() + ")";*/ String.format("(%5.2f, %5.2f)", getDoubleX(), getDoubleY());

		return outputString;
	}

	public Double getDoubleX()
	{
		double radians;

		radians = getAngleInRadians();

		deltaX = distance * Math.cos(radians);

		return deltaX;
	}

	public Double getDoubleY()
	{
		double radians;

		radians = getAngleInRadians();

		deltaY = distance * Math.sin(radians);

		return deltaY;
	}

	static void setModel(int x)
	{
		displayModel = x;
	}

	public char getInitDirection()
	{
		return initDirection;
	}

	public char getTurnToDirection()
	{
		return turntoDirection;
	}

	public String getComment()
	{
		return comment;
	}

	public int getDegree()
	{
		 return degree;
	}

	public int getMinutes()
	{
		return minutes;
	}

	public double getDistance()
	{
		return distance;
	}


	static SurveyCall getRandomInstance()
	{
		int n;
		char id, ttd;
		int de, mi;
		double dis;
		Random rand;
		String randString;

		SurveyCall call;

		rand = new Random();

		n = rand.nextInt(2);

		if (n == 0)
		{
			id = 'N';
			randString = "To the Giant Tree";
		}
		else
		{
			id = 'S';
			randString = "To the smallRock";
		}

		n = rand.nextInt(181);

		de = n;

		n = rand.nextInt(61);

		mi = n;

		n = rand.nextInt(2);

		if (n == 0)
			ttd = 'E';
		else
			ttd = 'W';

		n = rand.nextInt(351);

		dis = n;

		call = new SurveyCall(id, de, mi, ttd, dis, randString);

		return call;
	}

	public SurveyCall clone() throws CloneNotSupportedException
	{
		return (SurveyCall) super.clone();
	}
}