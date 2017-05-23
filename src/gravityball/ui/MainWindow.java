package gravityball.ui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONObject;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import gravityball.Program;
import gravityball.game.Scenes;

/** 主窗口 */
public class MainWindow extends JFrame {

	/** 程序设置 */
	private AppSettings settings;

	/** 程序场景 */
	private Scenes scenes;

	public MainWindow() {

		// 建立和修改设置
		settings = new AppSettings(true);
		settings.setResolution(800, 600);
		settings.setSamples(Program.NUMBER_SAMPLES);
		settings.setEmulateMouse(false);
		settings.setFrameRate(Program.FRAME_RATE);
		settings.setUseInput(false);

		// 新建场景，修改设置
		scenes = new Scenes();
		scenes.setSettings(settings);
		scenes.createCanvas();
		scenes.setDisplayStatView(false);
		scenes.setDisplayFps(false);

		// 将场景转化为Canvas
		final JmeCanvasContext ctx = (JmeCanvasContext) scenes.getContext();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Canvas c = ctx.getCanvas();

		// TODO 以下为临时测试代码
		setSize(800, 600);

		{
			JButton button = new JButton("Start2!");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!thisenabled) {
						getContentPane().add(c, BorderLayout.CENTER);

						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.loadFromFile(new JSONObject(
										readall(MainWindow.class.getResourceAsStream("/Maps/map1.json"))));
								scenes.gameStart();
							}
						});
					} else {
						scenes.enqueue(new Runnable() {
							@Override
							public void run() {
								scenes.gameReset();
							}
						});
						getContentPane().remove(c);
					}
					thisenabled = !thisenabled;
				}
			});
			getContentPane().add(button, BorderLayout.NORTH);
		}

		jLabel = new JLabel("Time:");

		getContentPane().add(jLabel, BorderLayout.SOUTH);

	}

	// 临时测试变量
	boolean thisenabled = false;
	public static JLabel jLabel;

	// 临时测试函数
	static String readall(InputStream input) {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		try {
			for (int n; (n = input.read(b)) != -1;) {
				out.append(new String(b, 0, n, "UTF-8"));
			}
		} catch (IOException e) {
		}
		return out.toString();
	}
}
