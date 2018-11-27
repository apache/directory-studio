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
* FIXME: Windows users have to replace the repository location `file://${basedir}/target/repository` in `eclipse-trgt-platform/template/org.apache.directory.studio.eclipse-trgt-platform.template` with the actual path, e.g. `file:///C:/Development/studio/eclipse-trgt-platform/target/repository`

### Build

You can use either of those two methods to build the project :

#### Do it manually

Build the 'Eclipse Target Platform' and generate MANIFEST.MF files first

    mvn -f pom-first.xml clean install

Build the main eclipse artifacts using Tycho

    mvn clean install

#### Use the script  (which runs the two previous commands)

On Linux / Mac OS X :

    $ ./build.sh

or on Windows :

    > build.bat

### Tests

* Unit tests included in src/test/java of each plugin are executed automatically and run in 'test' phase
* Core integration tests in tests/test.integration.core are executed automatically and run in 'integration-test' phase
* SWTBot based UI integration tests in tests/test.integration.ui are disabled by default. They can be enabled with `-Denable-ui-tests`. A failing test generates a screenshot. To not block the developer computer they can run within a virtual framebuffer:

        export DISPLAY=:99
        Xvfb :99 -screen 0 1024x768x16 &

### Build issues

Tycho doesn't handle snapshot dependencies well. The first time a snapshot dependency is used within the build it is cached in `~/.m2/repository/p2`. Afterwards any change in the dependency (e.g. ApacheDS or LDAP API) is not considered unless it is deleted from the cache.

To delete all Apache Directory related snapshots run:

    rm -rf ~/.m2/repository/p2/osgi/bundle/org.apache.directory.*


## Setup Eclipse workspace

Recommended IDE is 'Eclipse (Luna) for RCP Developers': <http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/lunasr2>

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
    * Linux GTK 32bit tar.gz
    * Linux GTK 64bit tar.gz
    * Mac OS X 64bit dmg
    * Windows 32bit exe installer and zip
    * Windows 64bit exe installer and zip
* Userguides


### Preparation

Update the copyright year, see <http://svn.apache.org/viewvc?rev=1807191&view=rev>
for the list of files that need to be changed.

Test the release build: rat check, javadoc and source jar generation, GPG signing, userguide generation

    mvn -f pom-first.xml clean install
    mvn -Papache-release,windows,macos -Duserguides clean install

Run UI tests (if possible on all platforms)

    mvn clean install -Denable-ui-tests


### Release build steps

As Tycho doesn't support the maven-release-plugin the release process is as follows:

#### Define release version

We use a release number scheme that suites for both, Maven and Eclipse.

    <MAJOR>.<MINOR>.<PATCH>.v<YYYYMMDD>[-M<X>|RC<X>]

Example for milestone version: `2.0.0.v20150529-M9`. Example for GA version: `2.0.1.v20150529`.

Define a variable for later use:

    export VERSION=2.0.0.v20150529-M9

Also create an empty directory used during the release process and store it in a variable:

    export RELEASE_DIR=$(pwd)

#### Create and checkout branch

    cd $RELEASE_DIR
    svn copy https://svn.apache.org/repos/asf/directory/studio/trunk https://svn.apache.org/repos/asf/directory/studio/branches/$VERSION -m "Prepare release $VERSION"
    svn checkout https://svn.apache.org/repos/asf/directory/studio/branches/$VERSION branch-$VERSION
    cd branch-$VERSION

#### Remove OpenLDAP feature

As long as the `org.apache.directory.studio.openldap.feature` is not ready for release it needs to be removed from `product/org.apache.directory.studio.product`.

#### Set the version and commit

    find . -name pom-first.xml | xargs sed -i 's/2.0.0-SNAPSHOT/'$VERSION'/'
    find . -name pom-first.xml | xargs sed -i 's/2.0.0.qualifier/'$VERSION'/'
    sed -i 's/2.0.0-SNAPSHOT/'$VERSION'/' pom.xml
    mvn -f pom-first.xml clean install
    svn revert pom.xml
    mvn org.eclipse.tycho:tycho-versions-plugin:0.24.0:set-version -DnewVersion=$VERSION
    svn commit -m "Set version number for release $VERSION"

#### Create and checkout tag

    cd $RELEASE_DIR
    svn copy https://svn.apache.org/repos/asf/directory/studio/branches/$VERSION https://svn.apache.org/repos/asf/directory/studio/tags/$VERSION -m "Tag release $VERSION"
    svn checkout https://svn.apache.org/repos/asf/directory/studio/tags/$VERSION tag-$VERSION
    cd tag-$VERSION

#### Build the release and deploy to staging Nexus repository

    mvn -f pom-first.xml clean install
    mvn -Papache-release,windows,macos -Duserguides -DretryFailedDeploymentCount=3 clean deploy

#### Close the staging Nexus repository

See <https://repository.apache.org/#stagingRepositories>.

#### Package and sign distribution packages

There is a script that collects and signs all update sites and distribution packages.

For non-interactive signing with GPG agent define env variable:

    export RELEASE_KEY=28686142

Run the dist script:

    cd dist
    ./dist.sh

Afterwards all distribution packages are located in `target`.

#### Upload the artifacts to SVN

    cd target/$VERSION
    svn mkdir https://dist.apache.org/repos/dist/dev/directory/studio/$VERSION -m "Create dev area for release $VERSION"
    svn co https://dist.apache.org/repos/dist/dev/directory/studio/$VERSION .
    svn add *
    svn commit -m "Add release $VERSION"

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

#### Eclipse Marketplace

Update entry in Eclipse Marketplace: <https://marketplace.eclipse.org/content/apache-directory-studio>

#### Update Apache Reporter

Add release to <https://reporter.apache.org/?directory>

#### Send announce email

Send the release announce email.

#### User guides

TODO

#### Cleanup

Delete old releases from `https://dist.apache.org/repos/dist/release/directory/studio/`, ensure they were already archived to `https://archive.apache.org/dist/directory/studio/`.


## Misc tips and tricks

How to search features, plugins, versions in P2 repo?

<https://stackoverflow.com/questions/10025599/how-to-find-out-which-feature-contains-a-needed-plug-in-on-an-eclipse-download-s>


Start OSGi console: 

    ss p2.console
    felix:start -t 999


List all IUs within a repository:

    provliu http://download.eclipse.org/eclipse/updates/4.5/ *


