package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class Ground extends ScenesObject {
	
	public Ground(Scenes scenes) {
		super(scenes);
	}

	@Override
	public void init() {
		Box box = new Box(1.f, 1.f, 0.03f);
		
		Geometry geometry = new Geometry("Ground", box);
		Material material = new Material(scenes.getAssetManager(),
		        "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap",
		        scenes.getAssetManager().loadTexture("Textures/wood.jpg"));
		material.setBoolean("UseMaterialColors",true);
		material.setColor("Diffuse",ColorRGBA.White);
		material.setColor("Specular",ColorRGBA.White);
		material.setColor("Ambient",new ColorRGBA(1.5f, 1.5f, 1.5f, 1.f));
		material.setFloat("Shininess", 64f);  // [0,128]
		geometry.setMaterial(material);
		geometry.setLocalTranslation(0.f, 0.f, -0.03f);
		objNode.attachChild(geometry);
	}

	@Override
	public void collisionDetect() {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeUpdate(float tpf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadFromFile(JSONObject j) {
		// TODO Auto-generated method stub

	}

}
