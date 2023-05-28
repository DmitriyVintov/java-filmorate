package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private int id;
    @NotNull
    @NotBlank
    private final String name;
    @NotBlank
    @Size(max = 200)
    private final String description;
    @MinimumDate("1895-12-28")
    private final LocalDate releaseDate;
    @NotNull
    @Positive
    private final long duration;
}