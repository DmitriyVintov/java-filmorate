package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private int id;
    @NotNull
    @Email
    private final String email;
    @NotNull
    @NotBlank
    private final String login;
    @NotNull
    private String name;
    @NotNull
    @Past
    private final LocalDate birthday;
}
