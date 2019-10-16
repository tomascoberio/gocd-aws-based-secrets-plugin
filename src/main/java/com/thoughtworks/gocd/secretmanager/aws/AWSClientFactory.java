package com.thoughtworks.gocd.secretmanager.aws;

import com.thoughtworks.gocd.secretmanager.aws.models.SecretConfig;

import java.util.HashMap;
import java.util.Map;

public class AWSClientFactory {
    private final Map<SecretConfig, SecretManagerClient> secretManagerCache = new HashMap<>();
    private AWSCredentialsProviderChain awsCredentialsProviderChain;

    public AWSClientFactory(AWSCredentialsProviderChain awsCredentialsProviderChain) {
        this.awsCredentialsProviderChain = awsCredentialsProviderChain;
    }

    public SecretManagerClient client(SecretConfig secretConfig) {
        if (secretManagerCache.containsKey(secretConfig)) {
            return secretManagerCache.get(secretConfig);
        }

        synchronized (secretManagerCache) {
            if (secretManagerCache.containsKey(secretConfig)) {
                return secretManagerCache.get(secretConfig);
            }

            SecretManagerClient secretsManager = new SecretManagerClient(secretConfig, awsCredentialsProviderChain);
            secretManagerCache.put(secretConfig, secretsManager);

            return secretsManager;
        }
    }
}
