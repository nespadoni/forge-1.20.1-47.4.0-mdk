package com.aurorionsmp.client;

public class BalloonData {
	private final String message;
	private final long creationTime;
	private final boolean isDistant;

	public BalloonData(String message, boolean isDistant) {
		this.message = message;
		this.creationTime = System.currentTimeMillis();
		this.isDistant = isDistant;
	}

	public String getMessage() {
		return message;
	}

	public boolean isDistant() {
		return isDistant;
	}

	public boolean isExpired(long duration) {
		return System.currentTimeMillis() - creationTime > duration;
	}

	public long getAge() {
		return System.currentTimeMillis() - creationTime;
	}
}