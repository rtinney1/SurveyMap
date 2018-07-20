import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Formatter;

public class PlotPanel extends JPanel implements MouseListener, Printable, ActionListener
{
	JList      surveyList;
	JPopupMenu popupMenu;
	MainFrame  mainFrame;
	JMenuItem  addMI;
	JMenuItem  editMI;
	JMenuItem  deleteMI;
	SurveyList surveyListModel;
	int[]      xPixelCoordArray;
	int[]      yPixelCoordArray;
	int        locationOfPoint;

	public PlotPanel(JList list, MainFrame fr)
	{
		surveyList = list;

		popupMenu = new JPopupMenu();
		mainFrame = fr;

		addMI = new JMenuItem("Add");
		addMI.addActionListener(this);
		addMI.setActionCommand("ADD");
		popupMenu.add(addMI);

		editMI = new JMenuItem("Edit");
		editMI.addActionListener(this);
		editMI.setActionCommand("EDIT");
		editMI.setEnabled(false);
		popupMenu.add(editMI);

		deleteMI = new JMenuItem("Delete");
		deleteMI.addActionListener(this);
		deleteMI.setActionCommand("DELETE");
		deleteMI.setEnabled(false);
		popupMenu.add(deleteMI);

		this.addMouseListener(this);
	}

	@Override
	public void paintComponent(Graphics g1)
	{
		super.paintComponent(g1);

		Graphics2D g2;
		double     width;
		double     height;

		g2 = (Graphics2D)g1;

		width = this.getWidth();
		height = this.getHeight();

		System.out.println("Inside paintComponent");

		if(surveyList != null)
			drawPlot(g2, width, height);
	}

	public int print(Graphics g1, PageFormat pf, int pageIndex)
	{
		Graphics2D g2;

		if(pageIndex > 0)
			return java.awt.print.Printable.NO_SUCH_PAGE;

		System.out.println("Page index: " + pageIndex);
		System.out.println("ImageableMargins: " + pf.getImageableX() + " " + pf.getImageableY());
		System.out.println("Imageeable width/height " + pf.getImageableWidth() + " " + pf.getImageableHeight());

		g2 = (Graphics2D) g1;

		g2.translate(pf.getImageableX(), pf.getImageableY());

		System.out.println("Printing Image");

		if(surveyList != null)
			drawPlot(g2, pf.getImageableWidth(), pf.getImageableHeight());
		else
			return java.awt.print.Printable.NO_SUCH_PAGE;

		return java.awt.print.Printable.PAGE_EXISTS;
	}

	public void drawPlot(Graphics2D g, double w, double h)
	{
		double     minX = 0;
		double     maxX = 0;
		double     minY = 0;
		double     maxY = 0;
		double     scalar;
		double     xOffset;
		double     yOffset;
		double     diffX;
		double     diffY;
		double     currX;
		double     currY;
		double     width;
		double     height;
		double     perimeter = 0;
		Graphics2D plotLine;
		int[]      selectedListIndicesArray;
		int        x = 0;//integer to progress through selectedListIndicesArray[];
		double[]   xCoordArrayForArea;
		double[]   yCoordArrayForArea;
		double     xProduct;
		double     yProduct;
		double     area;
		final int  MARGIN = 10;
		String     perimeterString;
		String     areaString;

		SurveyCall call;
		surveyListModel = (SurveyList)surveyList.getModel();

		width = w;
		height = h;
		plotLine = g;

		currX = 0;
		currY = 0;
		xProduct = 0;
		yProduct = 0;
		area = 0;

		System.out.println("Height: " + height);

		xPixelCoordArray = new int[surveyListModel.getSize() + 2];
		yPixelCoordArray = new int[surveyListModel.getSize() + 2];
		xCoordArrayForArea = new double[surveyListModel.getSize() + 1];
		yCoordArrayForArea = new double[surveyListModel.getSize() + 1];

		System.out.println("There is something inside the list");
		System.out.println("Size of surveyListModel is: " + surveyListModel.getSize());
		if(surveyListModel.getSize() != 0)
		{
			for(int i = 0; i < surveyListModel.getSize(); i++)//finds minX, maxX, minY, maxY
			{
				call = surveyListModel.getElementAt(i);

				currX = currX + call.getDoubleX();
				currY = currY + call.getDoubleY();

				xCoordArrayForArea[i] = currX;
				yCoordArrayForArea[i] = currY;
				System.out.println("xCoord: " + currX);
				System.out.println("yCoord: " + currY);

				perimeter = perimeter + call.getDistance();

				if(minX > currX)
					minX = currX;
				else if(maxX < currX)
					maxX = currX;

				if(minY > currY)
					minY = currY;
				else if(maxY < currY)
					maxY = currY;
			}

			perimeter = perimeter * 66;

			diffX = maxX - minX;
			diffY = maxY - minY;

			//calculate offset and scalar
			if((width/diffX) > (height/diffY))
			{
				System.out.println("yOffset");
				yOffset = MARGIN;
				scalar = (height - (2*MARGIN))/diffY;
				xOffset = ((width - (diffX * scalar))/2) + MARGIN;
			}
			else
			{
				System.out.println("xOffset");
				xOffset = MARGIN;
				scalar = (width - (2*MARGIN))/diffX;
				yOffset = ((height - (diffY * scalar))/2) + MARGIN;
			}

			//System.out.println("scalar: " + scalar);
			//System.out.println("yOffset: " + yOffset + " xOffset: " + xOffset);
			//System.out.println("Width: " + width + " height: " + height);

			xPixelCoordArray[0] = (int)(xOffset - scalar*minX);
			yPixelCoordArray[0] = (int)(yOffset - scalar*minY);
			System.out.println("1st and last xCoord: " + xPixelCoordArray[0]);
			System.out.println("1st and last yCoord: " + yPixelCoordArray[0]);
			//xCoordArrayForArea[0] = 0;
			//yCoordArrayForArea[0] = 0;

			xPixelCoordArray[surveyListModel.getSize() + 1] = (int)(xOffset - scalar*minX);
			yPixelCoordArray[surveyListModel.getSize() + 1] = (int)(yOffset - scalar*minY);
			//xCoordArrayForArea[surveyListModel.getSize() + 1] = 0;
			//yCoordArrayForArea[surveyListModel.getSize() + 1] = 0;

			System.out.println("Preparing to calculate pixel coords");

			for(int n = 0; n < surveyListModel.getSize(); n++)
			{
				xPixelCoordArray[n+1] = (int)((scalar * xCoordArrayForArea[n]) + xOffset - scalar*minX);
				yPixelCoordArray[n+1] = (int)((scalar * yCoordArrayForArea[n]) + yOffset - scalar*minY);
				System.out.println("Calculated xPixelCoord: " + xPixelCoordArray[n+1]);
				System.out.println("Calculated yPixelCoord: " + yPixelCoordArray[n+1]);
			}

			for(int n = 0; n < surveyListModel.getSize(); n++)
			{
				//System.out.println("Current xProduct: " + xProduct);
				//System.out.println("Current yProduct: " + yProduct);
				//System.out.println("Adding " + xCoordArrayForArea[n] + " * " + yCoordArrayForArea[n+1] + " = " + (xCoordArrayForArea[n] * yCoordArrayForArea[n+1]) + " to xProduct");
				//System.out.println("Adding " + yCoordArrayForArea[n] + " * " + xCoordArrayForArea[n+1] + " = " + (yCoordArrayForArea[n] * xCoordArrayForArea[n+1]) + " to yProduct");

				xProduct = xProduct + (xCoordArrayForArea[n] * yCoordArrayForArea[n+1]);
				yProduct = yProduct + (yCoordArrayForArea[n] * xCoordArrayForArea[n+1]);
			}

			System.out.println("Current xProduct: " + xProduct);
			System.out.println("Current yProduct: " + yProduct);

			area = Math.abs((yProduct - xProduct)/2);
			area = area/10;

			plotLine.setColor(Color.BLACK);

			perimeterString = String.format("Perimeter: %6.2f feet", perimeter);
			areaString = String.format("Area: %6.2f acre", area);
			plotLine.drawString(perimeterString, 15, (int)height - 15);
			plotLine.drawString(areaString, 15, (int)height - 25);

			selectedListIndicesArray = surveyList.getSelectedIndices();
			System.out.println("Preparing to draw plot");
			System.out.println("locationOfPoint " + locationOfPoint);
			for(int i = 0; i < surveyListModel.getSize() + 1; i++)
			{
				if(x < selectedListIndicesArray.length && selectedListIndicesArray != null && i == selectedListIndicesArray[x])
				{
					System.out.println("Selected Index is: " + selectedListIndicesArray[x]);
					System.out.println("Line drawn is from coord spots" + i + " and " + (i+1));
					plotLine.setColor(Color.RED);
					if(i == selectedListIndicesArray[x])
						x++;
				}
				else
				{
					plotLine.setColor(Color.BLACK);
					System.out.println("painting line");
				}

				plotLine.drawLine((int)xPixelCoordArray[i], (int)(height - yPixelCoordArray[i]), (int)xPixelCoordArray[i+1], (int)(height - yPixelCoordArray[i+1]));
				System.out.println("Drawing from " + (int)xPixelCoordArray[i] + "," + (int)(height - yPixelCoordArray[i]) + " to " + (int)xPixelCoordArray[i+1] + "," + (int)(height - yPixelCoordArray[i+1]));
			}
			//plotLine.drawLine((int)xPixelCoordArray[surveyListModel.getSize() + 1], (int)yPixelCoordArray[surveyListModel.getSize() + 1], (int)xPixelCoordArray[0], (int)yPixelCoordArray[i+1]);
		}//end beginning if
	}

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals("ADD"))
			mainFrame.addCall();
		else if(ae.getActionCommand().equals("EDIT"))
			mainFrame.editCall(locationOfPoint);
		else if(ae.getActionCommand().equals("DELETE"))
		{
			surveyListModel.removeElementAt(locationOfPoint);
			mainFrame.fileChanged = true;
			this.repaint();
		}
	}

	public void mouseClicked(MouseEvent e)
	{
		double distanceOfClosest = 99999999;
		double tempDistance;

		System.out.println("Size of xPixelArray: " + xPixelCoordArray.length );
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			System.out.println("You clicked right");

			for(int n = 0; n < xPixelCoordArray.length - 2; n++)
			{
				System.out.println("In a mouseevent " + n);
				tempDistance = distanceToLine(xPixelCoordArray[n], yPixelCoordArray[n], xPixelCoordArray[n+1], yPixelCoordArray[n+1], e.getX(), (this.getHeight() - e.getY()));
				System.out.println("tempDistance " + tempDistance);
				if(tempDistance < distanceOfClosest)
				{
					distanceOfClosest = tempDistance;
					locationOfPoint = n;
				}
			}

			surveyList.setSelectedIndex(locationOfPoint);

			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}//end if rightButtonClick
		else if(e.getButton() == MouseEvent.BUTTON1)
		{
			System.out.println("You clicked left");

			for(int n = 0; n < xPixelCoordArray.length - 2; n++)
			{
				System.out.println("In a mouseevent " + n);
				tempDistance = distanceToLine(xPixelCoordArray[n], yPixelCoordArray[n], xPixelCoordArray[n+1], yPixelCoordArray[n+1], e.getX(), (this.getHeight() - e.getY()));
				System.out.println("tempDistance " + tempDistance);
				if(tempDistance < distanceOfClosest)
				{
					distanceOfClosest = tempDistance;
					locationOfPoint = n;
				}
			}

			surveyList.setSelectedIndex(locationOfPoint);

			System.out.println("locationOfPoint " + locationOfPoint);
			this.repaint();
		}
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	private double distanceToLine(double a, double b, double c, double d, double x, double y)
	{
		double m1;
		double m2;
		double p;
		double q;
		double distance;

		System.out.println("" + a + " " + b + " " + c + " " + d + " " + x + " " + y);
		if(a == c && b == d)
		{
			System.out.println("*********");
			throw new IllegalArgumentException("The two points don't determine a line.");
		}
		else if(Math.abs(a - c) < 0.0001)
		{
			System.out.println("************///*************");
			distance = x - a;
		}
		else if(Math.abs(b - d) < 0.0001)
		{
			System.out.println("*******asdfaadfds**");
			distance = y - b;
		}
		else
		{
			m1 = (d-b)/(c-a);
			m2 = (a-c)/(d-b);
			p = (m2*x - m1*a + b-y)/(m2 - m1);
			q = m2*(p-x) + y;

			distance = Math.hypot(x-p, y-q);
		}
	//}
		//else
		//	distance = 99999;

		return Math.abs(distance);
	}
}