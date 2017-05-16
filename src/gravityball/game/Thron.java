package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.TechniqueDef.ShadowMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

public class Thron extends ScenesObject {
	
	private float locationX, locationY, radius;
	private Spatial geoThron;
	private float angle;

	public Thron(Scenes scenes) {
		super(scenes);
	}

	@Override
	public void init() {
		Box box = new Box(radius*1.2f, radius*1.2f, 0.001f);		
		geoThron = new Geometry("thron", box);
		geoThron.setLocalTranslation(locationX, locationY, 0.f);
		angle = 0.f;
		
		geoThron.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.Off);
		
		Material thronMat = new Material(scenes.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
		thronMat.setTexture("ColorMap", scenes.getAssetManager().loadTexture("Textures/blackhole.png"));
		thronMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		geoThron.setMaterial(thronMat);
		geoThron.setQueueBucket(Bucket.Transparent);
		
		this.objNode.attachChild(geoThron);
	}

	@Override
	public void collisionDetect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeUpdate(float tpf) {
		angle += tpf * 0.5f;
		geoThron.setLocalRotation(new Quaternion(new float[]{0.f, 0.f, angle}));
	}

	@Override
	public void loadFromFile(JSONObject j) {
		this.locationX = (float)j.getDouble("locationX");
		this.locationY = (float)j.getDouble("locationY");
		this.radius = (float)j.getDouble("radius");
	}

}
