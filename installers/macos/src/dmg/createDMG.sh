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

set -e

# Creating dmg and .background folders
mkdir dmg
mkdir -p dmg/.background

# Copy the application
tar -xf ../../../product/target/products/ApacheDirectoryStudio-*-macosx.cocoa.x86_64.tar.gz -C dmg

# Copy legal files
cp dmg/ApacheDirectoryStudio.app/Contents/Eclipse/LICENSE dmg/
cp dmg/ApacheDirectoryStudio.app/Contents/Eclipse/NOTICE dmg/

# Move background image
mv background.png dmg/.background/

# Move .DS_Store file
mv DS_Store dmg/.DS_Store

# Creating symbolic link to Applications folder
ln -s /Applications dmg/Applications

# Codesign the App with the ASF key, and verify
codesign --force --deep -s ${APPLE_SIGNING_ID} dmg/ApacheDirectoryStudio.app
codesign -dv --verbose=4 dmg/ApacheDirectoryStudio.app

# Creating the disk image
hdiutil create -srcfolder dmg/ -volname "ApacheDirectoryStudio" -o TMP.dmg
hdiutil convert -format UDZO TMP.dmg -o ApacheDirectoryStudio-${version}-macosx.cocoa.x86_64.dmg

# Cleaning
#rm TMP.dmg
#rm -rf dmg/
