# ftp rename plugin
Jenkins plugin for ftp rename

[![Jenkins Plugins](https://img.shields.io/jenkins/plugin/v/ftp-rename-plugin)](https://github.com/jenkinsci/ftp-rename-plugin/releases)
[![Jenkins Plugin installs](https://img.shields.io/jenkins/plugin/i/ftp-rename-plugin)](https://plugins.jenkins.io/ftp-rename-plugin)
[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/ftp-rename-plugin/master)](https://ci.jenkins.io/blue/organizations/jenkins/Plugins%2Fftp-rename-plugin/branches)
[![MIT License](https://img.shields.io/github/license/jenkinsci/ftp-rename-plugin.svg)](LICENSE)
[![javadoc](https://img.shields.io/badge/javadoc-available-brightgreen.svg)](https://javadoc.jenkins.io/plugin/ftp-rename-plugin/)
<a href='https://ko-fi.com/brunocantisano' target='_blank'><img height='15' style='border:0px;height:26px;' src='https://az743702.vo.msecnd.net/cdn/kofi3.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' />

[Jenkins](https://jenkins.io/) plugin for [FTP Rename](https://msdn.microsoft.com/en-us/library/hh127509.aspx) builder

![Java_Jenkins_Plugin](java_jenkins_plugin.png)

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
