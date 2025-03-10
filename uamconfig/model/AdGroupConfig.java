package com.arabbank.hdf.uam.brain.uamconfig.model;

import com.arabbank.hdf.uam.brain.validation.ad.groups.AdGroupException;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class AdGroupConfig {
    private final Set<AdGroupException> exceptions;

    public AdGroupConfig() {
        exceptions = new HashSet<>();
    }

    public void addException(AdGroupException exception) {
        exceptions.add(exception);
    }

    public boolean hasException(AdGroupException exception) {
        return exceptions.contains(exception);
    }
}
