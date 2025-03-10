package com.arabbank.hdf.uam.brain.mail;

import lombok.Data;

import java.util.List;

@Data
public class EmailRequest {
    private String itemCode;
    private String subject;
    private String text;
    private List<String> to;
    private List<String> cc;
}
