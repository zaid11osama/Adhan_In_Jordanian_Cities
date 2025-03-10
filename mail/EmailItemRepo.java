package com.arabbank.hdf.uam.brain.mail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface EmailItemRepo extends JpaRepository<EmailItem, Integer> {
    Optional<EmailItem> findByItemCode(String itemCode);
}
