package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Validated
@NoArgsConstructor(force = true)
public class Film {
    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = new Mpa(1,"");
    }


    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    @Min(value = 1, message = "Значение id не может быть меньше 1")
    private Integer id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(max = 200, message = "Должно быть не более 200 символов")
    private String description;
    @MinimumDate("1895-12-28")
    private LocalDate releaseDate;
    @NotNull(message = "Продолжительность фильма не может быть пустой")
    @Min(value = 0, message = "Продолжительность фильма не должна быть отрицательной")
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    @Valid
    private Set<Genre> genres = new HashSet<>();
    private Set<Director> directors = new HashSet<>();
    private Mpa mpa;
    private int rate = 0;

    public void setLike(Integer id) {
        likes.add(id);
    }

    public void removeLike(Integer id) {
        likes.remove(id);
    }
}