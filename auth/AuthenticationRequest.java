package com.arabbank.hdf.uam.brain.auth;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class AuthenticationRequest {
    private String username;
    private String password;
}
