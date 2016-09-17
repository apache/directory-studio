#
#   Licensed to the Apache Software Foundation (ASF) under one 
#   or more contributor license agreements.  See the NOTICE file
#   distributed with this work for additional information
#   regarding copyright ownership.  The ASF licenses this file
#   to you under the Apache License, Version 2.0 (the
#   "License"); you may not use this file except in compliance
#   with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing,
#   software distributed under the License is distributed on an
#   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
#   KIND, either express or implied.  See the License for the 
#   specific language governing permissions and limitations
#   under the License.
#

FROM debian:8

RUN \
     apt-get update && \
     apt-get install -y xvfb openssh-server && \
     apt-get clean && \
     rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/* && \
     mkdir /var/run/sshd && \
     ssh-keygen -q -f /root/.ssh/id_rsa -N "" && \
     cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

EXPOSE 6006

CMD /usr/sbin/sshd && ssh -o StrictHostKeyChecking=no -T -L *:6006:localhost:6000 root@localhost Xvfb :0 -screen 0 1024x768x16

