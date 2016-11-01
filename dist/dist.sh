#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# Script to collect and sign update sites and distribution packages.

set -e

# Check if version is set
if [ -z "$VERSION" ]; then
  echo "VERSION is not set"
  exit 1;
fi

# Only continue if target directory does not exist yet
WORK_DIR=$PWD
TARGET_DIR=${WORK_DIR}/target
if [ -e "$TARGET_DIR" ]; then
  echo "Target already exists"
  exit 1;
fi

echo
echo "### Creating dist folder"
DIST_DIR=${TARGET_DIR}/${VERSION}
mkdir -p ${DIST_DIR}


echo
echo "### Copying dist files"
cd ${WORK_DIR}
cp ../target/org.apache.directory.studio.parent-${VERSION}-source-release.zip ${DIST_DIR}/ApacheDirectoryStudio-${VERSION}-src.zip
#cp ../product/target/products/ApacheDirectoryStudio-${VERSION}-*.{zip,tar.gz} ${DIST_DIR}/
cp ../product/target/products/ApacheDirectoryStudio-${VERSION}-linux.gtk.x86_64.tar.gz ${DIST_DIR}/
cp ../product/target/products/ApacheDirectoryStudio-${VERSION}-linux.gtk.x86.tar.gz ${DIST_DIR}/
cp ../installers/windows/32bit/target/ApacheDirectoryStudio-${VERSION}-win32.win32.x86.exe ${DIST_DIR}/
cp ../installers/windows/64bit/target/ApacheDirectoryStudio-${VERSION}-win32.win32.x86_64.exe ${DIST_DIR}/
cp ../installers/macos/target/ApacheDirectoryStudio-${VERSION}-macosx.cocoa.x86_64.dmg ${DIST_DIR}/


echo
echo "### Checking legal files"
cd ${DIST_DIR}
for file in *
do
    echo "Checking $file"
    case $file in
        *.zip)
            cmd="unzip -l $file"
            ;;
        *.tar.gz)
            cmd="tar -tzvf $file"
            ;;
        *.exe)
            continue
            ;;
        *.dmg)
            continue
            ;;
        *)
            echo "Unknown file type: $file"
            exit 1
            ;;
    esac
    eval "$cmd" | grep "LICENSE"
    eval "$cmd" | grep "NOTICE"
done

echo
echo "### Signing dist files"
sh ${WORK_DIR}/sign.sh

echo
echo "### Copying update sites"
UPDATE_SITE_DIR=${TARGET_DIR}/${VERSION}/update
mkdir -p ${UPDATE_SITE_DIR}
cd ${WORK_DIR}
cp -a ../p2repositories/dependencies/target/repository ${UPDATE_SITE_DIR}/dependencies
cp -a ../p2repositories/eclipse/target/repository ${UPDATE_SITE_DIR}/eclipse

echo
echo "### Signing update sites"
cd ${UPDATE_SITE_DIR}/dependencies
sh ${WORK_DIR}/sign.sh
zip -r ${DIST_DIR}/ApacheDirectoryStudio-${VERSION}-p2repository-dependencies.zip *
cd ${UPDATE_SITE_DIR}/eclipse
sh ${WORK_DIR}/sign.sh
zip -r ${DIST_DIR}/ApacheDirectoryStudio-${VERSION}-p2repository.zip *


echo
echo "### Success"

