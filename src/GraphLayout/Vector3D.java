package GraphLayout;

public class Vector3D {
	private float x;
	private float y;
	private float z;

	public Vector3D(float x_, float y_, float z_) {

		x = x_;
		y = y_;
		z = z_;

	}

	public Vector3D(int x_, int y_, int z_) {

		x = x_;
		y = y_;
		z = z_;

	}

	public Vector3D() {

		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}

	public float getX() {

		return x;

	}

	public float getY() {

		return y;

	}

	public float getZ() {
		return z;
	}

	public void setX(float x_) {

		x = x_;

	}

	public void setY(float y_) {

		y = y_;

	}

	public void setZ(float z_) {
		z = z_;
	}

	public void setXY(float x_, float y_) {
		x = x_;
		y = y_;
	}

	public void setXYZ(float x_, float y_, float z_) {

		x = x_;
		y = y_;
		z = z_;

	}

	public void setVector(Vector3D v) {

		x = v.getX();
		y = v.getY();
		z = v.getZ();
	}

	public Vector3D copy() {

		return new Vector3D(x, y, z);
	}

	public float magnitude() {

		return (float) Math.sqrt(x * x + y * y + z * z);

	}

	public Vector3D add(Vector3D v) {

		return new Vector3D(x + v.getX(), y + v.getY(), z + v.getZ());

	}

	public Vector3D subtract(Vector3D v) {

		return new Vector3D(x - v.getX(), y - v.getY(), z - v.getZ());

	}

	public Vector3D multiply(float n) {

		return new Vector3D(x * n, y * n, z * n);

	}

	public Vector3D divide(float n) {

		return new Vector3D(x / n, y / n, z / n);
	}

	public void normalize() {

		float m = magnitude();

		x = x / m;
		y = y / m;
		z = z / m;

	}

	public void limit(float max) {

		if (magnitude() > max) {
			normalize();
			x *= max;
			y *= max;
			z *= max;
		}

	}

	public float heading2D() {

		float angle = (float) Math.atan2(y * -1.0f, x);
		return -1.0f * angle;

	}

	public static Vector3D add(Vector3D v1, Vector3D v2) {

		return new Vector3D(v1.getX() + v2.getX(), v1.getY() + v2.getY(),
				v1.getZ() + v2.getZ());

	}

	public static Vector3D subtract(Vector3D v1, Vector3D v2) {

		return new Vector3D(v1.getX() - v2.getX(), v1.getY() - v2.getY(),
				v1.getZ() - v2.getZ());
	}

	public static Vector3D divide(Vector3D v1, float n) {

		return new Vector3D(v1.getX() / n, v1.getY() / n, v1.getZ() / n);

	}

	public static Vector3D multiply(Vector3D v1, float n) {

		return new Vector3D(v1.getX() * n, v1.getY() * n, v1.getZ() * n);

	}

	public static float distance(Vector3D v1, Vector3D v2) {

		float dx = v1.getX() - v2.getX();
		float dy = v1.getY() - v2.getY();
		float dz = v1.getZ() - v2.getZ();
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

	}

}
