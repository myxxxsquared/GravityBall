package gravityball.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONObject;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import gravityball.Program;
import gravityball.game.Scenes;

public class MainWindow extends JFrame {
	
	private AppSettings settings;
	private Scenes scenes;
	
	public MainWindow() {
		settings = new AppSettings(true);
		settings.setResolution(800, 600);
		settings.setSamples(Program.NUMBER_SAMPLES);
		settings.setEmulateMouse(false);
		settings.setFrameRate(Program.FRAME_RATE);
		settings.setUseInput(false);
		
		scenes = new Scenes();
		scenes.setSettings(settings);
		scenes.createCanvas();
		
		scenes.setDisplayStatView(false);
		scenes.setDisplayFps(false);
		
		JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
		ctx.setSystemListener(scenes);
		Dimension dim = new Dimension(640, 480);
		ctx.getCanvas().setPreferredSize(dim);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setSize(800, 600);
		final Canvas c = ctx.getCanvas();
		
		
		JButton button = new JButton("Start!");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!thisenabled)
				{
					getContentPane().add(c, BorderLayout.CENTER);
					scenes.enqueue(new Runnable() {
						@Override
						public void run() {
							scenes.loadFromFile(new JSONObject(readall(".\\map\\map1.json")));
							scenes.gameStart();
						}
					});
				}
				else
				{
					getContentPane().remove(c);
					scenes.gamePause();
				}
				thisenabled  = ! thisenabled;
			}
		});
		
		jLabel = new JLabel("Time:");
		
		getContentPane().add(jLabel, BorderLayout.SOUTH);
		getContentPane().add(button, BorderLayout.NORTH);
	}
	
	boolean thisenabled = false;
	
	public static JLabel jLabel;
	
	static String readall(String filename) {
		String encoding = "utf-8";
		File file = new File(filename);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try(FileInputStream inputStream = new FileInputStream(file)) {
			inputStream.read(filecontent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
