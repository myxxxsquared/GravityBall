package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

public class Thron extends ScenesObject {

	/** 位置和半径 */
	private float locationX, locationY, radius;

	/** 刺的几何体 */
	private Spatial geoThron;

	/** 刺转过的角度 */
	private float angle;

	public Thron(Scenes scenes) {
		super(scenes);
	}

	@Override
	public void init() {
		// 初始化几何体
		Box box = new Box(radius * 1.2f, radius * 1.2f, 0.001f);
		geoThron = new Geometry("thron", box);

		// 初始化材质
		Material thronMat = new Material(scenes.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		thronMat.setTexture("ColorMap", scenes.getAssetManager().loadTexture("Textures/blackhole.png"));
		thronMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		geoThron.setMaterial(thronMat);
		geoThron.setQueueBucket(Bucket.Transparent);

		// 关闭阴影
		geoThron.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.Off);

		// 设置运动位置
		geoThron.setLocalTranslation(locationX, locationY, 0.f);
		angle = 0.f;

		// 添加到场景
		this.objNode.attachChild(geoThron);
	}

	@Override
	public void collisionDetect() {
		// TODO Auto-generated method stub
	}

	@Override
	public void timeUpdate(float tpf) {
		// 转过一个小角度
		angle += tpf * 0.5f;
		geoThron.setLocalRotation(new Quaternion(new float[] { 0.f, 0.f, angle }));
	}

	@Override
	public void loadFromFile(JSONObject j) {
		// 从文件加载参数
		this.locationX = (float) j.getDouble("locationX");
		this.locationY = (float) j.getDouble("locationY");
		this.radius = (float) j.getDouble("radius");
	}

}
