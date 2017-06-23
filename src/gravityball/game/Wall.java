package gravityball.game;

import org.json.JSONObject;

import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

public class Wall extends ScenesObject {
	/** 墙的两个顶点位置 */
	public float x1, y1, x2, y2;

	/** 墙面宽度 */
	public float width;

	/** 墙面高度 */
	public float height;

	/** 墙面长度 */
	public float length;

	/** 从全局坐标到局部坐标的变换矩阵 */
	public Matrix3f matTransport;

	/** 弹性系数 */
	public static float e = -0.5f;

	private AudioNode audioBump;

	public final static float MOVE_ADD_DELTA = 0.001f;
	public final static float BUMP_SOUND_V = 0.3f;

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

		// 声音
		audioBump = new AudioNode(scenes.getAssetManager(), "Sound/bump.ogg", DataType.Buffer);
		audioBump.setPositional(false);
		audioBump.setLooping(false);
		audioBump.setVolume(3.f);
		objNode.attachChild(audioBump);

		// 添加到场景
		this.objNode.attachChild(geoWall);
	}

	/** 生成 一个以x,y为原点，以tx,ty为x轴的变换矩阵 */
	public static Matrix3f genTransportMatrix(float tx, float ty, float x, float y) {
		Vector2f t_e = new Vector2f(tx, ty).normalize();
		Vector2f n_e = new Vector2f(ty, -tx).normalize();
		Matrix3f mat_tn = new Matrix3f(t_e.x, t_e.y, 0, n_e.x, n_e.y, 0, 0, 0, 1);
		Matrix3f mat_mov = new Matrix3f(1, 0, -x, 0, 1, -y, 0, 0, 1);
		return mat_tn.mult(mat_mov);
	}

	@Override
	public void collisionDetect() {
		ScenesBall ball = scenes.getBall();

		// 局部坐标
		Vector3f ballPointLocal = this.matTransport.mult(new Vector3f(ball.locationX, ball.locationY, 1));

		// 在局部坐标下判断是否碰撞
		if (Math.abs(ballPointLocal.y) < ball.radius + this.width) {
			// 指示是否真的发生碰撞
			boolean bump = false;

			// 碰撞的局部-全局转换矩阵
			Matrix3f trans = null;

			if (ballPointLocal.x > 0 && ballPointLocal.x < this.length) { // 如果和墙面碰撞
				bump = true;
				trans = this.matTransport;
			} else if (ballPointLocal.x > -(ball.radius + this.width) && ballPointLocal.x <= 0) { // 如果和第一个墙边碰撞
				trans = genTransportMatrix(ball.locationY - y1, -ball.locationX + x1, x1, y1);
				ballPointLocal = trans.mult(new Vector3f(ball.locationX, ball.locationY, 1));
				if (Math.abs(ballPointLocal.y) < ball.radius + this.width)
					bump = true;
			} else if (ballPointLocal.x >= this.length && ballPointLocal.x < this.length + ball.radius + this.width) { // 如果和第二个墙边碰撞
				trans = genTransportMatrix(ball.locationY - y2, -ball.locationX + x2, x2, y2);
				ballPointLocal = trans.mult(new Vector3f(ball.locationX, ball.locationY, 1));
				if (Math.abs(ballPointLocal.y) < ball.radius + this.width)
					bump = true;
			}

			if (bump) {
				// 将速度转化到局部坐标
				Vector3f ballVecLocal = trans.mult(new Vector3f(ball.velocityX, ball.velocityY, 0.f));
				Matrix3f transInv = trans.invert();

				// 处理球的位置，防止球进入墙内部（多增加一个小量，防止浮点数计算错误）
				if (ballPointLocal.y > 0) {
					ballPointLocal.y = ball.radius + this.width + MOVE_ADD_DELTA;
				} else {
					ballPointLocal.y = -(ball.radius + this.width + MOVE_ADD_DELTA);
				}
				Vector3f ballrec = transInv.mult(ballPointLocal);
				ball.locationX = ballrec.x;
				ball.locationY = ballrec.y;

				// 更新球的速度，更新前判断速度方向
				if (ballVecLocal.y > 0 && ballPointLocal.y < 0 || ballVecLocal.y < 0 && ballPointLocal.y > 0) {
					if(Math.abs(ballVecLocal.y) > BUMP_SOUND_V)
						this.audioBump.play();
					ballVecLocal.y *= e;
					ballVecLocal = transInv.mult(ballVecLocal);
					ball.velocityX = ballVecLocal.x;
					ball.velocityY = ballVecLocal.y;
					//限制反弹速度为0.8f
					if(Math.abs(ball.velocityX) > 0.8f)
						ball.velocityX = (ball.velocityX/
								Math.abs(ball.velocityX))*0.8f;
					if(Math.abs(ball.velocityY) > 0.8f)
						ball.velocityY = (ball.velocityY/
								Math.abs(ball.velocityY))*0.8f;
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

		//this.e = -0.5f;

		this.width = 0.01f;
		this.height = 0.08f;
		this.length = (float) Math
				.sqrt((this.x2 - this.x1) * (this.x2 - this.x1) + (this.y2 - this.y1) * (this.y2 - this.y1));

		this.matTransport = genTransportMatrix(x2 - x1, y2 - y1, x1, y1);
	}

}
