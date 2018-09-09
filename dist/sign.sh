#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

if [ -z "$RELEASE_KEY" ]; then
    echo "PGP Key ID: "
    read RELEASE_KEY

    echo "PGP Key Password: "
    stty -echo
    read PASSWORD
    stty echo
    echo ""
fi

for FILE in $(find . -maxdepth 2 -not '(' -name "sign.sh" -or -name ".*" -or -name "*.sha256" -or -name "*.sha512" -or -name "*.asc" ')' -and -type f) ; do
    if [ -f "$FILE.asc" ]; then
        echo "Skipping: $FILE"
        continue
    fi

    echo "Signing: $FILE ... "

    # SHA256
    if [ ! -f "$FILE.sha256" ];
    then
        gpg --default-key "$RELEASE_KEY" --print-md SHA256 "$FILE" > "$FILE".sha256
        echo "  - Generated '$FILE.sha256'"
    else
        echo "  - Skipped '$FILE.sha256' (file already existing)"
    fi

    # SHA512
    if [ ! -f "$FILE.sha512" ];
    then
        gpg --default-key "$RELEASE_KEY" --print-md SHA512 "$FILE" > "$FILE".sha512
        echo "  - Generated '$FILE.sha512'"
    else
        echo "  - Skipped '$FILE.sha512' (file already existing)"
    fi

    # ASC
    if [ ! -f "$FILE.asc" ];
    then
        echo "$PASSWORD" | gpg --default-key "$RELEASE_KEY" --detach-sign --armor --no-tty --yes --passphrase-fd 0 "$FILE"
        echo "  - Generated '$FILE.asc'"
    else
        echo "  - Skipped '$FILE.asc' (file already existing)"
    fi
done
