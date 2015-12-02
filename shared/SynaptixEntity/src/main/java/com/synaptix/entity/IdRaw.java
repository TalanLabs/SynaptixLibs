package com.synaptix.entity;

public final class IdRaw implements IId {

	private static final long serialVersionUID = 1585448539513037798L;

	private final String hex;

	public IdRaw(String hex) {
		super();

		if (hex == null) {
			throw new IllegalArgumentException("ID cannot be null");
		}

		this.hex = hex;
	}

	public String getHex() {
		return hex;
	}

	@Override
	public int hashCode() {
		return hex != null ? hex.hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IdRaw) {
			IdRaw o = (IdRaw) obj;
			return hex != null && o.hex != null && hex.equals(o.hex);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return hex;
	}
}
