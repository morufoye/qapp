package com.QAPP.api.utility;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail")
@Data
public class ConfigProperties {
    private String host;
    private String username;
    private String password;
    private String port;
}
