package com.pipa.tester.hr.interfaces;

/**
 * Representa a pontuação de um usuário, incluindo ID, score e ranking
 */
public interface IScore {
    /** ID do usuário */
    String getUserId();

    /** Pontuação do usuário */
    long getScore();

    /** Posição no ranking, onde 1 é o primeiro */
    int getPosition();

    /** Retorna uma string formatada do objeto */
    default String ticker() {
        return "Score[" + getPosition() + "," + getUserId() + "," + getScore() + "]";
    }
}
