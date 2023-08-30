package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class Director {

    @NotNull(message = "Id режиссера не может быть пустым")
    @Min(value = 1, message = "Значение id не может быть меньше 1")
    private int id;

    @NotBlank(message = "Имя режиссера не может быть пустым")
    private String name;

    public Director(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
