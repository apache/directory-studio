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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
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
 * @version $Rev$, $Date$
 */
public class SearchResultEditorInput implements IEditorInput
{

    /** The search input */
    private ISearch search;

    /** One instance hack flag */
    private static boolean oneInstanceHackEnabled = true;


    /**
     * Creates a new instance of SearchResultEditorInput.
     *
     * @param search the search input
     */
    public SearchResultEditorInput( ISearch search )
    {
        this.search = search;
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
        return "Search Result Editor";
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
        return getToolTipText().hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {

        boolean equal;

        if ( oneInstanceHackEnabled )
        {
            equal = ( obj instanceof SearchResultEditorInput );
        }
        else
        {
            if ( obj instanceof SearchResultEditorInput )
            {
                SearchResultEditorInput other = ( SearchResultEditorInput ) obj;
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
                    equal = other.search.equals( this.search );
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
