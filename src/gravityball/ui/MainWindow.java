package gravityball.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import gravityball.game.Scenes;

public class MainWindow extends JFrame {
	
	private AppSettings settings;
	private Scenes scenes;
	
	public MainWindow() {
		settings = new AppSettings(true);
		settings.setResolution(800, 600);
		settings.setSamples(8);
		settings.setEmulateMouse(false);
		settings.setFrameRate(10000);
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
}
