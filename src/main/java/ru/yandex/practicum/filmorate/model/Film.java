package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private Integer id;
    @NotNull
    @NotBlank
    private final String name;
    @NotBlank
    @Size(max = 200)
    private final String description;
    @MinimumDate("1895-12-28")
    private final LocalDate releaseDate;
    @NotNull
    @Min(value = 0, message = "Продолжительность фильма не должна быть отрицательной")
    private final long duration;
    private final Set<Integer> likes = new HashSet<>();

    public void setLikes(Integer id) {
        likes.add(id);
    }

    public void removeLike(Integer id) {
        likes.remove(id);
    }
}