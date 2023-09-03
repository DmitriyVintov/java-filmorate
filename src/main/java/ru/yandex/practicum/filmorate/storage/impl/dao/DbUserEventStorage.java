package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.model.UserEventOperation;
import ru.yandex.practicum.filmorate.model.UserEventType;

import java.util.List;

@Component
@AllArgsConstructor
public class DbUserEventStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String GET_USER_EVENT = "SELECT * from feed where user_id = ?";
    private static final String SET_USER_EVENT = "INSERT INTO feed(user_id, event_type, operation, entity_id) " +
            "values (?, ?, ?, ?)";

    public List<UserEvent> getAll(Integer id) throws NotFoundException {
        return jdbcTemplate.query(GET_USER_EVENT, userEventRowMapper(), id);
    }

    public void create(UserEvent userEvent) {
        jdbcTemplate.update(SET_USER_EVENT,
                userEvent.getUserId(),
                userEvent.getEventType().toString(),
                userEvent.getOperation().toString(),
                userEvent.getEntityId());
    }

    private RowMapper<UserEvent> userEventRowMapper() {
        return (rs, rowNum) -> {
            UserEvent userEvent = new UserEvent(
                    rs.getInt("user_id"),
                    UserEventType.valueOf(rs.getString("event_type")),
                    UserEventOperation.valueOf(rs.getString("operation")),
                    rs.getInt("entity_id")
            );
            userEvent.setEventId(rs.getInt("event_id"));
            userEvent.setTimestamp(rs.getTimestamp("timestamp").getTime());
            return userEvent;
        };
    }
}
