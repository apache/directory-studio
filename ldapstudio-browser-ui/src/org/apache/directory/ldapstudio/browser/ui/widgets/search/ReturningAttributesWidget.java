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

package org.apache.directory.ldapstudio.browser.ui.widgets.search;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.DialogContentAssistant;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.HistoryUtils;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


public class ReturningAttributesWidget extends BrowserWidget
{

    private Combo returningAttributesCombo;

    private ReturningAttributesContentAssistProcessor contentAssistProcessor;

    private IConnection connection;

    private String[] initialReturningAttributes;


    public ReturningAttributesWidget( IConnection connection, String[] initialReturningAttributes )
    {
        this.connection = connection;
        this.initialReturningAttributes = initialReturningAttributes;
    }


    public ReturningAttributesWidget()
    {
        this.connection = null;
        this.initialReturningAttributes = null;
    }


    public void createWidget( Composite parent )
    {

        returningAttributesCombo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );

        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        returningAttributesCombo.setLayoutData( gd );

        contentAssistProcessor = new ReturningAttributesContentAssistProcessor( new String[0] );
        DialogContentAssistant raca = new DialogContentAssistant();
        raca.enableAutoInsert( true );
        raca.enableAutoActivation( true );
        raca.setAutoActivationDelay( 500 );
        raca.setContentAssistProcessor( contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE );
        raca.install( returningAttributesCombo );

        String[] history = HistoryUtils.load( BrowserUIConstants.DIALOGSETTING_KEY_RETURNING_ATTRIBUTES_HISTORY );
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

        this.setConnection( this.connection );
    }


    public void setConnection( IConnection connection )
    {
        this.connection = connection;
        contentAssistProcessor.setPossibleAttributeTypes( connection == null ? new String[0] : connection.getSchema()
            .getAttributeTypeDescriptionNames() );
    }


    public void setInitialReturningAttributes( String[] initialReturningAttributes )
    {
        this.initialReturningAttributes = initialReturningAttributes;
        returningAttributesCombo.setText( Utils.arrayToString( this.initialReturningAttributes ) );
    }


    public void setEnabled( boolean b )
    {
        this.returningAttributesCombo.setEnabled( b );
    }


    public String[] getReturningAttributes()
    {
        String s = this.returningAttributesCombo.getText();
        return Utils.stringToArray( s );
    }


    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserUIConstants.DIALOGSETTING_KEY_RETURNING_ATTRIBUTES_HISTORY, Utils
            .arrayToString( getReturningAttributes() ) );
    }


    public void setFocus()
    {
        returningAttributesCombo.setFocus();

    }

}
