package main;

public class Note implements Comparable<Note>
{
	private int color;
	
	private double time;
	
	//
	
	Note(int c, double t)
	{
		color = c;
		time = t;
	}
	
	//
	
	public int getColor()
	{
		return color;
	}
	
	public void setColor(int c)
	{
		color = c;
	}
	
	
	public double getTime()
	{
		return time;
	}
	
	public void setTime(double t)
	{
		time = t;
	}
	
	//
	
	public int compareTo(Note n)
	{
		if(time > n.getTime())
		{
			return 1;
		}
		else if(time < n.getTime())
		{
			return -1;
		}
		else
		{
			if(color > n.getColor())
			{
				return 1;
			}
			else if(color < n.getColor())
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
	}
	
	//
	
	public String toString()
	{
		return color + " " + (Math.round(time * 1000000.0) / 1000000.0);
	}
}
