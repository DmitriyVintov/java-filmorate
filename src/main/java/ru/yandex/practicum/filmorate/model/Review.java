package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    @NotNull(message = "Id отзыва не может быть пустым")
    @Min(value = 1, message = "Значение id не может быть меньше 1")
    private Integer reviewId;

    @NotNull
    private String content;

    @NotNull
    @JsonProperty("isPositive")
    private Boolean isPositive;

    private Integer userId;

    @NotNull
    private Integer filmId;

    @JsonProperty("useful")
    private int rating = 0;
}