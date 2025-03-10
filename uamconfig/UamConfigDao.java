package com.arabbank.hdf.uam.brain.uamconfig;

import com.arabbank.hdf.uam.brain.constants.SQL;
import com.arabbank.hdf.uam.brain.validation.nonad.NonAdUserClass;
import com.arabbank.hdf.uam.brain.uamconfig.model.*;
import com.arabbank.uam.commons.oo.dataaccess.DataAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UamConfigDao {
    private final DataAccessService dao;
    private final JdbcTemplate jdbcTemplate;


    public Optional<Integer> readSystemAbandonDaysLimit(String systemKey) {
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            "select (ABANDON_LIMIT_IN_DAYS) from UAM_SYSTEMS_CONFIG " +
                                    "inner join UAM_SYSTEMS US on US.ID = UAM_SYSTEMS_CONFIG.SYSTEM_ID " +
                                    "where SYSTEM_KEY = ?",
                            Integer.class, systemKey
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    public Map<NonAdUserClass, Integer> readExpiryDays() {
        log.debug("reading expiry days");
        Map<NonAdUserClass, Integer> expiryDaysMap = new HashMap<>();

        dao.getList("select * from UAM_CONFIG_EXPIRY_DAYS", rs -> {
            final String userClass = rs.getString(SQL.Column.ExpiryDays.USER_CLASS);
            final int expiryDays = rs.getInt(SQL.Column.ExpiryDays.EXPIRY_DAYS);
            expiryDaysMap.put(NonAdUserClass.parseString(userClass), expiryDays);
            return null;
        });

        return expiryDaysMap;
    }

    public Optional<Integer> readNeverLoggedInThreshold() {
        log.debug("reading never logged in threshold");
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject("select neverLoggedinThreshold from UAM_CONFIG_NEVER_LOGGEDIN_THRESHOLD", Integer.class)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int readAdGroupMaxMembers() {
        return dao.readInteger("select top 1 MAX_MEMBER from UAM_CONFIG_AD_GROUPS_MAX_MEMBERS");
    }

    public List<AdGroupNamingConventionRecord> readAdGroupNamingConventions() {
        log.debug("reading Active Directory Groups naming conventions.");
        return dao.readRecordsByTableName(SQL.Table.UAM_CONFIG_ADGROUP_NAME_CONVENTION, AdGroupNamingConventionRecord.class);
    }

    public List<AdGroupOULocationRecord> readAdGroupOULocationRecords() {
        log.debug("reading Active Directory Groups OUs.");
        return dao.readRecordsByTableName(SQL.Table.UAM_CONFIG_AD_GROUPS_OU_LOCATION, AdGroupOULocationRecord.class);
    }

    public List<AdExceptionalGroupRecord> readExceptionalGroupsRecords() {
        log.debug("reading Active Directory Exceptional Groups.");
        return dao.readRecordsByTableName("UAM_CONFIG_AD_GROUPS_EXCEPTIONAL", AdExceptionalGroupRecord.class);
    }

    public List<AdGroupExceptionRecord> readAdGroupExceptions() {
        return dao.readRecords("select * from UAM_CONFIG_AD_GROUPS_EXCEPTIONS", AdGroupExceptionRecord.class);
    }
}
