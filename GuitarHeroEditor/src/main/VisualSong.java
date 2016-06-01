package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.JComponent;

public class VisualSong extends JComponent implements MouseListener, MouseMotionListener
{
	List<Note> notes;
	String songFile;
	
	double songTime;
	double startTime;
	double playerTime;
	
	double bpm;
	double bpmOffset;
	
	double zoom;
	
	double scrollTime;
	boolean scrollGrab;
	
	//
	
	VisualSong()
	{
		notes = new LinkedList<Note>();
		songFile = "";
		
		songTime = 60;
		startTime = 0;
		playerTime = 0;
		
		bpm = 60;
		bpmOffset = 0;
		
		zoom = 1;
		
		scrollTime = 0;
		scrollGrab = false;
		
		setPreferredSize(new Dimension(800, 400));
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		notes.add(new Note(1, 1));
	}
	
	//
	
	public String getSongFile()
	{
		return songFile;
	}
	
	public void setSongFile(String s)
	{
		songFile = s;
		
		try
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(songFile));
			AudioFormat format = audioInputStream.getFormat();
			songTime = (audioInputStream.getFrameLength() + 0.0) / format.getFrameRate();
		}
		catch(Exception e)
		{
			System.out.println("ERROR: Song duration could not be determined.  Song file may be invalid.");
			
			songTime = 60;
		}
		
		repaint();
	}
	
	
	public void setStartTime(double t)
	{
		startTime = t;
		repaint();
	}
	
	
	public void setBpm(double b)
	{
		bpm = b;
		repaint();
	}
	
	
	public void setBpmOffset(double b)
	{
		bpmOffset = b;
		repaint();
	}
	
	
	public void setZoom(double z)
	{
		zoom = z;
		repaint();
	}
	
	//
	
	private int getGlobalDistanceFromTime(double t)
	{
		return (int) (t * 100.0 * zoom);
	}
	
	private int getGlobalDistanceFromLocalDistance(int d)
	{
		return d + getGlobalDistanceFromTime(scrollTime);
	}
	
	private int getLocalDistanceFromTime(double t)
	{
		return (int) getGlobalDistanceFromTime(t) - getGlobalDistanceFromTime(scrollTime);
	}
	
	private int getLocalDistanceFromGlobalDistance(int d)
	{
		return d - getGlobalDistanceFromTime(scrollTime);
	}
	
	private int getBarDistanceFromTime(double t)
	{
		return (int) (((t + 0.0) / songTime) * getWidth());
	}
	
	private int getBarDistanceFromGlobalDistance(int d)
	{
		return (int) ((d + 0.0) / getGlobalDistanceFromTime(songTime) * getWidth());
	}
	
	private double getTimeFromGlobalDistance(int d)
	{
		return (d + 0.0) / (100.0 * zoom);
	}
	
	private double getTimeFromLocalDistance(int d)
	{
		return (d + 0.0 + getGlobalDistanceFromTime(scrollTime)) / (100 * zoom);
	}

	private double getTimeFromBarDistance(int d)
	{
		return ((d + 0.0) / getWidth()) * songTime;
	}
	
	//
	
	public void input(String filePath)
	{
		if(filePath.endsWith(".gth") || filePath.endsWith(".txt"))
		{
			notes.clear();
			
			Scanner scan = new Scanner(filePath);
			
			setSongFile(scan.nextLine());
			
			double lastTime = 0;
			
			while(scan.hasNextLine())
			{
				String temp = scan.nextLine();
				
				notes.add(new Note(Integer.parseInt(temp.split(" ")[0]), Integer.parseInt(temp.split(" ")[1]) - lastTime));
				
				lastTime = Integer.parseInt(temp.split(" ")[1]);
			}
			
			scan.close();
		}
		else if(filePath.endsWith(".wav"))
		{
			notes.clear();
			
			setSongFile(filePath);
		}
		else if(filePath.endsWith(".mp3"))
		{
			System.out.println("ERROR: Only .wav files are supported.");
		}
		else
		{
			System.out.println("ERROR: Invalid import file format.");
		}
		
		repaint();
	}
	
	public void output(String filePath)
	{
		try
		{
			PrintWriter writer = new PrintWriter(filePath, "UTF-8");
			
			writer.print(toString());
			
			writer.close();
		}
		catch(Exception e)
		{
			System.out.println("ERROR: There was a problem writing the file.");
			e.printStackTrace();
		}
	}
	
	//
	
	public void mousePressed(MouseEvent e)
	{
		if(e.getY() > getHeight() - 20)
		{	
			if(e.getX() < getBarDistanceFromTime(scrollTime) || e.getX() > getBarDistanceFromTime(scrollTime) + getBarDistanceFromGlobalDistance(getWidth()))
			{
				scrollTime = getTimeFromBarDistance(e.getX()) - getTimeFromGlobalDistance(getWidth() / 2);
				
				if(scrollTime < 0){scrollTime = 0;}
				if(scrollTime >= songTime - getTimeFromGlobalDistance(getWidth())){scrollTime = songTime - getTimeFromGlobalDistance(getWidth());}
			}
			
			scrollGrab = true;
		}
		else
		{
			Note temp = new Note(1, 0);
			
			temp.setColor((int) Math.round(e.getY() * (6.0 / getHeight())));
			if(temp.getColor() < 1){temp.setColor(1);}
			if(temp.getColor() > 5){temp.setColor(5);}
			
			temp.setTime(getTimeFromLocalDistance(e.getX()));
			
			if(e.getButton() == MouseEvent.BUTTON1)
			{
				boolean check = true;
				
				for(Note n : notes)
				{
					if(n.getColor() == temp.getColor() && Math.abs(getGlobalDistanceFromTime(n.getTime()) - getGlobalDistanceFromTime(temp.getTime())) < 20)
					{
						check = false;
					}
				}
				
				if(check)
				{
					notes.add(temp);
				}
			}
			else
			{
				for(Note n : notes)
				{
					if(n.getColor() == temp.getColor() && Math.abs(getGlobalDistanceFromTime(n.getTime()) - getGlobalDistanceFromTime(temp.getTime())) < 20)
					{
						notes.remove(n);
						
						break;
					}
				}
			}
		}
		
		repaint();
	}
	
	public void mouseReleased(MouseEvent e)
	{
		if(scrollGrab)
		{
			scrollGrab = false;
		}
	}
	
	int lastMouseX;
	
	public void mouseDragged(MouseEvent e)
	{
		if(scrollGrab)
		{
			scrollTime += getTimeFromBarDistance(e.getX() - lastMouseX);
			
			if(scrollTime < 0){scrollTime = 0;}
			if(scrollTime >= songTime - getTimeFromGlobalDistance(getWidth())){scrollTime = songTime - getTimeFromGlobalDistance(getWidth());}
			
			repaint();
		}
		
		lastMouseX = e.getX();
	}
	
	public void mouseMoved(MouseEvent e)
	{
		lastMouseX = e.getX();
	}
	
	public void mouseClicked(MouseEvent arg0){}

	public void mouseEntered(MouseEvent arg0){}

	public void mouseExited(MouseEvent arg0){}
	
	//
	
	public void paintComponent(Graphics g)
	{
		g.setColor(new Color(60, 60, 60));
		g.fillRect(0, 0, getWidth(), getHeight() - 20);
		
		g.setColor(new Color(240, 240, 240));
		g.fillRect(0, getHeight() - 20, getWidth() / 2, 20);
		
		if(getBarDistanceFromGlobalDistance(getWidth()) < getWidth())
		{
			g.setColor(new Color(180, 180, 180));
			g.fillRect(getBarDistanceFromTime(scrollTime) + 2, getHeight() - 18, getBarDistanceFromGlobalDistance(getWidth()) - 2, 16);
			
			g.setColor(new Color(10, 10, 10));
			g.drawRect(getBarDistanceFromTime(scrollTime) + 2, getHeight() - 18, getBarDistanceFromGlobalDistance(getWidth()) - 2, 16);
		}
		
		g.setColor(new Color(80, 80, 80));
		
		for(int i = getGlobalDistanceFromTime(-scrollTime) % getGlobalDistanceFromTime(60 / bpm); i < getWidth(); i += getGlobalDistanceFromTime(60 / bpm))
		{
			g.fillRect(i - 2, 0, 4, getHeight() - 20);
		}
		
		g.setColor(new Color(180, 180, 180));
		g.fillRect(0, (int) ((getHeight() * (1.0 / 6.0))) - 2, getWidth(), 4);
		g.fillRect(0, (int) ((getHeight() * (2.0 / 6.0))) - 2, getWidth(), 4);
		g.fillRect(0, (int) ((getHeight() * (3.0 / 6.0))) - 2, getWidth(), 4);
		g.fillRect(0, (int) ((getHeight() * (4.0 / 6.0))) - 2, getWidth(), 4);
		g.fillRect(0, (int) ((getHeight() * (5.0 / 6.0))) - 2, getWidth(), 4);
		
		for(Note n : notes)
		{
			if(getLocalDistanceFromTime(n.getTime()) < -20 || getLocalDistanceFromTime(n.getTime()) > getWidth() + 20)
			{
				continue;
			}
			
			switch(n.getColor())
			{
			case 1:
				g.setColor(new Color(40, 240, 40));
				break;
			case 2:
				g.setColor(new Color(240, 40, 40));
				break;
			case 3:
				g.setColor(new Color(240, 240, 40));
				break;
			case 4:
				g.setColor(new Color(40, 40, 240));
				break;
			case 5:
				g.setColor(new Color(240, 120, 40));
				break;
			default:
				g.setColor(new Color(240, 240, 240));
				break;
			}
			
			g.fillOval(getLocalDistanceFromTime(n.getTime()) - 20, (int) ((getHeight() * (n.getColor() / 6.0)) - 20), 40, 40);
		}
	}
	
	//
	
	public String toString()
	{
		notes.sort(null);
		
		String ret = songFile;
		
		double lastTime = 0;
		
		for(Note n : notes)
		{
			ret += "\n" + n.getColor() + " " + (n.getTime() - lastTime);
			
			lastTime = n.getTime();
		}
		
		return ret;
	}
}
