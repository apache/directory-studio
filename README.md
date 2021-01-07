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

* JDK 8 or newer
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

* Some UI integration tests expect a running OpenLDAP server and are skipped otherwise:

    docker run -it --rm -p 20389:389 -p 20636:636 --name openldap osixia/openldap:1.3.0

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


## Release

### Licenses

The root directory contains LICENSE and NOTICE files for the source distribution.
The product directory contains LICENSE and NOTICE files for the binary distributions, including licenses for bundled dependencies.


### Release artifacts

We release the following artifacts:

* Source zip (the main artifact we vote on)
* Maven artifacts
    * poms
    * plugins: pom, jar, javadoc, src
    * features: pom, jar, src
    * helps: pom, jar, src
* P2 repositories
    * the main features (LDAP Browser, Schema Editor, ApacheDS)
    * dependencies
* Product archives and installers for
    * Linux GTK 64bit tar.gz
    * maxOS 64bit dmg
    * Windows 64bit exe installer and zip
* Userguides

### Signing keys

Define the PGP key used to sign the artifacts and the Apple signing ID used to sign the DMG:

    export RELEASE_KEY=28686142
    export APPLE_SIGNING_ID=2GLGAFWEQD

Note: those are Stefan's keys, replace with your own.

### Preparation
Update the copyright year. Full text search/replace "2006-2020". Also change in `plugins/rcp/src/main/resources/splash.bmp` image.

Test the release build: rat check, javadoc and source jar generation, installer generation, GPG signing, userguide generation:

    mvn -f pom-first.xml clean install
    mvn -Papache-release,windows,macos -Duserguides clean install

Note: During creation of the macOS installer (DMG) the ApacheDirectoryStudio.app is signed with the ASF "Developer ID Application" key. See https://issues.apache.org/jira/browse/INFRA-16978 for the process to get one.

Test the build and sign process for distribution packages:

    export VERSION=2.0.0-SNAPSHOT
    cd dist
    ./dist.sh

Test the distribution packages:

* Test generated bin archives, installers, and update site (if possible on all platforms).
* Review generated user guides (pdf, html, Eclipse help)
* Review the generated source archive
* Verify signatures and checksums

Run UI tests (if possible on all platforms):

    mvn clean install -Denable-ui-tests

### Release build steps

As Tycho doesn't support the maven-release-plugin the release process is as follows:

#### Define release version

We use a release number scheme that suites for both, Maven and Eclipse.

    <MAJOR>.<MINOR>.<PATCH>.v<YYYYMMDD>[-M<X>|RC<X>]

Example for milestone version: `2.0.0.v20150529-M9`. Example for GA version: `2.0.1.v20150529`.

Define a variable for later use:

    export VERSION=2.0.0.v20150529-M9

#### Create and checkout branch

    git checkout -b $VERSION-prepare
    git push origin $VERSION-prepare

#### Remove OpenLDAP feature

As long as the `org.apache.directory.studio.openldap.feature` is not ready for release it needs to be removed from `product/org.apache.directory.studio.product`.

#### Set the version and commit

    find . -name pom-first.xml | xargs sed -i 's/2.0.0-SNAPSHOT/'$VERSION'/'
    find . -name pom-first.xml | xargs sed -i 's/2.0.0.qualifier/'$VERSION'/'
    sed -i 's/2.0.0-SNAPSHOT/'$VERSION'/' pom.xml
    mvn -f pom-first.xml clean install
    git checkout pom.xml
    mvn org.eclipse.tycho:tycho-versions-plugin:1.2.0:set-version -DnewVersion=$VERSION
    git commit -am "Set version number for release $VERSION"
    git push origin $VERSION-prepare

#### Create the release tag

    git tag $VERSION
    git push origin $VERSION

#### Clone the repo and checkout the tag into a fresh directory

Run the actual release within a fresh checkout to ensure no previous build artifacts are used.

    mkdir studio-release
    cd studio-release
    git clone https://gitbox.apache.org/repos/asf/directory-studio.git .
    git checkout $VERSION

#### Build the release and deploy to staging Nexus repository

    mvn -f pom-first.xml clean install
    mvn -Papache-release,windows,macos -Duserguides -DretryFailedDeploymentCount=3 clean deploy

#### Close the staging Nexus repository

See https://repository.apache.org/#stagingRepositories

#### Package and sign distribution packages

There is a script that collects and signs all update sites and distribution packages.

Run the dist script:

    cd dist
    ./dist.sh

Afterwards all distribution packages and user guides are located in `target`.

#### Upload the distribution packages to SVN

    cd target/dist/$VERSION
    svn mkdir https://dist.apache.org/repos/dist/dev/directory/studio/$VERSION -m "Create dev area for release $VERSION"
    svn co https://dist.apache.org/repos/dist/dev/directory/studio/$VERSION .
    svn add *
    svn commit -m "Add release $VERSION"
    cd ../../..

#### Upload the user guides to SVN

    cd target/ug/$VERSION
    svn mkdir https://svn.apache.org/repos/infra/websites/production/directory/content/studio/users-guide/$VERSION -m "Create user guides area for release $VERSION"
    svn co https://svn.apache.org/repos/infra/websites/production/directory/content/studio/users-guide/$VERSION .
    svn add *
    svn commit -m "Add release $VERSION"
    cd ../../..

Note 1: This publishes the user guides directly to the production CMS!

Note 2: In `content/extpaths.txt` the parent folder is already whitelisted.

### Call the vote

Start the vote.

### Publish

After successful vote publish the artifacts.

Release artifacts in Nexus.

Move distribution packages from `dev` area to `release`:

	svn mv https://dist.apache.org/repos/dist/dev/directory/studio/$VERSION https://dist.apache.org/repos/dist/release/directory/studio/$VERSION -m "Release $VERSION"

Wait 24h for mirror rsync.

#### Update site

The update site https://svn.apache.org/repos/asf/directory/site/trunk/content/studio/update needs to be updated.

In the following files

* `compositeArtifacts--xml.html`
* `compositeContent--xml.html`
* `product/compositeArtifacts--xml.html`
* `product/compositeContent--xml.html`

change the location path to the new release and also update the `p2.timestamp` to the current timestamp milliseconds (hint: `date +%s000`)

#### Website

Update news and download links

* `lib/path.pm`: `$version_studio` and `$version_studio_name`
* `content/index.mdtext`: version string
* `content/studio/changelog.mdtext`
* `content/studio/news.mdtext`
* `content/studio/users-guide.mdtext`

#### Eclipse Marketplace

Update entry in Eclipse Marketplace: <https://marketplace.eclipse.org/content/apache-directory-studio>

Also test to install the plugins from marketplace.

#### Mac Ports

Update entry in Mac Ports: <https://ports.macports.org/port/directory-studio>

#### Update Apache Reporter

Add release to <https://reporter.apache.org/?directory>

#### Send announce email

Send the release announce email.

#### Cleanup

Delete old releases from `https://dist.apache.org/repos/dist/release/directory/studio/`, ensure they were already archived to `https://archive.apache.org/dist/directory/studio/`.


## Misc tips and tricks

How to search features, plugins, versions in P2 repo?

<https://stackoverflow.com/questions/10025599/how-to-find-out-which-feature-contains-a-needed-plug-in-on-an-eclipse-download-s>


Start OSGi console: 

    ss p2.console
    felix:start -t 999


List all IUs within a repository:

    provliu http://download.eclipse.org/eclipse/updates/4.18/


