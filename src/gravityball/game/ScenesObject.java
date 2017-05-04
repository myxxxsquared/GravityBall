package gravityball.game;

import com.jogamp.opengl.GL;

public abstract class ScenesObject
{
	/** 绘制到场景 */
	public abstract void paint(GL gl);
	
	/** 碰撞检测  */
	public abstract void collisionDetect(Scenes scenes);
	
	/** 时间演化 */
	public abstract void timeEval(ScenesBall ball, int from, int to);
	
	/** 从文件加载 */
	public abstract void loadFromFile(Object JSONObj);
	
	public static ScenesObject createScenesObject(String name)
	{
		switch(name) {
		default:
			return null;
		}
	}
}
