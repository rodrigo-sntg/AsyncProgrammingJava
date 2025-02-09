package com.pipa.tester.hr.implementations;

import com.pipa.tester.hr.interfaces.IScore;
import com.pipa.tester.hr.interfaces.IScoreService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ScoreServiceImpl implements IScoreService {
	private static final AtomicReference<ScoreServiceImpl> instance = new AtomicReference<>();

	private final ConcurrentHashMap<String, ScoreImpl> scores = new ConcurrentHashMap<>();

	// Usamos um TreeSet com comparator que ordena por score decrescente e, em caso de empate, pelo userId.
	private final SortedSet<ScoreImpl> ranking = new TreeSet<>(
			Comparator.comparingLong(ScoreImpl::getScore).reversed()
					.thenComparing(ScoreImpl::getUserId)
	);

	private ScoreServiceImpl() {}

	public static ScoreServiceImpl getInstance() {
		instance.compareAndSet(null, new ScoreServiceImpl());
		return instance.get();
	}

	public synchronized void clearList() {
		scores.clear();
		ranking.clear();
	}

	@Override
	public void postScore(String userId, long points) {
		synchronized (this) {
			ScoreImpl scoreImpl = scores.get(userId);
			if (scoreImpl == null) {
				ScoreImpl newScore = new ScoreImpl(userId, points);
				scores.put(userId, newScore);
				ranking.add(newScore);
			} else {
				// Removemos o objeto do ranking antes de atualizar (evitando problemas de ordenação)
				ranking.remove(scoreImpl);
				scoreImpl.addScore(points);
				ranking.add(scoreImpl);
			}
			updateRanking();
		}
	}

	@Override
	public IScore retrieveScore(String userId) {
		return scores.get(userId);
	}

	/**
	 * Retorna um snapshot imutável do ranking.
	 * Como os objetos armazenados (ScoreImpl) são mutáveis e seus valores podem mudar depois
	 * de retornarmos a lista, aqui criamos novos objetos (ImmutableScore) com os valores atuais.
	 */
	@Override
	public List<IScore> retrieveRanking() {
		synchronized (this) {
			updateRanking();
			List<IScore> snapshot = new ArrayList<>(ranking.size());
			for (ScoreImpl s : ranking) {
				snapshot.add(new ImmutableScore(s.getUserId(), s.getScore(), s.getPosition()));
			}
			return snapshot;
		}
	}

	/**
	 * Atualiza as posições do ranking usando "dense ranking":
	 * se dois usuários têm a mesma pontuação, ambos recebem a mesma posição;
	 * a próxima posição é incrementada de 1.
	 */
	private void updateRanking() {
		int rank = 0;
		long lastScore = -1;
		for (ScoreImpl s : ranking) {
			if (s.getScore() != lastScore) {
				rank++;
				lastScore = s.getScore();
			}
			s.setPosition(rank);
		}
	}

	/**
	 * Classe auxiliar imutável que representa um snapshot de um Score.
	 */
	private static class ImmutableScore implements IScore {
		private final String userId;
		private final long score;
		private final int position;

		public ImmutableScore(String userId, long score, int position) {
			this.userId = userId;
			this.score = score;
			this.position = position;
		}

		@Override
		public String getUserId() {
			return userId;
		}

		@Override
		public long getScore() {
			return score;
		}

		@Override
		public int getPosition() {
			return position;
		}

		@Override
		public String ticker() {
			return "Score[" + getPosition() + "," + getUserId() + "," + getScore() + "]";
		}
	}
}
