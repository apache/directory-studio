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
package org.apache.directory.studio.apacheds.configuration.model.v152;


/**
 * This enum contains all the supported mechanisms.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum SupportedMechanismEnum
{
    /** The Simple mechanism */
    SIMPLE("SIMPLE"), //$NON-NLS-1$

    /** The CRAM-MD5 (SASL) mechanism */
    CRAM_MD5("CRAM-MD5 (SASL)"), //$NON-NLS-1$

    /** The DIGEST-MD5 (SASL) mechanism */
    DIGEST_MD5("DIGEST-MD5 (SASL)"), //$NON-NLS-1$

    /** The GSSAPI (SASL) mechanism */
    GSSAPI("GSSAPI (SASL)"), ; //$NON-NLS-1$

    /** The name */
    private String name;


    /**
     * Creates a new instance of SupportedMechanismEnum.
     *
     * @param name
     *      the name
     */
    private SupportedMechanismEnum( String name )
    {
        this.name = name;
    }


    /**
     * Gets the name.
     *
     * @return
     *      the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the name.
     *
     * @param name
     *      the name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    public String toString()
    {
        return name;
    }
}
