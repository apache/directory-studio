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

package org.apache.directory.ldapstudio.browser.view.views.wrappers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.model.Connection;
import org.apache.directory.ldapstudio.browser.view.ImageKeys;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.ldapstudio.dsmlv2.reponse.SearchResponse;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * EntryWrapper used to displays an entry in the TreeViewer of the Browser View
 */
public class EntryWrapper implements Comparable<EntryWrapper>, DisplayableTreeViewerElement
{
    private Object parent;

    private List<EntryWrapper> children;

    /** The real entry */
    private SearchResultEntry sre;

    /** HasChilden Flag */
    private boolean hasChildren = true;

    /** isBaseDN Flag */
    private boolean isBaseDN = false;


    /**
     * Default constructor
     * @param sre the Search Result Entry to wrap
     */
    public EntryWrapper( SearchResultEntry sre )
    {
        this.sre = sre;

        // Root DSE Special Case
        if ( sre.getObjectName().toString().equals( "" ) )
        {
            setHasChildren( false );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.view.views.wrappers.DisplayableTreeViewerElement#getDisplayName()
     */
    public String getDisplayName()
    {
        if ( isBaseDN() )
        {
            // Root DSE Special Case
            if ( sre.getObjectName().toString().equals( "" ) )
            {
                return "Root DSE";
            }

            return sre.getObjectName().toString();
        }
        else
        {
            return sre.getObjectName().getRdn().toString();
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.view.views.wrappers.DisplayableTreeViewerElement#getDisplayImage()
     */
    public Image getDisplayImage()
    {
        if ( hasChildren() )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, ImageKeys.FOLDER_ENTRY )
                .createImage();
        }
        else
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, ImageKeys.ENTRY ).createImage();
        }
    }


    /**
     * Gets the real entry
     * @return the real entry
     */
    public SearchResultEntry getEntry()
    {
        return sre;
    }


    /**
     * Gets the HasChildren Flag
     * @return
     */
    public boolean hasChildren()
    {
        return hasChildren;
    }


    /**
     * Sets the HasChildren Flag
     * @param hasChildren the value of the flag
     */
    public void setHasChildren( boolean hasChildren )
    {
        this.hasChildren = hasChildren;
    }


    /**
     * Gets the isBaseDN Flag
     * @return
     */
    public boolean isBaseDN()
    {
        return isBaseDN;
    }


    /**
     * Sets the isBaseDN Flag
     * @param isBaseDN the value of the flag
     */
    public void setIsBaseDN( boolean isBaseDN )
    {
        this.isBaseDN = isBaseDN;
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( EntryWrapper o )
    {
        EntryWrapper otherWrapper = ( EntryWrapper ) o;
        return getDisplayName().compareToIgnoreCase( otherWrapper.getDisplayName() );
    }


    public Connection getConnection()
    {
        Object parent = getParent();
        if ( parent instanceof EntryWrapper )
        {
            return ( ( EntryWrapper ) parent ).getConnection();
        }
        else if ( parent instanceof ConnectionWrapper )
        {
            return ( ( ConnectionWrapper ) parent ).getConnection();
        }
        return null;
    }


    /**
     * Get parent object in the TreeViewer Hierarchy
     * @return the parent
     */
    public Object getParent()
    {
        return parent;
    }


    /**
     * Set the parent object in the TreeViewer Hierarchy
     * @param parent the parent element
     */
    public void setParent( Object parent )
    {
        this.parent = parent;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof EntryWrapper )
        {
            EntryWrapper entryWrapper = ( EntryWrapper ) obj;

            return ( ( this.getConnection().equals( entryWrapper.getConnection() ) ) && ( this.sre.equals( entryWrapper
                .getEntry() ) ) );
        }

        return false;
    }


    public Object[] getChildren()
    {
        if ( children == null )
        {
            children = new ArrayList<EntryWrapper>();

            try
            {
                // Initialization of the DSML Engine and the DSML Response Parser
                Dsmlv2Engine engine = getDsmlv2Engine();
                Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();

                String request = "<batchRequest>" + "	<searchRequest dn=\"" + getEntry().getObjectName().getNormName()
                    + "\"" + "			scope=\"singleLevel\" derefAliases=\"neverDerefAliases\">"
                    + "		<filter><present name=\"objectclass\"></present></filter>" + "       <attributes>"
                    + "			<attribute name=\"*\"/>" + "			<attribute name=\"namingContexts\"/>"
                    + "			<attribute name=\"subSchemaSubEntry\"/>" + "			<attribute name=\"altServer\"/>"
                    + "			<attribute name=\"supportedExtension\"/>" + "			<attribute name=\"supportedControl\"/>"
                    + "			<attribute name=\"supportedSaslMechanism\"/>"
                    + "			<attribute name=\"supportedLdapVersion\"/>" + "       </attributes>" + "	</searchRequest>"
                    + "</batchRequest>";

                // Executing the request and sending the result to the Response Parser
                parser.setInput( engine.processDSML( request ) );
                parser.parse();

                LdapResponse ldapResponse = parser.getBatchResponse().getCurrentResponse();

                if ( ldapResponse instanceof ErrorResponse )
                {
                    ErrorResponse errorResponse = ( ( ErrorResponse ) ldapResponse );

                    // Displaying an error
                    MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Error !", "An error has ocurred.\n" + errorResponse.getMessage() );
                    return null;
                }
                else if ( ldapResponse instanceof SearchResponse )
                {

                    // Getting the Search Result Entry List containing our objects for the response
                    SearchResponse searchResponse = ( ( SearchResponse ) ldapResponse );
                    List<SearchResultEntry> sreList = searchResponse.getSearchResultEntryList();

                    // Adding each Search Result Entry
                    for ( int i = 0; i < sreList.size(); i++ )
                    {
                        EntryWrapper entryWrapper = new EntryWrapper( sreList.get( i ) );
                        entryWrapper.setParent( this );
                        children.add( entryWrapper );
                    }

                    // Sorting the list
                    Collections.sort( children );

                    boolean oldValue = hasChildren();

                    // Updating the HasChildren Flag of the Entry
                    setHasChildren( ( sreList.size() >= 1 ) );

                    // if the Value has changed, we update the UI to change the icon.
                    if ( oldValue != hasChildren() )
                    {
                        // Getting the Browser View
                        BrowserView browserView = ( BrowserView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage().findView( BrowserView.ID );

                        browserView.getViewer().update( this, null );
                    }
                }
            }
            catch ( Exception e )
            {
                // Displaying an error
                MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                    "An error has ocurred.\n" + e.getMessage() );
                return null;
            }
        }

        return children.toArray( new Object[0] );
    }


    /**
     * Erases the Children List
     */
    public void clearChildren()
    {
        children = null;
        hasChildren = true;
    }


    /**
     * Gets the Dsmlv2Engine
     * @return the Dsmlv2Engine
     */
    public Dsmlv2Engine getDsmlv2Engine()
    {
        Object parent = getParent();

        if ( parent instanceof EntryWrapper )
        {
            return ( ( EntryWrapper ) parent ).getDsmlv2Engine();
        }
        else if ( parent instanceof ConnectionWrapper )
        {
            return ( ( ConnectionWrapper ) parent ).getDsmlv2Engine();
        }

        return null;
    }


    /**
     * Refreshes the Entry
     * Executes a request on the server to re-fecth the attributes of the entry
     * and its children
     */
    public void refreshAttributes()
    {
        try
        {
            // Initialization of the DSML Engine and the DSML Response Parser
            Dsmlv2Engine engine = getDsmlv2Engine();
            Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();

            String request = "<batchRequest>" + "   <searchRequest dn=\"" + getEntry().getObjectName().getNormName()
                + "\"" + "         scope=\"baseObject\" derefAliases=\"neverDerefAliases\">"
                + "     <filter><present name=\"objectclass\"></present></filter>" + "       <attributes>"
                + "         <attribute name=\"*\"/>" + "            <attribute name=\"namingContexts\"/>"
                + "         <attribute name=\"subSchemaSubEntry\"/>" + "            <attribute name=\"altServer\"/>"
                + "         <attribute name=\"supportedExtension\"/>"
                + "           <attribute name=\"supportedControl\"/>"
                + "         <attribute name=\"supportedSaslMechanism\"/>"
                + "           <attribute name=\"supportedLdapVersion\"/>" + "       </attributes>"
                + "    </searchRequest>" + "</batchRequest>";

            // Executing the request and sending the result to the Response Parser
            parser.setInput( engine.processDSML( request ) );
            parser.parse();

            LdapResponse ldapResponse = parser.getBatchResponse().getCurrentResponse();

            if ( ldapResponse instanceof ErrorResponse )
            {
                ErrorResponse errorResponse = ( ( ErrorResponse ) ldapResponse );

                // Displaying an error
                MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                    "An error has ocurred.\n" + errorResponse.getMessage() );

                // Creating an empty children list (this prevents the refresh to getting a new error)
                children = new ArrayList<EntryWrapper>( 0 );

                return;
            }
            else if ( ldapResponse instanceof SearchResponse )
            {

                // Getting the Search Result Entry List containing our objects for the response
                SearchResponse searchResponse = ( ( SearchResponse ) ldapResponse );

                SearchResultEntry sre = searchResponse.getSearchResultEntryList().get( 0 );

                getEntry().setPartialAttributeList( sre.getPartialAttributeList() );
                return;
            }
        }
        catch ( Exception e )
        {
            // Displaying an error
            MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                "An error has ocurred.\n" + e.getMessage() );

            // Creating an empty children list (this prevents the refresh to getting a new error)
            children = new ArrayList<EntryWrapper>( 0 );

            return;
        }

        clearChildren();
    }
}
