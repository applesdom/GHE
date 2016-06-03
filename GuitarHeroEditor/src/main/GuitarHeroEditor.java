package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class GuitarHeroEditor
{
	static JFrame frame = new JFrame("Guitar Hero Editor");
	
	static JPanel panel = new JPanel();
	
	static JButton playButton = new JButton("START");
	
	static JPanel infoPanel = new JPanel();
	
	static JTextField songFileField = new JTextField();
	static JTextField bpmField = new JTextField("60");
	static JTextField bpmOffsetField = new JTextField("0.0");
	static JTextField zoomField = new JTextField("1.0");
	
	static JButton importButton = new JButton("IMPORT");
	static JButton exportButton = new JButton("EXPORT");
	
	static VisualSong song = new VisualSong();
	
	static JFileChooser fileChooser = new JFileChooser();
	
	public static void main(String[] args)
	{
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		//
		
		panel.setPreferredSize(new Dimension(800, 100));
		panel.setLayout(new GridLayout(1, 4, 5, 0));
		frame.add(panel, BorderLayout.NORTH);
		
		//
		
		playButton.setActionCommand("start");
		playButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{	
		        if(e.getActionCommand().equals("start"))
		        {
		        	song.playSong();
		        	
		        	playButton.setActionCommand("stop");
		        	playButton.setText("STOP");
		        	
		        	song.requestFocusInWindow();
		        }
		        else
		        {
		        	song.stopSong();
		        	
		        	playButton.setActionCommand("start");
		        	playButton.setText("START");
		        }
			}
		});
		panel.add(playButton);
		
		//
		
		infoPanel.setLayout(new GridLayout(4, 2, 5, 5));
		panel.add(infoPanel);
		
		//
		
		infoPanel.add(new JLabel("Song file:", JLabel.CENTER));
		songFileField.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					song.setSongFile(songFileField.getText());
				}
			}

			public void keyReleased(KeyEvent arg0){}
			public void keyTyped(KeyEvent arg0){}
		});
		infoPanel.add(songFileField);
		
		//
		
		infoPanel.add(new JLabel("BPM:", JLabel.CENTER));
		bpmField.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					try
					{
						song.setBpm(Double.parseDouble(bpmField.getText()));
					}
					catch(Exception e1)
					{
						System.out.println("ERROR: Invalid BPM input.  Enter a double.");
					}
				}
			}

			public void keyReleased(KeyEvent arg0){}
			public void keyTyped(KeyEvent arg0){}
		});
		infoPanel.add(bpmField);
		
		//
		
		infoPanel.add(new JLabel("BPM offset:", JLabel.CENTER));
		bpmOffsetField.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					try
					{
						song.setBpmOffset(Double.parseDouble(bpmOffsetField.getText()));
					}
					catch(Exception e1)
					{
						System.out.println("ERROR: Invalid BPM offset input.  Enter a double.");
					}
				}
			}

			public void keyReleased(KeyEvent arg0){}
			public void keyTyped(KeyEvent arg0){}
		});
		infoPanel.add(bpmOffsetField);
		
		//
		
		infoPanel.add(new JLabel("Zoom:", JLabel.CENTER));
		zoomField.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					try
					{
						song.setZoom(Double.parseDouble(zoomField.getText()));
					}
					catch(Exception e1)
					{
						System.out.println("ERROR: Invalid zoom input.  Enter a double.");
					}
				}
			}

			public void keyReleased(KeyEvent arg0){}
			public void keyTyped(KeyEvent arg0){}
		});
		infoPanel.add(zoomField);
		
		//
		
		importButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
		        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
		        {
		        	String filePath = fileChooser.getSelectedFile().getAbsolutePath();
		        	
		            song.input(filePath);
		            
		            songFileField.setText(song.getSongFile());
		        }
			}
		});
		panel.add(importButton);
		
		//
		
		exportButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
		        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION)
		        {
		        	String filePath = fileChooser.getSelectedFile().getAbsolutePath();
		        	
		            song.output(filePath);
		        }
			}
		});
		panel.add(exportButton);
		
		//
		
		song.setPreferredSize(new Dimension(800, 400));
		frame.add(song, BorderLayout.CENTER);
		
		//
		
		frame.pack();
	}
}
