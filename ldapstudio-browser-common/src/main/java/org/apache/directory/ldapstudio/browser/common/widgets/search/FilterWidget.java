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
import org.apache.directory.ldapstudio.browser.common.dialogs.FilterDialog;
import org.apache.directory.ldapstudio.browser.common.filtereditor.FilterContentAssistProcessor;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.BrowserWidget;
import org.apache.directory.ldapstudio.browser.common.widgets.DialogContentAssistant;
import org.apache.directory.ldapstudio.browser.common.widgets.HistoryUtils;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * The FileterWidget could be used to specify an LDAP filter. 
 * It is composed of a text with a content assit to enter 
 * a filter and a button to open a {@link FilterDialog}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterWidget extends BrowserWidget
{

    /** The filter combo. */
    private Combo filterCombo;

    /** The button to open the filter editor. */
    private Button filterEditorButton;

    /** The content assist processor. */
    private FilterContentAssistProcessor contentAssistProcessor;

    /** The connection. */
    private IConnection connection;

    /** The inital filter. */
    private String initalFilter;


    /**
     * Creates a new instance of FilterWidget.
     * 
     * @param connection the connection
     * @param initalFilter the inital filter
     */
    public FilterWidget( IConnection connection, String initalFilter )
    {
        this.connection = connection;
        this.initalFilter = initalFilter;
    }


    /**
     * Creates a new instance of FilterWidget with no connection and
     * no initial filter.
     */
    public FilterWidget()
    {
        this.connection = null;
        this.initalFilter = null;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
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

        // Content assist
        LdapFilterParser parser = new LdapFilterParser();
        contentAssistProcessor = new FilterContentAssistProcessor( parser );
        DialogContentAssistant fca = new DialogContentAssistant();
        fca.enableAutoInsert( true );
        fca.enableAutoActivation( true );
        fca.setAutoActivationDelay( 100 );
        fca.setContentAssistProcessor( contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE );
        fca.install( filterCombo );

        // Filter editor button
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
        String[] history = HistoryUtils.load( BrowserCommonConstants.DIALOGSETTING_KEY_SEARCH_FILTER_HISTORY );
        filterCombo.setItems( history );

        // initial values
        setConnection( connection );
        filterCombo.setText( initalFilter == null ? "(objectClass=*)" : initalFilter );
    }


    /**
     * Gets the filter.
     * 
     * @return the filter
     */
    public String getFilter()
    {
        return filterCombo.getText();
    }


    /**
     * Sets the filter.
     * 
     * @param filter the filter
     */
    public void setFilter( String filter )
    {
        filterCombo.setText( filter );
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
     * Saves dialog settings.
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserCommonConstants.DIALOGSETTING_KEY_SEARCH_FILTER_HISTORY, filterCombo.getText() );
    }


    /**
     * Sets the focus.
     */
    public void setFocus()
    {
        // filterCombo.setFocus();
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        filterCombo.setEnabled( b );
        filterEditorButton.setEnabled( b );
    }

}
