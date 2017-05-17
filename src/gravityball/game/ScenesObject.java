package gravityball.game;

import org.json.JSONObject;

import com.jme3.scene.Node;

public abstract class ScenesObject {
	/** 物体节点 */
	protected Node objNode;

	/** 获取物体的节点 */
	public Node getObjNode() {
		return objNode;
	}

	/** 对应的场景 */
	protected Scenes scenes;

	/** 获取对应的场景 */
	public Scenes getScenes() {
		return scenes;
	}

	/** 构造函数 */
	public ScenesObject(Scenes scenes) {
		this.scenes = scenes;
	}

	/** 初始化对象 */
	public void initObject() {
		objNode = new Node();
		scenes.getRootNode().attachChild(objNode);

		/** 调用子类的初始化 */
		init();
	}

	/** 初始化，绘制到场景 */
	public abstract void init();

	/** 碰撞检测 */
	public abstract void collisionDetect();

	/** 时间演化 */
	public abstract void timeUpdate(float tpf);

	/** 从文件加载 */
	public abstract void loadFromFile(JSONObject j);

	/** 创建特定类型的物体 */
	public static ScenesObject createScenesObject(String name, Scenes scenes) {
		switch (name) {
		case "ground":
			return new Ground(scenes);
		case "wall":
			return new Wall(scenes);
		case "thorn":
			return new Thron(scenes);
		default:
			return null;
		}
	}
}
