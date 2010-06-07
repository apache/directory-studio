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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * The input for the search result editor.
 * 
 * There is a trick to provide a single instance of the search result editor:
 * <ul>
 * <li>If oneInstanceHackEnabled is true the equals method returns always 
 *     true as long as the compared object is also of type SearchResultEditorInput. 
 *     With this trick only one instance of the search result editor is opened
 *     by the eclipse editor framework.
 * <li>If oneInstanceHackEnabled is false the equals method returns 
 *     true only if the wrapped objects (ISearch) are equal. This is 
 *     necessary for the history navigation because it must be able 
 *     to distinguish the different input objects.
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditorInput implements IEditorInput
{

    /** The search input */
    private ISearch search;

    /** Flag indicating this is a dummy input */
    private boolean dummy;


    /**
     * Creates a new instance of SearchResultEditorInput.
     *
     * @param search the search input
     */
    public SearchResultEditorInput( ISearch search )
    {
        this( search, false );
    }


    /**
     * Creates a new instance of SearchResultEditorInput.
     * 
     * @param search the search input
     * @param dummy the is dummy flag
     */
    /*package*/SearchResultEditorInput( ISearch search, boolean dummy )
    {
        this.search = search;
        this.dummy = dummy;
    }


    /**
     * This implementation always return false because
     * a search should not be visible in the 
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
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_BROWSER_SEARCHRESULTEDITOR );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        if ( search != null )
        {
            return search.getName();
        }

        return Messages.getString( "SearchResultEditorContentProvider.NoSearchSelected" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        if ( search != null )
        {
            String toolTipText = search.getUrl().toString();

            IBrowserConnection browserConnection = search.getBrowserConnection();
            if ( browserConnection != null && browserConnection.getConnection() != null )
            {
                toolTipText += " - " + browserConnection.getConnection().getName();//$NON-NLS-1$
            }
            return toolTipText;
        }

        return Messages.getString( "SearchResultEditorContentProvider.NoSearchSelected" ); //$NON-NLS-1$
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
     * Gets the search input, may be null.
     *
     * @return the search input or null
     */
    public ISearch getSearch()
    {
        return search;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        if ( dummy )
        {
            return 0;
        }

        return getToolTipText().hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( !( obj instanceof SearchResultEditorInput ) )
        {
            return false;
        }

        SearchResultEditorInput other = ( SearchResultEditorInput ) obj;

        if ( dummy && other.dummy )
        {
            return true;
        }
        if ( dummy != other.dummy )
        {
            return false;
        }

        if ( this.search == null && other.search == null )
        {
            return true;
        }
        else if ( this.search == null || other.search == null )
        {
            return false;
        }
        else
        {
            return other.search.equals( this.search );
        }
    }

}
