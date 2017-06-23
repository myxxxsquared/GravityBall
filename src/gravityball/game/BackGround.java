package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class BackGround extends ScenesObject {

	public BackGround(Scenes scenes) {
		super(scenes);
	}

	@Override
	public void init() {
		// 初始化几何体
		Box box = new Box(5.f, 5.f, 0.03f);
		Geometry geometry = new Geometry("BackGround", box);

		// 初始化素材
		Material material = new Material(scenes.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap", scenes.getAssetManager().loadTexture("Textures/star.jpg"));
		material.setBoolean("UseMaterialColors", true);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("Ambient", new ColorRGBA(1.5f, 1.5f, 1.5f, 1.f));
		//material.setFloat("Shininess", 64f);
		geometry.setMaterial(material);

		// 更新几何体位置
		geometry.setLocalTranslation(0.f, 0.f, -0.15f);

		// 添加到节点
		objNode.attachChild(geometry);
	}

	@Override
	public void collisionDetect() {
	}

	@Override
	public void timeUpdate(float tpf) {
	}

	@Override
	public void loadFromFile(JSONObject j) {
	}
}
