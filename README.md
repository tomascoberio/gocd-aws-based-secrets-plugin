# AWS Secrets Manager plugin for GoCD

This plugin allows users to utilize [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/) as a secret manager for GoCD.

TODO
- [ ] Verify config view template

### Configure the plugin

The plugin requires secret config in order to connect with AWS - 

```xml
<secretConfigs>
    <secretConfig id="aws" pluginId="com.thoughtworks.gocd.secretmanager.aws">
      <description>Some description</description>
      <configuration>
        <property>
          <key>endpoint</key>
          <value>your-aws-endpoint</value>
        </property>
        <property>
          <key>access_key</key>
          <value>encrypted-access-key</value>
        </property>
        <property>
          <key>secret_access_key</key>
          <value>encrypted-secret-access-key</value>
        </property>
        <property>
          <key>region</key>
          <value>aws-region</value>
        </property>
        <property>
          <key>secret_name</key>
          <value>secret-name</value>
        </property>
      </configuration>
    </secretConfig>
  </secretConfigs>
```

| Field           | Required  | Description                                        |
| --------------- | --------- | -------------------------------------------------- |
| endpoint        | true      | The endpoint for the plugin to talk to             |
| access_key       | true      | The access key as a part of AWS credentials        |
| secret_access_key | true      | The secret access key as a part of AWS credentials |
| region          | true      | Region in which AWS secrets manager is hosted      |
| secret_name      | true      | The name of the secret to be utilized              |

### Building the code base
To build the jar, run `./gradlew clean test assemble`

### Troubleshooting

To enable debug log add following environment variable - 

```
GO_SERVER_SYSTEM_PROPERTIES=-Dplugin.com.thoughtworks.gocd.secretmanager.aws.log.level=debug
``` 

The plugin logs are written to `LOG_DIR/plugin-com.thoughtworks.gocd.secretmanager.aws.log`. The log dir 
- on Linux is `/var/log/go-server`
- on Windows are written to `C:\Program Files\Go Server\logs` 
- on docker images are written to `/godata/logs`

