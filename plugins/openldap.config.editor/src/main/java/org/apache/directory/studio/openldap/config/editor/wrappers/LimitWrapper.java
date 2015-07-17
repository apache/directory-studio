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
package org.apache.directory.studio.openldap.config.editor.wrappers;

/**
 * An interface for the TimeLimitWrapper and SizeLimitWrapper
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface LimitWrapper extends Comparable<LimitWrapper>
{
    // Define some of the used constants
    public static final Integer HARD_SOFT = Integer.valueOf( -3 );
    public static final Integer UNLIMITED = Integer.valueOf( -1 );

    public static final String HARD_STR = "hard";
    public static final String NONE_STR = "none";
    public static final String SOFT_STR = "soft";
    public static final String UNLIMITED_STR = "unlimited";

    
    /**
     * Clear the TimeLimitWrapper (reset all the values to null)
     */
    void clear();
    
    
    /**
     * @return the globalLimit
     */
    Integer getGlobalLimit();


    /**
     * @param globalLimit the globalLimit to set
     */
    void setGlobalLimit( Integer globalLimit );


    /**
     * @return the softLimit
     */
    Integer getSoftLimit();


    /**
     * @param softLimit the softLimit to set
     */
    void setSoftLimit( Integer softLimit );


    /**
     * @return the hardLimit
     */
    Integer getHardLimit();


    /**
     * @param hardLimit the hardLimit to set
     */
    void setHardLimit( Integer hardLimit );
    
    
    /**
     * @return The Limit's type
     */
    String getType();
    
    
    /**
     * Tells if the LimitWrapper instance is valid
     * @return True if this is a valid instance
     */
    boolean isValid();
}
