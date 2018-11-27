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
package org.apache.directory.studio.templateeditor.editor.widgets;


import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.apache.directory.studio.templateeditor.EntryTemplatePluginUtils;
import org.apache.directory.studio.templateeditor.model.widgets.TemplateLink;


/**
 * This class implements an editor link.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorLink extends EditorWidget<TemplateLink>
{
    /** The Regex for matching an URL */
    private static final String REGEX_URL = "([a-zA-Z][a-zA-Z0-9+-.]*:[^\\s]+)"; //$NON-NLS-1$

    /** The Regex for matching an email address*/
    private static final String REGEX_EMAIL_ADDRESS = "([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4})"; //$NON-NLS-1$

    /** The link widget */
    private Link link;


    /**
     * Creates a new instance of EditorLink.
     * 
     * @param editor
     *      the associated editor
     * @param templateLink
     *      the associated template link
     * @param toolkit
     *      the associated toolkit
     */
    public EditorLink( IEntryEditor editor, TemplateLink templateLink, FormToolkit toolkit )
    {
        super( templateLink, editor, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    public Composite createWidget( Composite parent )
    {
        // Creating and initializing the widget UI
        Composite composite = initWidget( parent );

        // Updating the widget's content
        updateWidget();

        // Adding the listeners
        addListeners();

        return composite;
    }


    /**
     * Creates and initializes the widget UI.
     *
     * @param parent
     *      the parent composite
     * @return
     *      the associated composite
     */
    private Composite initWidget( Composite parent )
    {
        // Creating the link widget
        link = new Link( parent, SWT.NONE );
        link.setLayoutData( getGridata() );

        return parent;
    }


    /**
     * Updates the widget's content.
     */
    private void updateWidget()
    {
        // Checking is we need to display a value taken from the entry
        // or use the given value
        String attributeType = getWidget().getAttributeType();
        if ( attributeType != null )
        {
            link.setText( addLinksTags( EditorWidgetUtils.getConcatenatedValues( getEntry(), attributeType ) ) );
        }
        else
        {
            link.setText( addLinksTags( getWidget().getValue() ) );
        }
    }


    /**
     * Adds the listeners.
     */
    private void addListeners()
    {
        link.addListener( SWT.Selection, new Listener()
        {
            public void handleEvent( Event event )
            {
                // Creating the URL
                String url = null;

                // Getting the text that was clicked
                String text = event.text;
                if ( isUrl( text ) )
                {
                    url = text;
                }
                else if ( isEmailAddress( text ) )
                {
                    url = "mailto:" + text; //$NON-NLS-1$
                }

                if ( url != null )
                {
                    try
                    {
                        PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL( new URL( url ) );
                    }
                    catch ( Exception e )
                    {
                        // Logging the error
                        EntryTemplatePluginUtils.logError( e, "An error occurred while opening the link.", //$NON-NLS-1$
                            new Object[0] );

                        // Launching an error dialog
                        MessageDialog
                            .openError(
                                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                                Messages.getString( "EditorLink.ErrorMessageDialogTitle" ), Messages.getString( "EditorLink.ErrorMessageDialogMessage" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                else
                {
                    // Logging the error
                    EntryTemplatePluginUtils.logError( null, "An error occurred while opening the link. URL is null.", //$NON-NLS-1$
                        new Object[0] );

                    // Launching an error dialog
                    MessageDialog
                        .openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            Messages.getString( "EditorLink.ErrorMessageDialogTitle" ), Messages.getString( "EditorLink.ErrorMessageDialogMessage" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        } );
    }


    /**
     * Adds link tags (&lt;a&gt;link&lt;/a&gt;) to the links (URLs and email 
     * addresses) found in the given string.
     *
     * @param s
     *      the string
     * @return
     *      the string in which links have been added
     */
    private static String addLinksTags( String s )
    {
        List<String> links = new ArrayList<String>();

        // Getting the URLs
        links.addAll( Arrays.asList( getUrls( s ) ) );

        // Getting the email addresses
        links.addAll( Arrays.asList( getEmailAddresses( s ) ) );

        // Creating the final string
        StringBuilder sb = new StringBuilder();
        try
        {
            // Inserting link tags
            int start = 0;
            for ( String link : links )
            {
                int indexOfLink = s.indexOf( link );
                sb.append( s.subSequence( start, indexOfLink ) );
                sb.append( "<a>" ); //$NON-NLS-1$
                sb.append( link );
                sb.append( "</a>" ); //$NON-NLS-1$

                start = indexOfLink + link.length();
            }
            sb.append( s.substring( start, s.length() ) );
        }
        catch ( StringIndexOutOfBoundsException e )
        {
            // In case we hit a wrong index, we fail gracefully by
            // returning the original string
            return s;
        }

        // Returning the final string
        return sb.toString();
    }


    /**
     * Get the urls contained in the email address.
     *
     * @param s
     *      the string
     * @return
     *      an array containing the urls found in the given string
     */
    private static String[] getUrls( String s )
    {
        return getMatchingStrings( s, Pattern.compile( REGEX_URL + ".*" ) ); //$NON-NLS-1$
    }


    /**
     * Get the email addresses contained in the email address.
     *
     * @param s
     *      the string
     * @return
     *      an array containing the email addresses found in the given string
     */
    private static String[] getEmailAddresses( String s )
    {
        return getMatchingStrings( s, Pattern.compile( REGEX_EMAIL_ADDRESS + ".*" ) ); //$NON-NLS-1$
    }


    /**
     * Get the matching strings contained in a string using a pattern.
     *
     * @param s
     *      the string
     * @param p
     *      the pattern
     * @return
     *      an array containing the matching strings found in the given string
     */
    private static String[] getMatchingStrings( String s, Pattern p )
    {
        List<String> matchingStrings = new ArrayList<String>();

        while ( s.length() > 0 )
        {
            Matcher m = p.matcher( s );

            if ( m.matches() )
            {
                String link = m.group( 1 );
                matchingStrings.add( link );
                s = s.substring( link.length() );
            }
            else
            {
                s = s.substring( 1 );
            }
        }

        return matchingStrings.toArray( new String[0] );
    }


    /**
     * Indicates if the given string is a URL.
     *
     * @param s
     *      the string
     * @return
     *      <code>true</code> if the given string is a URL,
     *      <code>false</code> if not.
     */
    private boolean isUrl( String s )
    {
        return Pattern.matches( REGEX_URL, s );
    }


    /**
     * Indicates if the given string is an email address.
     *
     * @param s
     *      the string
     * @return
     *      <code>true</code> if the given string is an email address,
     *      <code>false</code> if not.
     */
    private boolean isEmailAddress( String s )
    {
        return Pattern.matches( REGEX_EMAIL_ADDRESS, s );
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        updateWidget();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }
}