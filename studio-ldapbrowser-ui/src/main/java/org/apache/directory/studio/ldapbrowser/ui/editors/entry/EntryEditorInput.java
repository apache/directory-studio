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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * The input for the entry editor.
 * 
 * There is a trick to provide a single instance of the entry editor:
 * <ul>
 * <li>If oneInstanceHackEnabled is true the equals method returns always 
 *     true as long as the compared object is also of type EntryEditorInput. 
 *     With this trick only one instance of the entry editor is opened
 *     by the eclipse editor framework.
 * <li>If oneInstanceHackEnabled is false the equals method returns 
 *     true only if the wrapped objects (IEntry, ISearchResult or 
 *     IBookmark) are equal. This is necessary for the history navigation
 *     because it must be able to distinguish the different 
 *     input objects.
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorInput implements IEditorInput
{

    /** The entry input */
    private IEntry entry;
    
    /** The search result input */
    private ISearchResult searchResult;
    
    /** The bookmark input */
    private IBookmark bookmark;

    /** One instance hack flag */
    private static boolean oneInstanceHackEnabled = true;


    /**
     * Creates a new instance of EntryEditorInput with an IEntry as 
     * domain object.
     *
     * @param entry the entry input
     */
    public EntryEditorInput( IEntry entry )
    {
        this.entry = entry;
        this.searchResult = null;
        this.bookmark = null;
    }


    /**
     * Creates a new instance of EntryEditorInput with an ISearchResult as 
     * domain object.
     *
     * @param searchResult the search result input
     */
    public EntryEditorInput( ISearchResult searchResult )
    {
        this.entry = null;
        this.searchResult = searchResult;
        this.bookmark = null;
    }


    /**
     * Creates a new instance of EntryEditorInput with an IBookmark as 
     * domain object.
     *
     * @param bookmark the bookmark input
     */
    public EntryEditorInput( IBookmark bookmark )
    {
        this.entry = null;
        this.searchResult = null;
        this.bookmark = bookmark;
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
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ATTRIBUTE );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "Entry Editor";
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return "";
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
     * Gets the resolved entry, either the entry input itself
     * or the entry behind the search result intput or the entry behind 
     * the bookmark input.
     * 
     * @return the resolved entry
     */
    public IEntry getResolvedEntry()
    {
        if ( entry != null )
        {
            return entry;
        }
        else if ( searchResult != null )
        {
            return searchResult.getEntry();
        }
        else if ( bookmark != null )
        {
            return bookmark.getEntry();
        }
        else
        {
            return null;
        }
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
        return getToolTipText().hashCode();
    }


    /**
     * The result depends 
     * 
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        boolean equal;

        if ( oneInstanceHackEnabled )
        {
            equal = ( obj instanceof EntryEditorInput );
        }
        else
        {
            if ( obj instanceof EntryEditorInput )
            {
                EntryEditorInput other = ( EntryEditorInput ) obj;
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
                    equal = other.getInput().equals( this.getInput() );
                }
            }
            else
            {
                equal = false;
            }
        }

        return equal;
    }


    /**
     * Enables or disabled the one instance hack.
     *
     * @param b 
     *      true to enable the one instance hack,
     *      false to disable the one instance hack
     */
    public static void enableOneInstanceHack( boolean b )
    {
        oneInstanceHackEnabled = b;
    }

}
