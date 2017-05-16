package gravityball;

import com.jme3.shadow.EdgeFilteringMode;

import gravityball.ui.MainWindow;

/** Main函数和程序设置 */
public class Program {

	/** Main函数 */
	public static void main(String[] args) {
		MainWindow mainWindow = new MainWindow();
		mainWindow.setVisible(true);
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
	public final static int SHADOW_SPLITS = 2;
}
