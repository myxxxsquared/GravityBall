package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
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

	/** 阻力系数 */
	public float alpha_v;
	/** 滚阻系数 */
	public float alpha_m;

	/** 半径 */
	public float radius;

	/** 中间变量，用于加速运算 */
	private double under_f;
	/** 中间变量，用于加速运算 */
	private double under_m;

	private Scenes scenes;

	public Scenes getScenes() {
		return scenes;
	}

	public ScenesBall(Scenes scenes) {
		this.scenes = scenes;
	}

	public void loadFromFile(JSONObject j) {
		// 从文件加载设置
		this.locationX = (float) j.getDouble("locationX");
		this.locationY = (float) j.getDouble("locationY");
		this.locationZ = (float) j.getDouble("locationZ");
		this.g = (float) j.getDouble("g");
		this.mass = (float) j.getDouble("mass");
		this.miu = (float) j.getDouble("miu");
		this.momentOfInertia = (float) j.getDouble("momentOfInertia");
		this.radius = (float) j.getDouble("radius");
		this.alpha_m = (float) j.getDouble("alpha_m");
		this.alpha_v = (float) j.getDouble("alpha_v");

		// 初始化运动参数
		this.velocityX = 0.f;
		this.velocityY = 0.f;
		this.velocityZ = 0.f;
		this.angularVelocityX = 0.f;
		this.angularVelocityY = 0.f;
		this.angularVelocityZ = 0.f;
		this.angular = new Quaternion(new float[] { 0.f, 0.f, 0.f });
		this.slopeX = 0.f;
		this.slopeY = 0.f;

		// 计算中间变量
		this.under_f = -1 - this.mass * Math.pow(this.radius, 2) / this.momentOfInertia;
		this.under_m = this.momentOfInertia / this.mass + this.radius * this.radius;
	}

	/** 用来确定球掉进了哪个洞里 */
	private float thron_x, thron_y;

	/** 用来设定球掉进了哪个洞里 */
	public void setThron(float x, float y) {
		thron_x = x;
		thron_y = y;
	}

	/** 球掉落进洞里 */
	public void Drop(float tpf) {
		if (this.radius >= 0.02f)
			this.radius *= (1 - tpf * 0.3);// 球缩小

		if (Math.abs(this.locationX - thron_x) >= 0.002f) {
			if (this.locationX > thron_x)
				this.locationX -= 0.05 * tpf;
			else
				this.locationX += 0.05 * tpf;
		} // 滚向中心
		if (Math.abs(this.locationY - thron_y) >= 0.002f) {
			if (this.locationY > thron_y)
				this.locationY -= 0.05 * tpf;
			else
				this.locationY += 0.05 * tpf;
		} // 滚向中心

		this.locationZ -= 0.05 * tpf;// 下落

		// 转速减缓
		this.angularVelocityX *= 0.99f;
		this.angularVelocityY *= 0.99f;
		this.angularVelocityZ *= 0.99f;
		// 计算角度变化
		Quaternion quaternion = new Quaternion();
		double angle = tpf * Math.sqrt(this.angularVelocityX * this.angularVelocityX
				+ this.angularVelocityY * this.angularVelocityY + this.angularVelocityZ * this.angularVelocityZ);
		if (angle > 0.001)
			quaternion.fromAngleAxis((float) angle,
					new Vector3f(this.angularVelocityX, this.angularVelocityY, this.angularVelocityZ).normalize());
		this.angular = quaternion.mult(this.angular);

		// 更新几何体位置
		updateSphere();
	}

	public void timeEval(float tpf) {
		// 计算受力
		double F_x = this.g * this.slopeX - this.alpha_v * this.velocityX;
		double F_y = this.g * this.slopeY - this.alpha_v * this.velocityY;
		double M_x = -this.alpha_m * this.angularVelocityX;
		double M_y = -this.alpha_m * this.angularVelocityY;

		double f_x;
		double f_y;

		// 计算相对速度
		double deltavx = -this.velocityX + this.radius * this.angularVelocityY;
		double deltavy = -this.velocityY - this.radius * this.angularVelocityX;
		double deltav = Math.sqrt(deltavx * deltavx + deltavy * deltavy);

		// 判断是否已经发生滑动，并计算受力
		if (deltav < 0.001) {
			f_x = F_x / this.under_f + M_y / this.under_m;
			f_y = F_y / this.under_f - M_x / this.under_m;
			double f = Math.sqrt(f_x * f_x + f_y * f_y);
			double f_max = this.miu * this.mass * this.g;
			if (f > f_max) {
				f_x = f_x / f * f_max;
				f_y = f_y / f * f_max;
			}
		} else if (deltav < 0.3) {
			double f_max = this.miu * this.mass * this.g;
			f_x = deltavx * f_max;
			f_y = deltavy * f_max;
		} else {
			double f_max = this.miu * this.mass * this.g;
			f_x = deltavx / deltav * f_max;
			f_y = deltavy / deltav * f_max;
		}

		// 计算加速度
		double a_x = (F_x + f_x) / this.mass;
		double a_y = (F_y + f_y) / this.mass;
		double beta_x = (M_x + f_y * this.radius) / this.momentOfInertia;
		double beta_y = (M_y - f_x * this.radius) / this.momentOfInertia;

		// 计算速度
		this.velocityX += a_x * tpf;
		this.velocityY += a_y * tpf;
		this.angularVelocityX += beta_x * tpf;
		this.angularVelocityY += beta_y * tpf;

		// 计算位置变化
		this.locationX += this.velocityX * tpf;
		this.locationY += this.velocityY * tpf;

		// 计算角度变化
		Quaternion quaternion = new Quaternion();
		double angle = tpf * Math.sqrt(this.angularVelocityX * this.angularVelocityX
				+ this.angularVelocityY * this.angularVelocityY + this.angularVelocityZ * this.angularVelocityZ);
		if (angle > 0.001)
			quaternion.fromAngleAxis((float) angle,
					new Vector3f(this.angularVelocityX, this.angularVelocityY, this.angularVelocityZ).normalize());
		this.angular = quaternion.mult(this.angular);

		// 更新几何体位置
		updateSphere();
	}

	/** 小球几何体 */
	private Spatial sphereGeo;
	
	/** 小球材质 */
	public Material sphereMat;

	/** 更新小球的位置 */
	private void updateSphere() {
		sphereGeo.setLocalScale(radius);
		sphereGeo.setLocalRotation(angular);
		sphereGeo.setLocalTranslation(locationX, locationY, locationZ);
	}

	public void initObject() {
		// 初始化球的几何体
		Sphere sphereMesh = new Sphere(16, 16, 1.f);
		sphereGeo = new Geometry("Shiny rock", sphereMesh);
		sphereMesh.setTextureMode(Sphere.TextureMode.Projected);

		// 初始化材质
		sphereMat = new Material(scenes.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		sphereMat.setTexture("DiffuseMap", scenes.getAssetManager().loadTexture("Textures/Terrain/Pond/Pond.jpg"));
		sphereMat.setBoolean("UseMaterialColors", true);
		sphereMat.setColor("Ambient", ColorRGBA.White);
		sphereMat.setColor("Diffuse", ColorRGBA.White);
		sphereMat.setColor("Specular", ColorRGBA.White);
		sphereMat.setFloat("Shininess", 64f); // [0,128]
		sphereGeo.setMaterial(sphereMat);

		// 添加到场景
		scenes.getRootNode().attachChild(sphereGeo);

		// 更新位置
		updateSphere();
	}

}
