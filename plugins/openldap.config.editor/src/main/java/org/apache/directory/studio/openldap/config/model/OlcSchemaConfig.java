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
 * Java bean for the 'OlcSchemaConfig' object class.
 */
public class OlcSchemaConfig extends OlcConfig
{
    /**
     * Field for the 'cn' attribute.
     */
    @ConfigurationElement(attributeType = "cn", isRdn = true)
    private List<String> cn = new ArrayList<String>();

    /**
     * Field for the 'olcAttributeTypes' attribute.
     */
    @ConfigurationElement(attributeType = "olcAttributeTypes")
    private List<String> olcAttributeTypes = new ArrayList<String>();

    /**
     * Field for the 'olcDitContentRules' attribute.
     */
    @ConfigurationElement(attributeType = "olcDitContentRules")
    private List<String> olcDitContentRules = new ArrayList<String>();

    /**
     * Field for the 'olcLdapSyntaxes' attribute.
     */
    @ConfigurationElement(attributeType = "olcLdapSyntaxes")
    private List<String> olcLdapSyntaxes = new ArrayList<String>();

    /**
     * Field for the 'olcObjectClasses' attribute.
     */
    @ConfigurationElement(attributeType = "olcObjectClasses")
    private List<String> olcObjectClasses = new ArrayList<String>();

    /**
     * Field for the 'olcObjectIdentifier' attribute.
     */
    @ConfigurationElement(attributeType = "olcObjectIdentifier")
    private List<String> olcObjectIdentifier = new ArrayList<String>();


    public void clearCn()
    {
        cn.clear();
    }


    public void clearOlcAttributeTypes()
    {
        olcAttributeTypes.clear();
    }


    public void clearOlcDitContentRules()
    {
        olcDitContentRules.clear();
    }


    public void clearOlcLdapSyntaxes()
    {
        olcLdapSyntaxes.clear();
    }


    public void clearOlcObjectClasses()
    {
        olcObjectClasses.clear();
    }


    public void clearOlcObjectIdentifier()
    {
        olcObjectIdentifier.clear();
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
    public void addOlcAttributeTypes( String... strings )
    {
        for ( String string : strings )
        {
            olcAttributeTypes.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcDitContentRules( String... strings )
    {
        for ( String string : strings )
        {
            olcDitContentRules.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcLdapSyntaxes( String... strings )
    {
        for ( String string : strings )
        {
            olcLdapSyntaxes.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcObjectClasses( String... strings )
    {
        for ( String string : strings )
        {
            olcObjectClasses.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcObjectIdentifier( String... strings )
    {
        for ( String string : strings )
        {
            olcObjectIdentifier.add( string );
        }
    }


    /**
     * @return the cn
     */
    public List<String> getCn()
    {
        return cn;
    }


    /**
     * @return the olcAttributeTypes
     */
    public List<String> getOlcAttributeTypes()
    {
        return olcAttributeTypes;
    }


    /**
     * @return the olcDitContentRules
     */
    public List<String> getOlcDitContentRules()
    {
        return olcDitContentRules;
    }


    /**
     * @return the olcLdapSyntaxes
     */
    public List<String> getOlcLdapSyntaxes()
    {
        return olcLdapSyntaxes;
    }


    /**
     * @return the olcObjectClasses
     */
    public List<String> getOlcObjectClasses()
    {
        return olcObjectClasses;
    }


    /**
     * @return the olcObjectIdentifier
     */
    public List<String> getOlcObjectIdentifier()
    {
        return olcObjectIdentifier;
    }


    /**
     * @param cn the cn to set
     */
    public void setCn( List<String> cn )
    {
        this.cn = cn;
    }


    /**
     * @param olcAttributeTypes the olcAttributeTypes to set
     */
    public void setOlcAttributeTypes( List<String> olcAttributeTypes )
    {
        this.olcAttributeTypes = olcAttributeTypes;
    }


    /**
     * @param olcDitContentRules the olcDitContentRules to set
     */
    public void setOlcDitContentRules( List<String> olcDitContentRules )
    {
        this.olcDitContentRules = olcDitContentRules;
    }


    /**
     * @param olcLdapSyntaxes the olcLdapSyntaxes to set
     */
    public void setOlcLdapSyntaxes( List<String> olcLdapSyntaxes )
    {
        this.olcLdapSyntaxes = olcLdapSyntaxes;
    }


    /**
     * @param olcObjectClasses the olcObjectClasses to set
     */
    public void setOlcObjectClasses( List<String> olcObjectClasses )
    {
        this.olcObjectClasses = olcObjectClasses;
    }


    /**
     * @param olcObjectIdentifier the olcObjectIdentifier to set
     */
    public void setOlcObjectIdentifier( List<String> olcObjectIdentifier )
    {
        this.olcObjectIdentifier = olcObjectIdentifier;
    }
}
