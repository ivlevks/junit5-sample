package org.example.junit.service;

import org.example.junit.dao.UserDao;
import org.example.junit.dto.User;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserService {
    private final List<User> users = new ArrayList<>();
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean delete(Integer userId) {
        return userDao.delete(userId);
    }

    public List<User> getAll() {
        return users;
    }

    public void add(User... user) {
        this.users.addAll(Arrays.asList(user));
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username or password is null");
        }

        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
