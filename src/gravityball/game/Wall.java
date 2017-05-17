package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class Wall extends ScenesObject {

	// 墙的两个顶点位置
	public float x1, y1, x2, y2;

	public Wall(Scenes scenes) {
		super(scenes);
	}

	// 墙的几何体
	private Geometry geoWall;

	@Override
	public void init() {
		// 计算墙的大小和位置
		float length = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		Box box = new Box(length / 2, 0.01f, 0.07f);
		geoWall = new Geometry("ball", box);
		geoWall.setLocalRotation(new Quaternion(new float[] { 0.f, 0.f, (float) Math.atan2(y2 - y1, x2 - x1) }));
		geoWall.setLocalTranslation((x1 + x2) / 2, (y1 + y2) / 2, 0.07f);

		// 计算墙的材质
		Material material = new Material(scenes.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap", scenes.getAssetManager().loadTexture("Textures/wood.jpg"));
		material.setBoolean("UseMaterialColors", true);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("Ambient", new ColorRGBA(1.5f, 1.5f, 1.5f, 1.f));
		material.setFloat("Shininess", 64f); // [0,128]
		geoWall.setMaterial(material);

		// 添加到场景
		this.objNode.attachChild(geoWall);
	}

	@Override
	public void collisionDetect() {
		// TODO Auto-generated method stub
	}

	@Override
	public void timeUpdate(float tpf) {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadFromFile(JSONObject j) {
		// 从文件加载位置
		this.x1 = (float) j.getDouble("x1");
		this.x2 = (float) j.getDouble("x2");
		this.y1 = (float) j.getDouble("y1");
		this.y2 = (float) j.getDouble("y2");
	}

}
