package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class UserEvent {
    public UserEvent(Integer userId, UserEventType eventType, UserEventOperation operation, long entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }

    private Integer eventId;
    private long timestamp;
    @NotNull
    @Min(value = 1, message = "Значение id не может быть меньше 1")
    private Integer userId;
    @NotNull
    private UserEventType eventType;
    @NotNull
    private UserEventOperation operation;
    @NotNull
    @Min(value = 1, message = "Значение id не может быть меньше 1")
    private long entityId;
}