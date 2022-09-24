/*
 * Copyright 2022 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.gocd.secretmanager.aws;

import com.thoughtworks.gocd.secretmanager.aws.models.SecretConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AWSClientFactory {
    private Map<SecretConfig, SecretManagerClient> secretManagerCache = new ConcurrentHashMap<>();
    private AWSCredentialsProviderChain awsCredentialsProviderChain;

    public AWSClientFactory(AWSCredentialsProviderChain awsCredentialsProviderChain) {
        this.awsCredentialsProviderChain = awsCredentialsProviderChain;
    }

    protected AWSClientFactory(AWSCredentialsProviderChain awsCredentialsProviderChain, Map<SecretConfig, SecretManagerClient> cache) {
        this.awsCredentialsProviderChain = awsCredentialsProviderChain;
        secretManagerCache = cache;
    }

    public SecretManagerClient client(SecretConfig secretConfig) {
        if (secretManagerCache.containsKey(secretConfig)) {
            return secretManagerCache.get(secretConfig);
        } else {
            closeAllClientsAndClearCache();
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

    /*
    * Currently since there is no mechanism to know if the SecretConfiguration has changed, there might be instances wherein
    * a client is created and not closed for ever. This is a temporary mechanism to clear cache and close the client until
    * we have a better way to know if the SecretConfig changed.
    * */
    private void closeAllClientsAndClearCache() {
        secretManagerCache.values().forEach(SecretManagerClient::close);
        secretManagerCache.clear();
    }
}
