package com.arabbank.hdf.uam.brain.mail;


import com.arabbank.hdf.uam.brain.mail.exception.InvalidItemCodeException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class EmailItemService {
    private final EmailItemService self;
    private final EmailItemRepo emailItemRepo;

    @Cacheable(cacheNames = "EMAIL_ITEM", key = "#itemCode")
    public Optional<EmailItem> getEmailItem(@NotNull String itemCode) {
        return emailItemRepo.findByItemCode(itemCode);
    }

    public boolean isEnabled(String itemCode) {
        return self.getEmailItem(itemCode)
                .orElseThrow(() -> new InvalidItemCodeException("Could not find email item with code: " + itemCode))
                .isEnabled();
    }
}
