package com.example.demo.refactor;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @author itanton
 */
public class TvShow extends Event {

    private Integer season;

    public TvShow(Integer id, String name, LocalDate releaseDate, Integer season) {
        super(id, name, releaseDate);
        this.season = season;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TvShow tvShow = (TvShow) o;
        return Objects.equals(season, tvShow.season);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), season);
    }

    @Override
    public String toString() { //TODO
        return "TvShow{" +
                "season='" + season + '\'' +
                '}';
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }
}
