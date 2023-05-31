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

import cd.go.plugin.base.executors.secrets.LookupExecutor;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.gocd.secretmanager.aws.models.Secret;
import com.thoughtworks.gocd.secretmanager.aws.models.Secrets;
import com.thoughtworks.gocd.secretmanager.aws.request.SecretConfigRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cd.go.plugin.base.GsonTransformer.fromJson;
import static cd.go.plugin.base.GsonTransformer.toJson;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;

public class SecretConfigLookupExecutor extends LookupExecutor<SecretConfigRequest> {
    private AWSClientFactory awsClientFactory;

    public SecretConfigLookupExecutor() {
        this(new AWSClientFactory(new AWSCredentialsProviderChain()));
    }

    SecretConfigLookupExecutor(AWSClientFactory awsClientFactory) {
        this.awsClientFactory = awsClientFactory;
    }

    @Override
    protected GoPluginApiResponse execute(SecretConfigRequest request) {
        final Secrets secrets = new Secrets();
        try {
            for (String key : request.getKeys()) {
                String secretString = retrieve(
                        request.getConfiguration().getAwsAccessKey(),
                        request.getConfiguration().getAwsSecretAccessKey(),
                        key,
                        "eu-north-1"
                );
                LOGGER.info(key + "=" + secretString);

                Map<String, String> result =  new Gson().fromJson(secretString, Map.class);

                ObjectMapper objectMapper = new ObjectMapper();
                String prettyPrintedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
                LOGGER.info("SECRET JSON TO MAP: " + prettyPrintedJson);
                secrets.add(key, secretString);
                /*
                for (String item : result.keySet()) {
                    String value = result.get(item);
                    secrets.add(item, value);
                }
                 */

            }
            return DefaultGoPluginApiResponse.success(toJson(secrets));
        } catch (Exception e) {
            LOGGER.error("Failed to lookup secret from AWS.", e);
            String errorMessage = format("Failed to lookup secrets from AWS - %s, See logs for more information.", e.getMessage());
            return DefaultGoPluginApiResponse.error(toJson(singletonMap("message", errorMessage)));
        }
    }

    public String retrieve(String key, String secretKey, String secretName, String region) {

        LOGGER.info("LETS BRING SECRET: " + secretName);

        try {
            // ----------------------------------------------------------------
            BasicAWSCredentials credentials = new BasicAWSCredentials(key, secretKey);
            AWSSecretsManager secretsManagerClient = AWSSecretsManagerClientBuilder.standard()
                    .withRegion(region)
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .build();

            ListSecretsRequest listSecretsRequest = new ListSecretsRequest();
            ListSecretsResult listSecretsResult = secretsManagerClient.listSecrets(listSecretsRequest);

            GetSecretValueRequest requestToAWS = new GetSecretValueRequest();

            LOGGER.info("[SECRETS AVAILABLE TO READ IN AWS:");
            for (SecretListEntry secret : listSecretsResult.getSecretList()) {
                LOGGER.info(secret.getName());

            }
            LOGGER.info("]");

            requestToAWS.setSecretId(secretName);
            GetSecretValueResult secretValue = secretsManagerClient.getSecretValue(requestToAWS);
            return secretValue.getSecretString();

        } catch (Exception e) {
            LOGGER.error("Error retrieving secret", e);
            return "_empty_";
        }
    }

    @Override
    protected SecretConfigRequest parseRequest(String body) {
        return fromJson(body, SecretConfigRequest.class);
    }
}
