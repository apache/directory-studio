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

import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.Base64;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.io.ConnectionIOException;
import org.apache.directory.studio.ldapbrowser.core.model.BookmarkParameter;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.StudioControl;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
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


/**
 * This class is used to read/write the 'connections.xml' file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserConnectionIO
{
    // XML tags
    private static final String BROWSER_CONNECTIONS_TAG = "browserConnections";

    private static final String BROWSER_CONNECTION_TAG = "browserConnection";
    private static final String ID_TAG = "id";

    private static final String SEARCHES_TAG = "searches";
    private static final String SEARCH_PARAMETER_TAG = "searchParameter";
    private static final String NAME_TAG = "name";
    private static final String SEARCH_BASE_TAG = "searchBase";
    private static final String FILTER_TAG = "filer";
    private static final String RETURNING_ATTRIBUTES_TAG = "returningAttributes";
    private static final String RETURNING_ATTRIBUTE_TAG = "returningAttribute";
    private static final String VALUE_TAG = "value";
    private static final String SCOPE_TAG = "scope";
    private static final String TIME_LIMIT_TAG = "timeLimit";
    private static final String COUNT_LIMIT_TAG = "countLimit";
    private static final String ALIASES_DEREFERENCING_METHOD_TAG = "aliasesDereferencingMethod";
    private static final String REFERRALS_HANDLING_METHOD_TAG = "referralsHandlingMethod";
    private static final String CONTROLS_TAG = "controls";
    private static final String CONTROL_TAG = "control";

    private static final String BOOKMARKS_TAG = "bookmarks";
    private static final String BOOKMARK_PARAMETER_TAG = "bookmarkParameter";
    private static final String DN_TAG = "dn";


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
            throw new ConnectionIOException( "The file does not seem to be a valid BrowserConnections file." );
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
                searchParameter.setSearchBase( new LdapDN( searchBaseAttribute.getValue() ) );
            }
            catch ( InvalidNameException e )
            {
                throw new ConnectionIOException( "Unable to parse 'Search Base' of search '"
                    + searchParameter.getName() + "' :" + searchBaseAttribute.getValue() );
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
                searchParameter.setScope( SearchScope.valueOf( scopeAttribute.getValue() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new ConnectionIOException( "Unable to parse 'Scope' of search '" + searchParameter.getName()
                    + "' as int value. Scope value :" + scopeAttribute.getValue() );
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
                throw new ConnectionIOException( "Unable to parse 'Time limit' of search '" + searchParameter.getName()
                    + "' as int value. Time limit value :" + timeLimitAttribute.getValue() );
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
                throw new ConnectionIOException( "Unable to parse 'Count limit' of search '"
                    + searchParameter.getName() + "' as int value. Count limit value :"
                    + countLimitAttribute.getValue() );
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
                throw new ConnectionIOException( "Unable to parse 'Aliases Dereferencing Method' of search '"
                    + searchParameter.getName() + "' as int value. Aliases Dereferencing Method value :"
                    + aliasesDereferencingMethodAttribute.getValue() );
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
                throw new ConnectionIOException( "Unable to parse 'Referrals Handling Method' of search '"
                    + searchParameter.getName() + "' as int value. Referrals Handling Method value :"
                    + referralsHandlingMethodAttribute.getValue() );
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
                        throw new ConnectionIOException( "Unable to parse 'Control' of search '"
                            + searchParameter.getName() + "'. Control value :" + valueAttribute.getValue() );
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

        // DN
        Attribute dnAttribute = bookmarkParameterElement.attribute( DN_TAG );
        if ( dnAttribute != null )
        {
            try
            {
                bookmarkParameter.setDn( new LdapDN( dnAttribute.getValue() ) );
            }
            catch ( InvalidNameException e )
            {
                throw new ConnectionIOException( "Unable to parse 'DN' of bookmark '" + bookmarkParameter.getName()
                    + "' :" + dnAttribute.getValue() );
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
        outformat.setEncoding( "UTF-8" );
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
        ISearch[] searches = browserConnection.getSearchManager().getSearches();
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
        String searchBase = searchParameter.getSearchBase() != null ? searchParameter.getSearchBase().getUpName() : "";
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
        searchParameterElement.addAttribute( SCOPE_TAG, searchParameter.getScope().toString() );

        // Time limit
        searchParameterElement.addAttribute( TIME_LIMIT_TAG, "" + searchParameter.getTimeLimit() );

        // Count limit
        searchParameterElement.addAttribute( COUNT_LIMIT_TAG, "" + searchParameter.getCountLimit() );

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

        // DN
        String dn = bookmarkParameter.getDn() != null ? bookmarkParameter.getDn().getUpName() : "";
        bookmarkParameterElement.addAttribute( DN_TAG, dn );
    }

}
