#!/bin/sh

echo -n "PGP Key ID: "
read DEFAULT_KEY

echo -n "PGP Key Password: "
stty -echo
read PASSWORD
stty echo
echo ""

for FILE in $(find . -not '(' -name "*.md5″ -or -name "*.sha1″ -or -name "*.asc" ')' -and -type f) ; do
    if [ -f "$FILE.asc" ]; then
        echo "Skipping: $FILE"
        continue
    fi

    echo -n "Signing: $FILE ... "

    openssl md5 < "$FILE" | cut "-d " -f1 > "$FILE.md5"

    echo "$PASSWORD" | gpg --default-key "$DEFAULT_KEY" --detach-sign --armor --no-tty --yes --passphrase-fd 0 "$FILE" && echo done.
done