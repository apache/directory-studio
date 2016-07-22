#Generates an installer that is compatible with Windows 64bit version 
if [ -z "$STUDIO_VERSION" ]; then
   echo "Please set the STUDIO_VERSION environment variable to generate the installer"
   exit 1
fi

PROD_NAME=ApacheDirectoryStudio
FILE_NAME=$PROD_NAME*-win32.win32.x86_64.zip
rm -rf release
unzip ../../product/target/products/$FILE_NAME
mv $PROD_NAME release
mv release/LICENSE release/LICENSE.txt

#set LANG to C to avoid "sed: RE error: illegal byte sequence" error
export LANG=C

sed -e "s/STUDIO_VERSION/x86_64-$STUDIO_VERSION/" windows_installer.nsi > temp_win_installer.nsi
sed -i.bak "s/BRANDING_VERSION/$STUDIO_VERSION/" temp_win_installer.nsi

makensis temp_win_installer.nsi
