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

package com.thoughtworks.gocd.secretmanager.aws.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SecretConfigTest {
    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    @Test
    void shouldDeSerializableFromAJSON() {
        String secretConfigJSON = "{\n" +
                "\"Endpoint\": \"aws endpoint\",\n" +
                "\"AccessKey\": \"access key\",\n" +
                "\"SecretAccessKey\": \"secret access key\",\n" +
                "\"Region\": \"us_west\",\n" +
                "\"SecretName\": \"my_secret\",\n" +
                "\"SecretCacheTTL\": \"1000\"\n" +
                "}";

        SecretConfig secretConfig = fromJSON(secretConfigJSON);

        assertThat(secretConfig.getAwsEndpoint()).isEqualTo("aws endpoint");
        assertThat(secretConfig.getAwsAccessKey()).isEqualTo("access key");
        assertThat(secretConfig.getAwsSecretAccessKey()).isEqualTo("secret access key");
        assertThat(secretConfig.getRegion()).isEqualTo("us_west");
        assertThat(secretConfig.getSecretName()).isEqualTo("my_secret");
        assertThat(secretConfig.getSecretCacheTTL()).isEqualTo(1000);
    }

    @Test
    void shouldDefaultSecretCacheTTLTo30Minutes() {
        SecretConfig secretConfig = fromJSON("{}");

        assertThat(secretConfig.getSecretCacheTTL()).isEqualTo(1800000L);
    }

    private SecretConfig fromJSON(String secretConfig) {
        return GSON.fromJson(secretConfig, SecretConfig.class);
    }
}
