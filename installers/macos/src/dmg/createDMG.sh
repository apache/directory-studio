#!/bin/bash
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

set -e

make_dmg () {
    platform=$1
    path_fo_file=$2

    # Creating dmg and .background folders
    mkdir dmg-$platform
    mkdir -p dmg-$platform/.background    

    # Copy the application
    tar -xf $path_fo_file -C dmg-$platform

    # Copy legal files
    cp dmg-$platform/ApacheDirectoryStudio.app/Contents/Eclipse/LICENSE dmg-$platform/
    cp dmg-$platform/ApacheDirectoryStudio.app/Contents/Eclipse/NOTICE dmg-$platform/

    # Move background image
    cp background.png dmg-$platform/.background/

    # Move .DS_Store file
    cp DS_Store dmg-$platform/.DS_Store

    # Creating symbolic link to Applications folder
    ln -s /Applications dmg-$platform/Applications

    # Codesign the App with the ASF key, and verify
    codesign --force --deep --timestamp --options runtime --entitlements entitlements.plist -s ${APPLE_SIGNING_ID} dmg-$platform/ApacheDirectoryStudio.app
    codesign -dv --verbose=4 dmg-$platform/ApacheDirectoryStudio.app

    # Creating the disk image
    hdiutil create -srcfolder dmg-$platform/ -volname "ApacheDirectoryStudio" -o TMP.dmg
    hdiutil convert -format UDZO TMP.dmg -o ApacheDirectoryStudio-${version}-macosx.cocoa.$platform.dmg

    # Cleaning
    rm TMP.dmg
    rm -rf dmg-$platform/
}

# Find platform,artifact
find_files () {
    file_list=$(ls -A1 ../../../product/target/products/ApacheDirectoryStudio-*-macosx.cocoa.*.tar.gz)
    for i in $file_list; do make_dmg $(echo $i | sed -e 's/.*cocoa.\(.*\).tar.*/\1/') $i ;done
}

find_files  