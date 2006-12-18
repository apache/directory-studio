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
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.dialogs.FilterDialog;
import org.apache.directory.ldapstudio.browser.ui.editors.filter.FilterContentAssistProcessor;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.DialogContentAssistant;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.HistoryUtils;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


public class FilterWidget extends BrowserWidget
{

    private Combo filterCombo;

    private Button filterEditorButton;

    private FilterContentAssistProcessor contentAssistProcessor;

    private IConnection connection;

    private String initalFilter;


    public FilterWidget( IConnection connection, String initalFilter )
    {
        this.connection = connection;
        this.initalFilter = initalFilter;
    }


    public FilterWidget()
    {
        this.connection = null;
        this.initalFilter = null;
    }


    public void createWidget( final Composite parent )
    {

        // Combo
        filterCombo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        filterCombo.setLayoutData( gd );

        filterCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                notifyListeners();
            }
        } );
        LdapFilterParser parser = new LdapFilterParser();
        contentAssistProcessor = new FilterContentAssistProcessor( parser );
        DialogContentAssistant fca = new DialogContentAssistant();
        fca.enableAutoInsert( true );
        fca.enableAutoActivation( true );
        fca.setAutoActivationDelay( 100 );
        fca.setContentAssistProcessor( contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE );
        fca.install( filterCombo );

        // Button
        filterEditorButton = BaseWidgetUtils.createButton( parent, "F&ilter Editor...", 1 );
        filterEditorButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( connection != null )
                {
                    FilterDialog dialog = new FilterDialog( parent.getShell(), "Filter Editor", filterCombo.getText(),
                        connection );
                    dialog.open();
                    String filter = dialog.getFilter();
                    if ( filter != null )
                    {
                        filterCombo.setText( filter );
                    }
                }
            }
        } );

        // filter history
        String[] history = HistoryUtils.load( BrowserUIConstants.DIALOGSETTING_KEY_SEARCH_FILTER_HISTORY );
        filterCombo.setItems( history );

        // initial values
        this.setConnection( this.connection );
        filterCombo.setText( initalFilter == null ? "(objectClass=*)" : initalFilter );

    }


    public String getFilter()
    {
        return this.filterCombo.getText();
    }


    public void setFilter( String filter )
    {
        this.filterCombo.setText( filter );
    }


    public void setConnection( IConnection connection )
    {
        this.connection = connection;
        contentAssistProcessor.setPossibleAttributeTypes( connection == null ? new String[0] : connection.getSchema()
            .getAttributeTypeDescriptionNames() );
    }


    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserUIConstants.DIALOGSETTING_KEY_SEARCH_FILTER_HISTORY, this.filterCombo.getText() );
    }


    public void setFocus()
    {
        // filterCombo.setFocus();
    }


    public void setEnabled( boolean b )
    {
        this.filterCombo.setEnabled( b );
        this.filterEditorButton.setEnabled( b );
    }

}
