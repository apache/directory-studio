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

DEFAULT_KEY=@gpg.keyname@
PASSWORD=@gpg.passphrase@

for FILE in $(find . -not '(' -name "sign.sh" -or -name ".*" -or -name "*.md5" -or -name "*.sha1" -or -name "*.asc" ')' -and -type f) ; do
    if [ -f "$FILE.asc" ]; then
        echo "Skipping: $FILE"
        continue
    fi

    echo "Signing: $FILE ... "

    # MD5
    if [ ! -f "$FILE.md5" ];
    then
        openssl md5 < "$FILE" | cut "-d " -f2 > "$FILE.md5"
        echo "  - Generated '$FILE.md5'"
    else
        echo "  - Skipped '$FILE.md5' (file already existing)"
    fi

    # SHA1
    if [ ! -f "$FILE.sha1" ];
    then
        gpg --default-key "$DEFAULT_KEY" --print-md SHA1 "$FILE" > "$FILE".sha1
        echo "  - Generated '$FILE.sha1'"
    else
        echo "  - Skipped '$FILE.sha1' (file already existing)"
    fi
 
    # ASC
    if [ ! -f "$FILE.asc" ];
    then
        echo "$PASSWORD" | gpg --default-key "$DEFAULT_KEY" --detach-sign --armor --no-tty --yes --passphrase-fd 0 "$FILE"
        echo "  - Generated '$FILE.asc'"
    else
        echo "  - Skipped '$FILE.asc' (file already existing)"
    fi
done
