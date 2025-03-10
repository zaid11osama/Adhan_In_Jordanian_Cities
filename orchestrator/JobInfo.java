package com.arabbank.hdf.uam.brain.orchestrator;

import lombok.Data;

@Data
public class JobInfo {
    private String code;
    private String name;
    private String cron;
}
