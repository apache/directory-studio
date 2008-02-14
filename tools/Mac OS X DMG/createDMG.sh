#!/bin/sh

# Creating dmg and .background folders
mkdir dmg
mkdir -p dmg/.background

# Creating symbolic link to Applications folder
ln -s /Applications dmg/Applications

# Copying release resources
cp -r release/* dmg/

# Copying background image and .DS_Store file
cp background.png dmg/.background/
cp DS_Store dmg/.DS_Store

# Creating the disk image
hdiutil makehybrid -hfs -hfs-volume-name "Apache Directory Studio" -hfs-openfolder dmg/ dmg/ -o TMP.dmg
hdiutil convert -format UDZO TMP.dmg -o ApacheDirectoryStudio.dmg

# Cleaning
rm TMP.dmg
rm -rf dmg/