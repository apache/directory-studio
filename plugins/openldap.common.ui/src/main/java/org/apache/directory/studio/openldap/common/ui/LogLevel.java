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
package org.apache.directory.studio.openldap.common.ui;

/**
 * The various LogLevel values :
 * <ul>
 * <li>none        0</li>
 * <li>trace       1</li>
 * <li>packets     2</li>
 * <li>args        4</li>
 * <li>conns       8</li>
 * <li>BER        16</li>
 * <li>filter     32</li>
 * <li>config     64</li>
 * <li>ACL       128</li>
 * <li>stats     256</li>
 * <li>stats2    512</li>
 * <li>shell    1024</li>
 * <li>parse    2048</li>
 * <li>sync    16384</li>
 * <li>any       -1</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum LogLevel
{
    NONE(0),
    TRACE(1),
    PACKETS(2),
    ARGS(4),
    CONNS(8),
    BER(16),
    FILTER(32),
    CONFIG(64),
    ACL(128),
    STATS(256),
    STATS2(512),
    SHELL(1024),
    PARSE(2048),
    // 4096 not used
    // 8196 not used
    SYNC(16384),
    // 327168 and -1 are equivalent
    ANY(-1);
    
    /** The inner value */
    private int value;
    
    
    /**
     * Creates a new instance of LogLevel.
     *
     * @param value The internal value
     */
    private LogLevel( int value )
    {
        this.value = value;
    }
    
    /**
     * @return The internal integer value
     */
    public int getValue()
    {
        return value;
    }
}
