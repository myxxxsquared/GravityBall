package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;

public class Coin extends ScenesObject {
	/** 位置和半径 */
	private float locationX, locationY, radius;

	/** 硬币的几何体 */
	private Spatial geoCoin;

	/** 刺转过的角度 */
	private float angle;

	public Coin(Scenes scenes) {
		super(scenes);
	}

	@Override
	public void init() {
		// 初始化几何体
		geoCoin = scenes.getAssetManager().loadModel("Models/coin.obj");
		
		// 初始化材质
		Material material = new Material(scenes.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap", scenes.getAssetManager().loadTexture("Textures/cointexture.png"));
		material.setBoolean("UseMaterialColors", true);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("Ambient", new ColorRGBA(1.5f, 1.5f, 1.5f, 1.f));
		material.setFloat("Shininess", 64f);
		material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		geoCoin.setMaterial(material);

		// 设置运动位置
		geoCoin.setLocalTranslation(locationX, locationY, radius);
		geoCoin.setLocalScale(radius);
		angle = 0.f;

		// 添加到场景
		this.objNode.attachChild(geoCoin);
	}

	@Override
	public void collisionDetect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeUpdate(float tpf) {
		// 转过一个小角度
		angle += tpf * 1.f;
		geoCoin.setLocalRotation(new Quaternion(new float[] { 0.f, 0.f, angle }));
	}

	@Override
	public void loadFromFile(JSONObject j) {
		// 从文件加载参数
		this.locationX = (float) j.getDouble("locationX");
		this.locationY = (float) j.getDouble("locationY");
		this.radius = (float) j.getDouble("radius");
	}

}
