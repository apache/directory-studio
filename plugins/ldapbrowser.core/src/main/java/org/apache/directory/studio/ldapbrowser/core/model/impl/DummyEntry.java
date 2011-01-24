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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeDescription;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ICompareableEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;


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
 */
public class DummyEntry implements IEntry, ICompareableEntry
{

    private static final long serialVersionUID = 4833907766031149971L;

    /** The Dn. */
    private Dn dn;

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The attribute map. */
    private Map<String, IAttribute> attributeMap;


    protected DummyEntry()
    {
    }


    /**
     * Creates a new instance of DummyEntry.
     * 
     * @param dn the Dn
     * @param browserConnection the browser connection
     */
    public DummyEntry( Dn dn, IBrowserConnection browserConnection )
    {
        this.dn = dn;
        this.browserConnection = browserConnection;
        attributeMap = new LinkedHashMap<String, IAttribute>();
    }


    /**
     * Sets the Dn.
     * 
     * @param dn the new Dn
     */
    public void setDn( Dn dn )
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
        List<IAttribute> attributeList = new ArrayList<IAttribute>();

        IAttribute myAttribute = getAttribute( attributeDescription );
        if ( myAttribute != null )
        {
            attributeList.add( myAttribute );
        }

        AttributeDescription ad = new AttributeDescription( attributeDescription );
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
        return browserConnection;
    }


    /**
     * {@inheritDoc}
     */
    public Dn getDn()
    {
        return dn;
    }


    /**
     * {@inheritDoc}
     */
    public LdapURL getUrl()
    {
        return Utils.getLdapURL( this );
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
     * This implementation always returns false.
     */
    public boolean hasMoreChildren()
    {
        return false;
    }


    /**
     * This implementation always returns null.
     */
    public StudioConnectionBulkRunnableWithProgress getNextPageChildrenRunnable()
    {
        return null;
    }


    /**
     * This implementation always returns null.
     */
    public StudioConnectionBulkRunnableWithProgress getTopPageChildrenRunnable()
    {
        return null;
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
        return getObjectClassDescriptions().contains(
            getBrowserConnection().getSchema().getObjectClassDescription( SchemaConstants.ALIAS_OC ) );
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
    public boolean isInitOperationalAttributes()
    {
        return true;
    }


    /**
     * This implementation always returns false.
     */
    public boolean isFetchAliases()
    {
        return false;
    }


    /**
     * This implementation always returns false.
     */
    public boolean isFetchReferrals()
    {
        return false;
    }


    /**
     * This implementation always returns false.
     */
    public boolean isFetchSubentries()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReferral()
    {
        return getObjectClassDescriptions().contains(
            getBrowserConnection().getSchema().getObjectClassDescription( SchemaConstants.REFERRAL_OC ) );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSubentry()
    {
        return getObjectClassDescriptions().contains(
            getBrowserConnection().getSchema().getObjectClassDescription( SchemaConstants.SUBENTRY_OC ) );
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
    public void setInitOperationalAttributes( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setFetchAliases( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setFetchReferrals( boolean b )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setFetchSubentries( boolean b )
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
    public void setTopPageChildrenRunnable( StudioConnectionBulkRunnableWithProgress topPageChildrenRunnable )
    {
    }


    /**
     * This implementation does nothing.
     */
    public void setNextPageChildrenRunnable( StudioConnectionBulkRunnableWithProgress nextPageChildrenRunnable )
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


    public Collection<ObjectClass> getObjectClassDescriptions()
    {
        Collection<ObjectClass> ocds = new ArrayList<ObjectClass>();
        IAttribute ocAttribute = getAttribute( SchemaConstants.OBJECT_CLASS_AT );
        if ( ocAttribute != null )
        {
            String[] ocNames = ocAttribute.getStringValues();
            Schema schema = getBrowserConnection().getSchema();
            for ( String ocName : ocNames )
            {
                ObjectClass ocd = schema.getObjectClassDescription( ocName );
                ocds.add( ocd );
            }
        }
        return ocds;
    }


    @Override
    public int hashCode()
    {
        return getDn().hashCode();
    }


    @Override
    public boolean equals( Object o )
    {
        // check argument
        if ( o == null || !( o instanceof ICompareableEntry ) )
        {
            return false;
        }
        ICompareableEntry e = ( ICompareableEntry ) o;

        // compare dn and connection
        return getDn() == null ? e.getDn() == null : ( getDn().equals( e.getDn() ) && getBrowserConnection().equals(
            e.getBrowserConnection() ) );
    }

}
