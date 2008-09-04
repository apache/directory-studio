#!/bin/sh
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#  
#  http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
  
# Looping on each file
for file in $( ls newplugins ); 
do
    # The path to the file
    tmpFile=newplugins/$file

    # By default the packaging is 'jar'
    packaging=jar

    # Extracting the groupId, artifactId and version
    case "$file" in
    # Test for _64_ in artifactId
    *"_64_"*)
        fullname=${file%%_64_*}_64
        groupId=${fullname%\.*}
        artifactId=${fullname##*.}
        tmp=${file%\.jar}
        version=${tmp#*_64_}
        ;;
    # Other cases
    *)
        fullname=${file%%_*}
        groupId=${fullname%\.*}
        artifactId=${fullname##*.}
        tmp=${file%\.jar}
        version=${tmp#*_}
        ;;
    esac
    
    # Pack to zip file if it's a directory
    if [ -d $tmpFile ]; 
    then
        echo "Zipping directory $tmpFile to $tmpFile.zip"
        tmpDir=$PWD
        cd $tmpFile
        zip -r ../$file.zip *
        tmpFile=$tmpFile.zip
        cd $tmpDir
        packaging=zip
    fi
    
    echo
    echo "=> Deploying $groupId:$artifactId:$version:$packaging to the repository"
    
    mvn deploy:deploy-file \
        -Durl=file://$PWD \
        -Dfile=$tmpFile \
        -DgroupId=$groupId \
        -DartifactId=$artifactId \
        -Dversion=$version \
        -Dpackaging=$packaging \
        -DgeneratePom=true

done