package com.goldenflame.pg102.model;

public class Review {
    private int score;
    private String comment;

    public Review(int score, String comment) {
        this.score = score;
        this.comment = comment;
    }

    public Review(int score) {
        this(score, null);
    }

    // Getters
    public int getScore() { return score; }
    public String getComment() { return comment; }
}