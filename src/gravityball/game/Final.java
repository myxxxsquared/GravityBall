package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

public class Final extends ScenesObject {
	/** 位置和半径 */
	private float locationX, locationY, radius;

	/** 终点的几何体 */
	private Spatial geoCoin;

	public Final(Scenes scenes) {
		super(scenes);
	}

	@Override
	public void init() {
		// 初始化几何体
		geoCoin = scenes.getAssetManager().loadModel("Models/final.obj");

		// 初始化材质
		Material material = new Material(scenes.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap", scenes.getAssetManager().loadTexture("Textures/flagtexture.png"));
		material.setBoolean("UseMaterialColors", true);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("Ambient", new ColorRGBA(1.5f, 1.5f, 1.5f, 1.f));
		material.setFloat("Shininess", 64f);
		material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		geoCoin.setMaterial(material);

		// 设置运动位置
		geoCoin.setLocalTranslation(locationX, locationY, 0.f);
		geoCoin.setLocalScale(radius);
		// 添加到场景
		this.objNode.attachChild(geoCoin);
	}

	@Override
	public void collisionDetect() {
		ScenesBall ball = scenes.getBall();
		// 判断是否与小球相碰
		float distence = (float) Math.sqrt((ball.locationX-this.locationX)*(ball.locationX-this.locationX) + 
				(ball.locationY-this.locationY)*(ball.locationY-this.locationY));
		// 过关条件
		if(distence <= ball.radius + 0.01){
			this.scenes.gameWin();
		}
	}

	@Override
	public void timeUpdate(float tpf) {
	}

	@Override
	public void loadFromFile(JSONObject j) {
		// 从文件加载参数
		this.locationX = (float) j.getDouble("locationX");
		this.locationY = (float) j.getDouble("locationY");
		this.radius = (float) j.getDouble("radius");
	}

}
