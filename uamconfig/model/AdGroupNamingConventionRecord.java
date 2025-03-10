package com.arabbank.hdf.uam.brain.uamconfig.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
public class AdGroupNamingConventionRecord {
    private String country;
    private String type;
    private String convention;
}
