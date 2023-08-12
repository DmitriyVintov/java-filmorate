package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor(force = true)
public class User {
    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    @Min(value = 1, message = "Значение id не может быть меньше 1")
    private Integer id;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Не верный формат email")
    private String email;
    @NotBlank(message = "Login не может быть пустым")
    @Pattern(regexp = "\\S*", message = "Логин не должен содержать пробелов")
    private String login;
    private String name;
    @NotNull(message = "Дата рождения не может быть пустой")
    @Past
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();

    public void setFriends(Integer id) {
        friends.add(id);
    }

    public void removeFriends(Integer id) {
        friends.remove(id);
    }
}
