package com.cys901238.aiapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bridge")
public record BridgeProperties(
    String programId,
    String gatewayHost,
    String gatewayService,
    String repositoryDestination,
    String openAiModel,
    int connectTimeoutMs,
    int readTimeoutMs
) {}
