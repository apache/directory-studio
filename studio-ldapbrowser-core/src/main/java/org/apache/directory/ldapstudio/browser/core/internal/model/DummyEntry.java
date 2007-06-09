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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.events.AttributeAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.AttributeDeletedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.RDN;
import org.apache.directory.ldapstudio.browser.core.model.URL;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Subschema;


public class DummyEntry implements IEntry
{

    private static final long serialVersionUID = 4833907766031149971L;

    private DN dn;

    private DummyConnection dummyConnection;

    private String connectionName;

    private Map attributeMap;


    protected DummyEntry()
    {
    }


    public DummyEntry( DN dn, IConnection connection )
    {
        if ( connection instanceof DummyConnection )
        {
            this.dummyConnection = ( DummyConnection ) connection;
        }
        else
        {
            this.connectionName = connection.getName();
        }

        this.dn = dn;
        attributeMap = new LinkedHashMap();
    }


    public void setDn( DN dn )
    {
        this.dn = dn;
    }


    public void addAttribute( IAttribute attributeToAdd ) throws ModelModificationException
    {
        attributeMap.put( attributeToAdd.getDescription().toLowerCase(), attributeToAdd );
        EventRegistry.fireEntryUpdated( new AttributeAddedEvent( attributeToAdd.getEntry().getConnection(), this,
            attributeToAdd ), this );
    }


    public void addChild( IEntry childrenToAdd )
    {
    }


    public void deleteAttribute( IAttribute attributeToDelete ) throws ModelModificationException
    {
        attributeMap.remove( attributeToDelete.getDescription().toLowerCase() );
        EventRegistry.fireEntryUpdated( new AttributeDeletedEvent( attributeToDelete.getEntry().getConnection(), this,
            attributeToDelete ), this );
    }


    public void deleteChild( IEntry childrenToDelete )
    {
    }


    public IAttribute getAttribute( String attributeDescription )
    {
        return ( IAttribute ) attributeMap.get( attributeDescription.toLowerCase() );
    }


    public AttributeHierarchy getAttributeWithSubtypes( String attributeDescription )
    {

        AttributeDescription ad = new AttributeDescription( attributeDescription );

        List attributeList = new ArrayList();
        Iterator iterator = attributeMap.values().iterator();
        while ( iterator.hasNext() )
        {
            IAttribute attribute = ( IAttribute ) iterator.next();

            AttributeDescription other = new AttributeDescription( attributeDescription );
            if ( other.isSubtypeOf( ad, getConnection().getSchema() ) )
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
            AttributeHierarchy ah = new AttributeHierarchy( this, attributeDescription, ( IAttribute[] ) attributeList
                .toArray( new IAttribute[attributeList.size()] ) );
            return ah;
        }
    }


    public IAttribute[] getAttributes()
    {
        return ( IAttribute[] ) attributeMap.values().toArray( new IAttribute[attributeMap.size()] );
    }


    public IConnection getConnection()
    {
        return dummyConnection != null ? dummyConnection : BrowserCorePlugin.getDefault().getConnectionManager()
            .getConnection( this.connectionName );
    }


    public DN getDn()
    {
        return dn;
    }


    public URL getUrl()
    {
        return new URL( getConnection(), getDn() );
    }


    public IEntry getParententry()
    {
        return null;
    }


    public RDN getRdn()
    {
        return dn.getRdn();
    }


    public IEntry[] getChildren()
    {
        return null;
    }


    public int getChildrenCount()
    {
        return -1;
    }


    public String getChildrenFilter()
    {
        return ""; //$NON-NLS-1$
    }


    public Subschema getSubschema()
    {
        return new Subschema( this );
    }


    public boolean hasMoreChildren()
    {
        return false;
    }


    public boolean hasParententry()
    {
        return false;
    }


    public boolean hasChildren()
    {
        return false;
    }


    public boolean isAlias()
    {
        return Arrays.asList( this.getSubschema().getObjectClassNames() ).contains( ObjectClassDescription.OC_ALIAS );
    }


    public boolean isAttributesInitialized()
    {
        return true;
    }


    public boolean isConsistent()
    {
        // check empty attributes and empty values
        Iterator attributeIterator = attributeMap.values().iterator();
        while ( attributeIterator.hasNext() )
        {
            IAttribute attribute = ( IAttribute ) attributeIterator.next();
            if ( !attribute.isConsistent() )
                return false;
        }

        // check objectclass attribute
        if ( !attributeMap.containsKey( IAttribute.OBJECTCLASS_ATTRIBUTE.toLowerCase() ) )
        {
            return false;
        }
        IAttribute ocAttribute = ( IAttribute ) attributeMap.get( IAttribute.OBJECTCLASS_ATTRIBUTE.toLowerCase() );
        String[] ocValues = ocAttribute.getStringValues();
        boolean structuralObjectClassAvailable = false;
        for ( int i = 0; i < ocValues.length; i++ )
        {
            ObjectClassDescription ocd = this.getConnection().getSchema().getObjectClassDescription( ocValues[i] );
            if ( ocd.isStructural() )
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
        String[] mustAttributeNames = this.getSubschema().getMustAttributeNames();
        for ( int i = 0; i < mustAttributeNames.length; i++ )
        {
            if ( !attributeMap.containsKey( mustAttributeNames[i].toLowerCase() ) )
                return false;
        }

        return true;
    }


    public boolean isDirectoryEntry()
    {
        return false;
    }


    public boolean isReferral()
    {
        return Arrays.asList( this.getSubschema().getObjectClassNames() ).contains( ObjectClassDescription.OC_REFERRAL );
    }


    public boolean isSubentry()
    {
        return Arrays.asList( this.getSubschema().getObjectClassNames() ).contains( ObjectClassDescription.OC_SUBENTRY );
    }


    public boolean isChildrenInitialized()
    {
        return false;
    }


    public void moveTo( IEntry newParent ) throws ModelModificationException
    {
    }


    public void rename( RDN newRdn, boolean deleteOldRdn ) throws ModelModificationException
    {

    }


    public void setAlias( boolean b )
    {
    }


    public void setAttributesInitialized( boolean b )
    {
    }


    public void setDirectoryEntry( boolean isDirectoryEntry )
    {
    }


    public void setHasMoreChildren( boolean b )
    {
    }


    public void setHasChildrenHint( boolean b )
    {
    }


    public void setReferral( boolean b )
    {
    }


    public void setSubentry( boolean b )
    {
    }


    public void setChildrenFilter( String filter )
    {
    }


    public void setChildrenInitialized( boolean b )
    {
    }


    public Object getAdapter( Class adapter )
    {
        return null;
    }

}
