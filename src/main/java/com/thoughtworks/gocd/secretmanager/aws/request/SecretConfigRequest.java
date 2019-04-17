package com.thoughtworks.gocd.secretmanager.aws.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thoughtworks.gocd.secretmanager.aws.models.SecretConfig;

import java.util.List;

public class SecretConfigRequest {
    @Expose
    @SerializedName("configuration")
    private SecretConfig configuration;

    @Expose
    @SerializedName("keys")
    private List<String> keys;

    public SecretConfigRequest() {
    }

    public SecretConfig getConfiguration() {
        return configuration;
    }

    public List<String> getKeys() {
        return keys;
    }
}
