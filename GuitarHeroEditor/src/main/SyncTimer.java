package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class SyncTimer
{
	private int interval;
	private int resolution;
	private ActionListener action;
	
	private Timer timer;
	private long lastTime;
	private long now;
	private long delta;
	
	//
	
	SyncTimer(int i, ActionListener a)
	{
		interval = i;
		resolution = 1;
		action = a;
		
		lastTime = System.nanoTime();
		delta = 0;
		
		initTimer();
	}
	
	SyncTimer(int i, int r, ActionListener a)
	{
		interval = i;
		resolution = r;
		action = a;
		
		initTimer();
	}
	
	//
	
	private void initTimer()
	{
		timer = new Timer(resolution, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				now = System.nanoTime();
				delta += now - lastTime;
				lastTime = now;
				
				while(delta >= interval * 1000000)
				{
					action.actionPerformed(e);
					
					delta -= (interval * 1000000);
				}
			}
		});
	}
	
	//
	
	public void start()
	{
		lastTime = System.nanoTime();
		delta = 0;
		
		timer.start();
	}
	
	public void stop()
	{
		timer.stop();
	}
}
