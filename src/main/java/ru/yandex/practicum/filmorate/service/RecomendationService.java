package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RecomendationService {

    private final FilmService filmService;
    private final UserService userService;

    public void getRecomendation(int userId) {
        Map<User, Map<Film, Integer>> userFilmsHashMap = new HashMap<>();
        Map<User, Map<Film, Integer>> diff = new HashMap<>();
        Map<User, Map<Film, Integer>> freq = new HashMap<>();
        Map<User, Integer> userIntegerHashMapIntersect = new HashMap<>();
        Map<User, List<Film>> userListMap = new HashMap<>();
        List<User> users = userService.getUsers();
        List<Film> allFilm = filmService.getFilms();
        //Составление карты предпочтений пользователей
        for (User user : users) {
            Map<Film, Integer> likesFilm = new HashMap<>();
            List<Film> userLikesFilms = filmService.getFilms().stream().filter(e -> e.getLikes()
                    .contains(user.getId())).collect(Collectors.toList());

            for (Film film : allFilm) {
                if (userLikesFilms.contains(film)) {
                    likesFilm.put(film, 1);
                } else {
                    likesFilm.put(film, 0);
                }
            }
            userFilmsHashMap.put(user, likesFilm);
        }

        //Поиск пересечений интересов среди пользователей
        for (User user : users) {
            for (User userFriend : users) {
                if (!Objects.equals(user, userFriend)) {
                    Map<Film, Integer> filmsUser = userFilmsHashMap.get(user);
                    Map<Film, Integer> filmsUserFriend = userFilmsHashMap.get(userFriend);
                    List<Film> intersect = new ArrayList<>();

                    for (Film film : filmsUser.keySet()) {
                        if (filmsUser.get(film) == 1 & filmsUserFriend.get(film) == 1) {
                            intersect.add(film);
                        }
                    }
                    if (userIntegerHashMapIntersect.getOrDefault(user, 0) < intersect.size())
                        userIntegerHashMapIntersect.put(user, intersect.size());
                }
            }
        }
        SortedSet<Map.Entry<User,Integer>> sortedIntersect = entriesSortedByValues(userIntegerHashMapIntersect);
        int max = 0;
        User user ;
        List<User> maxIntersect = new ArrayList<>();
        for (Map.Entry<User,Integer> ent : sortedIntersect) {
            log.info("Пользователь : {}. Макс пересечений : {}",ent.getKey().getName(),ent.getValue());
            if(max <= ent.getValue()){
                max = ent.getValue();
               maxIntersect.add(ent.getKey());
            }
        }

        User user1 = userService.getById(userId);
        List<Film> inS = new ArrayList<>();
        Map<User,List<Film>> diffMap = new HashMap<>();
            for ( User user2 : maxIntersect) {

                if (!Objects.equals(user1, user2)&&!diffMap.containsKey(user2)) {
                    Map<Film, Integer> filmsUser = userFilmsHashMap.get(user1);
                    Map<Film, Integer> filmsUserFriend = userFilmsHashMap.get(user2);

                    for (Map.Entry<Film,Integer> entry : filmsUser.entrySet()){
                        for (Map.Entry<Film, Integer> entry1 : filmsUserFriend.entrySet()){
                            if (Objects.equals(entry.getKey(),entry1.getKey())&&entry.getValue() == 0 && entry1.getValue()==1 && !inS.contains(entry.getKey())){
                                inS.add(entry.getKey());
                            }
                        }
                    }
                    diffMap.put(user1,inS);
                }
            }

        printMap(diffMap);

//        userIntegerHashMapIntersect.values().stream().peek(e ->log.info("В коллекции :",e.size())).close();

    }

    public void printMap (Map<User,List<Film>>userListMap) {
        for ( Map.Entry<User,List<Film>> ent : userListMap.entrySet()) {
            log.info("Польлзователь: {} Его не пролйканые фильмы : {}", ent.getKey().getId(),Arrays.toString(ent.getValue().toArray()));
        }
    }

    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e2.getValue().compareTo(e1.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
