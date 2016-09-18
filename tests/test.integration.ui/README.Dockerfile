> Licensed to the Apache Software Foundation (ASF) under one
> or more contributor license agreements.  See the NOTICE file
> distributed with this work for additional information
> regarding copyright ownership.  The ASF licenses this file
> to you under the Apache License, Version 2.0 (the
> "License"); you may not use this file except in compliance
> with the License.  You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing,
> software distributed under the License is distributed on an
> "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
> KIND, either express or implied.  See the License for the
> specific language governing permissions and limitations
> under the License.


# About

A docker image to run Xvfb within a docker container and make an X11 server available to the host.

Used to run Apache Directory Studio UI tests on Jenkins.


## Build image

    docker build -t apachedirectory/xvfb .


## Publish image

    docker push apachedirectory/xvfb


## Usage

    CONTAINER_NAME="dir-studio-ui-tests-xvfb"
    for PORT in $(seq 6006 6099); do netstat -tln | grep $PORT || break; done
    echo "Using TCP port $PORT for Xvfb"
    export DISPLAY=:$((PORT-6000))
    echo "Using DISPLAY $DISPLAY"
    docker run -d --name $CONTAINER_NAME -e DISPLAY=$DISPLAY -p $PORT:$PORT apachedirectory/xvfb
    xdpyinfo -display $DISPLAY
    mvn clean install -Denable-ui-tests
    docker stop $CONTAINER_NAME
    docker rm $CONTAINER_NAME


