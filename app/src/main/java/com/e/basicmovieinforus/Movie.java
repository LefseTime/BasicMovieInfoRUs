package com.e.basicmovieinforus;

public class Movie {
    private String poster;
    private String title;
    private String date;
    private String overview;

    public Movie(String poster, String title, String date, String overview) {
        this.poster = poster;
        this.title = title;
        this.date = date;
        this.overview = overview;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;

    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
