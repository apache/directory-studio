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

# Helper script for release

set -e

if [ -z "$VERSION" ]; then
  echo "VERSION is not set"
  exit 1;
fi

echo "Creating dist folder"
mkdir dist
cd dist

echo
echo "Copying dist files"
cp ../target/org.apache.directory.studio.parent-${VERSION}-source-release.zip ApacheDirectoryStudio-sources-${VERSION}.zip
cp ../product/target/products/ApacheDirectoryStudio-${VERSION}-*.{zip,tar.gz} .
cp ../p2repository/target/org.apache.directory.studio.p2repository-${VERSION}.zip .

echo
echo "Checking legal files"
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
        *)
            echo "Unknown file type: $file"
            exit 1
            ;;
    esac
    eval "$cmd" | grep "LICENSE.txt"
    eval "$cmd" | grep "NOTICE.txt"
done

echo
echo "Signing dist files"
../tools/sign.sh

echo
echo "Success"

