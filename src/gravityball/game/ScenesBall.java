package gravityball.game;

import com.jogamp.opengl.GL;

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
	
	public void paint(GL gl) {
		// TODO 绘制
	}
	
	public void timeEval(int from, int to) {
	}
}
