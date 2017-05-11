package gravityball.game;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

public class ScenesBall
{
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
	
	/** 大小 */
	public float size;
	/** 质量 */
	public float mass;
	/** 摩擦因数 */
	public float miu;
	/** 转动惯量 */
	public float momentOfInertia;
	
	/** 半径 */
	public float radius;
	
	private Scenes scenes;
	public Scenes getScenes() { return scenes; }

	public ScenesBall(Scenes scenes)
	{
		this.scenes = scenes;
		
		//以下为测试代码
		locationX = 0;
		locationY = 0;
		locationZ = 0.1f;
		radius = 0.1f;
	}
	
	public void loadFromFile(Object JSONObject) {
		
	}
	
	public void timeEval(float tpf) {
		this.locationX += tpf * 0.1;
		this.angular = this.angular.mult(new Quaternion(new float[]{0.f, 0.f, 0.2f * tpf}));
		updateSphere();
	}
	
	private Geometry sphereGeo;
	
	private void updateSphere() {
		sphereGeo.setLocalRotation(angular);
		sphereGeo.setLocalTranslation(locationX, locationY, locationZ);
	}
	
	public void initObject() {
	    Sphere sphereMesh = new Sphere(32,32, radius);
	    sphereGeo = new Geometry("Ball", sphereMesh);
	    sphereMesh.setTextureMode(Sphere.TextureMode.Projected);
	    Material sphereMat = new Material(scenes.getAssetManager(),
	        "Common/MatDefs/Light/Lighting.j3md");
	    sphereMat.setTexture("DiffuseMap",
	        scenes.getAssetManager().loadTexture("Textures/Terrain/Pond/Pond.jpg"));
	    sphereMat.setBoolean("UseMaterialColors",true);
	    sphereMat.setColor("Diffuse",ColorRGBA.White);
	    sphereMat.setColor("Specular",ColorRGBA.White);
	    sphereMat.setFloat("Shininess", 64f);  // [0,128]
	    sphereGeo.setMaterial(sphereMat);
	    scenes.getRootNode().attachChild(sphereGeo);
	    
	    this.angular = new Quaternion();
	    
	    updateSphere();
	}
}
