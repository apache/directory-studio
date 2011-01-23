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


import java.util.Collection;

import org.apache.directory.shared.ldap.name.Dn;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.filter.LdapURL;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * An implementation of {@link IEntry} that just holds another instance
 * of {@link IEntry} and delegates all method calls to this instance.
 * It is used for bookmarks, alias and referral entries.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class DelegateEntry implements IEntry
{

    private static final long serialVersionUID = -4488685394817691963L;

    /** The connection id. */
    protected String connectionId;

    /** The Dn. */
    protected Dn dn;

    /** The entry does not exist flag. */
    protected boolean entryDoesNotExist;

    /** The delegate. */
    protected IEntry delegate;


    protected DelegateEntry()
    {
    }


    /**
     * Creates a new instance of DelegateEntry.
     * 
     * @param browserConnection the browser connection of the delegate
     * @param dn the Dn of the delegate
     */
    protected DelegateEntry( IBrowserConnection browserConnection, Dn dn )
    {
        this.connectionId = browserConnection.getConnection() != null ? browserConnection.getConnection().getId()
            : null;
        this.dn = dn;
        this.entryDoesNotExist = false;
        this.delegate = null;
    }


    /**
     * Gets the delegate.
     * 
     * @return the delegate, may be null if the delegate doesn't exist.
     */
    protected IEntry getDelegate()
    {
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnectionById( connectionId );
        if ( browserConnection == null )
        {
            browserConnection = new DummyConnection( Schema.DEFAULT_SCHEMA );
        }

        // always get the fresh entry from cache
        delegate = browserConnection.getEntryFromCache( dn );

        if ( delegate != null
            && !delegate.getBrowserConnection().getConnection().getConnectionWrapper().isConnected() )
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
            IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
                .getBrowserConnectionById( connectionId );
            if ( browserConnection == null )
            {
                browserConnection = new DummyConnection( Schema.DEFAULT_SCHEMA );
            }
            return browserConnection;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Dn getDn()
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
    public LdapURL getUrl()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getUrl();
        }
        else
        {
            return Utils.getLdapURL( this );
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
    public boolean isInitOperationalAttributes()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isInitOperationalAttributes();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isFetchAliases()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isFetchAliases();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isFetchReferrals()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isFetchReferrals();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isFetchSubentries()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().isFetchSubentries();
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
            return new IAttribute[0];
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
            return new IEntry[0];
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
    public StudioConnectionBulkRunnableWithProgress getTopPageChildrenRunnable()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getTopPageChildrenRunnable();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public StudioConnectionBulkRunnableWithProgress getNextPageChildrenRunnable()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getNextPageChildrenRunnable();
        }
        else
        {
            return null;
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
    public void setInitOperationalAttributes( boolean b )
    {
        if ( !b )
        {
            if ( getDelegate() != null )
            {
                getDelegate().setInitOperationalAttributes( b );
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
                getDelegate().setInitOperationalAttributes( b );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setFetchAliases( boolean b )
    {
        if ( !b )
        {
            if ( getDelegate() != null )
            {
                getDelegate().setFetchAliases( b );
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
                getDelegate().setFetchAliases( b );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setFetchReferrals( boolean b )
    {
        if ( !b )
        {
            if ( getDelegate() != null )
            {
                getDelegate().setFetchReferrals( b );
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
                getDelegate().setFetchReferrals( b );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setFetchSubentries( boolean b )
    {
        if ( !b )
        {
            if ( getDelegate() != null )
            {
                getDelegate().setFetchSubentries( b );
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
                getDelegate().setFetchSubentries( b );
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
    public void setTopPageChildrenRunnable( StudioConnectionBulkRunnableWithProgress topPageChildrenRunnable )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setTopPageChildrenRunnable( topPageChildrenRunnable );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setNextPageChildrenRunnable( StudioConnectionBulkRunnableWithProgress nextPageChildrenRunnable )
    {
        if ( getDelegate() != null )
        {
            getDelegate().setNextPageChildrenRunnable( nextPageChildrenRunnable );
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
    public Collection<ObjectClass> getObjectClassDescriptions()
    {
        if ( getDelegate() != null )
        {
            return getDelegate().getObjectClassDescriptions();
        }
        else
        {
            return null;
        }
    }

}
