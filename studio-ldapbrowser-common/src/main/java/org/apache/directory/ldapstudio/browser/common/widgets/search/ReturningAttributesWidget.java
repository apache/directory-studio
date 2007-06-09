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

package org.apache.directory.ldapstudio.browser.common.widgets.search;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.BrowserWidget;
import org.apache.directory.ldapstudio.browser.common.widgets.DialogContentAssistant;
import org.apache.directory.ldapstudio.browser.common.widgets.HistoryUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
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
 * @version $Rev$, $Date$
 */
public class ReturningAttributesWidget extends BrowserWidget
{

    /** The returning attributes combo. */
    private Combo returningAttributesCombo;

    /** The content assist processor. */
    private ReturningAttributesContentAssistProcessor contentAssistProcessor;

    /** The connection. */
    private IConnection connection;

    /** The initial returning attributes. */
    private String[] initialReturningAttributes;


    /**
     * Creates a new instance of ReturningAttributesWidget.
     * 
     * @param initialReturningAttributes the initial returning attributes
     * @param connection the connection
     */
    public ReturningAttributesWidget( IConnection connection, String[] initialReturningAttributes )
    {
        this.connection = connection;
        this.initialReturningAttributes = initialReturningAttributes;
    }


    /**
     * Creates a new instance of ReturningAttributesWidget with no connection
     * and no initial returning attributes. 
     *
     */
    public ReturningAttributesWidget()
    {
        this.connection = null;
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
        contentAssistProcessor = new ReturningAttributesContentAssistProcessor( new String[0] );
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
            history[i] = Utils.arrayToString( Utils.stringToArray( history[i] ) );
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

        setConnection( connection );
    }


    /**
     * Sets the connection.
     * 
     * @param connection the connection
     */
    public void setConnection( IConnection connection )
    {
        this.connection = connection;
        contentAssistProcessor.setPossibleAttributeTypes( connection == null ? new String[0] : connection.getSchema()
            .getAttributeTypeDescriptionNames() );
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
        return Utils.stringToArray( s );
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

}
