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

package org.apache.directory.studio.ldapbrowser.common.widgets.search;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.DialogContentAssistant;
import org.apache.directory.studio.ldapbrowser.common.widgets.HistoryUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * The ReturningAttributesWidget could be used to enter a list of attribute types
 * return by an LDPA search. It is composed of a combo with content assist
 * and a history.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ReturningAttributesWidget extends BrowserWidget
{

    /** The returning attributes combo. */
    private Combo returningAttributesCombo;

    /** The content assist processor. */
    private ReturningAttributesContentAssistProcessor contentAssistProcessor;

    /** The connection. */
    private IBrowserConnection browserConnection;

    /** The initial returning attributes. */
    private String[] initialReturningAttributes;


    /**
     * Creates a new instance of ReturningAttributesWidget.
     * 
     * @param initialReturningAttributes the initial returning attributes
     * @param browserConnection the browser  connection
     */
    public ReturningAttributesWidget( IBrowserConnection browserConnection, String[] initialReturningAttributes )
    {
        this.browserConnection = browserConnection;
        this.initialReturningAttributes = initialReturningAttributes;
    }


    /**
     * Creates a new instance of ReturningAttributesWidget with no connection
     * and no initial returning attributes. 
     *
     */
    public ReturningAttributesWidget()
    {
        this.browserConnection = null;
        this.initialReturningAttributes = null;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {
        // Combo
        returningAttributesCombo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        returningAttributesCombo.setLayoutData( gd );

        // Content assist
        contentAssistProcessor = new ReturningAttributesContentAssistProcessor( null );
        DialogContentAssistant raca = new DialogContentAssistant();
        raca.enableAutoInsert( true );
        raca.enableAutoActivation( true );
        raca.setAutoActivationDelay( 500 );
        raca.setContentAssistProcessor( contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE );
        raca.install( returningAttributesCombo );

        // History
        String[] history = HistoryUtils.load( BrowserCommonConstants.DIALOGSETTING_KEY_RETURNING_ATTRIBUTES_HISTORY );
        for ( int i = 0; i < history.length; i++ )
        {
            history[i] = Utils.arrayToString( stringToArray( history[i] ) );
        }
        returningAttributesCombo.setItems( history );
        returningAttributesCombo.setText( Utils.arrayToString( this.initialReturningAttributes ) );

        returningAttributesCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                notifyListeners();
            }
        } );

        setBrowserConnection( browserConnection );
    }


    /**
     * Sets the browser connection.
     * 
     * @param browserConnection the browser connection
     */
    public void setBrowserConnection( IBrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;

        List<String> proposals = new ArrayList<String>();
        if ( browserConnection != null )
        {
            // add attribute types
            proposals.addAll( SchemaUtils.getNames( browserConnection.getSchema().getAttributeTypeDescriptions() ) );

            // add @<object class names>
            Collection<String> ocNames = SchemaUtils.getNames( browserConnection.getSchema()
                .getObjectClassDescriptions() );
            for ( String ocName : ocNames )
            {
                proposals.add( "@" + ocName ); //$NON-NLS-1$
            }

            proposals.add( "+" ); //$NON-NLS-1$
            proposals.add( "*" ); //$NON-NLS-1$
        }

        contentAssistProcessor.setProposals( proposals );
    }


    /**
     * Sets the initial returning attributes.
     * 
     * @param initialReturningAttributes the initial returning attributes
     */
    public void setInitialReturningAttributes( String[] initialReturningAttributes )
    {
        this.initialReturningAttributes = initialReturningAttributes;
        returningAttributesCombo.setText( Utils.arrayToString( initialReturningAttributes ) );
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        this.returningAttributesCombo.setEnabled( b );
    }


    /**
     * Gets the returning attributes.
     * 
     * @return the returning attributes
     */
    public String[] getReturningAttributes()
    {
        String s = this.returningAttributesCombo.getText();
        return stringToArray( s );
    }


    /**
     * Saves dialog settings.
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserCommonConstants.DIALOGSETTING_KEY_RETURNING_ATTRIBUTES_HISTORY, Utils
            .arrayToString( getReturningAttributes() ) );
    }


    /**
     * Sets the focus.
     */
    public void setFocus()
    {
        returningAttributesCombo.setFocus();

    }


    /**
     * Splits the given string into an array. Only the following
     * characters are kept, all other are used to split the string
     * and are truncated:
     * <li>a-z
     * <li>A-Z
     * <li>0-9
     * <li>-
     * <li>.
     * <li>;
     * <li>_
     * <li>*
     * <li>+
     * <li>@
     * 
     * @param s the string to split
     * 
     * @return the array with the splitted string, or null
     */
    public static String[] stringToArray( String s )
    {
        if ( s == null )
        {
            return null;
        }
        else
        {
            List<String> attributeList = new ArrayList<String>();

            StringBuffer temp = new StringBuffer();
            for ( int i = 0; i < s.length(); i++ )
            {
                char c = s.charAt( i );

                if ( ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) || ( c >= '0' && c <= '9' ) || c == '-'
                    || c == '.' || c == ';' || c == '_' || c == '*' || c == '+' || c == '@' )
                {
                    temp.append( c );
                }
                else
                {
                    if ( temp.length() > 0 )
                    {
                        attributeList.add( temp.toString() );
                        temp = new StringBuffer();
                    }
                }
            }
            if ( temp.length() > 0 )
            {
                attributeList.add( temp.toString() );
            }

            return ( String[] ) attributeList.toArray( new String[attributeList.size()] );
        }
    }

}
