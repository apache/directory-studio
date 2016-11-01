#!/bin/sh

# Creating dmg and .background folders
mkdir dmg
mkdir -p dmg/.background

# Copy the application
cp -a ../../../product/target/products/org.apache.directory.studio.product/macosx/cocoa/x86_64/ApacheDirectoryStudio.app dmg/

# Copy legal files
cp dmg/ApacheDirectoryStudio.app/Contents/Eclipse/LICENSE dmg/
cp dmg/ApacheDirectoryStudio.app/Contents/Eclipse/NOTICE dmg/

# Move background image
mv background.png dmg/.background/

# Move .DS_Store file
mv DS_Store dmg/.DS_Store

# Creating symbolic link to Applications folder
ln -s /Applications dmg/Applications

# Creating the disk image
hdiutil makehybrid -hfs -hfs-volume-name "Apache Directory Studio" -hfs-openfolder dmg/ dmg/ -o TMP.dmg
hdiutil convert -format UDZO TMP.dmg -o ApacheDirectoryStudio-${version}-macosx.cocoa.x86_64.dmg

# Cleaning
#rm TMP.dmg
#rm -rf dmg/
