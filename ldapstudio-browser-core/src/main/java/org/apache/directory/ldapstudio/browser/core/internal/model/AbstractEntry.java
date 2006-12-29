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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.AttributeAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.AttributeDeletedEvent;
import org.apache.directory.ldapstudio.browser.core.events.AttributesInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ChildrenInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryDeletedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.RDN;
import org.apache.directory.ldapstudio.browser.core.model.URL;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.Subschema;
import org.eclipse.search.ui.ISearchPageScoreComputer;


public abstract class AbstractEntry implements IEntry
{

    public static final int HAS_CHILDREN_HINT_FLAG = 1;

    public static final int IS_DIRECTORY_ENTRY_FLAG = 2;

    public static final int IS_ALIAS_FLAG = 4;

    public static final int IS_REFERRAL_FLAG = 8;

    public static final int IS_SUBENTRY_FLAG = 16;

    private volatile int flags;


    protected AbstractEntry()
    {
        this.flags = HAS_CHILDREN_HINT_FLAG;
    }


    protected abstract void setParent( IEntry newParent );


    protected abstract void setRdn( RDN newRdn );


    public void addChild( IEntry childToAdd, ModelModifier source )
    {

        ChildrenInfo ci = this.getJNDIConnection().getChildrenInfo( this );
        if ( ci == null )
        {
            ci = new ChildrenInfo();
            this.getJNDIConnection().setChildrenInfo( this, ci );
        }

        if ( ci.childrenSet == null )
        {
            ci.childrenSet = new LinkedHashSet();
        }
        ci.childrenSet.add( childToAdd );
        this.entryModified( new EntryAddedEvent( childToAdd.getConnection(), childToAdd, source ) );
    }


    public void deleteChild( IEntry childToDelete, ModelModifier source )
    {

        ChildrenInfo ci = this.getJNDIConnection().getChildrenInfo( this );

        if ( ci != null )
        {
            ci.childrenSet.remove( childToDelete );
            if ( ci.childrenSet == null || ci.childrenSet.isEmpty() )
            {
                this.getJNDIConnection().setChildrenInfo( this, null );
            }
            this.entryModified( new EntryDeletedEvent( this.getJNDIConnection(), childToDelete, source ) );
        }
    }


    public void addAttribute( IAttribute attributeToAdd, ModelModifier source ) throws ModelModificationException
    {

        String oidString = attributeToAdd.getAttributeDescription().toOidString( getConnection().getSchema() );

        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai == null )
        {
            ai = new AttributeInfo();
            this.getJNDIConnection().setAttributeInfo( this, ai );
        }

        if ( !this.equals( attributeToAdd.getEntry() ) )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__attributes_entry_is_not_myself );
        }
        // else
        // if(ai.attributeMap.containsKey(attributeToAdd.getDescription().toLowerCase()))
        // {
        else if ( ai.attributeMap.containsKey( oidString.toLowerCase() ) )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__attribute_already_exists );
        }
        else
        {
            // ai.attributeMap.put(attributeToAdd.getDescription().toLowerCase(),
            // attributeToAdd);
            ai.attributeMap.put( oidString.toLowerCase(), attributeToAdd );
            this.entryModified( new AttributeAddedEvent( this.getJNDIConnection(), this, attributeToAdd, source ) );
        }
    }


    public void deleteAttribute( IAttribute attributeToDelete, ModelModifier source ) throws ModelModificationException
    {

        String oidString = attributeToDelete.getAttributeDescription().toOidString( getConnection().getSchema() );

        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );

        // if(ai != null && ai.attributeMap != null &&
        // ai.attributeMap.containsKey(attributeToDelete.getDescription().toLowerCase()))
        // {
        // IAttribute attribute =
        // (IAttribute)ai.attributeMap.get(attributeToDelete.getDescription().toLowerCase());
        // ai.attributeMap.remove(attributeToDelete.getDescription().toLowerCase());
        if ( ai != null && ai.attributeMap != null && ai.attributeMap.containsKey( oidString.toLowerCase() ) )
        {
            IAttribute attribute = ( IAttribute ) ai.attributeMap.get( oidString.toLowerCase() );
            ai.attributeMap.remove( oidString.toLowerCase() );
            if ( ai.attributeMap.isEmpty() )
            {
                this.getJNDIConnection().setAttributeInfo( this, null );
            }
            this.entryModified( new AttributeDeletedEvent( this.getJNDIConnection(), this, attribute, source ) );
        }
        else
        {
            throw new ModelModificationException( BrowserCoreMessages.model__attribute_does_not_exist );
        }
    }


    public boolean isConsistent()
    {

        // if(!this.isAttributesInitialized() && this.isDirectoryEntry())
        // return true;

        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );

        if ( ai == null || ai.attributeMap == null )
        {
            return isDirectoryEntry();
        }

        // check empty attributes and empty values
        Iterator attributeIterator = ai.attributeMap.values().iterator();
        while ( attributeIterator.hasNext() )
        {
            IAttribute attribute = ( IAttribute ) attributeIterator.next();
            if ( !attribute.isConsistent() )
                return false;
        }

        if ( !this.isDirectoryEntry() )
        {
            // check objectclass attribute
            if ( !ai.attributeMap.containsKey( IAttribute.OBJECTCLASS_ATTRIBUTE_OID.toLowerCase() ) )
            {
                return false;
            }
            IAttribute ocAttribute = ( IAttribute ) ai.attributeMap.get( IAttribute.OBJECTCLASS_ATTRIBUTE_OID
                .toLowerCase() );
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
            // String[] mustAttributeNames =
            // this.getSubschema().getMustAttributeNames();
            // for(int i=0; i<mustAttributeNames.length; i++) {
            // if(!ai.attributeMap.containsKey(mustAttributeNames[i].toLowerCase()))
            // return false;
            // }
            AttributeTypeDescription[] mustAtds = this.getSubschema().getMustAttributeTypeDescriptions();
            for ( int i = 0; i < mustAtds.length; i++ )
            {
                AttributeTypeDescription mustAtd = mustAtds[i];
                if ( !ai.attributeMap.containsKey( mustAtd.getNumericOID().toLowerCase() ) )
                    return false;
            }
        }

        return true;
    }


    public boolean isDirectoryEntry()
    {
        return ( this.flags & IS_DIRECTORY_ENTRY_FLAG ) != 0;
    }


    public void setDirectoryEntry( boolean isDirectoryEntry )
    {
        if ( isDirectoryEntry )
            this.flags = this.flags | IS_DIRECTORY_ENTRY_FLAG;
        else
            this.flags = this.flags & ~IS_DIRECTORY_ENTRY_FLAG;
    }


    public boolean isAlias()
    {
        if ( ( this.flags & IS_ALIAS_FLAG ) != 0 )
        {
            return true;
        }

        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai != null )
        {
            return Arrays.asList( this.getSubschema().getObjectClassNames() )
                .contains( ObjectClassDescription.OC_ALIAS );
        }

        return false;
    }


    public void setAlias( boolean b )
    {
        if ( b )
            this.flags = this.flags | IS_ALIAS_FLAG;
        else
            this.flags = this.flags & ~IS_ALIAS_FLAG;
    }


    public boolean isReferral()
    {
        if ( ( this.flags & IS_REFERRAL_FLAG ) != 0 )
        {
            return true;
        }

        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai != null )
        {
            return Arrays.asList( this.getSubschema().getObjectClassNames() ).contains(
                ObjectClassDescription.OC_REFERRAL );
        }

        return false;
    }


    public void setReferral( boolean b )
    {
        if ( b )
            this.flags = this.flags | IS_REFERRAL_FLAG;
        else
            this.flags = this.flags & ~IS_REFERRAL_FLAG;
    }


    public boolean isSubentry()
    {
        if ( ( this.flags & IS_SUBENTRY_FLAG ) != 0 )
        {
            return true;
        }

        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai != null )
        {
            return Arrays.asList( this.getSubschema().getObjectClassNames() ).contains(
                ObjectClassDescription.OC_SUBENTRY );
        }

        return false;
    }


    public void setSubentry( boolean b )
    {
        if ( b )
            this.flags = this.flags | IS_SUBENTRY_FLAG;
        else
            this.flags = this.flags & ~IS_SUBENTRY_FLAG;
    }


    /**
     * Triggers firering of the modification event.
     * 
     * @param event
     */
    private void entryModified( EntryModificationEvent event )
    {
        EventRegistry.fireEntryUpdated( event, this );
    }


    public RDN getRdn()
    {
        return this.getDn().getRdn();
    }


    public boolean isAttributesInitialized()
    {
        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        return ai != null && ai.attributesInitialzed;
    }


    public void setAttributesInitialized( boolean b, ModelModifier source )
    {

        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai == null && b )
        {
            ai = new AttributeInfo();
            this.getJNDIConnection().setAttributeInfo( this, ai );
        }

        if ( ai != null )
        {
            ai.attributesInitialzed = b;
        }

        if ( ai != null && !b )
        {
            ai.attributeMap.clear();
            this.getJNDIConnection().setAttributeInfo( this, null );
        }

        this.entryModified( new AttributesInitializedEvent( this, source ) );
    }


    public IAttribute[] getAttributes()
    {
        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai == null || ai.attributeMap == null )
        {
            return null;
        }
        else
        {
            return ( IAttribute[] ) ai.attributeMap.values().toArray( new IAttribute[0] );
        }
    }


    public IAttribute getAttribute( String attributeDescription )
    {
        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai == null || ai.attributeMap == null )
        {
            return null;
        }
        else
        {
            AttributeDescription ad = new AttributeDescription( attributeDescription );
            String oidString = ad.toOidString( getConnection().getSchema() );
            return ( IAttribute ) ai.attributeMap.get( oidString.toLowerCase() );
        }
    }


    public AttributeHierarchy getAttributeWithSubtypes( String attributeDescription )
    {

        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai == null || ai.attributeMap == null )
        {
            return null;
        }
        else
        {
            List attributeList = new ArrayList();

            IAttribute myAttribute = getAttribute( attributeDescription );
            if ( myAttribute != null )
            {
                attributeList.add( myAttribute );
            }

            AttributeDescription ad = new AttributeDescription( attributeDescription );
            Map clonedAttributeMap = new HashMap( ai.attributeMap );
            Iterator iterator = clonedAttributeMap.values().iterator();
            while ( iterator.hasNext() )
            {
                IAttribute attribute = ( IAttribute ) iterator.next();

                AttributeDescription other = attribute.getAttributeDescription();
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
                IAttribute[] attributes = ( IAttribute[] ) attributeList.toArray( new IAttribute[attributeList.size()] );
                AttributeHierarchy ah = new AttributeHierarchy( this, attributeDescription, attributes );
                return ah;
            }
        }
    }


    public Subschema getSubschema()
    {
        AttributeInfo ai = this.getJNDIConnection().getAttributeInfo( this );
        if ( ai == null )
        {
            ai = new AttributeInfo();
            this.getJNDIConnection().setAttributeInfo( this, ai );
        }
        if ( ai.subschema == null )
        {
            ai.subschema = new Subschema( this );
        }

        return ai.subschema;
    }


    public void setChildrenInitialized( boolean b, ModelModifier source )
    {
        ChildrenInfo ci = this.getJNDIConnection().getChildrenInfo( this );
        if ( ci == null && b )
        {
            ci = new ChildrenInfo();
            this.getJNDIConnection().setChildrenInfo( this, ci );
        }

        if ( ci != null )
        {
            ci.childrenInitialzed = b;
        }

        if ( ci != null && !b )
        {
            if ( ci.childrenSet != null )
            {
                ci.childrenSet.clear();
            }
            this.getJNDIConnection().setChildrenInfo( this, null );
        }

        this.entryModified( new ChildrenInitializedEvent( this, source ) );
    }


    public boolean isChildrenInitialized()
    {
        ChildrenInfo ci = this.getJNDIConnection().getChildrenInfo( this );
        return ci != null && ci.childrenInitialzed;
    }


    public IEntry[] getChildren()
    {
        int count = getChildrenCount();
        if ( count < 0 )
        {
            return null;
        }
        else if ( count == 0 )
        {
            return new IEntry[0];
        }
        else
        {
            IEntry[] children = new IEntry[count];
            ChildrenInfo ci = this.getJNDIConnection().getChildrenInfo( this );
            int i = 0;
            if ( ci.childrenSet != null )
            {
                Iterator it = ci.childrenSet.iterator();
                for ( ; it.hasNext(); i++ )
                {
                    children[i] = ( IEntry ) it.next();
                }
            }
            return children;
        }
    }


    public int getChildrenCount()
    {
        if ( isSubentry() )
        {
            return 0;
        }
        ChildrenInfo ci = this.getJNDIConnection().getChildrenInfo( this );
        if ( ci == null )
        {
            return -1;
        }
        else
        {
            return ci.childrenSet == null ? 0 : ci.childrenSet.size();
        }
    }


    public void setHasMoreChildren( boolean b, ModelModifier source )
    {
        ChildrenInfo ci = this.getJNDIConnection().getChildrenInfo( this );
        if ( ci == null )
        {
            ci = new ChildrenInfo();
            this.getJNDIConnection().setChildrenInfo( this, ci );
        }
        ci.hasMoreChildren = b;

        this.entryModified( new ChildrenInitializedEvent( this, source ) );
    }


    public boolean hasMoreChildren()
    {
        ChildrenInfo ci = this.getJNDIConnection().getChildrenInfo( this );
        return ci != null && ci.hasMoreChildren;
    }


    public void setHasChildrenHint( boolean b, ModelModifier source )
    {
        if ( b )
            this.flags = this.flags | HAS_CHILDREN_HINT_FLAG;
        else
            this.flags = this.flags & ~HAS_CHILDREN_HINT_FLAG;
    }


    public boolean hasChildren()
    {
        return ( this.flags & HAS_CHILDREN_HINT_FLAG ) != 0 || this.getChildrenCount() > 0;
    }


    public String getChildrenFilter()
    {
        return this.getJNDIConnection().getChildrenFilter( this );
    }


    public void setChildrenFilter( String childrenFilter )
    {
        this.getJNDIConnection().setChildrenFilter( this, childrenFilter );
    }


    public boolean hasParententry()
    {
        return this.getParententry() != null;
    }


    private Connection getJNDIConnection()
    {
        return ( Connection ) this.getConnection();
    }


    public String toString()
    {
        return this.getDn().toString();
    }


    public boolean equals( Object o )
    {

        // check argument
        if ( o == null || !( o instanceof IEntry ) )
        {
            return false;
        }
        IEntry e = ( IEntry ) o;

        // compare dn and connection
        return this.getDn() == null ? e.getDn() == null : ( this.getDn().equals( e.getDn() ) && this.getConnection()
            .equals( e.getConnection() ) );
    }


    public Object getAdapter( Class adapter )
    {

        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IConnection.class )
        {
            return this.getConnection();
        }
        if ( adapter == IEntry.class )
        {
            return this;
        }
        return null;
    }


    public IEntry getEntry()
    {
        return this;
    }


    public URL getUrl()
    {
        return new URL( getConnection(), getDn() );
    }

}
