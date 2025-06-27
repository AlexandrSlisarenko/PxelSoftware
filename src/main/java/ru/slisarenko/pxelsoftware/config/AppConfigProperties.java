package ru.slisarenko.pxelsoftware.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfigProperties {
    private Integer maxDepositPercent;
    private long startUpdateDepositMilliseconds;
    private long intervalUpdateDepositMilliseconds;
    private long startUpdateStateDepositMilliseconds;
    private long intervalUpdateStateDepositMin;
    private String cron;
}

