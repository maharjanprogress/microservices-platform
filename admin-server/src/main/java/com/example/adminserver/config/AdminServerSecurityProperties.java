package com.example.adminserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "monitoring.security")
public class AdminServerSecurityProperties {

    private String adminUsername = "admin";
    private String adminPassword = "admin-secret";
    private String actuatorUsername = "actuator";
    private String actuatorPassword = "actuator-secret";

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getActuatorUsername() {
        return actuatorUsername;
    }

    public void setActuatorUsername(String actuatorUsername) {
        this.actuatorUsername = actuatorUsername;
    }

    public String getActuatorPassword() {
        return actuatorPassword;
    }

    public void setActuatorPassword(String actuatorPassword) {
        this.actuatorPassword = actuatorPassword;
    }
}
