INSERT iNTO genres
values (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

INSERT INTO mpa_ratings
values (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

-- INSERT INTO users ("email", "login", "user_name", "birthday")
-- values ('user1@mail.ru', 'user1', 'nameU1', '2000-01-01'),
--        ('user2@mail.ru', 'user2', 'nameU2', '2000-01-02'),
--        ('user3@mail.ru', 'user3', 'nameU3', '2000-01-03'),
--        ('user4@mail.ru', 'user4', 'nameU4', '2000-01-04');
--
-- INSERT INTO films ("film_name", "description", "release_date", "duration", "mpa_id", "rate")
-- values ('фильм1', 'описание фильма 1', '2023-01-01', 120, 1, 3),
--        ('фильм2', 'описание фильма 2', '2023-01-02', 150, 3, 4),
--        ('фильм3', 'описание фильма 3', '2023-01-03', 180, 3, 1),
--        ('фильм4', 'описание фильма 4', '2023-01-04', 200, 2, 5);
--
-- INSERT INTO likes
-- values (1, 1), (1, 2), (2, 4), (2, 1);
--
-- INSERT INTO "films_genres"
-- values (1, 1), (1, 2), (2, 3), (3, 1), (4, 2);
--
-- INSERT INTO friends ("user_id", "friend_id")
-- values (1, 2), (2, 1), (3, 4), (1, 3), (2, 3);
