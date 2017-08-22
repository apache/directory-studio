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
package org.apache.directory.studio.openldap.config.model;


import java.util.ArrayList;
import java.util.List;


/**
 * Java bean for the 'olcIncludeFile' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcIncludeFile extends OlcConfig
{
    /**
     * Field for the 'cn' attribute.
     */
    @ConfigurationElement(attributeType = "cn", isRdn = true)
    private List<String> cn = new ArrayList<>();

    /**
     * Field for the 'olcInclude' attribute.
     */
    @ConfigurationElement(attributeType = "olcInclude", isOptional = false)
    private List<String> olcInclude = new ArrayList<>();

    /**
     * Field for the 'olcRootDSE' attribute.
     */
    @ConfigurationElement(attributeType = "olcRootDSE")
    private List<String> olcRootDSE = new ArrayList<>();


    /**
     * Creates a new instance of olcBackendConfig.
     */
    public OlcIncludeFile()
    {
    }


    /**
     * Creates a copy instance of olcInclude.
     *
     * @param o the initial object
     */
    public OlcIncludeFile( OlcIncludeFile o )
    {
        olcInclude = copyListString( o.olcInclude );
    }


    /**
     * @param strings
     */
    public void addCn( String... strings )
    {
        for ( String string : strings )
        {
            cn.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcInclude( String... strings )
    {
        for ( String string : strings )
        {
            olcInclude.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcRootDSE( String... strings )
    {
        for ( String string : strings )
        {
            olcRootDSE.add( string );
        }
    }


    public void clearCn()
    {
        cn.clear();
    }


    /**
     */
    public void clearOlcInclude()
    {
        olcInclude.clear();
    }


    public void clearOlcRootDSE()
    {
        olcRootDSE.clear();
    }


    /**
     * @return the cn
     */
    public List<String> getCn()
    {
        return copyListString( cn );
    }


    /**
     * @return the olcInclude
     */
    public List<String> getOlcInclude()
    {
        return copyListString( olcInclude );
    }


    /**
     * @return the olcRootDSE
     */
    public List<String> getOlcRootDSE()
    {
        return copyListString( olcRootDSE );
    }


    /**
     * @param cn the cn to set
     */
    public void setCn( List<String> cn )
    {
        this.cn = copyListString( cn );
    }


    /**
     * @param olcInclude the olcInclude to set
     */
    public void setOlcInclude( List<String> olcInclude )
    {
        this.olcInclude = copyListString( olcInclude );
    }


    /**
     * @param olcRootDSE the olcRootDSE to set
     */
    public void setOlcRootDSE( List<String> olcRootDSE )
    {
        this.olcRootDSE = copyListString( olcRootDSE );
    }


    /**
     * Gets a copy of this object.
     *
     * @return a copy of this object
     */
    public OlcIncludeFile copy()
    {
        return new OlcIncludeFile( this );
    }
}
