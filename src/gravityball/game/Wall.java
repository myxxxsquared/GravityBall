package gravityball.game;

import org.json.JSONObject;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.plugins.blender.math.Matrix;
import com.jme3.scene.shape.Box;

public class Wall extends ScenesObject {
	// 墙的两个顶点位置
	public float x1, y1, x2, y2;

	private float width;
	private float height;

	public Wall(Scenes scenes) {
		super(scenes);
	}

	// 墙的几何体
	private Geometry geoWall;

	@Override
	public void init() {
		// 计算墙的大小和位置
		float length = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		Box box = new Box(length / 2, this.width, this.height);
		geoWall = new Geometry("ball", box);
		geoWall.setLocalRotation(new Quaternion(new float[] { 0.f, 0.f, (float) Math.atan2(y2 - y1, x2 - x1) }));
		geoWall.setLocalTranslation((x1 + x2) / 2, (y1 + y2) / 2, this.height / 2);

		// 计算墙的材质
		Material material = new Material(scenes.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		material.setTexture("DiffuseMap", scenes.getAssetManager().loadTexture("Textures/wood.jpg"));
		material.setBoolean("UseMaterialColors", true);
		material.setColor("Diffuse", ColorRGBA.White);
		material.setColor("Specular", ColorRGBA.White);
		material.setColor("Ambient", new ColorRGBA(1.5f, 1.5f, 1.5f, 1.f));
		material.setFloat("Shininess", 64f); // [0,128]
		geoWall.setMaterial(material);

		// 添加到场景
		this.objNode.attachChild(geoWall);
	}

	public static Matrix3f 生成方向矩阵(float tx, float ty, float x, float y) {
		Vector2f t_e = new Vector2f(tx, ty).normalize();
		Vector2f n_e = new Vector2f(ty, -tx).normalize();
		Matrix3f mat_tn = new Matrix3f(t_e.x, t_e.y, 0, n_e.x, n_e.y, 0, 0, 0, 1);
		Matrix3f mat_mov = new Matrix3f(1, 0, -x, 0, 1, -y, 0, 0, 1);
		return mat_tn.mult(mat_mov);
	}

	@Override
	public void collisionDetect() {		
		ScenesBall ball = scenes.getBall();
		final float wall_length = (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

		Matrix3f mat = 生成方向矩阵(x2 - x1, y2 - y1, x1, y1);
		Vector3f locball_loc = mat.mult(new Vector3f(ball.locationX, ball.locationY, 1));

		if (Math.abs(locball_loc.y) < ball.radius + this.width) {
			boolean bump = false;
			Matrix3f A = null, Ainv = null;

			if (locball_loc.x > 0 && locball_loc.x < wall_length) {				
				bump = true;
				A = mat;
				Ainv = mat.invert();
			} 
			else if (locball_loc.x > -(ball.radius + this.width) && x1 <= 0) {
				A = 生成方向矩阵(ball.locationY - y1, -ball.locationX + x1, x1, y1);
				Ainv = A.invert();
				locball_loc = A.mult(new Vector3f(ball.locationX, ball.locationY, 1));
				if(Math.abs(locball_loc.y) < ball.radius + this.width)
					bump = true;
			} else if (locball_loc.x >= wall_length && x1 < wall_length + ball.radius + this.width) {
				A = 生成方向矩阵(ball.locationY - y2, -ball.locationX + x2, x2, y2);
				Ainv = A.invert();
				locball_loc = A.mult(new Vector3f(ball.locationX, ball.locationY, 1));
				if(Math.abs(locball_loc.y) < ball.radius + this.width)
					bump = true;
			}

			if (bump) {
				Vector3f mult = A.mult(new Vector3f(ball.velocityX, ball.velocityY, 0.f));

				if (locball_loc.y > 0) {
					locball_loc.y = ball.radius + this.width + 0.001f;
				} else {
					locball_loc.y = -(ball.radius + this.width) - 0.001f;
				}
				Vector3f ballrec = Ainv.mult(locball_loc);
				ball.locationX = ballrec.x;
				ball.locationY = ballrec.y;

				if (mult.y > 0 && locball_loc.y < 0 || mult.y < 0 && locball_loc.y > 0) {
					mult.y *= -0.3;
					mult = Ainv.mult(mult);
					ball.velocityX = mult.x;
					ball.velocityY = mult.y;
				}
			}
		}
	}

	@Override
	public void timeUpdate(float tpf) {
		// TODO Auto-generated method stub
	}

	@Override
	public void loadFromFile(JSONObject j) {
		// 从文件加载位置
		this.x1 = (float) j.getDouble("x1");
		this.x2 = (float) j.getDouble("x2");
		this.y1 = (float) j.getDouble("y1");
		this.y2 = (float) j.getDouble("y2");

		this.width = 0.01f;
		this.height = 0.08f;
	}

}
