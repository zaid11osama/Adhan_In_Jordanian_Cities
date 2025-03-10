package com.arabbank.hdf.uam.brain.uamconfig.model;

import lombok.Data;

@Data
public class AdGroupCountLimitRecord {
    private String adGroupName;
    private int maxNumOfMembers;
    private int minNumOfMembers;
}
