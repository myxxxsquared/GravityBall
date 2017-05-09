package gravityball.game;

import glm.mat._4.Mat4;

public class Quaternion {
	
	public final float i, j, k, w;
	
	public Quaternion(float i, float j, float k, float w) {
		this.i = i;
		this.j = j;
		this.k = k;
		this.w = w;
	}
	
	public Quaternion normalize() {
		// TODO 正则化
		return null;
	}
	
	public static Quaternion rotation(float x, float y, float z, float theta) {
		// TODO 旋转生成四元数
		return null;
	}
	
	public Quaternion multiply(Quaternion q2) {
		// TODO 四元数乘法
		return null;
	}
	
	public Mat4 toMat4() {
		// TODO 四元数化为矩阵
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Quaternion))
			return false;
		Quaternion qobj = (Quaternion)obj;
		return i == qobj.i &&
				j == qobj.j &&
				k == qobj.k &&
				w == qobj.w;
	}
}