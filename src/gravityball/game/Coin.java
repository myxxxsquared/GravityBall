package gravityball.game;

import org.json.JSONObject;

import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
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

	/** 转过的角度 */
	private float angle;
	
	/** 吃金币音效 */
	private AudioNode audioEatting;

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
		
		// 初始化声音
		audioEatting = new AudioNode(scenes.getAssetManager(), "Sound/coin.ogg", DataType.Buffer);
		audioEatting.setPositional(false);
		audioEatting.setLooping(false);
		audioEatting.setVolume(3.f);
		objNode.attachChild(audioEatting);

		// 设置运动位置
		geoCoin.setLocalTranslation(locationX, locationY, radius);
		geoCoin.setLocalScale(radius);
		angle = 0.f;
		eaten = false;

		// 添加到场景
		this.objNode.attachChild(geoCoin);
	}

	private boolean eaten;

	@Override
	public void collisionDetect() {
		if (eaten)
			return;
		ScenesBall ball = scenes.getBall();
		
		// 判断是否与小球相碰
		float distence = (float) Math.sqrt((ball.locationX - this.locationX) * (ball.locationX - this.locationX)
				+ (ball.locationY - this.locationY) * (ball.locationY - this.locationY));
		if (distence <= ball.radius + this.radius - 0.01) {
			// 这时候这个金币被吃掉
			eaten = true;
			// 分数增加100
			scenes.addScore(100);
			scenes.getRootNode().detachChild(this.objNode);
			// 不能在迭代ArrayList的同时remove()
			audioEatting.play();
		}

	}

	@Override
	public void timeUpdate(float tpf) {
		if (eaten)
			return;
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
