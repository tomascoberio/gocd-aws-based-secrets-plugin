/*
 * Copyright 2019 ThoughtWorks, Inc.
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
