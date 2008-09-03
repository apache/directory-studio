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
    tmpFile=newplugins/$file
  
    # pack to jar file if it's a directory
    if [ -d $tmpFile ]; 
    then
        echo "Zipping directory $tmpFile to $tmpFile.jar"
        tmpDir=$PWD
        cd $tmpFile/..
        zip -r $file.jar $file
        tmpFile=$tmpFile.jar
        cd $tmpDir
    fi

    # Test for _64_ in artifactId
    case "$file" in
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
    
    echo
    echo "=> Deploying $groupId:$artifactId:$version to the repository"
    
    mvn deploy:deploy-file \
        -Durl=file://$PWD \
        -Dfile=$tmpFile \
        -DgroupId=$groupId \
        -DartifactId=$artifactId \
        -Dversion=$version \
        -Dpackaging=jar \
        -DgeneratePom=true

done