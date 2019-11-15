
# AWS Secrets Manager plugin for GoCD

This is a GoCD Secrets Plugin which allows users to use [AWS Secrets Manager](https://aws.amazon.com/secrets-manager/) as a secret manager for GoCD.

## Table of Contents
* [Building the code base](#building-the-code-base)
* [Install and configure the plugin](/INSTALL.md)
* [Troubleshooting](#troubleshooting)

### Building the code base
To build the jar, run `./gradlew clean test assemble`

## Troubleshooting

#### Enable Debug Logs

##### If you are on GoCD version 19.6 and above:

Edit the file `wrapper-properties.conf` on your GoCD server and add the following options. The location of the `wrapper-properties.conf` can be found in the [installation documentation](https://docs.gocd.org/current/installation/installing_go_server.html) of the GoCD server.

```properties
# We recommend that you begin with the index `100` and increment the index for each system property
wrapper.java.additional.100=-Dplugin.com.thoughtworks.gocd.secretmanager.aws.log.level=debug
```

##### GoCD server 19.6 and above on docker using one of the supported GoCD server images:

set the environment variable `GOCD_SERVER_JVM_OPTIONS`:

```shell
docker run -e "GOCD_SERVER_JVM_OPTIONS=-Dplugin.com.thoughtworks.gocd.secretmanager.aws.log.level=debug" ...
```

The plugin logs are written to `LOG_DIR/plugin-com.thoughtworks.gocd.secretmanager.aws.log`. The log dir 
- on Linux is `/var/log/go-server`
- on Windows are written to `C:\Program Files\Go Server\logs` 
- on docker images are written to `/godata/logs`

