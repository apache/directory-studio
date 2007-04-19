#!/bin/bash
# --------------------------------------------------------------
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
# --------------------------------------------------------------
#
# release.sh -- Builds LDAP Studio for all Operating Systems and Architectures
# This script creates all packages for a release of LDAP Studio.
# They are generated in the 'packages' folder of the root folder of the project.
# The following files are generated:
#    - LDAP_Studio_MacOSX (the content of this folder needs to be put into a disk image.)
#    - LDAP_Studio_Windows (the content of this folder needs to be used to generate the Windows installer)
#    - LDAP_Studio_Linux_i386.tar.gz (this package is ready for distribution)
#    - LDAP_Studio_Linux_PPC.tar.gz (this package is ready for distribution)
#
# To execute this script, just type "./build.sh" in a terminal.
#

# Moving to the root folder
cd ..

# Testing it the 'packages' folder exists
if [ -d packages ]
then
    # Emptying the 'packages' folder
    cd packages
    rm -rf *
    cd .. 
else
    # Creating the 'packages folder'
    mkdir packages
fi

# Emptying Ivy's cache
rm -rf ~/.ivy

#
# -----------------------------------------------------------------------------
#

# Cleaning the project (just in case)
ant clean

# Generating LDAP Studio for Mac OS X
# (i386 and PPC distribution are identical, so no need to generate both)
ant -Dldapstudio-rcp.os.name=macosx -Dldapstudio-rcp.os.arch=i386

# Moving the generated folder to the 'packages' folder 
mv target/LDAP\ Studio packages

# Renaming it correctly
mv packages/LDAP\ Studio packages/LDAP_Studio_MacOSX

#
# -----------------------------------------------------------------------------
#

# Cleaning the project
ant clean

# Generating LDAP Studio for Windows
ant -Dldapstudio-rcp.os.name=win32 -Dldapstudio-rcp.os.arch=x86

# Moving the generated folder to the 'packages' folder 
mv target/LDAP\ Studio packages
mv packages/LDAP\ Studio packages/LDAP_Studio_Windows

#
# -----------------------------------------------------------------------------
#

# Cleaning the project
ant clean

# Generating LDAP Studio for Linux i386
ant -Dldapstudio-rcp.os.name=linux -Dldapstudio-rcp.os.arch=i386

# Archiving the generated folder
cd target
tar czvf LDAP_Studio_Linux_i386.tar.gz LDAP\ Studio
rm -rf LDAP\ Studio
cd ..

# Moving the generated folder to the 'packages' folder 
mv target/LDAP_Studio_Linux_i386.tar.gz packages

#
# -----------------------------------------------------------------------------
#

# Cleaning the project
ant clean

# Generating LDAP Studio for Linux PPC
ant -Dldapstudio-rcp.os.name=linux -Dldapstudio-rcp.os.arch=ppc

# Archiving the generated folder
cd target
tar czvf LDAP_Studio_Linux_PPC.tar.gz LDAP\ Studio
rm -rf LDAP\ Studio
cd ..

# Moving the generated folder to the 'packages' folder 
mv target/LDAP_Studio_Linux_PPC.tar.gz packages

#
# -----------------------------------------------------------------------------
#
echo ""
echo "Done!"
