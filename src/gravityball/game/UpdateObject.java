package gravityball.game;

import org.json.JSONObject;

import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;

public class UpdateObject extends ScenesObject {
	/** 位置和半径 */
	private float locationX, locationY, radius;

	/** 道具的几何体 */
	private Spatial geoUpdate;

	/** 转过的角度 */
	private float angle;

	private AudioNode audioEatting;

	/**
	 * 道具类型 
	 * 1 弹性增强3.0 
	 * 2 体积缩小 
	 * 3 g减小 
	 * 4 操作颠倒
	 */
	private int type;

	public UpdateObject(Scenes scenes) {
		super(scenes);
	}

	@Override
	public void init() {
		// 初始化几何体
		geoUpdate = scenes.getAssetManager().loadModel("Models/questionmark.obj");

		// 初始化材质
		Material material = new Material(scenes.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap", scenes.getAssetManager().loadTexture("Textures/cointexture.png"));
		material.setBoolean("UseMaterialColors", true);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("Ambient", new ColorRGBA(1.5f, 1.5f, 1.5f, 1.f));
		material.setFloat("Shininess", 64f);
		material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
		geoUpdate.setMaterial(material);

		audioEatting = new AudioNode(scenes.getAssetManager(), "Sound/up.ogg", DataType.Buffer);
		audioEatting.setPositional(false);
		audioEatting.setLooping(false);
		audioEatting.setVolume(3.f);
		objNode.attachChild(audioEatting);

		// 设置运动位置
		geoUpdate.setLocalTranslation(locationX, locationY, radius);
		geoUpdate.setLocalScale(radius);
		angle = 0.f;
		eaten = false;

		// 添加到场景
		this.objNode.attachChild(geoUpdate);
	}

	/** 标记被吃掉 */
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
			// 这时候这个道具被吃掉
			eaten = true;
			switch (type) {
			case 1: {
				// 弹性增强，球变成黄色
				Wall.e = -3.0f;
				ball.sphereMat.setColor("Diffuse", ColorRGBA.Yellow);
				ball.sphereMat.setColor("Ambient", ColorRGBA.Yellow);
				break;
			}
			case 2: {
				// 球的体积缩小，相应的质量也减少(为了运动效果，非成比例)
				ball.mass *= 0.5;
				ball.radius *= 0.5;
				ball.locationZ *= 0.5;
				break;
			}
			case 3: {
				// g减小，球变成灰色
				ball.g *= 0.5;
				ball.sphereMat.setColor("Diffuse", ColorRGBA.Gray);
				ball.sphereMat.setColor("Ambient", ColorRGBA.Gray);
				break;
			}
			case 4: {
				// 操作颠倒
				scenes.opposide = true;
				break;
			}
			}
			scenes.getRootNode().detachChild(this.objNode);
			// 不能在迭代ArrayList的同时remove()
			audioEatting.play();
		}

	}

	/** 用于道具效果的恢复 */
	private float eaten_time;
	private boolean restore;

	@Override
	public void timeUpdate(float tpf) {
		if (eaten && !restore) {
			// 道具效果持续15s，15s后恢复正常
			if (eaten_time > 15f) {
				ScenesBall ball = scenes.getBall();
				switch (type) {
				case 1: {
					// 恢复弹性和球的颜色
					Wall.e = -0.5f;
					ball.sphereMat.setColor("Diffuse", ColorRGBA.White);
					ball.sphereMat.setColor("Ambient", ColorRGBA.White);
					break;
				}
				case 2: {
					// ball.radius *= 2; 球的大小不恢复，防止卡住
					break;
				}
				case 3: {
					// 恢复g和球的颜色
					ball.g *= 2;
					ball.sphereMat.setColor("Diffuse", ColorRGBA.White);
					ball.sphereMat.setColor("Ambient", ColorRGBA.White);
					break;
				}
				case 4: {
					// 恢复操作颠倒
					scenes.opposide = false;
					break;
				}
				}
				this.restore = true;
				return;
			}
			eaten_time += tpf;
		}
		if (eaten)
			return;
		// 转过一个小角度(未被吃掉时)
		angle += tpf * 1.f;
		geoUpdate.setLocalRotation(new Quaternion(new float[] { 0.f, 0.f, angle }));
	}

	@Override
	public void loadFromFile(JSONObject j) {
		// 从文件加载参数
		this.locationX = (float) j.getDouble("locationX");
		this.locationY = (float) j.getDouble("locationY");
		this.radius = (float) j.getDouble("radius");
		this.eaten_time = 0;
		this.restore = false;
		// 道具的类型是一个 1~4的随机整数
		this.type = (int) (Math.random() * 3.99) + 1;
	}

}
