INSERT iNTO genres ("name")
values ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO mpa_ratings ("name")
values ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');
--
-- INSERT INTO users (email, login, name, birthday)
-- values ('user1@mail.ru', 'user1', 'nameU1', '2000-01-01'),
--        ('user2@mail.ru', 'user2', 'nameU2', '2000-01-02'),
--        ('user3@mail.ru', 'user3', 'nameU3', '2000-01-03'),
--        ('user4@mail.ru', 'user4', 'nameU4', '2000-01-04');
--
-- INSERT INTO films (name, description, release_date, duration, mpa)
-- values ('фильм1', 'описание фильма 1', '2023-01-01', 120, 1),
--        ('фильм2', 'описание фильма 2', '2023-01-02', 150, 2),
--        ('фильм3', 'описание фильма 3', '2023-01-03', 180, 3),
--        ('фильм4', 'описание фильма 4', '2023-01-04', 200, 4);
--
-- INSERT INTO likes
-- values (1, 1), (1, 2), (2, 4), (2, 1);
--
-- INSERT INTO film_genre
-- values (1, 1), (1, 2), (2, 3), (3, 1), (4, 2);
--
-- INSERT INTO friends (user_id, friend_id)
-- values (1, 2), (2, 1), (3, 4), (1, 3), (2, 3);
