package com.arabbank.hdf.uam.brain.uamconfig;

import com.arabbank.hdf.uam.brain.uamconfig.model.*;
import com.arabbank.hdf.uam.brain.validation.ad.groups.AdGroupEntry;
import com.arabbank.hdf.uam.brain.validation.ad.groups.AdGroupException;
import com.arabbank.hdf.uam.brain.validation.ad.groups.AdGroupSimpleType;
import com.arabbank.hdf.uam.brain.validation.nonad.NonAdUserClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Omar-Othman
 */
@Service
@RequiredArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UamConfigService { //TODO: revamp with @Cacheable and maybe SOC
    private final UamConfigDao dao;
    private final UamConfigService self;

    private Map<NonAdUserClass, Integer> expiryDays;
    @Getter
    private int neverLoggedInThreshold;
    private Map<String, AdGroupConfig> groupConfigMap;
    private Map<Integer, String> adGroupsOuLocationMap;
    private final Map<Integer, AdGroupException> adGroupsExceptionMap = new HashMap<>();


    @Getter
    @Setter
    private int adGroupsMaxMembersLimit = Integer.MAX_VALUE;


    @PostConstruct
    public void init() {
        expiryDays = new HashMap<>();

        // ad groups
        groupConfigMap = new HashMap<>();
        adGroupsOuLocationMap = new HashMap<>();
        List<AdGroupExceptionRecord> records = dao.readAdGroupExceptions();
        for (AdGroupExceptionRecord record : records) {
            adGroupsExceptionMap.put(record.getExceptionId(), AdGroupException.valueOf(record.getExceptionCode()));
        }
    }

    public void resetNonAdUsersConfig() { // TODO: use @Cacheable and remove this method
        this.expiryDays = dao.readExpiryDays();
        this.neverLoggedInThreshold = dao.readNeverLoggedInThreshold().orElse(Integer.MAX_VALUE);
    }

    @Cacheable(cacheNames = "IMPORTED_SYSTEMS_CONFIG", key = "#systemKey")
    public int getSystemAbandonDaysLimit(String systemKey) {
        return dao.readSystemAbandonDaysLimit(systemKey).orElse(Integer.MAX_VALUE);
    }

    public int getExpiryDays(NonAdUserClass userClass) {
        return expiryDays.getOrDefault(userClass, 0);
    }

    public int getTempExpiryDays() {
        return expiryDays.getOrDefault(NonAdUserClass.TEMP, Integer.MAX_VALUE);
    }

    public int getThirdExpiryDays() {
        return expiryDays.getOrDefault(NonAdUserClass.THIRD_PARTY, Integer.MAX_VALUE);
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * AD Groups
     *
     * * * * * * * * * * * * * * * * * * * * * * * * */
    public void resetAdGroupsConfigs() {
        // Max Members
        adGroupsMaxMembersLimit = dao.readAdGroupMaxMembers();

        // OU Location
        adGroupsOuLocationMap.clear();
        List<AdGroupOULocationRecord> ouLocationRecords = dao.readAdGroupOULocationRecords();
        for (AdGroupOULocationRecord record : ouLocationRecords) {
            adGroupsOuLocationMap.put(Objects.hash(record.getCountry().toLowerCase(), record.getType().toLowerCase()), record.getOu());
        }

        // Exceptional Groups
        groupConfigMap.clear();
        List<AdExceptionalGroupRecord> records = dao.readExceptionalGroupsRecords();

        for (AdExceptionalGroupRecord record : records) {
            List<AdGroupException> exceptions = parseJsonExceptions(record.getExceptions());

            for (AdGroupException exception : exceptions) {
                getAdGroupConfig(record.getGroupName()).addException(exception);
            }
        }
    }

    public AdGroupConfig getAdGroupConfig(String groupName) {
        AdGroupConfig groupConfig = groupConfigMap.computeIfAbsent(groupName, k -> new AdGroupConfig());
        groupConfigMap.put(groupName, groupConfig);

        return groupConfig;
    }

    public String getNamingConvention(AdGroupEntry group) {
        return self.getAdGroupsNameingConventionMap().get(
                Objects.hash("jordan", AdGroupSimpleType.fromAdGroupType(group.getType()))
        ); // FIXME: country value hard coded
    }

    @Cacheable(cacheNames = "config.adGroups", key = "'nameConvention'")
    public Map<Integer, String> getAdGroupsNameingConventionMap() {
        Map<Integer, String> map = new HashMap<>();
        List<AdGroupNamingConventionRecord> namingConventionRecords = dao.readAdGroupNamingConventions();
        for (AdGroupNamingConventionRecord record : namingConventionRecords) {
            AdGroupSimpleType groupType = EnumUtils.getEnumIgnoreCase(AdGroupSimpleType.class, record.getType());
            map.put(Objects.hash(record.getCountry().toLowerCase(), groupType), record.getConvention());
        }
        return map;
    }

    public String getAdGroupOU(AdGroupEntry adGroup) {
        return adGroupsOuLocationMap.get(Objects.hash("jordan", adGroup.getUamClassification()));
    }

    private List<AdGroupException> parseJsonExceptions(String jsonList) {
        try {
            List<Integer> exceptionIds = jsonList == null
                    ? Collections.emptyList()
                    : new ObjectMapper().readValue(jsonList, new TypeReference<List<Integer>>() {
            });
            return exceptionIds.stream().map(adGroupsExceptionMap::get).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
