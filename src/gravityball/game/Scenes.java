package gravityball.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Stream;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

public class Scenes {
	enum ScenesStatus {
		NOT_INITED, READY, PLAYING, PUASED, END
	}
	
	private int width, height;
	private ScenesBall ball;
	private ArrayList<ScenesObject> objects;
	
	private Date lastEvalTime;
	private int millionsecond;
	private float speed;
	
	private ScenesStatus status;
	private int score;
	
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public ScenesBall getBall() { return ball; }
	public ArrayList<ScenesObject> getObjects() { return objects; }
	public Date getLastEvalTime() { return lastEvalTime; }
	public float getSpeed() { return speed; }
	public void setSpeed(float speed) { this.speed = speed; }
	public ScenesStatus getStatus() { return status; }
	public int getScore() { return score; }
	public void addScore(int score) { this.score += score; }
	
	public void paint(GL2 gl) {
		
	}
	
	public void loadFromFile(Object JSONObject) {
		
	}
	
	public void reset() {
		
	}
	
	public void start() {
		
	}
	
	public void pause() {
		
	}
	
	public void win() {
		
	}
	
	public void lose() {
		
	}
	
	public void end() {
		
	}
	
	public void timeEval(int millionsecond) {
		
	}
	
	public void timeEval() {
		
	}
}
