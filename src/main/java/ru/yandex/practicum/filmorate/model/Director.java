package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
public class Director {
    private int id;
    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;

    public Director(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
