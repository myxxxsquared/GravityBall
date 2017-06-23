package gravityball;

import javax.swing.JFrame;

import com.jme3.shadow.EdgeFilteringMode;

import gravityball.ui.MainWindow;

/** Main函数和程序设置 */
public class Program {

	public static MainWindow mainWindow;
	/** Main函数 */
	public static void main(String[] args) {
		mainWindow = new MainWindow();
		mainWindow.setVisible(true);
		mainWindow.setLocation(340, 100);
		mainWindow.setSize(640, 600);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/** 采样次数 */
	public final static int NUMBER_SAMPLES = 8;
	/** 最大帧频率 */
	public final static int FRAME_RATE = 60;
	/** 阴影渲染模式 */
	public final static EdgeFilteringMode SHADOW_MODE = EdgeFilteringMode.PCFPOISSON;
	/** 阴影贴图大小 */
	public final static int SHADOW_MAP_SIZE = 4096;
	/** 阴影采样分割次数 */
	public final static int SHADOW_SPLITS = 1;
}
