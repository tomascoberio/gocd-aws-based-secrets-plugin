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

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.*;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.gocd.secretmanager.aws.models.SecretConfig;
import com.thoughtworks.gocd.secretmanager.aws.request.SecretConfigRequest;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class SecretConfigLookupExecutorTest {
    @Mock
    private AWSSecretsManager secretsManager;
    @Mock
    private ListSecretsResult secretsResult;
    private SecretConfigRequest request;

    @BeforeEach
    void setUp() {
        initMocks(this);
        SecretConfig secretConfig = mock(SecretConfig.class);
        request = mock(SecretConfigRequest.class);
        when(request.getConfiguration()).thenReturn(secretConfig);
        when(secretsManager.listSecrets(any(ListSecretsRequest.class))).thenReturn(secretsResult);
    }

    @Test
    void shouldReturnLookupResponse() throws JSONException {
        String KEY1 = "key1";

        when(request.getKeys()).thenReturn(Collections.singletonList(KEY1));
        SecretListEntry key1 = new SecretListEntry().withName(KEY1);
        List<SecretListEntry> secretsListEntry = Collections.singletonList(key1);
        when(secretsResult.getSecretList()).thenReturn(secretsListEntry);

        GetSecretValueResult result1 = mock(GetSecretValueResult.class);
        when(secretsManager.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(result1);

        when(result1.getName()).thenReturn(KEY1);
        when(result1.getSecretString()).thenReturn("value1");

        final GoPluginApiResponse response = new SecretConfigLookupExecutor(secretsManager)
                .execute(request);

        assertThat(response.responseCode()).isEqualTo(200);
        final String expectedResponse = "[{\"key\":\"key1\",\"value\":\"value1\"}]";
        assertEquals(expectedResponse, response.responseBody(), true);
    }
}