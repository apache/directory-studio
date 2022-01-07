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
Update the copyright year. Full text search/replace "2006-2021". Also change in `plugins/rcp/src/main/resources/splash.bmp` image.

Test the release build: rat check, javadoc and source jar generation, installer generation, GPG signing, userguide generation:

    mvn -f pom-first.xml clean install
    mvn -Papache-release,windows,macos -Duserguides clean install

Note: During creation of the macOS installer (DMG) the ApacheDirectoryStudio.app is signed with the ASF "Developer ID Application" key. See https://issues.apache.org/jira/browse/INFRA-16978 for the process to get one.

Test the notarization of the macOS installer (requires app-specific password generated at https://appleid.apple.com/):

    cd installers/macos/target
    xcrun altool --notarize-app --primary-bundle-id "org.apache.directory.studio" --username "you@apache.org" --password "app-specific-password" --file ApacheDirectoryStudio-*.dmg

Wait for the successful notarization (email notification).

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

As long as the `org.apache.directory.studio.openldap.feature` is not ready for release it needs to be removed from `product/org.apache.directory.studio.product` then commit

    git commit -am "Remove openldap feature for release"

#### Set the version and commit

    find . -name pom-first.xml | xargs sed -i 's/2.0.0-SNAPSHOT/'$VERSION'/'
    find . -name pom-first.xml | xargs sed -i 's/2.0.0.qualifier/'$VERSION'/'
    sed -i 's/2.0.0-SNAPSHOT/'$VERSION'/' pom.xml
    mvn -f pom-first.xml clean install
    git checkout pom.xml
    mvn org.eclipse.tycho:tycho-versions-plugin:1.7.0:set-version -DnewVersion=$VERSION
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

#### Notarize the macOS installer

    cd installers/macos/target
    xcrun altool --notarize-app --primary-bundle-id "org.apache.directory.studio" --username "you@apache.org" --password "app-specific-password" --file ApacheDirectoryStudio-*.dmg

Wait for the successful notarization (email notification), then staple (attach) the notarization ticket to the DMG:

    xcrun stapler staple ApacheDirectoryStudio-*.dmg

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

#### Upload the user guides

Upload the content of `target/ug/$VERSION` using WebDAVs to `nightlies.apache.org/directory/studio/$VERSION/userguide`.

### Call the vote

Start the vote.

### Publish

After successful vote publish the artifacts.

Release artifacts in Nexus.

Move distribution packages from `dev` area to `release`:

	svn mv https://dist.apache.org/repos/dist/dev/directory/studio/$VERSION https://dist.apache.org/repos/dist/release/directory/studio/$VERSION -m "Release $VERSION"

Wait 24h for mirror rsync.

#### Update site

The update site needs to be updated.

In the following files

* `static/studio/update/compositeArtifacts--xml.html`
* `static/studio/update/compositeContent--xml.html`
* `static/studio/update/product/compositeArtifacts--xml.html`
* `static/studio/update/product/compositeContent--xml.html`

change the location path to the new release and also update the `p2.timestamp` to the current timestamp milliseconds (hint: `date +%s000`)

#### Website

Update version, changelog, and news:

* `config.toml`: `version_studio` and `version_studio_name`
* `static/studio/.htaccess`
* `source/studio/changelog.md`
* `source/studio/news.md`

#### Eclipse Marketplace

Update entry in Eclipse Marketplace: <https://marketplace.eclipse.org/content/apache-directory-studio>

Also test to install the plugins from marketplace.

#### Mac Ports

Update entry in Mac Ports: <https://ports.macports.org/port/directory-studio>

#### Update Apache Reporter

Add release to <https://reporter.apache.org/addrelease.html?directory>

#### Send announce email

Send the release announce email.

#### Cleanup

Delete old releases from `https://dist.apache.org/repos/dist/release/directory/studio/`, ensure they were already archived to `https://archive.apache.org/dist/directory/studio/`.

