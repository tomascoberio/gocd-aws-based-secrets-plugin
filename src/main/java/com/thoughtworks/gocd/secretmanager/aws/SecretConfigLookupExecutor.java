package com.thoughtworks.gocd.secretmanager.aws;

import cd.go.plugin.base.executors.secrets.LookupExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.gocd.secretmanager.aws.models.Secrets;
import com.thoughtworks.gocd.secretmanager.aws.request.SecretConfigRequest;

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
        SecretManagerClient client;
        try {
            client = awsClientFactory.client(request.getConfiguration());

            List<String> secretIds = request.getKeys();

            if (secretIds == null || secretIds.isEmpty()) {
                return DefaultGoPluginApiResponse.badRequest("No secret key provided!!!");
            }

            Map result = client.lookup(request.getConfiguration().getSecretName());

            final Secrets secrets = new Secrets();

            secretIds.forEach(secretId -> {
                if (result.containsKey(secretId)) {
                    secrets.add(secretId, result.get(secretId).toString());
                }
            });

            return DefaultGoPluginApiResponse.success(toJson(secrets));
        } catch (Exception e) {
            LOGGER.error("Failed to lookup secret from AWS.", e);
            String errorMessage = format("Failed to lookup secrets from AWS - %s, See logs for more information.", e.getMessage());
            return DefaultGoPluginApiResponse.error(toJson(singletonMap("message", errorMessage)));
        }
    }

    @Override
    protected SecretConfigRequest parseRequest(String body) {
        return fromJson(body, SecretConfigRequest.class);
    }
}
