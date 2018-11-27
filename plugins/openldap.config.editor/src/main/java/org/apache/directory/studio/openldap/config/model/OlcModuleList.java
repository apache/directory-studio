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
 * Java bean for the 'OlcGlobal' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcModuleList extends OlcConfig
{
    /**
     * Field for the 'cn' attribute.
     */
    @ConfigurationElement(attributeType = "cn", isRdn = true, version="2.4.0")
    private List<String> cn = new ArrayList<>();

    /**
     * Field for the 'olcAllows' attribute.
     */
    @ConfigurationElement(attributeType = "olcModuleLoad", version="2.4.0")
    private List<String> olcModuleLoad = new ArrayList<>();

    /**
     * Field for the 'olcModulePath' attribute.
     */
    @ConfigurationElement(attributeType = "olcModulePath", version="2.4.0")
    private String olcModulePath;


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
    public void addOlcModuleLoad( String... strings )
    {
        for ( String string : strings )
        {
            olcModuleLoad.add( string );
        }
    }


    public void clearCn()
    {
        cn.clear();
    }


    public void clearOlcModuleLoad()
    {
        olcModuleLoad.clear();
    }


    /**
     * @return the cn
     */
    public List<String> getCn()
    {
        return copyListString( cn );
    }


    /**
     * @return the olcModuleLoad
     */
    public List<String> getOlcModuleLoad()
    {
        return copyListString( olcModuleLoad );
    }


    /**
     * @return the olcModulePath
     */
    public String getOlcModulePath()
    {
        return olcModulePath;
    }


    /**
     * @param cn the cn to set
     */
    public void setCn( List<String> cn )
    {
        this.cn = copyListString( cn );
    }


    /**
     * @param olcModuleLoad the olcModuleLoad to set
     */
    public void setOlcModuleLoad( List<String> olcModuleLoad )
    {
        this.olcModuleLoad = copyListString( olcModuleLoad );
    }


    /**
     * @param olcArgsFile the olcArgsFile to set
     */
    public void setOlcModulePath( String olcModulePath )
    {
        this.olcModulePath = olcModulePath;
    }
}
