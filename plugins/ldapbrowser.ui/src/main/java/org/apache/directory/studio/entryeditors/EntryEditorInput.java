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

package org.apache.directory.studio.entryeditors;


import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.impl.RootDSE;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * The input for the entry editor.
 * 
 * This class also manages the shared reference copy and the shared working copy
 * of the original entry. Each manual-save editor <b>must</b> use these entry 
 * copies!
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorInput implements IEditorInput
{
    /** The entry input */
    private IEntry entry;

    /** The search result input */
    private ISearchResult searchResult;

    /** The bookmark input */
    private IBookmark bookmark;

    /** The entry editor extension. */
    private EntryEditorExtension extension;
    
    /** The resolved entry, based on the entry, searchResult and bookmark */
    private IEntry resolvedEntry;


    /**
     * Creates a new instance of EntryEditorInput with an IEntry as 
     * domain object.
     *
     * @param entry the entry input
     */
    public EntryEditorInput( IEntry entry, EntryEditorExtension extension )
    {
        this( entry, null, null, extension );
    }


    /**
     * Creates a new instance of EntryEditorInput with an ISearchResult as 
     * domain object.
     *
     * @param searchResult the search result input
     */
    public EntryEditorInput( ISearchResult searchResult, EntryEditorExtension extension )
    {
        this( null, searchResult, null, extension );
    }


    /**
     * Creates a new instance of EntryEditorInput with an IBookmark as 
     * domain object.
     *
     * @param bookmark the bookmark input
     */
    public EntryEditorInput( IBookmark bookmark, EntryEditorExtension extension )
    {
        this( null, null, bookmark, extension );
    }


    /**
     * The private constructor, which is called by all the public constructors.
     */
    private EntryEditorInput( IEntry entry, ISearchResult searchResult, IBookmark bookmark,
        EntryEditorExtension extension )
    {
        this.entry = entry;
        this.searchResult = searchResult;
        this.bookmark = bookmark;
        this.extension = extension;
        
        if ( entry != null )
        {
            resolvedEntry = entry;
        }
        else if ( searchResult != null )
        {
            resolvedEntry = searchResult.getEntry();
        }
        else if ( bookmark != null )
        {
            resolvedEntry = bookmark.getEntry();
        }

        if ( resolvedEntry != null )
        {
            resolvedEntry = resolvedEntry.getBrowserConnection().getEntryFromCache( resolvedEntry.getDn() );
        }
    }


    /**
     * This implementation always return false because
     * an entry should not be visible in the 
     * "File Most Recently Used" menu.
     * 
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return extension.getIcon();
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        if ( resolvedEntry != null )
        {
            if ( resolvedEntry instanceof RootDSE )
            {
                return Messages.getString( "EntryEditorNavigationLocation.RootDSE" ); //$NON-NLS-1$
            }
            else
            {
                return resolvedEntry.getDn().getName();
            }
        }

        return Messages.getString( "EntryEditorInput.NoEntrySelected" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        if ( resolvedEntry != null )
        {
            IBrowserConnection connection = resolvedEntry.getBrowserConnection();
            
            if ( ( connection != null ) && ( connection.getConnection() != null ) )
            {
                return getName() + " - " + connection.getConnection().getName();//$NON-NLS-1$
            }
            else
            {
                return getName();
            }
        }

        return Messages.getString( "EntryEditorInput.NoEntrySelected" ); //$NON-NLS-1$
    }


    /**
     * This implementation always return null.
     * 
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        return null;
    }


    /**
     * Gets the entry editor extension.
     * 
     * @return the entry editor extension
     */
    public EntryEditorExtension getExtension()
    {
        return extension;
    }


    /**
     * Gets the resolved entry, either the entry input itself
     * or the entry behind the search result input or the entry behind 
     * the bookmark input.
     * 
     * @return the resolved entry
     */
    public IEntry getResolvedEntry()
    {
        return resolvedEntry;
    }


    /**
     * Gets the shared working copy of the shared reference entry.
     * This is used by all manual-save entry editors.
     *
     * @return the shared working copy of the shared reference entry 
     */
    public IEntry getSharedWorkingCopy( IEntryEditor editor )
    {
        if ( resolvedEntry != null )
        {
            return BrowserUIPlugin.getDefault().getEntryEditorManager().getSharedWorkingCopy( resolvedEntry, editor );
        }

        return null;
    }


    /**
     * Checks if the shared working copy is dirty.
     * @param entryEditor 
     * 
     * @return true, if the shared working copy is dirty
     */
    public boolean isSharedWorkingCopyDirty( IEntryEditor editor )
    {
        boolean dirty = BrowserUIPlugin.getDefault().getEntryEditorManager().isSharedWorkingCopyDirty(
            resolvedEntry, editor );
        
        return dirty;
    }


    /**
     * Save the difference between the shared reference copy and the shared working copy
     * to the directory server.
     * 
     * @param handleError the handle error
     * @param editor the entry editor
     * 
     * @return the status or null if there was nothing to save
     */
    public IStatus saveSharedWorkingCopy( boolean handleError, IEntryEditor editor )
    {
        IStatus status = BrowserUIPlugin.getDefault().getEntryEditorManager().saveSharedWorkingCopy(
            resolvedEntry, handleError, editor );
        
        return status;
    }


    /**
     * Resets the shared reference copy and the shared working copy.
     * 
     * @param editor the entry editor
     */
    public void resetSharedWorkingCopy( IEntryEditor editor )
    {
        BrowserUIPlugin.getDefault().getEntryEditorManager().resetSharedWorkingCopy( resolvedEntry, editor );
    }


    /**
     * Gets the entry input, may be null.
     *
     * @return the entry input or null
     */
    public IEntry getEntryInput()
    {
        return entry;
    }


    /**
     * Gets the search result input, may be null.
     *
     * @return the search result input or null
     */
    public ISearchResult getSearchResultInput()
    {
        return searchResult;
    }


    /**
     * Gets the bookmark input, may be null.
     *
     * @return the bookmark input or null
     */
    public IBookmark getBookmarkInput()
    {
        return bookmark;
    }


    /**
     * Gets the input, may be null.
     *
     * @return the input or null
     */
    public Object getInput()
    {
        if ( entry != null )
        {
            return entry;
        }
        else if ( searchResult != null )
        {
            return searchResult;
        }
        else if ( bookmark != null )
        {
            return bookmark;
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        if ( extension == null )
        {
            return 0;
        }
        else if ( extension.isMultiWindow() )
        {
            if ( resolvedEntry == null )
            {
                return 0;
            }
            else
            {
                return resolvedEntry.getDn().hashCode();
            }
        }
        else
        {
            return 0;
        }
    }


    /**
     * The result depends 
     * 
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( !( obj instanceof EntryEditorInput ) )
        {
            return false;
        }

        EntryEditorInput other = ( EntryEditorInput ) obj;

        if ( extension == null && other.extension == null )
        {
            return true;
        }

        if ( this.getExtension() != other.getExtension() )
        {
            return false;
        }

        if ( this.getInput() == null && other.getInput() == null )
        {
            return true;
        }
        else if ( this.getInput() == null || other.getInput() == null )
        {
            return false;
        }
        else
        {
            return other.getInput().equals( this.getInput() );
        }
    }

    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( resolvedEntry );
        
        return sb.toString();
    }
}
