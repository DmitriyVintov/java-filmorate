package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Genre {
    @NotNull
    private final Integer id;
    @NotNull
    @NotBlank
    private final String name;
}
