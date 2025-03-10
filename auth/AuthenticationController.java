package com.arabbank.hdf.uam.brain.auth;

import com.arabbank.hdf.uam.brain.security.JwtUtil;
import com.arabbank.hdf.uam.brain.security.LdapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class AuthenticationController {
    private final JwtUtil jwtTokenUtil;
    private final LdapService ldapService;
    private final UserRepo userRepo;

    public AuthenticationController(JwtUtil jwtTokenUtil, LdapService ldapService, UserRepo userRepo) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.ldapService = ldapService;
        this.userRepo = userRepo;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
    {
        ldapService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        if (!userRepo.isUamUser(authenticationRequest.getUsername())) {
            throw new UserNotFoundException("Not UAM user");
        }
        final String jwt = jwtTokenUtil.generateToken(authenticationRequest.getUsername());
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
