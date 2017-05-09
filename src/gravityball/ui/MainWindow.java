package gravityball.ui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jogamp.opengl.GLCapabilities;

public class MainWindow extends JFrame {

	GamePanel gamePanel;
	
	public MainWindow() {
		// TODO 生成UI
		setSize(800, 600);
		
		GLCapabilities caps = new GLCapabilities(null);
		caps.setSampleBuffers(true);
		caps.setNumSamples(8);
		caps.setDoubleBuffered(true);
		
		gamePanel = new GamePanel(caps);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(gamePanel, BorderLayout.CENTER);
		
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});
	}
}
