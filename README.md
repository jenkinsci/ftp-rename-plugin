# ftp rename plugin
Jenkins plugin for ftp rename

Automating a rename in a ftp server.

# Usage
In Jenkins job, set the ftp server info.

![ScreenShot](ftp_rename.png?raw=true )

In a job configuration add a ftp rename step and define the old name and the new name.

![ScreenShot](job_config.png?raw=true)

## Tutorial

  * [Developing a Jenkins Plugin](https://wiki.jenkins.io/display/JENKINS/Plugin+tutorial)

  * [Deploying a Jenkins Plugin to JFrog Artifactory](https://wiki.jenkins.io/display/JENKINS/Hosting+Plugins)

```sh
mvn -Dresume=false release:prepare release:perform
mvn release:rollback
mvn release:clean
```