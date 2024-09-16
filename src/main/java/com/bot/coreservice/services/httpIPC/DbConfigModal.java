package com.bot.coreservice.services.httpIPC;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DbConfigModal {
    String organizationCode;
    String code;
    String schema;
    String databaseName;
    String server;
    String port;
    String database;
    String userId;
    String password;
    int connectionTimeout;
    int connectionLifetime;
    int minPoolSize;
    int maxPoolSize;
    boolean pooling;
}
