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
package org.apache.directory.studio.ldapbrowser.core;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.util.Base64;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.connection.core.io.ConnectionIOException;
import org.apache.directory.studio.ldapbrowser.core.model.BookmarkParameter;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Bookmark;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.osgi.util.NLS;


/**
 * This class is used to read/write the 'connections.xml' file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserConnectionIO
{
    // XML tags
    private static final String BROWSER_CONNECTIONS_TAG = "browserConnections"; //$NON-NLS-1$

    private static final String BROWSER_CONNECTION_TAG = "browserConnection"; //$NON-NLS-1$
    private static final String ID_TAG = "id"; //$NON-NLS-1$

    private static final String SEARCHES_TAG = "searches"; //$NON-NLS-1$
    private static final String SEARCH_PARAMETER_TAG = "searchParameter"; //$NON-NLS-1$
    private static final String NAME_TAG = "name"; //$NON-NLS-1$
    private static final String SEARCH_BASE_TAG = "searchBase"; //$NON-NLS-1$
    private static final String FILTER_TAG = "filer"; //$NON-NLS-1$
    private static final String RETURNING_ATTRIBUTES_TAG = "returningAttributes"; //$NON-NLS-1$
    private static final String RETURNING_ATTRIBUTE_TAG = "returningAttribute"; //$NON-NLS-1$
    private static final String VALUE_TAG = "value"; //$NON-NLS-1$
    private static final String SCOPE_TAG = "scope"; //$NON-NLS-1$
    private static final String TIME_LIMIT_TAG = "timeLimit"; //$NON-NLS-1$
    private static final String COUNT_LIMIT_TAG = "countLimit"; //$NON-NLS-1$
    private static final String ALIASES_DEREFERENCING_METHOD_TAG = "aliasesDereferencingMethod"; //$NON-NLS-1$
    private static final String REFERRALS_HANDLING_METHOD_TAG = "referralsHandlingMethod"; //$NON-NLS-1$
    private static final String CONTROLS_TAG = "controls"; //$NON-NLS-1$
    private static final String CONTROL_TAG = "control"; //$NON-NLS-1$

    private static final String BOOKMARKS_TAG = "bookmarks"; //$NON-NLS-1$
    private static final String BOOKMARK_PARAMETER_TAG = "bookmarkParameter"; //$NON-NLS-1$
    private static final String DN_TAG = "dn"; //$NON-NLS-1$

    // Scope values
    private static final String SCOPE_OBJECT = "OBJECT"; //$NON-NLS-1$
    private static final String SCOPE_ONELEVEL = "ONELEVEL"; //$NON-NLS-1$
    private static final String SCOPE_SUBTREE = "SUBTREE"; //$NON-NLS-1$
    private static final String SCOPE_OBJECT_2 = "base"; //$NON-NLS-1$
    private static final String SCOPE_ONELEVEL_2 = "one"; //$NON-NLS-1$
    private static final String SCOPE_SUBTREE_2 = "sub"; //$NON-NLS-1$


    /**
     * Loads the browser connections using the input stream.
     *
     * @param stream
     *      the input stream
     * @param browserConnectionMap
     *      the map of browser connections
     * @throws ConnectionIOException 
     *      if an error occurs when converting the document
     */
    public static void load( InputStream stream, Map<String, IBrowserConnection> browserConnectionMap )
        throws ConnectionIOException
    {
        SAXReader saxReader = new SAXReader();
        Document document = null;

        try
        {
            document = saxReader.read( stream );
        }
        catch ( DocumentException e )
        {
            throw new ConnectionIOException( e.getMessage() );
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( BROWSER_CONNECTIONS_TAG ) )
        {
            throw new ConnectionIOException( BrowserCoreMessages.BrowserConnectionIO_TheFileDoesNotSeemToBeValid );
        }

        for ( Iterator<?> i = rootElement.elementIterator( BROWSER_CONNECTION_TAG ); i.hasNext(); )
        {
            Element browserConnectionElement = ( Element ) i.next();
            readBrowserConnection( browserConnectionElement, browserConnectionMap );
        }
    }


    /**
     * Reads a browser connection from the given Element.
     *
     * @param element
     *      the element
     * @param browserConnectionMap
     *      the map of browser connections     *      
     * @throws ConnectionIOException
     *      if an error occurs when converting values
     */
    private static void readBrowserConnection( Element element, Map<String, IBrowserConnection> browserConnectionMap )
        throws ConnectionIOException
    {
        // ID
        Attribute idAttribute = element.attribute( ID_TAG );
        if ( idAttribute != null )
        {
            String id = idAttribute.getValue();
            IBrowserConnection browserConnection = browserConnectionMap.get( id );

            if ( browserConnection != null )
            {
                Element searchesElement = element.element( SEARCHES_TAG );
                if ( searchesElement != null )
                {
                    for ( Iterator<?> i = searchesElement.elementIterator( SEARCH_PARAMETER_TAG ); i.hasNext(); )
                    {
                        Element searchParameterElement = ( Element ) i.next();
                        SearchParameter searchParameter = readSearch( searchParameterElement, browserConnection );
                        ISearch search = new Search( browserConnection, searchParameter );
                        browserConnection.getSearchManager().addSearch( search );
                    }
                }

                Element bookmarksElement = element.element( BOOKMARKS_TAG );
                if ( bookmarksElement != null )
                {
                    for ( Iterator<?> i = bookmarksElement.elementIterator( BOOKMARK_PARAMETER_TAG ); i.hasNext(); )
                    {
                        Element bookmarkParameterElement = ( Element ) i.next();
                        BookmarkParameter bookmarkParameter = readBookmark( bookmarkParameterElement, browserConnection );
                        IBookmark bookmark = new Bookmark( browserConnection, bookmarkParameter );
                        browserConnection.getBookmarkManager().addBookmark( bookmark );
                    }
                }
            }
        }
    }


    private static SearchParameter readSearch( Element searchParameterElement, IBrowserConnection browserConnection )
        throws ConnectionIOException
    {
        SearchParameter searchParameter = new SearchParameter();

        // Name
        Attribute nameAttribute = searchParameterElement.attribute( NAME_TAG );
        if ( nameAttribute != null )
        {
            searchParameter.setName( nameAttribute.getValue() );
        }

        // Search base
        Attribute searchBaseAttribute = searchParameterElement.attribute( SEARCH_BASE_TAG );
        if ( searchBaseAttribute != null )
        {
            try
            {
                searchParameter.setSearchBase( new Dn( searchBaseAttribute.getValue() ) );
            }
            catch ( LdapInvalidDnException e )
            {
                throw new ConnectionIOException( NLS.bind(
                    BrowserCoreMessages.BrowserConnectionIO_UnableToParseSearchBase,
                    new String[]
                        { searchParameter.getName(), searchBaseAttribute.getValue() } ) );
            }
        }

        // Filter
        Attribute filterAttribute = searchParameterElement.attribute( FILTER_TAG );
        if ( filterAttribute != null )
        {
            searchParameter.setFilter( filterAttribute.getValue() );
        }

        // Returning Attributes
        Element returningAttributesElement = searchParameterElement.element( RETURNING_ATTRIBUTES_TAG );
        if ( returningAttributesElement != null )
        {
            List<String> returningAttributes = new ArrayList<String>();
            for ( Iterator<?> i = returningAttributesElement.elementIterator( RETURNING_ATTRIBUTE_TAG ); i.hasNext(); )
            {
                Element returningAttributeElement = ( Element ) i.next();

                Attribute valueAttribute = returningAttributeElement.attribute( VALUE_TAG );
                if ( valueAttribute != null )
                {
                    returningAttributes.add( valueAttribute.getValue() );
                }
            }
            searchParameter.setReturningAttributes( returningAttributes
                .toArray( new String[returningAttributes.size()] ) );
        }

        // Scope
        Attribute scopeAttribute = searchParameterElement.attribute( SCOPE_TAG );
        if ( scopeAttribute != null )
        {
            try
            {
                searchParameter.setScope( convertSearchScope( scopeAttribute.getValue() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new ConnectionIOException( NLS.bind(
                    BrowserCoreMessages.BrowserConnectionIO_UnableToParseScope, new String[]
                        { searchParameter.getName(), scopeAttribute.getValue() } ) );
            }
        }

        // Time limit
        Attribute timeLimitAttribute = searchParameterElement.attribute( TIME_LIMIT_TAG );
        if ( timeLimitAttribute != null )
        {
            try
            {
                searchParameter.setTimeLimit( Integer.parseInt( timeLimitAttribute.getValue() ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ConnectionIOException( NLS.bind(
                    BrowserCoreMessages.BrowserConnectionIO_UnableToParseTimeLimit,
                    new String[]
                        { searchParameter.getName(), timeLimitAttribute.getValue() } ) );
            }
        }

        // Count limit
        Attribute countLimitAttribute = searchParameterElement.attribute( COUNT_LIMIT_TAG );
        if ( countLimitAttribute != null )
        {
            try
            {
                searchParameter.setCountLimit( Integer.parseInt( countLimitAttribute.getValue() ) );
            }
            catch ( NumberFormatException e )
            {
                throw new ConnectionIOException( NLS.bind(
                    BrowserCoreMessages.BrowserConnectionIO_UnableToParseCountLimit,
                    new String[]
                        { searchParameter.getName(), countLimitAttribute.getValue() } ) );
            }
        }

        // Alias dereferencing method
        Attribute aliasesDereferencingMethodAttribute = searchParameterElement
            .attribute( ALIASES_DEREFERENCING_METHOD_TAG );
        if ( aliasesDereferencingMethodAttribute != null )
        {
            try
            {
                searchParameter.setAliasesDereferencingMethod( Connection.AliasDereferencingMethod
                    .valueOf( aliasesDereferencingMethodAttribute.getValue() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new ConnectionIOException(
                    NLS.bind(
                        BrowserCoreMessages.BrowserConnectionIO_UnableToParseAliasesDereferencingMethod,
                        new String[]
                            { searchParameter.getName(), aliasesDereferencingMethodAttribute.getValue() } ) );
            }
        }

        // Referrals handling method
        Attribute referralsHandlingMethodAttribute = searchParameterElement.attribute( REFERRALS_HANDLING_METHOD_TAG );
        if ( referralsHandlingMethodAttribute != null )
        {
            try
            {
                searchParameter.setReferralsHandlingMethod( Connection.ReferralHandlingMethod
                    .valueOf( referralsHandlingMethodAttribute.getValue() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new ConnectionIOException(
                    NLS.bind(
                        BrowserCoreMessages.BrowserConnectionIO_UnableToParseReferralsHandlingMethod,
                        new String[]
                            { searchParameter.getName(), referralsHandlingMethodAttribute.getValue() } ) );
            }
        }

        // Controls
        Element controlsElement = searchParameterElement.element( CONTROLS_TAG );
        if ( controlsElement != null )
        {
            for ( Iterator<?> i = controlsElement.elementIterator( CONTROL_TAG ); i.hasNext(); )
            {
                Element controlElement = ( Element ) i.next();

                Attribute valueAttribute = controlElement.attribute( VALUE_TAG );
                if ( valueAttribute != null )
                {
                    byte[] bytes = Base64.decode( valueAttribute.getValue().toCharArray() );
                    ByteArrayInputStream bais = null;
                    ObjectInputStream ois = null;
                    try
                    {
                        bais = new ByteArrayInputStream( bytes );
                        ois = new ObjectInputStream( bais );
                        StudioControl control = ( StudioControl ) ois.readObject();
                        searchParameter.getControls().add( control );
                        ois.close();
                    }
                    catch ( Exception e )
                    {
                        throw new ConnectionIOException( NLS.bind(
                            BrowserCoreMessages.BrowserConnectionIO_UnableToParseControl, new String[]
                                { searchParameter.getName(), valueAttribute.getValue() } ) );
                    }
                }
            }
        }

        return searchParameter;
    }


    private static BookmarkParameter readBookmark( Element bookmarkParameterElement,
        IBrowserConnection browserConnection ) throws ConnectionIOException
    {
        BookmarkParameter bookmarkParameter = new BookmarkParameter();

        // Name
        Attribute nameAttribute = bookmarkParameterElement.attribute( NAME_TAG );
        if ( nameAttribute != null )
        {
            bookmarkParameter.setName( nameAttribute.getValue() );
        }

        // Dn
        Attribute dnAttribute = bookmarkParameterElement.attribute( DN_TAG );
        if ( dnAttribute != null )
        {
            try
            {
                bookmarkParameter.setDn( new Dn( dnAttribute.getValue() ) );
            }
            catch ( LdapInvalidDnException e )
            {
                throw new ConnectionIOException( NLS.bind( BrowserCoreMessages.BrowserConnectionIO_UnableToParseDn,
                    new String[]
                        { bookmarkParameter.getName(), dnAttribute.getValue() } ) );
            }
        }

        return bookmarkParameter;
    }


    /**
     * Saves the browser connections using the output stream.
     *
     * @param stream
     *      the OutputStream
     * @param browserConnectionMap
     *      the map of browser connections
     * @throws IOException
     *      if an I/O error occurs
     */
    public static void save( OutputStream stream, Map<String, IBrowserConnection> browserConnectionMap )
        throws IOException
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Creating the root element
        Element root = document.addElement( BROWSER_CONNECTIONS_TAG );

        if ( browserConnectionMap != null )
        {
            for ( IBrowserConnection browserConnection : browserConnectionMap.values() )
            {
                writeBrowserConnection( root, browserConnection );
            }
        }

        // Writing the file to disk
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$
        XMLWriter writer = new XMLWriter( stream, outformat );
        writer.write( document );
        writer.flush();
    }


    /**
     * Writes the given browser connection to the given parent Element.
     *
     * @param parent
     *      the parent Element
     * @param browserConnection
     *      the browser connection
     * @throws IOException 
     */
    private static void writeBrowserConnection( Element parent, IBrowserConnection browserConnection )
        throws IOException
    {
        Element browserConnectionElement = parent.addElement( BROWSER_CONNECTION_TAG );

        // ID
        browserConnectionElement.addAttribute( ID_TAG, browserConnection.getConnection().getId() );

        // Searches
        Element searchesElement = browserConnectionElement.addElement( SEARCHES_TAG );
        List<ISearch> searches = browserConnection.getSearchManager().getSearches();
        for ( ISearch search : searches )
        {
            Element searchParameterElement = searchesElement.addElement( SEARCH_PARAMETER_TAG );
            writeSearch( searchParameterElement, search.getSearchParameter() );
        }

        // Bookmarks
        Element bookmarksElement = browserConnectionElement.addElement( BOOKMARKS_TAG );
        IBookmark[] bookmarks = browserConnection.getBookmarkManager().getBookmarks();
        for ( IBookmark bookmark : bookmarks )
        {
            Element bookmarkParameterElement = bookmarksElement.addElement( BOOKMARK_PARAMETER_TAG );
            writeBookmark( bookmarkParameterElement, bookmark.getBookmarkParameter() );
        }
    }


    private static void writeSearch( Element searchParameterElement, SearchParameter searchParameter )
        throws IOException
    {
        // Name
        searchParameterElement.addAttribute( NAME_TAG, searchParameter.getName() );

        // Search base
        String searchBase = searchParameter.getSearchBase() != null ? searchParameter.getSearchBase().getName() : ""; //$NON-NLS-1$
        searchParameterElement.addAttribute( SEARCH_BASE_TAG, searchBase );

        // Filter
        searchParameterElement.addAttribute( FILTER_TAG, searchParameter.getFilter() );

        // Returning Attributes
        Element returningAttributesElement = searchParameterElement.addElement( RETURNING_ATTRIBUTES_TAG );
        for ( String ra : searchParameter.getReturningAttributes() )
        {
            Element raElement = returningAttributesElement.addElement( RETURNING_ATTRIBUTE_TAG );
            raElement.addAttribute( VALUE_TAG, ra );
        }

        // Scope
        searchParameterElement.addAttribute( SCOPE_TAG, convertSearchScope( searchParameter.getScope() ) );

        // Time limit
        searchParameterElement.addAttribute( TIME_LIMIT_TAG, "" + searchParameter.getTimeLimit() ); //$NON-NLS-1$

        // Count limit
        searchParameterElement.addAttribute( COUNT_LIMIT_TAG, "" + searchParameter.getCountLimit() ); //$NON-NLS-1$

        // Alias dereferencing method
        searchParameterElement.addAttribute( ALIASES_DEREFERENCING_METHOD_TAG, searchParameter
            .getAliasesDereferencingMethod().toString() );

        // Referrals handling method
        searchParameterElement.addAttribute( REFERRALS_HANDLING_METHOD_TAG, searchParameter
            .getReferralsHandlingMethod().toString() );

        // Controls
        Element controlsElement = searchParameterElement.addElement( CONTROLS_TAG );
        for ( StudioControl studioControl : searchParameter.getControls() )
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( studioControl );
            oos.close();
            byte[] bytes = baos.toByteArray();
            String controlsValue = new String( Base64.encode( bytes ) );

            Element controlElement = controlsElement.addElement( CONTROL_TAG );
            controlElement.addAttribute( VALUE_TAG, controlsValue );
        }
    }


    private static void writeBookmark( Element bookmarkParameterElement, BookmarkParameter bookmarkParameter )
    {
        // Name
        bookmarkParameterElement.addAttribute( NAME_TAG, bookmarkParameter.getName() );

        // Dn
        String dn = bookmarkParameter.getDn() != null ? bookmarkParameter.getDn().getName() : ""; //$NON-NLS-1$
        bookmarkParameterElement.addAttribute( DN_TAG, dn );
    }


    /**
     * Converts the given search scope to a string.
     *
     * @param scope the search scope
     * @return the converted string for the search scope
     * @see  https://issues.apache.org/jira/browse/DIRSTUDIO-771
     */
    private static String convertSearchScope( SearchScope scope )
    {
        if ( scope != null )
        {
            switch ( scope )
            {
                case OBJECT:
                    return SCOPE_OBJECT;
                case ONELEVEL:
                    return SCOPE_ONELEVEL;
                case SUBTREE:
                    return SCOPE_SUBTREE;
            }
        }

        return SCOPE_SUBTREE;
    }


    /**
     * Converts the given string to a search scope.
     * 
     * @param scope the scope string
     * @return the corresponding search scope
     * @throws IllegalArgumentException if the string could not be converted
     * @see  https://issues.apache.org/jira/browse/DIRSTUDIO-771
     */
    private static SearchScope convertSearchScope( String scope ) throws IllegalArgumentException
    {
        if ( ( SCOPE_OBJECT.equalsIgnoreCase( scope ) || SCOPE_OBJECT_2.equalsIgnoreCase( scope ) ) )
        {
            return SearchScope.OBJECT;
        }
        else if ( ( SCOPE_ONELEVEL.equalsIgnoreCase( scope ) || SCOPE_ONELEVEL_2.equalsIgnoreCase( scope ) ) )
        {
            return SearchScope.ONELEVEL;
        }
        else if ( ( SCOPE_SUBTREE.equalsIgnoreCase( scope ) || SCOPE_SUBTREE_2.equalsIgnoreCase( scope ) ) )
        {
            return SearchScope.SUBTREE;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
