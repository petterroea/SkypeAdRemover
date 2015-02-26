package com.petterroea.skypeadremover;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SkypeAdRemoverGui {
	
	public File targetFile = new File("C:\\Windows\\System32\\drivers\\etc\\hosts");
	public String dataToInject = "127.0.0.1 rad.msn.com";
	
	private JFrame frame;
	public SkypeAdRemoverGui()
	{
		frame = new JFrame("Skype ad remover");
		frame.setSize(300, 200);
		frame.setResizable(false);
		frame.setLayout(new GridBagLayout());
		//Grid bag constraints
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		//Title
		JLabel title = new JLabel("Skype ad remover");
		title.setFont(title.getFont().deriveFont(25.0f));
		frame.add(title, c);
		//Button
		if(isInstalled()) {
			JButton button = new JButton("Uninstall hack");
			button.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					uninstall();
					showFinishedDialog("The hack has been uninstalled. \nYou need to restart skype for the changes to take effect. \nWhat do you want to do?");
				}});
			c.gridy = 1;
			frame.add(button, c);
			//Status label
			JLabel status = new JLabel("Skype ads are currently disabled.\nPress to revert.", SwingConstants.CENTER);
			c.gridy = 2;
			frame.add(status, c);
		} else {
			JButton button = new JButton("Remove skype ads");
			button.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					install();
					showFinishedDialog("The hack has been installed. \nYou need to restart skype for the changes to take effect. \nWhat do you want to do?");
				}});
			c.gridy = 1;
			frame.add(button, c);
			//Status label
			JLabel status = new JLabel("Press the button to begin", SwingConstants.CENTER);
			c.gridy = 2;
			frame.add(status, c);
		}
		
		c.gridy=3;
		JLabel disclaimer = new JLabel("All use of this software is at own risk.", SwingConstants.CENTER);
		frame.add(disclaimer, c);
		
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static boolean isAdmin(){
	    Preferences prefs = Preferences.systemRoot();
	    try{
	        prefs.put("foo", "bar"); // SecurityException on Windows
	        prefs.remove("foo");
	        prefs.flush(); // BackingStoreException on Linux
	        return true;
	    }catch(Exception e){
	        return false;
	    }
	}
	
	public void showFinishedDialog(String customText) {
		Object[] options = {"Kill skype",
                "Restart pc",
                "Nothing"};
		int n = JOptionPane.showOptionDialog(frame,
			    customText,
			    "Message",
			    JOptionPane.YES_NO_CANCEL_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    options[2]);
		if(n==0) {
			try {
				Runtime.getRuntime().exec("taskkill /F /IM Skype.exe");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
					    "There was an error killing skype: \n"+e.toString(),
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else if(n==1) {
			 Runtime r=Runtime.getRuntime();
			 try {
				r.exec("shutdown /r /t 0");
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
					    "There was an error shutting down: \n"+e.toString(),
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	
	public static boolean checkCompatability()
	{
		if(!System.getProperty("os.name").toLowerCase().contains("windows")) {
			System.out.println(System.getProperty("os.name"));
			JOptionPane.showMessageDialog(null,
				    "This program is only designed for the Windows version of skype, sorry",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if(!isAdmin())
		{
			JOptionPane.showMessageDialog(null,
				    "This program needs to be ran as administrator!",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	public boolean isInstalled()
	{
		String[] data = com.petterroea.util.FileUtils.readFile(targetFile).split("\n");
		//Read through each line and check if it contains our injectable data
		for(int i = 0; i < data.length; i++) {
			if(data[i].equalsIgnoreCase(dataToInject))
				return true;
		}
		
		return false;
	}
	
	public void install()
	{
		String[] data = com.petterroea.util.FileUtils.readFile(targetFile).split("\n");
		//To avoid errors, we rebuild the file per line so we are sure we arent pasting our data onto a line with something else
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < data.length; i++) {
			sb.append(data[i]);
			sb.append("\n");
		}
		sb.append(dataToInject);
		com.petterroea.util.FileUtils.writeFile(targetFile, sb.toString());
	}
	
	public void uninstall()
	{
		String[] data = com.petterroea.util.FileUtils.readFile(targetFile).split("\n");
		//Rebuild file ignoring lines containing the data we injected
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < data.length; i++) {
			if(!data[i].equalsIgnoreCase(dataToInject))
			{
				sb.append(data[i]);
				sb.append("\n");
			}
		}
		com.petterroea.util.FileUtils.writeFile(targetFile, sb.toString());
	}
	
	public static void main(String[] args) {
		if(checkCompatability())
			new SkypeAdRemoverGui();
	}
}
