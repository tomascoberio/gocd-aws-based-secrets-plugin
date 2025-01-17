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

package com.thoughtworks.gocd.secretmanager.aws.models;

import cd.go.plugin.base.annotations.Property;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SecretConfig {
    public static final String ACCESS_KEY = "AccessKey";
    public static final String SECRET_ACCESS_KEY = "SecretAccessKey";
    @Expose
    @SerializedName("Endpoint")
    @Property(name = "Endpoint", required = true)
    private String awsEndpoint;

    @Expose
    @SerializedName(ACCESS_KEY)
    @Property(name = "AccessKey", secure = true)
    private String awsAccessKey;

    @Expose
    @SerializedName(SECRET_ACCESS_KEY)
    @Property(name = "SecretAccessKey", secure = true)
    private String awsSecretAccessKey;

    @Expose
    @SerializedName("Region")
    @Property(name = "Region", required = true)
    private String region;

    @Expose
    @SerializedName("SecretName")
    @Property(name = "SecretName", required = true)
    private String secretName;

    @Expose
    @SerializedName("SecretCacheTTL")
    @Property(name = "SecretCacheTTL", required = false)
    private String secretCacheTTL;

//  Required for GSON de-serialization from JSON
    public SecretConfig() {

    }

    public SecretConfig(String awsEndpoint, String awsAccessKey, String awsSecretAccessKey) {
        this.awsEndpoint = awsEndpoint;
        this.awsAccessKey = awsAccessKey;
        this.awsSecretAccessKey = awsSecretAccessKey;
    }

    public String getSecretName() {
        return secretName;
    }

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public String getAwsSecretAccessKey() {
        return awsSecretAccessKey;
    }

    public String getRegion() {
        return region;
    }

    public String getAwsEndpoint() {
        return awsEndpoint;
    }

    public long getSecretCacheTTL() {
        return toLong(secretCacheTTL, TimeUnit.MINUTES.toMillis(30));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecretConfig that = (SecretConfig) o;
        return Objects.equals(awsEndpoint, that.awsEndpoint) &&
                Objects.equals(awsAccessKey, that.awsAccessKey) &&
                Objects.equals(awsSecretAccessKey, that.awsSecretAccessKey) &&
                Objects.equals(region, that.region) &&
                Objects.equals(secretName, that.secretName) &&
                Objects.equals(secretCacheTTL, that.secretCacheTTL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(awsEndpoint, awsAccessKey, awsSecretAccessKey, region, secretName, secretCacheTTL);
    }

    private long toLong(String valueAsString, long defaultValue) {
        if (StringUtils.isBlank(valueAsString)) {
            return defaultValue;
        }

        try {
            return Long.parseLong(valueAsString);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}