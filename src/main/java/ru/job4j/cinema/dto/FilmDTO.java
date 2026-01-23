package ru.job4j.cinema.dto;

import java.util.Objects;

public class FilmDTO {

    private int id;
    private String name;
    private String description;
    private int year;
    private String genre;
    private int minAge;
    private int duration;
    private int fileId;

    public FilmDTO() {
    }

    private FilmDTO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.year = builder.year;
        this.genre = builder.genre;
        this.minAge = builder.minAge;
        this.duration = builder.duration;
        this.fileId = builder.fileId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public static class Builder {
        private int id;
        private String name;
        private String description;
        private int year;
        private String genre;
        private int minAge;
        private int duration;
        private int fileId;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder genre(String genre) {
            this.genre = genre;
            return this;
        }

        public Builder minAge(int minAge) {
            this.minAge = minAge;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder fileId(int fileId) {
            this.fileId = fileId;
            return this;
        }

        public FilmDTO build() {
            return new FilmDTO(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilmDTO filmDTO = (FilmDTO) o;
        return id == filmDTO.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
