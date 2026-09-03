package com.sunrisedental.dao;

import com.sunrisedental.model.User;
import java.util.List;

public interface UserDAO {
    User findByUsername(String username);
    User findById(int id);
    List<User> findAll();
    boolean create(User user);
    boolean update(User user);
    boolean delete(int id);
}
