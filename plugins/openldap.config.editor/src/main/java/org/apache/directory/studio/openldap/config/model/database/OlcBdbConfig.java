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

import org.apache.directory.studio.openldap.common.ui.model.DatabaseTypeEnum;
import org.apache.directory.studio.openldap.config.model.ConfigurationElement;


/**
 * Java bean for the 'olcBdbConfig' object class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcBdbConfig extends OlcDatabaseConfig
{
    /**
     * Field for the 'olcDbDirectory' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbDirectory", isOptional = false)
    private String olcDbDirectory;

    /**
     * Field for the 'olcDbCacheFree' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbCacheFree")
    private Integer olcDbCacheFree;

    /**
     * Field for the 'olcDbCacheSize' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbCacheSize")
    private Integer olcDbCacheSize;

    /**
     * Field for the 'olcDbCheckpoint' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbCheckpoint")
    private String olcDbCheckpoint;

    /**
     * Field for the 'olcDbConfig' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbConfig")
    private List<String> olcDbConfig = new ArrayList<String>();

    /**
     * Field for the 'olcDbCryptFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbCryptFile")
    private String olcDbCryptFile;

    /**
     * Field for the 'olcDbCryptKey' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbCryptKey")
    private byte[] olcDbCryptKey;

    /**
     * Field for the 'olcDbDirtyRead' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbDirtyRead")
    private Boolean olcDbDirtyRead;

    /**
     * Field for the 'olcDbDNcacheSize' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbDNcacheSize")
    private Integer olcDbDNcacheSize;

    /**
     * Field for the 'olcDbIDLcacheSize' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIDLcacheSize")
    private Integer olcDbIDLcacheSize;

    /**
     * Field for the 'olcDbIndex' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbIndex")
    private List<String> olcDbIndex = new ArrayList<String>();

    /**
     * Field for the 'olcDbLinearIndex' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbLinearIndex")
    private Boolean olcDbLinearIndex;

    /**
     * Field for the 'olcDbLockDetect' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbLockDetect")
    private String olcDbLockDetect;

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
     * Field for the 'olcDbPageSize' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbPageSize")
    private List<String> olcDbPageSize = new ArrayList<String>();

    /**
     * Field for the 'olcDbSearchStack' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbSearchStack")
    private Integer olcDbSearchStack;

    /**
     * Field for the 'olcDbShmKey' attribute.
     */
    @ConfigurationElement(attributeType = "olcDbShmKey")
    private Integer olcDbShmKey;


    /**
     * @param strings
     */
    public void addOlcDbConfig( String... strings )
    {
        for ( String string : strings )
        {
            olcDbConfig.add( string );
        }
    }


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
    public void addOlcDbPageSize( String... strings )
    {
        for ( String string : strings )
        {
            olcDbPageSize.add( string );
        }
    }


    public void clearOlcDbConfig()
    {
        olcDbConfig.clear();
    }


    public void clearOlcDbIndex()
    {
        olcDbIndex.clear();
    }


    public void clearOlcDbPageSize()
    {
        olcDbPageSize.clear();
    }


    /**
     * @return the olcDbCacheFree
     */
    public Integer getOlcDbCacheFree()
    {
        return olcDbCacheFree;
    }


    /**
     * @return the olcDbCacheSize
     */
    public Integer getOlcDbCacheSize()
    {
        return olcDbCacheSize;
    }


    /**
     * @return the olcDbCheckpoint
     */
    public String getOlcDbCheckpoint()
    {
        return olcDbCheckpoint;
    }


    /**
     * @return the olcDbConfig
     */
    public List<String> getOlcDbConfig()
    {
        return copyListString( olcDbConfig );
    }


    /**
     * @return the olcDbCryptFile
     */
    public String getOlcDbCryptFile()
    {
        return olcDbCryptFile;
    }


    /**
     * @return the olcDbCryptKey
     */
    public byte[] getOlcDbCryptKey()
    {
        if ( olcDbCryptKey != null )
        {
            byte[] copy = new byte[olcDbCryptKey.length];
            System.arraycopy( olcDbCryptKey, 0, copy, 0, olcDbCryptKey.length );

            return copy;
        }

        return olcDbCryptKey;
    }


    /**
     * @return the olcDbDirectory
     */
    public String getOlcDbDirectory()
    {
        return olcDbDirectory;
    }


    /**
     * @return the olcDbDirtyRead
     */
    public Boolean getOlcDbDirtyRead()
    {
        return olcDbDirtyRead;
    }


    /**
     * @return the olcDbDNcacheSize
     */
    public Integer getOlcDbDNcacheSize()
    {
        return olcDbDNcacheSize;
    }


    /**
     * @return the olcDbIDLcacheSize
     */
    public Integer getOlcDbIDLcacheSize()
    {
        return olcDbIDLcacheSize;
    }


    /**
     * @return the olcDbIndex
     */
    public List<String> getOlcDbIndex()
    {
        return copyListString( olcDbIndex );
    }


    /**
     * @return the olcDbLinearIndex
     */
    public Boolean getOlcDbLinearIndex()
    {
        return olcDbLinearIndex;
    }


    /**
     * @return the olcDbLockDetect
     */
    public String getOlcDbLockDetect()
    {
        return olcDbLockDetect;
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
     * @return the olcDbPageSize
     */
    public List<String> getOlcDbPageSize()
    {
        return copyListString( olcDbPageSize );
    }


    /**
     * @return the olcDbSearchStack
     */
    public Integer getOlcDbSearchStack()
    {
        return olcDbSearchStack;
    }


    /**
     * @return the olcDbShmKey
     */
    public Integer getOlcDbShmKey()
    {
        return olcDbShmKey;
    }


    /**
     * @param olcDbCacheFree the olcDbCacheFree to set
     */
    public void setOlcDbCacheFree( Integer olcDbCacheFree )
    {
        this.olcDbCacheFree = olcDbCacheFree;
    }


    /**
     * @param olcDbCacheSize the olcDbCacheSize to set
     */
    public void setOlcDbCacheSize( Integer olcDbCacheSize )
    {
        this.olcDbCacheSize = olcDbCacheSize;
    }


    /**
     * @param olcDbCheckpoint the olcDbCheckpoint to set
     */
    public void setOlcDbCheckpoint( String olcDbCheckpoint )
    {
        this.olcDbCheckpoint = olcDbCheckpoint;
    }


    /**
     * @param olcDbConfig the olcDbConfig to set
     */
    public void setOlcDbConfig( List<String> olcDbConfig )
    {
        this.olcDbConfig = copyListString( olcDbConfig );
    }


    /**
     * @param olcDbCryptFile the olcDbCryptFile to set
     */
    public void setOlcDbCryptFile( String olcDbCryptFile )
    {
        this.olcDbCryptFile = olcDbCryptFile;
    }


    /**
     * @param olcDbCryptKey the olcDbCryptKey to set
     */
    public void setOlcDbCryptKey( byte[] olcDbCryptKey )
    {
        if ( olcDbCryptKey != null )
        {
            this.olcDbCryptKey = new byte[olcDbCryptKey.length];
            System.arraycopy( olcDbCryptKey, 0, this.olcDbCryptKey, 0, olcDbCryptKey.length );
        }
        else
        {
            this.olcDbCryptKey = olcDbCryptKey;
        }
    }


    /**
     * @param olcDbDirectory the olcDbDirectory to set
     */
    public void setOlcDbDirectory( String olcDbDirectory )
    {
        this.olcDbDirectory = olcDbDirectory;
    }


    /**
     * @param olcDbDirtyRead the olcDbDirtyRead to set
     */
    public void setOlcDbDirtyRead( Boolean olcDbDirtyRead )
    {
        this.olcDbDirtyRead = olcDbDirtyRead;
    }


    /**
     * @param olcDbDNcacheSize the olcDbDNcacheSize to set
     */
    public void setOlcDbDNcacheSize( Integer olcDbDNcacheSize )
    {
        this.olcDbDNcacheSize = olcDbDNcacheSize;
    }


    /**
     * @param olcDbIDLcacheSize the olcDbIDLcacheSize to set
     */
    public void setOlcDbIDLcacheSize( Integer olcDbIDLcacheSize )
    {
        this.olcDbIDLcacheSize = olcDbIDLcacheSize;
    }


    /**
     * @param olcDbIndex the olcDbIndex to set
     */
    public void setOlcDbIndex( List<String> olcDbIndex )
    {
        this.olcDbIndex = copyListString( olcDbIndex );
    }


    /**
     * @param olcDbLinearIndex the olcDbLinearIndex to set
     */
    public void setOlcDbLinearIndex( Boolean olcDbLinearIndex )
    {
        this.olcDbLinearIndex = olcDbLinearIndex;
    }


    /**
     * @param olcDbLockDetect the olcDbLockDetect to set
     */
    public void setOlcDbLockDetect( String olcDbLockDetect )
    {
        this.olcDbLockDetect = olcDbLockDetect;
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
     * @param olcDbPageSize the olcDbPageSize to set
     */
    public void setOlcDbPageSize( List<String> olcDbPageSize )
    {
        this.olcDbPageSize = copyListString( olcDbPageSize );
    }


    /**
     * @param olcDbSearchStack the olcDbSearchStack to set
     */
    public void setOlcDbSearchStack( Integer olcDbSearchStack )
    {
        this.olcDbSearchStack = olcDbSearchStack;
    }


    /**
     * @param olcDbShmKey the olcDbShmKey to set
     */
    public void setOlcDbShmKey( Integer olcDbShmKey )
    {
        this.olcDbShmKey = olcDbShmKey;
    }


    /**
     * {@inheritDoc}
     */
    public String getOlcDatabaseType()
    {
        return DatabaseTypeEnum.BDB.toString().toLowerCase();
    };
}
