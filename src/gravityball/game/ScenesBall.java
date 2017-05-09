package gravityball.game;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLArrayData;
import com.jogamp.opengl.util.GLArrayDataWrapper;

public class ScenesBall
{
	/** 位置 */
	public float locationX, locationY, locationZ;
	/** 速度 */
	public float velocityX, velocityY, velocityZ;

	/** 角速度 */
	public float angularVelocityX, angularVelocityY, angularVelocityZ;
	/** 旋转位置 */
	public Quaternion angular;
	
	/** 场地倾斜程度 */
	public float slopeX, slopeY;
	
	/** 大小 */
	public float size;
	/** 质量 */
	public float mass;
	/** 摩擦因数 */
	public float miu;
	/** 转动惯量 */
	public float momentOfInertia;
	
	/** 半径 */
	public float radius;
	
	public void paint(GL2 gl2) {
		// TODO 绘制
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPushMatrix();
		gl2.glLoadIdentity();
		
		gl2.glTranslatef(locationX, locationY, locationZ);
		angular.glRotate(gl2);
		
		gl2.glPopMatrix();
	}
	
	public ScenesBall()
	{
		//以下为测试代码
		locationX = 0;
		locationY = 0;
		locationZ = 0.2f;
		radius = 0.2f;
	}
	
	public void loadFromFile(Object JSONObject) {
		
	}
	
	public void timeEval(int from, int to) {
	}
}
