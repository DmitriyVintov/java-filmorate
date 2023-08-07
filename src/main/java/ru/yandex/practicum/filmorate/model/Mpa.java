package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Mpa {
    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull
    private int id;
    @NotNull
    @NotBlank
    private String name;
}