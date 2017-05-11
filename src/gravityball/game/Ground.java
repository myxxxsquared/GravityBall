package gravityball.game;

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
		Box box = new Box(2.f, 2.f, 0.1f);
		Geometry geometry = new Geometry("Ground", box);
		Material material = new Material(scenes.getAssetManager(),
		        "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap",
		        scenes.getAssetManager().loadTexture("Textures/Terrain/Pond/Pond.jpg"));
		material.setBoolean("UseMaterialColors",true);
		material.setColor("Diffuse",ColorRGBA.White);
		material.setColor("Specular",ColorRGBA.White);
		material.setFloat("Shininess", 64f);  // [0,128]
		geometry.setMaterial(material);
		geometry.setLocalTranslation(0.f, 0.f, -0.1f);
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
	public void loadFromFile(Object JSONObj) {
		// TODO Auto-generated method stub

	}

}
