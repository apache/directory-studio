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

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.openldap.config.model.ConfigurationElement;
import org.apache.directory.studio.openldap.config.model.OlcConfig;
import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;


/**
 * Java bean for the 'olcDatabaseConfig' object class. It stores the common parameters
 * for any DB :
 * <ul>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OlcDatabaseConfig extends OlcConfig
{
    /**
     * The overlays list
     */
    private List<OlcOverlayConfig> overlays = new ArrayList<OlcOverlayConfig>();

    /**
     * Field for the 'olcDatabase' attribute.
     */
    @ConfigurationElement(attributeType = "olcDatabase", isOptional = false, isRdn = true)
    private String olcDatabase;

    /**
     * Field for the 'olcAccess' attribute.
     */
    @ConfigurationElement(attributeType = "olcAccess")
    private List<String> olcAccess = new ArrayList<String>();

    /**
     * Field for the 'olcAddContentAcl' attribute.
     */
    @ConfigurationElement(attributeType = "olcAddContentAcl")
    private Boolean olcAddContentAcl;

    /**
     * Field for the 'olcDisabled' attribute. (Added in OpenLDAP 2.4.36)
     */
    @ConfigurationElement(attributeType = "olcDisabled")
    private Boolean olcDisabled;

    /**
     * Field for the 'olcExtraAttrs' attribute. (Added in OpenLDAP 2.4.22)
     */
    @ConfigurationElement(attributeType = "olcExtraAttrs")
    private List<String> olcExtraAttrs;

    /**
     * Field for the 'olcHidden' attribute.
     */
    @ConfigurationElement(attributeType = "olcHidden")
    private Boolean olcHidden;

    /**
     * Field for the 'olcLastMod' attribute.
     */
    @ConfigurationElement(attributeType = "olcLastMod")
    private Boolean olcLastMod;

    /**
     * Field for the 'olcLimits' attribute.
     */
    @ConfigurationElement(attributeType = "olcLimits")
    private List<String> olcLimits = new ArrayList<String>();

    /**
     * Field for the 'olcMaxDerefDepth' attribute.
     */
    @ConfigurationElement(attributeType = "olcMaxDerefDepth")
    private Integer olcMaxDerefDepth;

    /**
     * Field for the 'olcMirrorMode' attribute.
     */
    @ConfigurationElement(attributeType = "olcMirrorMode")
    private Boolean olcMirrorMode;

    /**
     * Field for the 'olcMonitoring' attribute.
     */
    @ConfigurationElement(attributeType = "olcMonitoring")
    private Boolean olcMonitoring;

    /**
     * Field for the 'olcPlugin' attribute.
     */
    @ConfigurationElement(attributeType = "olcPlugin")
    private List<String> olcPlugin = new ArrayList<String>();

    /**
     * Field for the 'olcReadOnly' attribute.
     */
    @ConfigurationElement(attributeType = "olcReadOnly")
    private Boolean olcReadOnly;

    /**
     * Field for the 'olcReplica' attribute.
     */
    @ConfigurationElement(attributeType = "olcReplica")
    private List<String> olcReplica = new ArrayList<String>();

    /**
     * Field for the 'olcReplicaArgsFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcReplicaArgsFile")
    private String olcReplicaArgsFile;

    /**
     * Field for the 'olcReplicaPidFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcReplicaPidFile")
    private String olcReplicaPidFile;

    /**
     * Field for the 'olcReplicationInterval' attribute.
     */
    @ConfigurationElement(attributeType = "olcReplicationInterval")
    private Integer olcReplicationInterval;

    /**
     * Field for the 'olcReplogFile' attribute.
     */
    @ConfigurationElement(attributeType = "olcReplogFile")
    private String olcReplogFile;

    /**
     * Field for the 'olcRequires' attribute.
     */
    @ConfigurationElement(attributeType = "olcRequires")
    private List<String> olcRequires = new ArrayList<String>();

    /**
     * Field for the 'olcRestrict' attribute.
     */
    @ConfigurationElement(attributeType = "olcRestrict")
    private List<String> olcRestrict = new ArrayList<String>();

    /**
     * Field for the 'olcRootDN' attribute.
     */
    @ConfigurationElement(attributeType = "olcRootDN")
    private Dn olcRootDN;

    /**
     * Field for the 'olcRootPW' attribute.
     */
    @ConfigurationElement(attributeType = "olcRootPW")
    private String olcRootPW;

    /**
     * Field for the 'olcSchemaDN' attribute.
     */
    @ConfigurationElement(attributeType = "olcSchemaDN")
    private Dn olcSchemaDN;

    /**
     * Field for the 'olcSecurity' attribute.
     */
    @ConfigurationElement(attributeType = "olcSecurity")
    private List<String> olcSecurity = new ArrayList<String>();

    /**
     * Field for the 'olcSizeLimit' attribute.
     */
    @ConfigurationElement(attributeType = "olcSizeLimit")
    private String olcSizeLimit;

    /**
     * Field for the 'olcSubordinate' attribute.
     */
    @ConfigurationElement(attributeType = "olcSubordinate")
    private String olcSubordinate;

    /**
     * Field for the 'olcSuffix' attribute.
     */
    @ConfigurationElement(attributeType = "olcSuffix")
    private List<Dn> olcSuffix = new ArrayList<Dn>();

    /**
     * Field for the 'olcSyncrepl' attribute.
     */
    @ConfigurationElement(attributeType = "olcSyncrepl")
    private List<String> olcSyncrepl = new ArrayList<String>();

    /**
     * Field for the 'olcSyncUseSubentry' attribute. (Added in OpenLDAP 2.4.20)
     */
    @ConfigurationElement(attributeType = "olcSyncUseSubentry")
    private Boolean olcSyncUseSubentry;

    /**
     * Field for the 'olcTimeLimit' attribute.
     */
    @ConfigurationElement(attributeType = "olcTimeLimit")
    private List<String> olcTimeLimit = new ArrayList<String>();

    /**
     * Field for the 'olcUpdateDN' attribute.
     */
    @ConfigurationElement(attributeType = "olcUpdateDN")
    private Dn olcUpdateDN;

    /**
     * Field for the 'olcUpdateRef' attribute.
     */
    @ConfigurationElement(attributeType = "olcUpdateRef")
    private List<String> olcUpdateRef = new ArrayList<String>();


    /**
     * @param strings
     */
    public void addOlcAccess( String... strings )
    {
        for ( String string : strings )
        {
            olcAccess.add( string );
        }
    }


    /**
     * @param strings The olcExtraAttrs to add
     */
    public void addOlcExtraAttrs( String... strings )
    {
        for ( String string : strings )
        {
            olcExtraAttrs.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcLimits( String... strings )
    {
        for ( String string : strings )
        {
            olcLimits.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcPlugin( String... strings )
    {
        for ( String string : strings )
        {
            olcPlugin.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcReplica( String... strings )
    {
        for ( String string : strings )
        {
            olcReplica.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcRequires( String... strings )
    {
        for ( String string : strings )
        {
            olcRequires.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcRestrict( String... strings )
    {
        for ( String string : strings )
        {
            olcRestrict.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcSecurity( String... strings )
    {
        for ( String string : strings )
        {
            olcSecurity.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcSuffix( Dn... dns )
    {
        for ( Dn dn : dns )
        {
            olcSuffix.add( dn );
        }
    }


    /**
     * @param strings
     */
    public void addOlcSyncrepl( String... strings )
    {
        for ( String string : strings )
        {
            olcSyncrepl.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcTimeLimit( String... strings )
    {
        for ( String string : strings )
        {
            olcTimeLimit.add( string );
        }
    }


    /**
     * @param strings
     */
    public void addOlcUpdateRef( String... strings )
    {
        for ( String string : strings )
        {
            olcUpdateRef.add( string );
        }
    }


    public void clearOlcAccess()
    {
        olcAccess.clear();
    }


    public void clearOlcExtraAttrs()
    {
        olcExtraAttrs.clear();
    }


    public void clearOlcLimits()
    {
        olcLimits.clear();
    }


    public void clearOlcPlugin()
    {
        olcPlugin.clear();
    }


    public void clearOlcReplica()
    {
        olcReplica.clear();
    }


    public void clearOlcRequires()
    {
        olcRequires.clear();
    }


    public void clearOlcRestrict()
    {
        olcRestrict.clear();
    }


    public void clearOlcSecurity()
    {
        olcSecurity.clear();
    }


    public void clearOlcSuffix()
    {
        olcSuffix.clear();
    }


    public void clearOlcSyncrepl()
    {
        olcSyncrepl.clear();
    }


    public void clearOlcTimeLimit()
    {
        olcTimeLimit.clear();
    }


    public void clearOlcUpdateRef()
    {
        olcUpdateRef.clear();
    }


    /**
     * @return the olcAccess
     */
    public List<String> getOlcAccess()
    {
        return copyListString( olcAccess );
    }


    /**
     * @return the olcAddContentAcl
     */
    public Boolean getOlcAddContentAcl()
    {
        return olcAddContentAcl;
    }


    /**
     * @return the olcDatabase
     */
    public String getOlcDatabase()
    {
        return olcDatabase;
    }


    /**
     * @return the olcDisabled
     */
    public Boolean getOlcDisabled()
    {
        return olcDisabled;
    }


    /**
     * @return the olcExtraAttrs
     */
    public List<String> getOlcExtraAttrs()
    {
        return copyListString( olcExtraAttrs );
    }


    /**
     * @return the olcHidden
     */
    public Boolean getOlcHidden()
    {
        return olcHidden;
    }


    /**
     * @return the olcLastMod
     */
    public Boolean getOlcLastMod()
    {
        return olcLastMod;
    }


    /**
     * @return the olcLimits
     */
    public List<String> getOlcLimits()
    {
        return copyListString( olcLimits );
    }


    /**
     * @return the olcMaxDerefDepth
     */
    public Integer getOlcMaxDerefDepth()
    {
        return olcMaxDerefDepth;
    }


    /**
     * @return the olcMirrorMode
     */
    public Boolean getOlcMirrorMode()
    {
        return olcMirrorMode;
    }


    /**
     * @return the olcMonitoring
     */
    public Boolean getOlcMonitoring()
    {
        return olcMonitoring;
    }


    /**
     * @return the olcPlugin
     */
    public List<String> getOlcPlugin()
    {
        return copyListString( olcPlugin );
    }


    /**
     * @return the olcReadOnly
     */
    public Boolean getOlcReadOnly()
    {
        return olcReadOnly;
    }


    /**
     * @return the olcReplica
     */
    public List<String> getOlcReplica()
    {
        return copyListString( olcReplica );
    }


    /**
     * @return the olcReplicaArgsFile
     */
    public String getOlcReplicaArgsFile()
    {
        return olcReplicaArgsFile;
    }


    /**
     * @return the olcReplicaPidFile
     */
    public String getOlcReplicaPidFile()
    {
        return olcReplicaPidFile;
    }


    /**
     * @return the olcReplicationInterval
     */
    public Integer getOlcReplicationInterval()
    {
        return olcReplicationInterval;
    }


    /**
     * @return the olcReplogFile
     */
    public String getOlcReplogFile()
    {
        return olcReplogFile;
    }


    /**
     * @return the olcRequires
     */
    public List<String> getOlcRequires()
    {
        return copyListString( olcRequires );
    }


    /**
     * @return the olcRestrict
     */
    public List<String> getOlcRestrict()
    {
        return copyListString( olcRestrict );
    }


    /**
     * @return the olcRootDN
     */
    public Dn getOlcRootDN()
    {
        return olcRootDN;
    }


    /**
     * @return the olcRootPW
     */
    public String getOlcRootPW()
    {
        return olcRootPW;
    }


    /**
     * @return the olcSchemaDN
     */
    public Dn getOlcSchemaDN()
    {
        return olcSchemaDN;
    }


    /**
     * @return the olcSecurity
     */
    public List<String> getOlcSecurity()
    {
        return copyListString( olcSecurity );
    }


    /**
     * @return the olcSizeLimit
     */
    public String getOlcSizeLimit()
    {
        return olcSizeLimit;
    }


    /**
     * @return the olcSubordinate
     */
    public String getOlcSubordinate()
    {
        return olcSubordinate;
    }


    /**
     * @return the olcSuffix
     */
    public List<Dn> getOlcSuffix()
    {
        return olcSuffix;
    }


    /**
     * @return the olcSyncrepl
     */
    public List<String> getOlcSyncrepl()
    {
        return copyListString( olcSyncrepl );
    }


    /**
     * @return the olcSyncUseSubentry
     */
    public Boolean getOlcSyncUseSubentry()
    {
        return olcSyncUseSubentry;
    }


    /**
     * @return the olcTimeLimit
     */
    public List<String> getOlcTimeLimit()
    {
        return copyListString( olcTimeLimit );
    }


    /**
     * @return the olcUpdateDN
     */
    public Dn getOlcUpdateDN()
    {
        return olcUpdateDN;
    }


    /**
     * @return the olcUpdateRef
     */
    public List<String> getOlcUpdateRef()
    {
        return copyListString( olcUpdateRef );
    }


    /**
     * @return the overlays
     */
    public List<OlcOverlayConfig> getOverlays()
    {
        return overlays;
    }


    /**
     * @param overlays
     */
    public void setOverlays( List<OlcOverlayConfig> overlays )
    {
        this.overlays = overlays;
    }


    public void clearOverlays()
    {
        overlays.clear();
    }


    /**
     * @param o
     * @return
     * @see java.util.List#add(java.lang.Object)
     */
    public boolean addOverlay( OlcOverlayConfig o )
    {
        return overlays.add( o );
    }


    /**
     * @param o
     * @return
     * @see java.util.List#remove(java.lang.Object)
     */
    public boolean removeOverlay( OlcOverlayConfig o )
    {
        return overlays.remove( o );
    }


    /**
     * @param olcAccess the olcAccess to set
     */
    public void setOlcAccess( List<String> olcAccess )
    {
        this.olcAccess = copyListString( olcAccess );
    }


    /**
     * @param olcAddContentAcl the olcAddContentAcl to set
     */
    public void setOlcAddContentAcl( Boolean olcAddContentAcl )
    {
        this.olcAddContentAcl = olcAddContentAcl;
    }


    /**
     * @param olcDatabase the olcDatabase to set
     */
    public void setOlcDatabase( String olcDatabase )
    {
        this.olcDatabase = olcDatabase;
    }


    /**
     * @param oldDisabled the olcDisabled to set
     */
    public void setOlcDisabled( Boolean olcDisabled )
    {
        this.olcDisabled = olcDisabled;
    }


    /**
     * @param olcExtraAttrs the olcExtraAttrs to set
     */
    public void setOlcExtraAttrs( List<String> olcExtraAttrs )
    {
        this.olcExtraAttrs = copyListString( olcExtraAttrs );
    }


    /**
     * @param olcHidden the olcHidden to set
     */
    public void setOlcHidden( Boolean olcHidden )
    {
        this.olcHidden = olcHidden;
    }


    /**
     * @param olcLastMod the olcLastMod to set
     */
    public void setOlcLastMod( Boolean olcLastMod )
    {
        this.olcLastMod = olcLastMod;
    }


    /**
     * @param olcLimits the olcLimits to set
     */
    public void setOlcLimits( List<String> olcLimits )
    {
        this.olcLimits = copyListString( olcLimits );
    }


    /**
     * @param olcMaxDerefDepth the olcMaxDerefDepth to set
     */
    public void setOlcMaxDerefDepth( Integer olcMaxDerefDepth )
    {
        this.olcMaxDerefDepth = olcMaxDerefDepth;
    }


    /**
     * @param olcMirrorMode the olcMirrorMode to set
     */
    public void setOlcMirrorMode( Boolean olcMirrorMode )
    {
        this.olcMirrorMode = olcMirrorMode;
    }


    /**
     * @param olcMonitoring the olcMonitoring to set
     */
    public void setOlcMonitoring( Boolean olcMonitoring )
    {
        this.olcMonitoring = olcMonitoring;
    }


    /**
     * @param olcPlugin the olcPlugin to set
     */
    public void setOlcPlugin( List<String> olcPlugin )
    {
        this.olcPlugin = copyListString( olcPlugin );
    }


    /**
     * @param olcReadOnly the olcReadOnly to set
     */
    public void setOlcReadOnly( Boolean olcReadOnly )
    {
        this.olcReadOnly = olcReadOnly;
    }


    /**
     * @param olcReplica the olcReplica to set
     */
    public void setOlcReplica( List<String> olcReplica )
    {
        this.olcReplica = copyListString( olcReplica );
    }


    /**
     * @param olcReplicaArgsFile the olcReplicaArgsFile to set
     */
    public void setOlcReplicaArgsFile( String olcReplicaArgsFile )
    {
        this.olcReplicaArgsFile = olcReplicaArgsFile;
    }


    /**
     * @param olcReplicaPidFile the olcReplicaPidFile to set
     */
    public void setOlcReplicaPidFile( String olcReplicaPidFile )
    {
        this.olcReplicaPidFile = olcReplicaPidFile;
    }


    /**
     * @param olcReplicationInterval the olcReplicationInterval to set
     */
    public void setOlcReplicationInterval( Integer olcReplicationInterval )
    {
        this.olcReplicationInterval = olcReplicationInterval;
    }


    /**
     * @param olcReplogFile the olcReplogFile to set
     */
    public void setOlcReplogFile( String olcReplogFile )
    {
        this.olcReplogFile = olcReplogFile;
    }


    /**
     * @param olcRequires the olcRequires to set
     */
    public void setOlcRequires( List<String> olcRequires )
    {
        this.olcRequires = copyListString( olcRequires );
    }


    /**
     * @param olcRestrict the olcRestrict to set
     */
    public void setOlcRestrict( List<String> olcRestrict )
    {
        this.olcRestrict = copyListString( olcRestrict );
    }


    /**
     * @param olcRootDN the olcRootDN to set
     */
    public void setOlcRootDN( Dn olcRootDN )
    {
        this.olcRootDN = olcRootDN;
    }


    /**
     * @param olcRootPW the olcRootPW to set
     */
    public void setOlcRootPW( String olcRootPW )
    {
        this.olcRootPW = olcRootPW;
    }


    /**
     * @param olcSchemaDN the olcSchemaDN to set
     */
    public void setOlcSchemaDN( Dn olcSchemaDN )
    {
        this.olcSchemaDN = olcSchemaDN;
    }


    /**
     * @param olcSecurity the olcSecurity to set
     */
    public void setOlcSecurity( List<String> olcSecurity )
    {
        this.olcSecurity = copyListString( olcSecurity );
    }


    /**
     * @param olcSizeLimit the olcSizeLimit to set
     */
    public void setOlcSizeLimit( String olcSizeLimit )
    {
        this.olcSizeLimit = olcSizeLimit;
    }


    /**
     * @param olcSubordinate the olcSubordinate to set
     */
    public void setOlcSubordinate( String olcSubordinate )
    {
        this.olcSubordinate = olcSubordinate;
    }


    /**
     * @param olcSuffix the olcSuffix to set
     */
    public void setOlcSuffix( List<Dn> olcSuffix )
    {
        this.olcSuffix = olcSuffix;
    }


    /**
     * @param olcSyncrepl the olcSyncrepl to set
     */
    public void setOlcSyncrepl( List<String> olcSyncrepl )
    {
        this.olcSyncrepl = copyListString( olcSyncrepl );
    }


    /**
     * @param olcSyncUseSubentry the olcSyncUseSubentry to set
     */
    public void setOlcSyncUseSubentry( Boolean olcSyncUseSubentry )
    {
        this.olcSyncUseSubentry = olcSyncUseSubentry;
    }


    /**
     * @param olcTimeLimit the olcTimeLimit to set
     */
    public void setOlcTimeLimit( List<String> olcTimeLimit )
    {
        this.olcTimeLimit = copyListString( olcTimeLimit );
    }


    /**
     * @param olcUpdateDN the olcUpdateDN to set
     */
    public void setOlcUpdateDN( Dn olcUpdateDN )
    {
        this.olcUpdateDN = olcUpdateDN;
    }


    /**
     * @param olcUpdateRef the olcUpdateRef to set
     */
    public void setOlcUpdateRef( List<String> olcUpdateRef )
    {
        this.olcUpdateRef = copyListString( olcUpdateRef );
    }


    /**
     * Gets the type of the database.
     *
     * @return the type of the database
     */
    public String getOlcDatabaseType()
    {
        return "default";
    }
}
