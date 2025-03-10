package com.arabbank.hdf.uam.brain.config;

import com.arabbank.hdf.uam.brain.utils.UamUtils;
import com.arabbank.uam.commons.oo.dataaccess.DataAccessService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.net.ServerSocketFactory;
import javax.sql.DataSource;
import java.net.InetAddress;
import java.net.ServerSocket;

@Configuration
@EnableCaching
@RequiredArgsConstructor
@EnableTransactionManagement
public class AppConfig {
    private final DataSource dataSource;

    @Bean
    public DataAccessService dataAccessService() {
        return new DataAccessService(dataSource);
    }

    @Profile("!prod")
    @Bean(destroyMethod = "stop")
    public GreenMail greenMail() {
        GreenMail greenMail = new GreenMail(ServerSetupTest.SMTP)
                .withConfiguration(GreenMailConfiguration.aConfig().withUser("omar-othman", "1234"));
        if (UamUtils.isPortAvailable(ServerSetupTest.SMTP.getPort())) {
            greenMail.start();
        }
        return greenMail;
    }
}
