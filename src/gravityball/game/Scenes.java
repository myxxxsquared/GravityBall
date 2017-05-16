package gravityball.game;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.JComponent;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.system.JmeCanvasContext;

import gravityball.Program;
import gravityball.ui.MainWindow;

/** 描述一个场景 */
public class Scenes extends SimpleApplication {
	
	/** 场景状态 */
	enum ScenesStatus {
		/**未初始化*/
		NOT_INITED,
		/**准备就绪，可以开始*/
		READY,
		/**正在游戏*/
		PLAYING,
		/**暂停*/
		PAUSED,
		/**游戏结束，未知是否获胜*/
		END,
		/**游戏获胜*/
		WIN,
		/**游戏失败*/
		LOSE
	}
	
	/**场景中的小球*/
	private ScenesBall ball;
	/**场景中的所有物体*/
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
	
	public void loadFromFile(JSONObject j) {
		if(!(this.status == ScenesStatus.NOT_INITED))
			throw new RuntimeException("this.status == ScenesStatus.NOT_INITED");
		
		JSONObject jball = j.getJSONObject("ball");
		JSONArray jobjects = j.getJSONArray("objects");
		
		time = 0.f;
		speed = 1.f;
		score = 0;
		
		ball = new ScenesBall(this);
		ball.loadFromFile(jball);
		objects = new ArrayList<>();
		for (Object object : jobjects) {
			JSONObject obj = (JSONObject) object;
			ScenesObject newobj = ScenesObject.createScenesObject(obj.getString("name"), this);
			newobj.loadFromFile(obj);
			objects.add(newobj);
		}
		
		this.status = ScenesStatus.READY;
	}
	public void gameReset() {
		this.status = ScenesStatus.NOT_INITED;
		
		this.rootNode.detachAllChildren();
		
		if(this.dlight!=null)
			this.rootNode.removeLight(this.dlight);
		this.dlight = null;
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
	
	private DirectionalLight dlight;
	private AmbientLight alight;
	
	public void updateLightCamera() {
		Quaternion q = new Quaternion();
		
		double div = Math.sqrt(ball.slopeX * ball.slopeX + ball.slopeY * ball.slopeY);
		float angular = (float) Math.atan(div);

		if(Math.abs(angular) > 0.001)
			q.fromAngleNormalAxis(angular, new Vector3f(ball.slopeY, -ball.slopeX, 0.f).normalize());
				
		dlight.setDirection(q.mult(new Vector3f(1,0,-2).normalizeLocal()));
		dlight.setColor(new ColorRGBA(0.5f,0.5f,0.5f,1.0f));
		
		alight.setColor(new ColorRGBA(0.5f,0.5f,0.5f,1.0f));
	    
	    rootNode.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
	    
		cam.setLocation(q.mult(new Vector3f(0.f, -1.5f, 2.5f)));
		cam.lookAt(new Vector3f(0.f, 0.f, 0.f), q.mult(new Vector3f(0.f, 0.f, 1.f)));		
		Dimension dimension = ((JmeCanvasContext)getContext()).getCanvas().getSize();
		cam.setFrustumPerspective(45.f, (float)dimension.width / (float)dimension.height , 0.1f, 5.f);
		cam.update();
	}
	
	private void initObjects() {
		
		dlight = new DirectionalLight();
		alight = new AmbientLight();
		
		updateLightCamera();
		
	    final int SHADOWMAP_SIZE = Program.SHADOW_MAP_SIZE;
	    DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, Program.SHADOW_SPLITS);
	    dlsr.setLight(dlight);
	    dlsr.setEdgeFilteringMode(Program.SHADOW_MODE);
	    
	    viewPort.addProcessor(dlsr);
	    
	    getRootNode().addLight(dlight);
	    getRootNode().addLight(alight);
		
		ball.initObject();
		for (ScenesObject scenesObject : objects) {
			scenesObject.initObject();
		}
		this.rootNode.updateGeometricState();
		
		this.viewPort.setBackgroundColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f));
	}
	
	public static void convertPointFromScreen(Point p,Component c) {
        int x,y;

        do {
            if(c instanceof JComponent) {
                x = c.getX();
                y = c.getY();
            }  else if(c instanceof java.awt.Window) {
                try {
                    Point pp = ((Window)c).getLocation();
                    
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

            if(c instanceof java.awt.Window || c instanceof java.applet.Applet)
                break;
            c = c.getParent();
        } while(c != null);
    }
	
	@Override
	public void simpleUpdate(float tpf) {
		Point point = MouseInfo.getPointerInfo().getLocation();
		Canvas c = ((JmeCanvasContext)getContext()).getCanvas();
		convertPointFromScreen(point, c);
		
		float x = point.x * 2.f / c.getWidth() - 1;
		float y = 1 - point.y * 2.f / c.getHeight();
		
		if(x<-1) x=-1;
		if(x>1) x=1;
		if(y<-1) y=-1;
		if(y>1) y=1;

		ball.slopeX = x / 10;
		ball.slopeY = y / 10;
		
		
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
		
		updateLightCamera();
	}

}
