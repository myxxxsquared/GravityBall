package gravityball.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.JmeCanvasContext;

import gravityball.Program;
import gravityball.ui.MainWindow;

/** 描述一个场景 */
public class Scenes extends SimpleApplication {

	/** 场景状态 */
	public enum ScenesStatus {
		/** 未初始化 */
		NOT_INITED,
		/** 准备就绪，可以开始 */
		READY,
		/** 正在游戏 */
		PLAYING,
		/** 暂停 */
		PAUSED,
		/** 游戏结束，未知是否获胜 */
		END,
		/** 游戏获胜 */
		WIN,
		/** 游戏失败 */
		LOSE
	}

	/** 场景中的小球 */
	private ScenesBall ball;
	/** 场景中的所有物体 */
	private ArrayList<ScenesObject> objects;

	/** 游戏中的时间 */
	private float time;
	/** 游戏速度 */
	private float speed;

	/** 指示当前场景的状态 */
	private ScenesStatus status;

	/** 玩家的分数 */
	private int score;

	/** 声音文件名称 */
	private String soundFileName;

	/** 获取当前游戏时间 */
	public float getTime() {
		return time;
	}

	/** 获取场景中的球对象 */
	public ScenesBall getBall() {
		return ball;
	}

	/** 获取场景中的对象列表 */
	public ArrayList<ScenesObject> getObjects() {
		return objects;
	}

	/** 获取游戏速度 */
	public float getSpeed() {
		return speed;
	}

	/** 设置游戏速度 */
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/** 获取当前场景状态 */
	public ScenesStatus getStatus() {
		return status;
	}

	/** 获取当前得分 */
	public int getScore() {
		return score;
	}

	/** 增加分数 */
	public void addScore(int score) {
		this.score += score;
	}

	/** 构造一个空的场景 */
	public Scenes() {
		status = ScenesStatus.NOT_INITED;
	}

	/** 从文件读取场景 */
	public void loadFromFile(JSONObject j) {
		// 确定场景未初始化
		if (!(this.status == ScenesStatus.NOT_INITED))
			throw new RuntimeException("this.status == ScenesStatus.NOT_INITED");

		// 初始化变量
		time = 0.f;
		speed = 1.f;
		score = 0;
		this.soundFileName = j.getString("background_sound");

		// 加载球
		JSONObject jball = j.getJSONObject("ball");
		ball = new ScenesBall(this);
		ball.loadFromFile(jball);

		// 加载对象
		JSONArray jobjects = j.getJSONArray("objects");
		objects = new ArrayList<>();
		for (Object object : jobjects) {
			JSONObject obj = (JSONObject) object;
			ScenesObject newobj = ScenesObject.createScenesObject(obj.getString("name"), this);
			newobj.loadFromFile(obj);
			objects.add(newobj);
		}

		initObjects();

		// 标记为加载完成
		this.status = ScenesStatus.READY;
	}

	/** 将游戏重置为NOT_INITED */
	public void gameReset() {
		this.status = ScenesStatus.NOT_INITED;
		if (this.alight != null)
			this.rootNode.removeLight(this.alight);
		if (this.dlight != null)
			this.rootNode.removeLight(this.dlight);
		this.rootNode.detachAllChildren();
		try{
			this.audioBackground.stop();
		}
		catch(Exception e){
			
		}
		this.viewPort.clearProcessors();
		lose_window_vis = false;
	}

	/** 开始游戏 */
	public void gameStart() {
		if (!(this.status == ScenesStatus.READY || this.status == ScenesStatus.PAUSED))
			throw new RuntimeException("this.status == ScenesStatus.READY || this.status == ScenesStatus.PAUSED");
		this.audioBackground.play();
		this.status = ScenesStatus.PLAYING;
	}

	/** 暂停游戏 */
	public void gamePause() {
		if (!(this.status == ScenesStatus.PLAYING))
			throw new RuntimeException("this.status == ScenesStatus.PLAYING");
		this.status = ScenesStatus.PAUSED;
	}

	/** 恢复游戏 */
	public void gameResume() {
		if (!(this.status == ScenesStatus.PAUSED))
			throw new RuntimeException("this.status == ScenesStatus.PAUSED");
		this.status = ScenesStatus.PLAYING;
	}

	private static JLabel win_label = new JLabel("                                       You win!!!");
	private static JFrame win_window = new JFrame();

	/** 游戏获胜 */
	public void gameWin() {
		if (!(this.status == ScenesStatus.PLAYING))
			throw new RuntimeException("this.status == ScenesStatus.PLAYING");
		audioBackground.stop();
		audioWin.play();
		this.status = ScenesStatus.WIN;
		/*
		 * win_window.setSize(300, 200);
		 * win_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		 * win_window.setLocation(400, 200);
		 * win_window.getContentPane().add(win_label, BorderLayout.CENTER);
		 * win_window.setVisible(true);
		 */
		Program.mainWindow.game.setVisible(false);
		if (Program.mainWindow.gameLevel == 5)
			Program.mainWindow.winFinal.setVisible(true);
		else
			Program.mainWindow.win.setVisible(true);
	}

	private static JLabel lose_label = new JLabel("                                       You lose!!!");
	private static JFrame lose_window = new JFrame();
	private static boolean lose_window_vis = false;

	/** 游戏失败 */
	public void gameLose() {
		if (!(this.status == ScenesStatus.PLAYING))
			throw new RuntimeException("this.status == ScenesStatus.PLAYING");
		this.status = ScenesStatus.LOSE;
		audioBackground.stop();
		audioLose.play();
	}

	/** 游戏结束 */
	public void gameEnd() {
		if (!(this.status == ScenesStatus.PLAYING || this.status == ScenesStatus.PAUSED))
			throw new RuntimeException("this.status == ScenesStatus.PLAYING || this.status == ScenesStatus.PAUSED");
		this.status = ScenesStatus.END;
	}

	@Override
	public void simpleInitApp() {
	}

	private DirectionalLight dlight;
	private AmbientLight alight;
	private AudioNode audioBackground;
	private AudioNode audioLose;
	private AudioNode audioWin;

	/** 更新灯光位置和摄像机 */
	private void updateLightCamera() {
		// 生成旋转四元数
		Quaternion q = new Quaternion();
		double div = Math.sqrt(ball.slopeX * ball.slopeX + ball.slopeY * ball.slopeY);
		float angular = (float) Math.atan(div);
		if (Math.abs(angular) > 0.001)
			q.fromAngleNormalAxis(angular, new Vector3f(ball.slopeY, -ball.slopeX, 0.f).normalize());

		// 设置灯光位置
		dlight.setDirection(q.mult(new Vector3f(1, 0, -2).normalizeLocal()));
		dlight.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		alight.setColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));

		// 设置阴影模式
		rootNode.setShadowMode(ShadowMode.CastAndReceive);

		// 设置摄像机位置
		cam.setLocation(q.mult(new Vector3f(0.f, -1.5f, 2.5f)));
		cam.lookAt(new Vector3f(0.f, 0.f, 0.f), q.mult(new Vector3f(0.f, 0.f, 1.f)));
		Dimension dimension = ((JmeCanvasContext) getContext()).getCanvas().getSize();
		cam.setFrustumPerspective(45.f, (float) dimension.width / (float) dimension.height, 0.1f, 5.f);
		cam.update();
	}

	DirectionalLightShadowRenderer dlsr;

	/** 初始化场景 */
	private void initObjects() {
		// 新建灯光
		dlight = new DirectionalLight();
		alight = new AmbientLight();
		updateLightCamera();
		getRootNode().addLight(dlight);
		getRootNode().addLight(alight);

		// 生成阴影
		final int SHADOWMAP_SIZE = Program.SHADOW_MAP_SIZE;
		dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, Program.SHADOW_SPLITS);
		dlsr.setLight(dlight);
		dlsr.setEdgeFilteringMode(Program.SHADOW_MODE);
		viewPort.addProcessor(dlsr);

		// 生成背景声音
		audioBackground = new AudioNode(assetManager, soundFileName, DataType.Buffer);
		audioBackground.setLooping(true);
		audioBackground.setPositional(true);
		audioBackground.setVolume(3.f);
		rootNode.attachChild(audioBackground);

		audioLose = new AudioNode(assetManager, "Sound/over.ogg", DataType.Buffer);
		audioLose.setPositional(false);
		audioLose.setLooping(false);
		audioLose.setVolume(3.f);
		rootNode.attachChild(audioLose);

		audioWin = new AudioNode(assetManager, "Sound/win.ogg", DataType.Buffer);
		audioWin.setPositional(false);
		audioWin.setLooping(false);
		audioWin.setVolume(3.f);
		rootNode.attachChild(audioWin);

		// 初始化球和对象
		ball.initObject();
		for (ScenesObject scenesObject : objects)
			scenesObject.initObject();

		// 设置背景
		this.viewPort.setBackgroundColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f));
	}

	/** 获取点相对窗口的位置 */
	public static void convertPointFromScreen(Point p, Component c) {
		int x, y;

		do {
			if (c instanceof JComponent) {
				x = c.getX();
				y = c.getY();
			} else if (c instanceof java.awt.Window) {
				try {
					Point pp = ((Window) c).getLocation();

					x = pp.x;
					y = pp.y;
				} catch (IllegalComponentStateException icse) {
					x = c.getX();
					y = c.getY();
				}
			} else {
				x = c.getX();
				y = c.getY();
			}

			p.x -= x;
			p.y -= y;

			if (c instanceof java.awt.Window || c instanceof java.applet.Applet)
				break;
			c = c.getParent();
		} while (c != null);
	}

	/** 刷新场景 */
	@Override
	public void simpleUpdate(float tpf) {
		if (tpf > 0.1f)
			tpf = 0.1f;

		// TODO 临时代码，用于显示帧频率
		if (MainWindow.jLabel != null)
			MainWindow.jLabel.setText(Float.toString(1 / tpf));

		if (this.status == ScenesStatus.PLAYING) {
			// 根据鼠标位置刷新斜率
			Point point = MouseInfo.getPointerInfo().getLocation();
			Canvas c = ((JmeCanvasContext) getContext()).getCanvas();
			convertPointFromScreen(point, c);
			float x = point.x * 2.f / c.getWidth() - 1;
			float y = 1 - point.y * 2.f / c.getHeight();
			if (x < -1)
				x = -1;
			if (x > 1)
				x = 1;
			if (y < -1)
				y = -1;
			if (y > 1)
				y = 1;
			ball.slopeX = x / 10;
			ball.slopeY = y / 10;

			// 时间演化
			this.time += tpf;
			for (ScenesObject scenesObject : objects)
				scenesObject.collisionDetect();
			ball.timeEval(tpf);
			for (ScenesObject scenesObject : objects)
				scenesObject.timeUpdate(tpf);
		} else if (this.status == ScenesStatus.LOSE && ball.locationZ >= -0.1f) {
			// 球缓缓下沉
			this.time += tpf;
			ball.Drop(tpf);
			for (ScenesObject scenesObject : objects)
				scenesObject.timeUpdate(tpf);
		}

		else if (ball.locationZ < -0.1f && lose_window_vis == false) {
			lose_window_vis = true;
			/*
			 * lose_window.setSize(300, 200);
			 * lose_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			 * lose_window.setLocation(400, 200);
			 * lose_window.getContentPane().add(lose_label,
			 * BorderLayout.CENTER); lose_window.setVisible(true);
			 */

			Program.mainWindow.game.setVisible(false);
			Program.mainWindow.lose.setVisible(true);
		}

		// 刷新摄像机和灯光
		updateLightCamera();
	}

}
