package com.thoughtworks.gocd.secretmanager.aws.models;

import java.util.ArrayList;

public class Secrets extends ArrayList<Secret> {
    public void add(String key, String value) {
        add(new Secret(key, value));
    }
}
