package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

public class ScenesBall {
	/** 位置 */
	public float locationX, locationY, locationZ;
	/** 速度 */
	public float velocityX, velocityY, velocityZ;

	/** 角速度 */
	public float angularVelocityX, angularVelocityY, angularVelocityZ;
	/** 旋转位置 */
	public Quaternion angular;

	/** 场地倾斜程度 */
	public float slopeX, slopeY;

	/** 场地重力加速度 */
	public float g;

	/** 质量 */
	public float mass;
	/** 摩擦因数 */
	public float miu;
	/** 转动惯量 */
	public float momentOfInertia;

	/** 半径 */
	public float radius;

	private Scenes scenes;

	public Scenes getScenes() {
		return scenes;
	}

	public ScenesBall(Scenes scenes) {
		this.scenes = scenes;
	}

	public void loadFromFile(JSONObject j) {
		this.locationX = (float) j.getDouble("locationX");
		this.locationY = (float) j.getDouble("locationY");
		this.locationZ = (float) j.getDouble("locationZ");
		this.g = (float) j.getDouble("g");
		this.mass = (float) j.getDouble("mass");
		this.miu = (float) j.getDouble("miu");
		this.momentOfInertia = (float) j.getDouble("momentOfInertia");
		this.radius = (float) j.getDouble("radius");

		this.velocityX = 0.f;
		this.velocityY = 0.f;
		this.velocityZ = 0.f;
		this.angularVelocityX = 0.f;
		this.angularVelocityY = 0.f;
		this.angularVelocityZ = 0.f;
		this.angular = new Quaternion(new float[] { 0.f, 0.f, 0.f });
		this.slopeX = 0.f;
		this.slopeY = 0.f;
	}

	public void timeEval(float tpf) {
		updateSphere();
	}

	/** 小球几何体 */
	private Spatial sphereGeo;

	private void updateSphere() {
		sphereGeo.setLocalScale(radius);
		sphereGeo.setLocalRotation(angular);
		sphereGeo.setLocalTranslation(locationX, locationY, locationZ);
	}

	public void initObject() {
		Sphere sphereMesh = new Sphere(16, 16, 1.f);
		sphereGeo = new Geometry("Shiny rock", sphereMesh);
		sphereMesh.setTextureMode(Sphere.TextureMode.Projected);

		Material sphereMat = new Material(scenes.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		sphereMat.setTexture("DiffuseMap", scenes.getAssetManager().loadTexture("Textures/Terrain/Pond/Pond.jpg"));
		sphereMat.setBoolean("UseMaterialColors", true);
		sphereMat.setColor("Ambient", ColorRGBA.White);
		sphereMat.setColor("Diffuse", ColorRGBA.White);
		sphereMat.setColor("Specular", ColorRGBA.White);
		sphereMat.setFloat("Shininess", 64f); // [0,128]
		sphereGeo.setMaterial(sphereMat);

		scenes.getRootNode().attachChild(sphereGeo);

		updateSphere();
	}
}
