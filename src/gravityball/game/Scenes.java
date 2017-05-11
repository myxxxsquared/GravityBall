package gravityball.game;

import java.awt.Dimension;
import java.util.ArrayList;

import org.json.JSONArray;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeCanvasContext;

import gravityball.ui.MainWindow;

public class Scenes extends SimpleApplication {
	enum ScenesStatus {
		NOT_INITED, READY, PLAYING, PAUSED, END, WIN, LOSE
	}
	
	private ScenesBall ball;
	private ArrayList<ScenesObject> objects;
	
	private float time;
	private float speed;
	
	private ScenesStatus status;
	private int score;
	
	public float getTime() { return time; }
	public ScenesBall getBall() { return ball; }
	public ArrayList<ScenesObject> getObjects() { return objects; }
	public float getSpeed() { return speed; }
	public void setSpeed(float speed) { this.speed = speed; }
	public ScenesStatus getStatus() { return status; }
	public int getScore() { return score; }
	public void addScore(int score) { this.score += score; }
	
	public Scenes()
	{
		status = ScenesStatus.NOT_INITED;
	}
	
	public void loadFromFile(Object JSONObject) {
		if(!(this.status == ScenesStatus.NOT_INITED))
			throw new RuntimeException("this.status == ScenesStatus.NOT_INITED");
		
		ball = null;
		objects = null;
		time = 0.f;
		speed = 1.f;
		score = 0;

		this.status = ScenesStatus.READY;
	}
	public void gameReset() {
		this.status = ScenesStatus.NOT_INITED;
		
		this.rootNode.detachAllChildren();
		
		if(this.light!=null)
			this.rootNode.removeLight(this.light);
		this.light = null;
	}
	public void gameStart() {
		if(!(this.status == ScenesStatus.READY || this.status == ScenesStatus.PAUSED))
			throw new RuntimeException("this.status == ScenesStatus.READY || this.status == ScenesStatus.PAUSED");
		
		if(this.status == ScenesStatus.READY)
			initObjects();
		
		this.status = ScenesStatus.PLAYING;
	}
	public void gamePause() {
		if(!(this.status == ScenesStatus.PLAYING))
			throw new RuntimeException("this.status == ScenesStatus.PLAYING");
		this.status = ScenesStatus.PAUSED;
	}
	public void gameWin() {
		if(!(this.status == ScenesStatus.PLAYING))
			throw new RuntimeException("this.status == ScenesStatus.PLAYING");
		this.status = ScenesStatus.WIN;
	}
	public void gameLose() {
		if(!(this.status == ScenesStatus.PLAYING))
			throw new RuntimeException("this.status == ScenesStatus.PLAYING");
		this.status = ScenesStatus.LOSE;
	}
	public void gameEnd() {
		if(!(this.status == ScenesStatus.PLAYING || this.status == ScenesStatus.PAUSED))
			throw new RuntimeException("this.status == ScenesStatus.PLAYING || this.status == ScenesStatus.PAUSED");
		this.status = ScenesStatus.END;
	}

	@Override
	public void simpleInitApp() {
		
	}
	
	private DirectionalLight light;
	
	public void updateLightCamera() {
		cam.setLocation(new Vector3f(0.f, -1.f, 1.f));
		cam.lookAt(new Vector3f(0.f, 0.f, 0.f), new Vector3f(0.f, 0.f, 1.f));		
		Dimension dimension = ((JmeCanvasContext)getContext()).getCanvas().getSize();
		cam.setFrustumPerspective(45.f, (float)dimension.width / (float)dimension.height , 0.1f, 5.f);
		cam.update();
		
		light.setDirection(new Vector3f(1,0,-2).normalizeLocal());
	    light.setColor(ColorRGBA.White);
	}
	
	private void initObjects() {
		light = new DirectionalLight();
		updateLightCamera();
	    getRootNode().addLight(light);
		
		ball.initObject();
		for (ScenesObject scenesObject : objects) {
			scenesObject.initObject();
		}
		this.rootNode.updateGeometricState();
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		
		if(MainWindow.jLabel != null)
		{
			MainWindow.jLabel.setText(Float.toString(1/tpf));
		}
		
		if(this.status == ScenesStatus.PLAYING)
		{
			this.time += tpf;
			
			ball.timeEval(tpf);
			for (ScenesObject scenesObject : objects) {
				scenesObject.timeUpdate(tpf);
			}
		}
	}

}
