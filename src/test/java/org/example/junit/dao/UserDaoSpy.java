package org.example.junit.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDao {

    private final UserDao userDao;
    private Map<Integer, Boolean> map = new HashMap<>();

    public UserDaoSpy(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean delete(Integer userId) {
        return map.getOrDefault(userId, userDao.delete(userId));
    }
}
