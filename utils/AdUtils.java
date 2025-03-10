package com.arabbank.hdf.uam.brain.utils;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

public class AdUtils {
    private static final long AD_EPOCH_DIFF = 11644473600000L; // Number of milliseconds between AD epoch and Java epoch


    public static long localDateTimeToFileTime(LocalDateTime dateTime) {
        // Convert LocalDateTime to milliseconds since the Unix epoch (1970-01-01T00:00:00Z)
        long millisecondsSinceUnixEpoch = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        return (AD_EPOCH_DIFF + millisecondsSinceUnixEpoch) * 10_000;
    }


    /**
     * Convert fileTime to LocalDateTime.
     * File time is 100-nanosecond intervals since AD epoch (Jan 1, 1601 UTC),
     * which is the value used in active directory to represent a date.
     * Example of fields using file time: lastLogonTimeStamp, pwdLastSet and accountExpires.
     *
     * @param fileTime 100-nanosecond intervals since (Jan 1, 1601 UTC).
     * @return LocalDateTime converted from fileTime.
     */
    public static LocalDateTime fileTimeToDate(Long fileTime) {
        if (fileTime == null) {
            return null;
        }
        long millisecondsSinceAdEpoch = (fileTime / 10000) - AD_EPOCH_DIFF;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millisecondsSinceAdEpoch), ZoneId.systemDefault());
    }

    public static Map<String, List<String>> getDnMap(Name dn) {
        Map<String, List<String>> map = new HashMap<>();
        String[] dnTokens = dn.toString().split("(?<!\\\\),");

        for (String token : dnTokens) {
            int indexOfEquals = token.indexOf('=');

            String key = token.substring(0, indexOfEquals).toUpperCase(); // to upper case is important
            String value = Rdn.unescapeValue(token.substring(indexOfEquals + 1)).toString();
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        return map;
    }


    public static String getFirstToken(Name dn, String key) {
        return getDnMap(dn).getOrDefault(key.toUpperCase(), Collections.singletonList(null))
                .get(0);
    }

    public static String getOu(Name dn) {
        return getFirstToken(dn, "OU");
    }

    public static String getDomain(Name dn) {
        return getFirstToken(dn, "DC");
    }
}