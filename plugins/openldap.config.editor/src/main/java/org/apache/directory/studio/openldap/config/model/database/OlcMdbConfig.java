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
package org.apache.directory.studio.openldap.config.model.database;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.openldap.config.model.ConfigurationElement;
import org.apache.directory.studio.openldap.config.model.OlcDatabaseConfig;


/**
 * Java bean for the 'olcMdbConfig' object class. There are a few parameter
 * that can be managed for the MDB databse :
 * <ul>
 * <li>directory : the place on disk the DB will be stored</li>
 * <li>maxSize : the size of the database, in bytes. As it can't grow automatically, set it to
 * the expected maximum DB size</li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcMdbConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcDbDirectory' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbDirectory", isOptional = false)
    private String olcDbDirectory;

    /**
     * Field for the 'olcDbCheckpoint' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbCheckpoint")
    private String olcDbCheckpoint;

    /**
     * Field for the 'olcDbEnvFlags' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbEnvFlags")
    private List<String> olcDbEnvFlags = new ArrayList<String>();

    /**
     * Field for the 'olcDbIndex' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIndex")
    private List<String> olcDbIndex = new ArrayList<String>();

    /**
     * Field for the 'olcDbMaxEntrySize' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbMaxEntrySize")
    private Integer olcDbMaxEntrySize;

    /**
     * Field for the 'olcDbMaxReaders' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbMaxReaders")
    private Integer olcDbMaxReaders;

    /**
     * Field for the 'olcDbMaxSize' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbMaxSize")
    private Long olcDbMaxSize;

    /**
     * Field for the 'olcDbMode' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbMode")
    private String olcDbMode;

    /**
     * Field for the 'olcDbNoSync' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbNoSync")
    private Boolean olcDbNoSync;

    /**
     * Field for the 'olcDbSearchStack' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbSearchStack")
    private Integer olcDbSearchStack;


    /**
     * @param strings
     */
    public void addOlcDbIndex( String... strings )
    {
        for ( String string : strings )
        {
            olcDbIndex.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcDbEnvFlags( String... strings )
    {
        for ( String string : strings )
        {
            olcDbEnvFlags.add( string );
        }
    }


    public void clearOlcDbIndex()
    {
        olcDbIndex.clear();
    }


    public void clearOlcDbEnvFlags()
    {
        olcDbEnvFlags.clear();
    }


    /**
     * {@inheritDoc}
     */
    public String getOlcDatabaseType()
    {
        return "mdb";
    }


    /**
     * @return the olcDbCheckpoint
     */
    public String getOlcDbCheckpoint()
    {
        return olcDbCheckpoint;
    }


    /**
     * @return the olcDbDirectory
     */
    public String getOlcDbDirectory()
    {
        return olcDbDirectory;
    }


    /**
     * @return the olcDbIndex
     */
    public List<String> getOlcDbIndex()
    {
        return copyListString( olcDbIndex );
    }


    /**
     * @return the olcDbEnvFlags
     */
    public List<String> getOlcDbEnvFlags()
    {
        return copyListString( olcDbEnvFlags );
    }


    /**
     * @return the olcDbMaxEntrySize
     */
    public Integer getOlcDbMaxEntrySize()
    {
        return olcDbMaxEntrySize;
    }


    /**
     * @return the olcDbMaxReaders
     */
    public Integer getOlcDbMaxReaders()
    {
        return olcDbMaxReaders;
    }


    /**
     * @return the olcDbMaxSize
     */
    public Long getOlcDbMaxSize()
    {
        return olcDbMaxSize;
    }


    /**
     * @return the olcDbMode
     */
    public String getOlcDbMode()
    {
        return olcDbMode;
    }


    /**
     * @return the olcDbNoSync
     */
    public Boolean getOlcDbNoSync()
    {
        return olcDbNoSync;
    }


    /**
     * @return the olcDbSearchStack
     */
    public Integer getOlcDbSearchStack()
    {
        return olcDbSearchStack;
    }


    /**
     * @param olcDbCheckpoint the olcDbCheckpoint to set
     */
    public void setOlcDbCheckpoint( String olcDbCheckpoint )
    {
        this.olcDbCheckpoint = olcDbCheckpoint;
    }


    /**
     * @param olcDbDirectory the olcDbDirectory to set
     */
    public void setOlcDbDirectory( String olcDbDirectory )
    {
        this.olcDbDirectory = olcDbDirectory;
    }


    /**
     * @param olcDbIndex the olcDbIndex to set
     */
    public void setOlcDbIndex( List<String> olcDbIndex )
    {
        this.olcDbIndex = copyListString( olcDbIndex );
    }


    /**
     * @param olcDbEnvFlags the olcDbEnvFlags to set
     */
    public void setOlcDbEnvFlagsx( List<String> olcDbEnvFlags )
    {
        this.olcDbEnvFlags = copyListString( olcDbEnvFlags );
    }


    /**
     * @param olcDbMaxEntrySize the olcDbMaxEntrySize to set
     */
    public void setOlcDbMaxEntrySize( Integer olcDbMaxEntrySize )
    {
        this.olcDbMaxEntrySize = olcDbMaxEntrySize;
    }


    /**
     * @param olcDbMaxReaders the olcDbMaxReaders to set
     */
    public void setOlcDbMaxReaders( Integer olcDbMaxReaders )
    {
        this.olcDbMaxReaders = olcDbMaxReaders;
    }


    /**
     * @param olcDbMaxSize the olcDbMaxSize to set
     */
    public void setOlcDbMaxSize( Long olcDbMaxSize )
    {
        this.olcDbMaxSize = olcDbMaxSize;
    }


    /**
     * @param olcDbMode the olcDbMode to set
     */
    public void setOlcDbMode( String olcDbMode )
    {
        this.olcDbMode = olcDbMode;
    }


    /**
     * @param olcDbNoSync the olcDbNoSync to set
     */
    public void setOlcDbNoSync( Boolean olcDbNoSync )
    {
        this.olcDbNoSync = olcDbNoSync;
    }


    /**
     * @param olcDbSearchStack the olcDbSearchStack to set
     */
    public void setOlcDbSearchStack( Integer olcDbSearchStack )
    {
        this.olcDbSearchStack = olcDbSearchStack;
    }
}
