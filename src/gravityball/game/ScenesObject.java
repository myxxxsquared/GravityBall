package gravityball.game;

import org.json.JSONObject;

import com.jme3.scene.Node;

public abstract class ScenesObject
{
	/** 物体节点 */
	protected Node objNode;
	public Node getObjNode() { return objNode; }
	
	protected Scenes scenes;
	public Scenes getScenes() { return scenes; }
	
	public ScenesObject(Scenes scenes) {
		this.scenes = scenes;
	}
	
	public void initObject() {
		objNode = new Node();
		scenes.getRootNode().attachChild(objNode);
		init();
	}
	
	/** 初始化，绘制到场景 */
	public abstract void init();
	
	/** 碰撞检测  */
	public abstract void collisionDetect();
	
	/** 时间演化 */
	public abstract void timeUpdate(float tpf);
	
	/** 从文件加载 */
	public abstract void loadFromFile(JSONObject j);
	
	public static ScenesObject createScenesObject(String name, Scenes scenes)
	{
		switch(name) {
		case "ground":
			return new Ground(scenes);
		case "wall":
			return new Wall(scenes);
		default:
			return null;
		}
	}
}
