package com.pipa.tester.hr.implementations;

import com.pipa.tester.hr.interfaces.IScore;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ScoreImpl implements IScore {
	private final String userId;
	private final AtomicLong score;
	private final AtomicInteger position;

	public ScoreImpl(String userId, long score) {
		this.userId = userId;
		this.score = new AtomicLong(score);
		this.position = new AtomicInteger(0);
	}

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public long getScore() {
		return score.get();
	}

	public void addScore(long points) {
		score.addAndGet(points);
	}

	@Override
	public int getPosition() {
		return position.get();
	}

	public void setPosition(int pos) {
		this.position.set(pos);
	}

	@Override
	public String toString() {
		return ticker();
	}
}
