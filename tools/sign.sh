#!/bin/sh

echo -n "PGP Key ID: "
read DEFAULT_KEY

echo -n "PGP Key Password: "
stty -echo
read PASSWORD
stty echo
echo ""

for FILE in $(find . -not '(' -name "sign.sh" -or -name ".*" -or -name "*.md5" -or -name "*.sha1" -or -name "*.asc" ')' -and -type f) ; do
    if [ -f "$FILE.asc" ]; then
        echo "Skipping: $FILE"
        continue
    fi

    echo -n "Signing: $FILE ... "

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
        gpg --print-md SHA1 "$FILE" > "$FILE".sha1
        echo "  - Generated '$FILE.sha1'"
    else
        echo "  - Skipped '$FILE.sha1' (file already existing)"
    fi
 
    # ASC
    if [ ! -f "$FILE.asc" ];
    then
        echo "$PASSWORD" | gpg --detach-sign --armor --no-tty --yes --passphrase-fd 0 "$FILE"
        echo "  - Generated '$FILE.asc'"
    else
        echo "  - Skipped '$FILE.asc' (file already existing)"
    fi
done
