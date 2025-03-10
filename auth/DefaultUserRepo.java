package com.arabbank.hdf.uam.brain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DefaultUserRepo implements UserRepo {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean isUamUser(String username) {


        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "select cast(count(*) as bit) from UAM_USERS " +
                        "where USER_NAME = ? and IS_DELETED = 0",
                Boolean.class,
                username));

    }
}
