package com.ahamed.demoexchange.repository;

import com.ahamed.demoexchange.model.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class UserRepository {

    final JdbcTemplate jdbcTemplate;

    public User findByUsername(String username) {
        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE username = ?", (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));

            return user;
        }, username);

    }
}
