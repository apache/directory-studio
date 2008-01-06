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


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.AttributeDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EmptyValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueModifiedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.URL;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Subschema;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * An implementation of {@link IEntry} that just holds another instance
 * of {@link IEntry} and delegates all method calls to this instance.
 * It is used for bookmarks, alias and referral entries.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DelegateEntry implements IEntry, EntryUpdateListener
{

    private static final long serialVersionUID = -4488685394817691963L;

    /** The connection id. */
    private String connectionId;

    /** The DN. */
    private LdapDN dn;

    /** The entry does not exist flag. */
    private boolean entryDoesNotExist;

    /** The delegate. */
    private IEntry delegate;


    protected DelegateEntry()
    {
    }


    /**
     * Creates a new instance of DelegateEntry.
     * 
     * @param connection the connection of the delegate
     * @param dn the DN of the delegate
     */
    public DelegateEntry( IBrowserConnection connection, LdapDN dn )
    {
        this.connectionId = connection.getConnection().getId();
        this.dn = dn;
        this.entryDoesNotExist = false;
        this.delegate = null;
        EventRegistry.addEntryUpdateListener( this, BrowserCorePlugin.getDefault().getEventRunner() );
    }


    /**
     * Gets the delegate.
     * 
     * @return the delegate, may be null if the delegate doesn't exist.
     */
    protected IEntry getDelegate()
    {
        if ( delegate != null
            && !delegate.getBrowserConnection().getConnection().getJNDIConnectionWrapper().isConnected() )
        {
            entryDoesNotExist = false;
            delegate = null;
        }
        return delegate;
    }


    /**
     * Sets the delegate.
     * 
     * @param delegate the new delegate
     */
    protected void setDelegate( IEntry delegate )
    {
        this.delegate = delegate;
    }


    /**
     * {@inheritDoc}
     */
    public IBrowserConnection getBrowserConnection()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getBrowserConnection();
        }
        else
        {
            return BrowserCorePlugin.getDefault().getConnectionManager().getBrowserConnectionById( connectionId );
        }
    }


    /**
     * {@inheritDoc}
     */
    public LdapDN getDn()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getDn();
        }
        else
        {
            return dn;
        }
    }


    /**
     * {@inheritDoc}
     */
    public URL getUrl()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getUrl();
        }
        else
        {
            return new URL( getBrowserConnection(), getDn() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAttributesInitialized()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isAttributesInitialized();
        }
        else if ( entryDoesNotExist )
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isOperationalAttributesInitialized()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isOperationalAttributesInitialized();
        }
        else if ( entryDoesNotExist )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean isChildrenInitialized()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isChildrenInitialized();
        }
        else if ( entryDoesNotExist )
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirectoryEntry()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isDirectoryEntry();
        }
        else
        {
            return true;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void addAttribute( IAttribute attributeToAdd )
    {
        if ( getDelegate() != null )
        {
            getDelegate().addAttribute( attributeToAdd );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void addChild( IEntry childrenToAdd )
    {
        if ( getDelegate() != null )
        {
            getDelegate().addChild( childrenToAdd );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void deleteAttribute( IAttribute attributeToDelete )
    {
        if ( getDelegate() != null )
        {
            getDelegate().deleteAttribute( attributeToDelete );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void deleteChild( IEntry childrenToDelete )
    {
        if ( getDelegate() != null )
        {
            getDelegate().deleteChild( childrenToDelete );
        }
    }


    /**
     * {@inheritDoc}
     */
    public IAttribute getAttribute( String attributeDescription )
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getAttribute( attributeDescription );
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public AttributeHierarchy getAttributeWithSubtypes( String attributeDescription )
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getAttributeWithSubtypes( attributeDescription );
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public IAttribute[] getAttributes()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getAttributes();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public IEntry getParententry()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getParententry();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Rdn getRdn()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getRdn();
        }
        else
        {
            Rdn rdn = dn.getRdn();
            return rdn == null ? new Rdn() : rdn;
        }
    }


    /**
     * {@inheritDoc}
     */
    public IEntry[] getChildren()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getChildren();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public int getChildrenCount()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getChildrenCount();
        }
        else
        {
            return -1;
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getChildrenFilter()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getChildrenFilter();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Subschema getSubschema()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getSubschema();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasMoreChildren()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().hasMoreChildren();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasParententry()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().hasParententry();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasChildren()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().hasChildren();
        }
        else
        {
            return true;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isConsistent()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isConsistent();
        }
        else
        {
            return true;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setAttributesInitialized( boolean b )
    {
        if ( !b )
        {
            if ( getDelegate() != null )
            {
                getDelegate().setAttributesInitialized( b );
            }
            setDelegate( null );
            entryDoesNotExist = false;
        }
        else
        {
            if ( getDelegate() == null )
            {
                setDelegate( getBrowserConnection().getEntryFromCache( dn ) );
                if ( getDelegate() == null )
                {
                    // entry doesn't exist!
                    entryDoesNotExist = true;
                }
            }
            if ( getDelegate() != null )
            {
                getDelegate().setAttributesInitialized( b );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setOperationalAttributesInitialized( boolean b )
    {
        if ( !b )
        {
            if ( getDelegate() != null )
            {
                getDelegate().setOperationalAttributesInitialized( b );
            }
            setDelegate( null );
            entryDoesNotExist = false;
        }
        else
        {
            if ( getDelegate() == null )
            {
                setDelegate( getBrowserConnection().getEntryFromCache( dn ) );
                if ( getDelegate() == null )
                {
                    // entry doesn't exist!
                    entryDoesNotExist = true;
                }
            }
            if ( getDelegate() != null )
            {
                getDelegate().setOperationalAttributesInitialized( b );
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void setDirectoryEntry( boolean b )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setDirectoryEntry( b );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setHasMoreChildren( boolean b )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setHasMoreChildren( b );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setHasChildrenHint( boolean b )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setHasChildrenHint( b );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setChildrenFilter( String filter )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setChildrenFilter( filter );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setChildrenInitialized( boolean b )
    {
        if ( !b )
        {
            if ( getDelegate() != null )
            {
                getDelegate().setChildrenInitialized( b );
            }
            entryDoesNotExist = false;
        }
        else
        {
            if ( this.getDelegate() == null )
            {
                setDelegate( getBrowserConnection().getEntryFromCache( dn ) );
                if ( this.getDelegate() == null )
                {
                    // entry doesn't exist!
                    entryDoesNotExist = true;
                }
            }
            if ( getDelegate() != null )
            {
                getDelegate().setChildrenInitialized( b );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isAlias()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isAlias();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setAlias( boolean b )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setAlias( b );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReferral()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isReferral();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setReferral( boolean b )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setReferral( b );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSubentry()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isSubentry();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setSubentry( boolean b )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setSubentry( b );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void entryUpdated( EntryModificationEvent event )
    {
        if ( event.getModifiedEntry() == getDelegate() )
        {
            if ( event instanceof AttributeAddedEvent )
            {
                AttributeAddedEvent e = ( AttributeAddedEvent ) event;
                AttributeAddedEvent delegateEvent = new AttributeAddedEvent( e.getConnection(), this, e
                    .getAddedAttribute() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof AttributeDeletedEvent )
            {
                AttributeDeletedEvent e = ( AttributeDeletedEvent ) event;
                AttributeDeletedEvent delegateEvent = new AttributeDeletedEvent( e.getConnection(), this, e
                    .getDeletedAttribute() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof AttributesInitializedEvent )
            {
                AttributesInitializedEvent delegateEvent = new AttributesInitializedEvent( this );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof EmptyValueAddedEvent )
            {
                EmptyValueAddedEvent e = ( EmptyValueAddedEvent ) event;
                EmptyValueAddedEvent delegateEvent = new EmptyValueAddedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getAddedValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof EmptyValueDeletedEvent )
            {
                EmptyValueDeletedEvent e = ( EmptyValueDeletedEvent ) event;
                EmptyValueDeletedEvent delegateEvent = new EmptyValueDeletedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getDeletedValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ChildrenInitializedEvent )
            {
                ChildrenInitializedEvent delegateEvent = new ChildrenInitializedEvent( this );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ValueAddedEvent )
            {
                ValueAddedEvent e = ( ValueAddedEvent ) event;
                ValueAddedEvent delegateEvent = new ValueAddedEvent( e.getConnection(), this, e.getModifiedAttribute(),
                    e.getAddedValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ValueDeletedEvent )
            {
                ValueDeletedEvent e = ( ValueDeletedEvent ) event;
                ValueDeletedEvent delegateEvent = new ValueDeletedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getDeletedValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ValueModifiedEvent )
            {
                ValueModifiedEvent e = ( ValueModifiedEvent ) event;
                ValueModifiedEvent delegateEvent = new ValueModifiedEvent( e.getConnection(), this, e
                    .getModifiedAttribute(), e.getOldValue(), e.getNewValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
            else if ( event instanceof ValueRenamedEvent )
            {
                ValueRenamedEvent e = ( ValueRenamedEvent ) event;
                ValueRenamedEvent delegateEvent = new ValueRenamedEvent( e.getConnection(), this, e.getOldValue(), e
                    .getNewValue() );
                EventRegistry.fireEntryUpdated( delegateEvent, this );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter )
    {
        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IBrowserConnection.class )
        {
            return this.getBrowserConnection();
        }
        if ( adapter == IEntry.class )
        {
            return this;
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o )
    {
        // check argument
        if ( o == null || !( o instanceof IEntry ) )
        {
            return false;
        }
        IEntry e = ( IEntry ) o;

        // compare dn and connection
        return getDn() == null ? e.getDn() == null : ( getDn().equals( e.getDn() ) && getBrowserConnection().equals(
            e.getBrowserConnection() ) );
    }
}
