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

## Build from command line

You can use either of those two methods to build the project :

### Do it manually

1. Build the 'Eclipse Target Platform' and generate MANIFEST.MF files first

    mvn -f pom-first.xml clean install

2. Build the main eclipse artifacts using Tycho

    mvn clean install

### Use the script  (which does run the two previous commands)

On Linux / Mac OS X :

    $ ./build.sh

or on Windows :

    > build.bat

### Tests

* Unit tests included in src/test/java of each plugin are executed automatically and run in 'test' phase
* Core integration tests in tests/test.integration.core are executed automatically and run in 'integration-test' phase
* SWTBot based UI integration tests in tests/test/integration.ui are diabled by default. They can be enabled with -Denable-ui-tests. A failing test generates a screenshot. To not block the developer computer UI tests they can run within a virtual framebuffer:
        export DISPLAY=:99
        Xvfb :99 -screen 0 1024x768x16 &

### Build issues

Tycho doesn't handle snapshot dependencies well. The first time a snapshot dependency is used within the build it is cached in `~/.m2/repository/p2`. Afterwards any change in the dependency (e.g. ApacheDS or LDAP API) is the considered unless it is deleted from the cache.


## Setup Eclipse workspace

Recommended IDE is 'Eclipse (Luna) for RCP Developers': <http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/lunasr1>

1. Import 'Eclipse Target Platorm' project first

    * File -> Import... -> Maven -> Existing Maven Projects
    * Choose 'studio/eclipse-trgt-platform' as root directory
    * Only this single project is selected
    * Finish

2. Initialize target platform

    * Open the `eclipse-trgt-platform.target` file with the 'Target Editor'
    * In the top right corner click 'Set as Target Platform'

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


## Release

tentative, not fully tested yet...

### Release artifacts

We release the following artifacts:

* Source zip (the main artifact we vote on)
* Maven artifacts
    * poms
    * plugins: pom, jar, javadoc, src
    * features: pom, jar, src
    * helps: pom, jar, src
* P2 repository zip
* Product archives Mac OS X, Linux (32+64bit), Windows (32+64bit)
* Userguides


### Preparation

Test the release build: rat check, javadoc and source jar generation, GPG signing, userguide generation

    mvn -f pom-first.xml clean install
    mvn -Papache-release -Duserguides clean install

Run UI tests (if possilbe on all platforms)

    mvn clean install -Denable-ui-tests


### Release steps

As Tycho doesn't support the maven-release-plugin the release process is as follows:

#### Define release version

We use a release number scheme that suites for both, Maven and Eclipse.

    <MAJOR>.<MINOR>.<PATCH>.v<YYYYMMDD>[-M<X>|RC<X>]

Example for milestone version: `2.0.0.v20150529-M9`
Example for GA version: `2.0.1.v20150529`

Define a variable for later use:

    export VERSION=2.0.0.v20150529-M9

#### Create and checkout branch

    svn copy https://svn.apache.org/repos/asf/directory/studio/trunk https://svn.apache.org/repos/asf/directory/studio/branches/$VERSION -m "Prepare release $VERSION"

#### Set the version

    find . -name pom-first.xml | xargs sed -i 's/2.0.0-SNAPSHOT/'$VERSION'/'
    find . -name pom-first.xml | xargs sed -i 's/2.0.0.qualifier/'$VERSION'/'
    mvn -f pom-first.xml clean install
    mvn org.eclipse.tycho:tycho-versions-plugin:0.22.0:set-version -DnewVersion=$VERSION

#### Commit

    svn commit -m "Set version numbers for release $VERSION"

### Create and checkout tag

    svn copy https://svn.apache.org/repos/asf/directory/studio/branches/$VERSION https://svn.apache.org/repos/asf/directory/studio/tags/$VERSION -m "Tag release $VERSION"

#### Build the release

    mvn -f pom-first.xml clean install
    mvn -Papache-release -Puserguides clean install/deploy


#### Sign distribution packages

There is a script that

* creates `dist` folder
* copies all distribution packages (sources, products, P2 repo) to dist folder
* checks that all packages contain LICENSE.txt and NOTICE.txt
* sign the packages

For non-interactive signing with GPG agent define env variable with key to use

	export RELEASE_KEY=28686142

Run helper script

    tools/dist.sh

Afterwards all distribution packages are in `dist` folder.


tbc.


## Misc tips and tricks

How to search features, plugins, versions in P2 repo?

<https://stackoverflow.com/questions/10025599/how-to-find-out-which-feature-contains-a-needed-plug-in-on-an-eclipse-download-s>


