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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.widgets.ExtendedContentAssistCommandAdapter;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.dialogs.FilterDialog;
import org.apache.directory.studio.ldapbrowser.common.filtereditor.FilterContentAssistProcessor;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.HistoryUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * The FileterWidget could be used to specify an LDAP filter. 
 * It is composed of a combo with a content assist to enter 
 * a filter and a button to open a {@link FilterDialog}.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FilterWidget extends BrowserWidget
{
    /** The filter combo. */
    private Combo filterCombo;

    /** The filter content proposal adapter */
    private ExtendedContentAssistCommandAdapter filterCPA;

    /** The button to open the filter editor. */
    private Button filterEditorButton;

    /** The content assist processor. */
    private FilterContentAssistProcessor contentAssistProcessor;

    /** The connection. */
    private IBrowserConnection browserConnection;

    /** The inital filter. */
    private String initalFilter;

    /** The filter parser. */
    private LdapFilterParser parser;


    /**
     * Creates a new instance of FilterWidget.
     * 
     * @param initalFilter the initial filter
     */
    public FilterWidget( String initalFilter )
    {
        this.initalFilter = initalFilter;
    }


    /**
     * Creates a new instance of FilterWidget with no connection and
     * no initial filter.
     */
    public FilterWidget()
    {
        this.browserConnection = null;
        this.initalFilter = null;
    }


    /**
     * Creates the widget.
     * 
     * @param parent the parent
     */
    public void createWidget( final Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        composite.setLayoutData( gd );

        // filter combo with field decoration and content proposal
        filterCombo = BaseWidgetUtils.createCombo( composite, new String[0], -1, 1 );
        filterCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                notifyListeners();
            }
        } );
        filterCombo.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                // close proposal popup when paste a string

                // either with 3rd mouse button (linux)
                if ( !filterCombo.getText().equals( e.text ) && e.character == 0 && e.start == e.end )
                {
                    filterCPA.closeProposalPopup();
                }

                // or with ctrl+v / command+v
                if ( !filterCombo.getText().equals( e.text ) && e.stateMask == SWT.MOD1 && e.start == e.end )
                {
                    filterCPA.closeProposalPopup();
                }
            }
        } );
        parser = new LdapFilterParser();
        contentAssistProcessor = new FilterContentAssistProcessor( parser );
        filterCPA = new ExtendedContentAssistCommandAdapter( filterCombo, new ComboContentAdapter(),
            contentAssistProcessor, null, null, true );

        // auto edit strategy
        new FilterWidgetAutoEditStrategyAdapter( filterCombo, parser );

        // Filter editor button
        filterEditorButton = BaseWidgetUtils.createButton( parent, Messages
            .getString( "FilterWidget.FilterEditorButton" ), 1 ); //$NON-NLS-1$
        filterEditorButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( browserConnection != null )
                {
                    FilterDialog dialog = new FilterDialog( parent.getShell(), Messages
                        .getString( "FilterWidget.FilterEditor" ), filterCombo.getText(), //$NON-NLS-1$
                        browserConnection );
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
        filterCombo.setText( initalFilter == null ? "(objectClass=*)" : initalFilter ); //$NON-NLS-1$
    }


    /**
     * Gets the filter or null if the filter is invalid. 
     * 
     * @return the filter or null if the filter is invalid
     */
    public String getFilter()
    {
        if ( "".equals( filterCombo.getText() ) ) //$NON-NLS-1$
        {
            return ""; //$NON-NLS-1$
        }
        parser.parse( filterCombo.getText() );
        return parser.getModel().isValid() ? filterCombo.getText() : null;
    }


    /**
     * Sets the filter.
     * 
     * @param filter the filter
     */
    public void setFilter( String filter )
    {
        if ( filterCombo == null )
        {
            initalFilter = filter;
        }
        else
        {
            filterCombo.setText( filter );
        }
    }


    /**
     * Sets the browser connection.
     * 
     * @param browserConnection the browser connection
     */
    public void setBrowserConnection( IBrowserConnection browserConnection )
    {
        if ( this.browserConnection != browserConnection )
        {
            this.browserConnection = browserConnection;
            contentAssistProcessor.setSchema( browserConnection == null ? null : browserConnection.getSchema() );
            filterCPA.setAutoActivationCharacters( contentAssistProcessor
                .getCompletionProposalAutoActivationCharacters() );
        }
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
