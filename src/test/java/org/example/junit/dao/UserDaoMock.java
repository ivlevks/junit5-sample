package org.example.junit.dao;

import java.util.HashMap;
import java.util.Map;

public class UserDaoMock extends UserDao{

    private Map<Integer, Boolean> map = new HashMap<>();


    @Override
    public boolean delete(Integer userId) {
        return map.getOrDefault(userId, false);
    }
}

