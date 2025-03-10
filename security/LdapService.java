package com.arabbank.hdf.uam.brain.security;

import com.arabbank.hdf.uam.brain.auth.UserNotFoundException;
import com.arabbank.hdf.uam.brain.validation.ad.AdUserEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LdapService {
    private final LdapTemplate ldapTemplate;
    private final ContextSource contextSource;

    public Optional<AdUserEntry> getUserInfo(String sAMAccountName) {
        try {
            AdUserEntry userEntry = ldapTemplate.findOne(LdapQueryBuilder.query()
                    .where("sAMAccountName")
                    .is(sAMAccountName), AdUserEntry.class);
            return Optional.of(userEntry);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<DirContextOperations> getDirContextOperations(String sAMAccountName) {
        try {
            return Optional.of(ldapTemplate.searchForObject(LdapQueryBuilder.query()
                    .where("sAMAccountName")
                    .is(sAMAccountName), ctx -> (DirContextAdapter) ctx)
            );
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void authenticate(String sAMAccountName, String password) {
        DirContextOperations dirContextOperations = getDirContextOperations(sAMAccountName)
                .orElseThrow(UserNotFoundException::new);
        try {
            contextSource.getContext(dirContextOperations.getNameInNamespace(), password);
        } catch (AuthenticationException e) {
            log.error("AuthenticationException", e);
            throw new UserNotFoundException(e);
        }
    }

    public AdUserEntry authenticateAndFetchData(String sAMAccountName, String password) {
        AdUserEntry userEntry = getUserInfo(sAMAccountName)
                .orElseThrow(UserNotFoundException::new);

        contextSource.getContext(userEntry.getDistinguishedName().toString(), password);
        return userEntry;
    }
}

