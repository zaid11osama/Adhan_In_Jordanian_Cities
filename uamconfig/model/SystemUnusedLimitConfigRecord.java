package com.arabbank.hdf.uam.brain.uamconfig.model;

import lombok.Data;

@Data
public class SystemUnusedLimitConfigRecord {
    private String systemCode;
    private int unusedDaysLimit;
}
