package gravityball;

import com.jme3.shadow.EdgeFilteringMode;

import gravityball.ui.MainWindow;

public class Program {

	public static void main(String[] args) {
		MainWindow mainWindow = new MainWindow();
		mainWindow.setVisible(true);
	}
	
	public final static int NUMBER_SAMPLES = 8;
	public final static int FRAME_RATE = 60;
	public final static EdgeFilteringMode SHADOW_MODE = EdgeFilteringMode.PCF8;
	public final static int SHADOW_MAP_SIZE = 512;
	public final static int SHADOW_SPLITS = 2;
}
