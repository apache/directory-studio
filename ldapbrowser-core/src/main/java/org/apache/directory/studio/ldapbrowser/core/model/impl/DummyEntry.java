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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.syntax.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeDescription;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.URL;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Subschema;


/**
 * An {@link DummyEntry} is an implementation if {@link IEntry} that doesn't 
 * represent a directory entry. 
 * 
 * Most methods do nothing. It isn't possible to add child entries.
 * It only contains a map for attributes and a connection to retrieve
 * schema information. 
 * 
 * It is used for temporary {@link IEntry} objects, e.g. in the new entry wizard. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DummyEntry implements IEntry
{

    private static final long serialVersionUID = 4833907766031149971L;

    /** The DN. */
    private LdapDN dn;

    /** The dummy connection. */
    private DummyConnection dummyConnection;

    /** The connection id. */
    private String connectionId;

    /** The attribute map. */
    private Map<String, IAttribute> attributeMap;


    protected DummyEntry()
    {
    }


    /**
     * Creates a new instance of DummyEntry.
     * 
     * @param dn the DN
     * @param browserConnection the browser connection
     */
    public DummyEntry( LdapDN dn, IBrowserConnection browserConnection )
    {
        if ( browserConnection instanceof DummyConnection )
        {
            this.dummyConnection = ( DummyConnection ) browserConnection;
        }
        else
        {
            this.connectionId = browserConnection.getConnection().getId();
        }

        this.dn = dn;
        attributeMap = new LinkedHashMap<String, IAttribute>();
    }


    /**
     * Sets the DN.
     * 
     * @param dn the new DN
     */
    public void setDn( LdapDN dn )
    {
        this.dn = dn;
    }


    /**
     * {@inheritDoc}
     */
    public void addAttribute( IAttribute attributeToAdd )
    {
        String oidString = attributeToAdd.getAttributeDescription().toOidString( getBrowserConnection().getSchema() );
        attributeMap.put( oidString.toLowerCase(), attributeToAdd );
        EventRegistry.fireEntryUpdated( new AttributeAddedEvent( attributeToAdd.getEntry().getBrowserConnection(),
            this, attributeToAdd ), this );
    }


    /**
     * This implementation does nothing.
     */
    public void addChild( IEntry childrenToAdd )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void deleteAttribute( IAttribute attributeToDelete )
    {
        String oidString = attributeToDelete.getAttributeDescription().toOidString( getBrowserConnection().getSchema() );
        attributeMap.remove( oidString.toLowerCase() );
        EventRegistry.fireEntryUpdated( new AttributeDeletedEvent( attributeToDelete.getEntry().getBrowserConnection(),
            this, attributeToDelete ), this );
    }


    public void deleteChild( IEntry childrenToDelete )
    {
    }


    /**
     * {@inheritDoc}
     */
    public IAttribute getAttribute( String attributeDescription )
    {
        AttributeDescription ad = new AttributeDescription( attributeDescription );
        String oidString = ad.toOidString( getBrowserConnection().getSchema() );
        return attributeMap.get( oidString.toLowerCase() );
    }


    /**
     * {@inheritDoc}
     */
    public AttributeHierarchy getAttributeWithSubtypes( String attributeDescription )
    {
        AttributeDescription ad = new AttributeDescription( attributeDescription );

        List<IAttribute> attributeList = new ArrayList<IAttribute>();
        for ( IAttribute attribute : attributeMap.values() )
        {
            AttributeDescription other = attribute.getAttributeDescription();
            if ( other.isSubtypeOf( ad, getBrowserConnection().getSchema() ) )
            {
                attributeList.add( attribute );
            }
        }

        if ( attributeList.isEmpty() )
        {
            return null;
        }
        else
        {
            AttributeHierarchy ah = new AttributeHierarchy( this, attributeDescription, attributeList
                .toArray( new IAttribute[attributeList.size()] ) );
            return ah;
        }
    }


    /**
     * {@inheritDoc}
     */
    public IAttribute[] getAttributes()
    {
        return attributeMap.values().toArray( new IAttribute[attributeMap.size()] );
    }


    /**
     * {@inheritDoc}
     */
    public IBrowserConnection getBrowserConnection()
    {
        return dummyConnection != null ? dummyConnection : BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnectionById( this.connectionId );
    }


    /**
     * {@inheritDoc}
     */
    public LdapDN getDn()
    {
        return dn;
    }


    /**
     * {@inheritDoc}
     */
    public URL getUrl()
    {
        return new URL( getBrowserConnection(), getDn() );
    }


    /**
     * This implementation always returns null.
     */
    public IEntry getParententry()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Rdn getRdn()
    {
        Rdn rdn = dn.getRdn();
        return rdn == null ? new Rdn() : rdn;
    }


    /**
     * This implementation always returns null.
     */
    public IEntry[] getChildren()
    {
        return null;
    }


    /**
     * This implementation always returns -1.
     */
    public int getChildrenCount()
    {
        return -1;
    }


    /**
     * This implementation always returns the empty string.
     */
    public String getChildrenFilter()
    {
        return ""; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public Subschema getSubschema()
    {
        return new Subschema( this );
    }


    /**
     * This implementation always returns false.
     */
    public boolean hasMoreChildren()
    {
        return false;
    }


    /**
     * This implementation always returns false.
     */
    public boolean hasParententry()
    {
        return false;
    }


    /**
     * This implementation always returns false.
     */
    public boolean hasChildren()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAlias()
    {
        return Arrays.asList( getSubschema().getObjectClassNames() ).contains( SchemaConstants.ALIAS_OC );
    }


    /**
     * This implementation always returns true.
     */
    public boolean isAttributesInitialized()
    {
        return true;
    }


    /**
     * This implementation always returns true.
     */
    public boolean isOperationalAttributesInitialized()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isConsistent()
    {
        // check empty attributes and empty values
        for ( IAttribute attribute : attributeMap.values() )
        {
            if ( !attribute.isConsistent() )
            {
                return false;
            }
        }

        // check objectClass attribute
        if ( !attributeMap.containsKey( IAttribute.OBJECTCLASS_ATTRIBUTE_OID.toLowerCase() ) )
        {
            return false;
        }
        IAttribute ocAttribute = attributeMap.get( IAttribute.OBJECTCLASS_ATTRIBUTE_OID.toLowerCase() );
        String[] ocValues = ocAttribute.getStringValues();
        boolean structuralObjectClassAvailable = false;
        for ( String ocValue : ocValues )
        {
            ObjectClassDescription ocd = this.getBrowserConnection().getSchema().getObjectClassDescription( ocValue );
            if ( ocd.getKind() == ObjectClassTypeEnum.STRUCTURAL )
            {
                structuralObjectClassAvailable = true;
                break;
            }
        }
        if ( !structuralObjectClassAvailable )
        {
            return false;
        }

        // check must-attributes
        String[] mustAttributeNames = getSubschema().getMustAttributeNames();
        for ( String mustAttributeName : mustAttributeNames )
        {
            AttributeDescription ad = new AttributeDescription( mustAttributeName );
            String oidString = ad.toOidString( getBrowserConnection().getSchema() );
            if ( !attributeMap.containsKey( oidString.toLowerCase() ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * This implementation always returns false.
     */
    public boolean isDirectoryEntry()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReferral()
    {
        return Arrays.asList( getSubschema().getObjectClassNames() ).contains( SchemaConstants.REFERRAL_OC );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSubentry()
    {
        return Arrays.asList( this.getSubschema().getObjectClassNames() ).contains( SchemaConstants.SUBENTRY_OC );
    }


    /**
     * This implementation always returns false.
     */
    public boolean isChildrenInitialized()
    {
        return false;
    }


    /**
     * This implementation does nothing.
     */
    public void setAlias( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setAttributesInitialized( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setOperationalAttributesInitialized( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setDirectoryEntry( boolean isDirectoryEntry )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setHasMoreChildren( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setHasChildrenHint( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setReferral( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setSubentry( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setChildrenFilter( String filter )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setChildrenInitialized( boolean b )
    {
    }


    /**
     * This implementation always returns null.
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter )
    {
        return null;
    }

}
