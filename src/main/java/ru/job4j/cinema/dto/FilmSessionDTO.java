package ru.job4j.cinema.dto;

import java.time.LocalDateTime;

public class FilmSessionDTO {
    private int id;
    private String filmName;
    private String hallName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int price;
    private int hallRowCount;
    private int hallPlaceCount;

    public FilmSessionDTO() {
    }

    private FilmSessionDTO(Builder builder) {
        this.id = builder.id;
        this.filmName = builder.filmName;
        this.hallName = builder.hallName;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.price = builder.price;
        this.hallRowCount = builder.hallRowCount;
        this.hallPlaceCount = builder.hallPlaceCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getHallRowCount() {
        return hallRowCount;
    }

    public void setHallRowCount(int hallRowCount) {
        this.hallRowCount = hallRowCount;
    }

    public int getHallPlaceCount() {
        return hallPlaceCount;
    }

    public void setHallPlaceCount(int hallPlaceCount) {
        this.hallPlaceCount = hallPlaceCount;
    }

    public static class Builder {
        private int id;
        private String filmName;
        private String hallName;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private int price;
        private int hallRowCount;
        private int hallPlaceCount;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder filmName(String filmName) {
            this.filmName = filmName;
            return this;
        }

        public Builder hallName(String hallName) {
            this.hallName = hallName;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder price(int price) {
            this.price = price;
            return this;
        }

        public Builder hallRowCount(int hallRowCount) {
            this.hallRowCount = hallRowCount;
            return this;
        }

        public Builder hallPlaceCount(int hallPlaceCount) {
            this.hallPlaceCount = hallPlaceCount;
            return this;
        }

        public FilmSessionDTO build() {
            return new FilmSessionDTO(this);
        }
    }

    @Override
    public String toString() {
        return "FilmSessionDTO{"
                + "id=" + id
                + ", filmName='" + filmName + '\''
                + ", hallName='" + hallName + '\''
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", price=" + price
                + ", hallRowCount=" + hallRowCount
                + ", hallPlaceCount=" + hallPlaceCount
                + '}';
    }
}
