package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor(force = true)
public class Film {
    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;
    @NotNull
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @MinimumDate("1895-12-28")
    private LocalDate releaseDate;
    @NotNull
    @Min(value = 0, message = "Продолжительность фильма не должна быть отрицательной")
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;
    private int rate = 0;

    public void setLike(Integer id) {
        likes.add(id);
    }

    public void removeLike(Integer id) {
        likes.remove(id);
    }
}