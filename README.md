# AWS Secrets Manager plugin for GoCD

This plugin allows users to utilize [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/) as a secret manager for GoCD.

### Configure the plugin

The plugin requires secret config in order to connect with AWS - 

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
    </secretConfig>
  </secretConfigs>
```

| Field           | Required  | Description                                        |
| --------------- | --------- | -------------------------------------------------- |
| Endpoint        | true      | The endpoint for the plugin to talk to             |
| AccessKey       | true      | The access key as a part of AWS credentials        |
| SecretAccessKey | true      | The secret access key as a part of AWS credentials |
| Region          | true      | Region in which AWS secrets manager is hosted      |
| SecretName      | true      | The name of the secret to be utilized              |

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

#### If you are on GoCD version 19.5 and lower:

* On Linux:

    Enabling debug level logging can help you troubleshoot an issue with this plugin. To enable debug level logs, edit the file `/etc/default/go-server` (for Linux) to add:

    ```shell
    export GO_SERVER_SYSTEM_PROPERTIES="$GO_SERVER_SYSTEM_PROPERTIES -Dplugin.com.thoughtworks.gocd.secretmanager.aws.log.level=debug"
    ```

    If you're running the server via `./server.sh` script:

    ```shell
    $ GO_SERVER_SYSTEM_PROPERTIES="-Dplugin.com.thoughtworks.gocd.secretmanager.aws.log.level=debug" ./server.sh
    ```

* On windows:

    Edit the file `config/wrapper-properties.conf` inside the GoCD Server installation directory (typically `C:\Program Files\Go Server`):

    ```
    # config/wrapper-properties.conf
    # since the last "wrapper.java.additional" index is 15, we use the next available index.
    wrapper.java.additional.16=-Dplugin.com.thoughtworks.gocd.secretmanager.aws.log.level=debug
    ```

The plugin logs are written to `LOG_DIR/plugin-com.thoughtworks.gocd.secretmanager.aws.log`. The log dir 
- on Linux is `/var/log/go-server`
- on Windows are written to `C:\Program Files\Go Server\logs` 
- on docker images are written to `/godata/logs`

