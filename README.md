
# AWS Secrets Manager plugin for GoCD

This is a GoCD Secrets Plugin which allows users to use [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/) as a secret manager for GoCD.

## Table of Contents
* [Configure the plugin](#configure-the-plugin)
* [Building the code base](#building-the-code-base)
* [Troubleshooting](#troubleshooting)

### Configure the plugin

The plugin needs to be configured with a secret config in order to connect to AWS Secrets Manager. The configuration can be added from the Secrets Management page under Admin > Secret Management.

Alternatively, the configuration can be added directly to the config.xml using the configuration.

```xml
<secretConfigs>
    <secretConfig id="aws" pluginId="com.thoughtworks.gocd.secretmanager.aws">
      <description>Dummy description</description>
      <configuration>
        <property>
          <key>Endpoint</key>
          <value>your-aws-endpoint</value>
        </property>
        <property>
          <key>AccessKey</key>
          <value>encrypted-access-key</value>
        </property>
        <property>
          <key>SecretAccessKey</key>
          <value>encrypted-secret-access-key</value>
        </property>
        <property>
          <key>Region</key>
          <value>aws-region</value>
        </property>
        <property>
          <key>SecretName</key>
          <value>secret-name</value>
        </property>
      </configuration>
      <rules>
        <allow action="refer" type="environment">env_*</allow>
        <deny action="refer" type="pipeline_group">my_group</deny>
        <allow action="refer" type="pipeline_group">other_group</allow>
      </rules>
    </secretConfig>
  </secretConfigs>
```

`<rules>` tag defines where this secretConfig is allowed/denied to be referred. For more details about rules and examples refer the GoCD Secret Management [documentation](https://docs.gocd.org/current/configuration/secrets_management.html)

| Field           | Required  | Description                                                         |
| --------------- | --------- | --------------------------------------------------------------------|
| Endpoint        | true      | The AWS service endpoint for the plugin to connect.                 |
| AccessKey       | true      | The access key as a part of AWS credentials.                        |
| SecretAccessKey | true      | The secret access key as a part of AWS credentials.                 |
| Region          | true      | Region in which AWS secrets manager is hosted.                      |
| SecretName      | true      | The name of the secret to be utilized.                              |
| SecretCacheTTL  | false     | The secrets cache TTL in milliseconds, defaults to 30 minutes. |

### Caching
The plugin caches secrets for a duration configured using the SecretCacheTTL. Currently GoCD does not provide a
way to invalidate the cache. To invalidate the cache, change the SecretCacheTTL and save the SecretConfig.

### Building the code base
To build the jar, run `./gradlew clean test assemble`

## Troubleshooting

### Enable Debug Logs

#### If you are on GoCD version 19.6 and above:

Edit the file `wrapper-properties.conf` on your GoCD server and add the following options. The location of the `wrapper-properties.conf` can be found in the [installation documentation](https://docs.gocd.org/current/installation/installing_go_server.html) of the GoCD server.

```properties
# We recommend that you begin with the index `100` and increment the index for each system property
wrapper.java.additional.100=-Dplugin.com.thoughtworks.gocd.secretmanager.aws.log.level=debug
```

If you're running with GoCD server 19.6 and above on docker using one of the supported GoCD server images, set the environment variable `GOCD_SERVER_JVM_OPTIONS`:

```shell
docker run -e "GOCD_SERVER_JVM_OPTIONS=-Dplugin.com.thoughtworks.gocd.secretmanager.aws.log.level=debug" ...
```

The plugin logs are written to `LOG_DIR/plugin-com.thoughtworks.gocd.secretmanager.aws.log`. The log dir 
- on Linux is `/var/log/go-server`
- on Windows are written to `C:\Program Files\Go Server\logs` 
- on docker images are written to `/godata/logs`

