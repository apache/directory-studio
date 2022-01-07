> Licensed to the Apache Software Foundation (ASF) under one
> or more contributor license agreements.  See the NOTICE file
> distributed with this work for additional information
> regarding copyright ownership.  The ASF licenses this file
> to you under the Apache License, Version 2.0 (the
> "License"); you may not use this file except in compliance
> with the License.  You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing,
> software distributed under the License is distributed on an
> "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
> KIND, either express or implied.  See the License for the
> specific language governing permissions and limitations
> under the License.

# Apache Directory Studio (TM)

The Eclipse-based LDAP browser and directory client.

Apache Directory Studio is a complete directory tooling platform intended to be used with any LDAP server however it is particularly designed for use with ApacheDS. It is an Eclipse RCP application, composed of several Eclipse (OSGi) plugins, that can be easily upgraded with additional ones. These plugins can even run within Eclipse itself.

## Build from command line

### Prerequisites

* JDK 11 or newer
* Maven 3 or newer
* Sufficient heap space for Maven: `export MAVEN_OPTS="-Xmx512m"`

### Build

You can use either of those two methods to build the project :

#### Do it manually

Build the 'Eclipse Target Platform' and generate MANIFEST.MF files first

    mvn -f pom-first.xml clean install

Build the main eclipse artifacts using Tycho

    mvn clean install

#### Use the script  (which runs the two previous commands)

On Linux / macOS :

    $ ./build.sh

or on Windows :

    > build.bat

### Tests

* Unit tests included in `src/test/java` of each plugin are executed automatically and run in 'test' phase
* Core integration tests in `tests/test.integration.core` are executed automatically and run in 'integration-test' phase
* UI integration tests based on SWTBot in `tests/test.integration.ui` are disabled by default. They can be enabled with `-Denable-ui-tests`. A failing test generates a screenshot. To not block the developer computer they can run within a virtual framebuffer:

    export DISPLAY=:99
    Xvfb :99 -screen 0 1024x768x16 &

* The core and UI integration tests run against ApacheDS, OpenLDAP, and 389ds. The ApacheDS is always started in embedded mode. The others are expected to run, e.g. with the following commands, otherwise those tests are skipped. 

    docker run -it --rm -p 20389:389 -p 20636:636 --name openldap -e LDAP_TLS_VERIFY_CLIENT=never osixia/openldap:1.3.0
    docker run -it --rm -p 21389:3389 -p 21636:3636  --name fedora389ds -e DS_DM_PASSWORD=admin 389ds/dirsrv bash -c "set -m; /usr/lib/dirsrv/dscontainer -r & while ! /usr/lib/dirsrv/dscontainer -H; do sleep 5; done; sleep 5; /usr/sbin/dsconf localhost backend create --suffix dc=example,dc=org --be-name example; fg"
	
### Build issues

Tycho doesn't handle snapshot dependencies well. The first time a snapshot dependency is used within the build it is cached in `~/.m2/repository/p2`. Afterwards any change in the dependency (e.g. ApacheDS or LDAP API) is not considered unless it is deleted from the cache.

To delete all Apache Directory related snapshots run:

    rm -rf ~/.m2/repository/p2/osgi/bundle/org.apache.directory.*
    <workspace>/.metadata/.plugins/org.eclipse.pde.core/.bundle_pool/*


## Setup Eclipse workspace

Recommended IDE is 'Eclipse for RCP and RAP Developers': <https://www.eclipse.org/downloads/packages/>

1. Import 'Eclipse Target Platform' project first

    * File -> Import... -> Maven -> Existing Maven Projects
    * Choose 'studio/eclipse-trgt-platform' as root directory
    * Only this single project is selected
    * Finish

2. Initialize target platform

    * Open the `eclipse-trgt-platform.target` file with the 'Target Editor'
    * Wait for the target platform to initialize, this takes multiple minutes!

3. Import the main plugins

    * File -> Import... -> Maven -> Existing Maven Projects
    * Chosse 'studio' as root directory
    * All the plugins are selected
    * Finish

During import some Maven plugin connectors need to be installed, accept the installation and restart.

## Run

### From command line

The build produces binaries for all platforms. Archived versions can be found in `product/target/products/`, unpacked versions can be found below `product/target/products/org.apache.directory.studio.product` 

### Within Eclipse

* Open the product configuration 'org.apache.directory.studio.product' with the Product Configuration Editor
* Click the link 'Launch an Eclipse application'

## Misc tips and tricks

How to search features, plugins, versions in P2 repo?

<https://stackoverflow.com/questions/10025599/how-to-find-out-which-feature-contains-a-needed-plug-in-on-an-eclipse-download-s>


Start OSGi console: 

    ss p2.console
    felix:start -t 999


List all IUs within a repository:

    provliu http://download.eclipse.org/eclipse/updates/4.18/

