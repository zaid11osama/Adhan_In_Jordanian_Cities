package com.arabbank.hdf.uam.brain.uamconfig.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AdGroupExceptionRecord {
    private int exceptionId;
    private String exceptionCode;
    private String exceptionName;

    @Override
    public String toString() {
        return String.valueOf(exceptionId);
    }
}
