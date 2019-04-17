package com.thoughtworks.gocd.secretmanager.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.github.bdpiparva.plugin.base.dispatcher.LookupExecutor;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.gocd.secretmanager.aws.models.SecretConfig;
import com.thoughtworks.gocd.secretmanager.aws.models.Secrets;
import com.thoughtworks.gocd.secretmanager.aws.request.SecretConfigRequest;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.bdpiparva.plugin.base.GsonTransformer.fromJson;
import static com.github.bdpiparva.plugin.base.GsonTransformer.toJson;
import static java.util.Collections.singletonMap;

public class SecretConfigLookupExecutor extends LookupExecutor<SecretConfigRequest> {
    private AWSSecretsManagerClientBuilder clientBuilder;
    private AWSSecretsManager secretsManager;

    public SecretConfigLookupExecutor() {
        clientBuilder = AWSSecretsManagerClientBuilder
                .standard();
    }

    public SecretConfigLookupExecutor(AWSSecretsManager secretsManager) {
        this.secretsManager = secretsManager;
    }

    @Override
    protected GoPluginApiResponse execute(SecretConfigRequest request) {
        try {
            AWSSecretsManager client = getClient(request.getConfiguration());

            List<String> secretIds = request.getKeys();
            if (secretIds == null || secretIds.isEmpty()) {
                return DefaultGoPluginApiResponse.badRequest("No secret key provided!!!");
            }
            String nextToken = "";
            final Secrets secrets = new Secrets();
            do {
                ListSecretsResult listSecretsResult = client.listSecrets(new ListSecretsRequest());

                nextToken = listSecretsResult.getNextToken();
                List<SecretListEntry> secretList = listSecretsResult.getSecretList();

                if (secretList == null || secretList.isEmpty()) {
                    return DefaultGoPluginApiResponse.badRequest("No secrets found!!!");
                }

                List<GetSecretValueResult> getSecretValueResults = secretList.stream()
                        .filter(secretListEntry -> secretIds.contains(secretListEntry.getName()))
                        .map(secretListEntry -> client.getSecretValue(new GetSecretValueRequest()
                                .withSecretId(secretListEntry.getKmsKeyId())))
                        .collect(Collectors.toList());

                for (GetSecretValueResult secretValueResult : getSecretValueResults) {
                    String secretName = secretValueResult.getName();
                    String secretStringValue = secretValueResult.getSecretString();
                    ByteBuffer secretBinaryValue = secretValueResult.getSecretBinary();

                    if (!StringUtils.isEmpty(secretStringValue)) {
                        secrets.add(secretName, secretStringValue);
                    } else if (secretBinaryValue != null) {
                        secrets.add(secretName, secretBinaryValue.toString());
                    }
                }
            } while (nextToken != null);
            return DefaultGoPluginApiResponse.success(toJson(secrets));
        } catch (Exception e) {
            LOGGER.error("Failed to lookup secret from AWS.", e);
            return DefaultGoPluginApiResponse.error(toJson(singletonMap("message", "Failed to lookup secrets from AWS. See logs for more information.")));
        }
    }

    @Override
    protected SecretConfigRequest parseRequest(String body) {
        return fromJson(body, SecretConfigRequest.class);
    }


    private AWSSecretsManager getClient(SecretConfig requestConfiguration) {
        if (secretsManager == null) {
            AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(requestConfiguration.getAwsEndpoint(), requestConfiguration.getRegion());
            AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(requestConfiguration.getAwsAccessKey(), requestConfiguration.getAwsSecretAccessKey()));
            return clientBuilder
                    .withCredentials(credentialsProvider)
                    .withEndpointConfiguration(config)
                    .build();
        }
        return secretsManager;
    }
}
