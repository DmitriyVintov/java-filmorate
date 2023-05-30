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
    @NotBlank
    @Email
    private final String email;
    @NotNull
    @NotBlank
    @Pattern(regexp = "\\S*", message = "Логин не должен содержать пробелов")
    private final String login;
    private String name;
    @NotNull
    @Past
    private final LocalDate birthday;
}
