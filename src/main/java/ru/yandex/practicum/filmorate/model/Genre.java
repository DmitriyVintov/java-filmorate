package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Genre {
    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull
    private Integer id;
    @NotNull
    @NotBlank
    private String name;
}
