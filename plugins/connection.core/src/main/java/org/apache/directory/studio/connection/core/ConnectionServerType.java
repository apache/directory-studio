/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.studio.connection.core;


/**
 * This enum contains all detectable directory server types.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum ConnectionServerType
{
    APACHEDS,
    IBM_DIRECTORY_SERVER,
    IBM_SECUREWAY_DIRECTORY,
    IBM_TIVOLI_DIRECTORY_SERVER,
    IBM_SECURITY_DIRECTORY_SERVER,
    MICROSOFT_ACTIVE_DIRECTORY_2000,
    MICROSOFT_ACTIVE_DIRECTORY_2003,
    NETSCAPE,
    NOVELL,
    OPENLDAP,
    OPENLDAP_2_0,
    OPENLDAP_2_1,
    OPENLDAP_2_2,
    OPENLDAP_2_3,
    OPENLDAP_2_4,
    SIEMENS_DIRX,
    SUN_DIRECTORY_SERVER,
    RED_HAT_389,
    FORGEROCK_OPEN_DJ,
    UNKNOWN;
}