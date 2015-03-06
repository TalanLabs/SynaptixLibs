package com.synaptix.swingx.mapviewer.util;

public class Vec2 {

	public final double x;

	public final double y;

	public Vec2() {
		this(0, 0);
	}

	public Vec2(double x, double y) {
		super();

		this.x = x;
		this.y = y;
	}

	public final Vec2 add2(Vec2 vec2) {
		return new Vec2(this.x + vec2.x, this.y + vec2.y);
	}

	public final Vec2 add2(double x, double y) {
		return new Vec2(this.x + x, this.y + y);
	}

	public final Vec2 subtract2(Vec2 vec2) {
		return new Vec2(this.x - vec2.x, this.y - vec2.y);
	}

	public final Vec2 subtract2(double x, double y) {
		return new Vec2(this.x - x, this.y - y);
	}

	public final Vec2 multiply2(double value) {
		return new Vec2(this.x * value, this.y * value);
	}

	public final Vec2 multiply2(Vec2 vec2) {
		return new Vec2(this.x * vec2.x, this.y * vec2.y);
	}

	/**
	 * Calcul la longueur du vecteur
	 * 
	 * @return
	 */
	public final double getLength2() {
		return Math.sqrt(this.getLengthSquared2());
	}

	/**
	 * Calcul la longueur au carré su vecteur
	 * 
	 * @return
	 */
	public final double getLengthSquared2() {
		return (this.x * this.x) + (this.y * this.y);
	}

	/**
	 * Normalise le vecteur
	 * 
	 * @return
	 */
	public final Vec2 normalize2() {
		double length = this.getLength2();
		if (length == 0) {
			return this;
		} else {
			return new Vec2(this.x / length, this.y / length);
		}
	}

	/**
	 * Renvoi la perpendiculaire au vecteur
	 * 
	 * @return
	 */
	public final Vec2 perpendicular() {
		return new Vec2(-y, x);
	}

	/**
	 * Calcul la distance entre les 2 vecteurs
	 * 
	 * @param vec2
	 * @return
	 */
	public final double distanceTo2(Vec2 vec2) {
		return Math.sqrt(this.distanceToSquared2(vec2));
	}

	/**
	 * Calcul la distance au carré entre les 2 vecteurs
	 * 
	 * @param vec2
	 * @return
	 */
	public final double distanceToSquared2(Vec2 vec2) {
		double tmp;
		double result = 0.0;
		tmp = this.x - vec2.x;
		result += tmp * tmp;
		tmp = this.y - vec2.y;
		result += tmp * tmp;
		return result;
	}

	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.x).append(", ");
		sb.append(this.y);
		sb.append(")");
		return sb.toString();
	}
}
