import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Vector;
import java.io.*;

class DoubleInputVerifier extends InputVerifier
{
	double inclusiveMin;
	double inclusiveMax;

	public DoubleInputVerifier(double inclMin, double inclMax)
	{
		inclusiveMin = inclMin;
		inclusiveMax = inclMax;
	}

	public boolean verify(JComponent input)
	{
		double num;
		String str;
		JTextField tf;

		tf = (JTextField)input;
		str = tf.getText().trim();

		if(str.equals(""))
			return true;

		try
		{
			num = Double.parseDouble(str);
			if(num < inclusiveMin)
			{
				JOptionPane.showMessageDialog(null, "Value must be at least " + inclusiveMin, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			else if (num > inclusiveMax)
			{
				JOptionPane.showMessageDialog(null, "Value must be at most " + inclusiveMax, "Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			else
				return true;
		}
		catch(NumberFormatException nfe)
		{
			JOptionPane.showMessageDialog(null, "Value not numeric", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
}