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

package org.apache.directory.studio.ldapbrowser.common.widgets.browser;


import java.util.Arrays;
import java.util.Collection;

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.widgets.ExtendedContentAssistCommandAdapter;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.HistoryUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.ListContentProposalProvider;
import org.apache.directory.studio.ldapbrowser.core.jobs.SearchRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IQuickSearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.QuickSearch;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * The BrowserQuickSearchWidget implements an instant search 
 * for the browser widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserQuickSearchWidget
{

    /** The Constant VALUE_HISTORY_DIALOGSETTING_KEY. */
    public static final String VALUE_HISTORY_DIALOGSETTING_KEY = BrowserQuickSearchWidget.class.getName()
        + ".valueHistory"; //$NON-NLS-1$

    /** The Constant ATTRIBUTE_HISTORY_DIALOGSETTING_KEY. */
    public static final String ATTRIBUTE_HISTORY_DIALOGSETTING_KEY = BrowserQuickSearchWidget.class.getName()
        + ".attributeHistory"; //$NON-NLS-1$

    /** An empty string array */
    private static final String[] EMPTY = new String[0];

    /** The browser widget. */
    private BrowserWidget browserWidget;

    /** The parent, used to create the composite. */
    private Composite parent;

    /** The outer composite. */
    private Composite composite;

    /** The inner composite, it is created/destroyed when showing/hiding the quick search. */
    private Composite innerComposite;

    /** The quick search attribute combo. */
    private Combo quickSearchAttributeCombo;

    /** The quick search attribute proposal provider. */
    private ListContentProposalProvider quickSearchAttributePP;

    /** The quick search operator combo. */
    private Combo quickSearchOperatorCombo;

    /** The quick search value combo. */
    private Combo quickSearchValueCombo;

    /** The quick search value proposal provider. */
    private ListContentProposalProvider quickSearchValuePP;

    /** The quick search scope button. */
    private Button quickSearchScopeButton;

    /** The quick search run button. */
    private Button quickSearchRunButton;

    /** Listener that listens for selections of connections */
    private ISelectionChangedListener selectionListener = new ISelectionChangedListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation sets the input when another connection was selected.
         */
        public void selectionChanged( SelectionChangedEvent event )
        {
            setEnabled( getSelectedEntry() != null );
        }
    };


    /**
     * Creates a new instance of BrowserQuickSearchWidget.
     * 
     * @param browserWidget the browser widget
     */
    public BrowserQuickSearchWidget( BrowserWidget browserWidget )
    {
        this.browserWidget = browserWidget;

        if ( HistoryUtils.load( ATTRIBUTE_HISTORY_DIALOGSETTING_KEY ).length == 0 )
        {
            BrowserCommonActivator.getDefault().getDialogSettings().put( ATTRIBUTE_HISTORY_DIALOGSETTING_KEY,
                new String[]
                    { "cn", //$NON-NLS-1$
                        "sn", //$NON-NLS-1$
                        "givenName", //$NON-NLS-1$
                        "mail", //$NON-NLS-1$
                        "uid", //$NON-NLS-1$
                        "description", //$NON-NLS-1$
                        "o", //$NON-NLS-1$
                        "ou", //$NON-NLS-1$
                        "member" //$NON-NLS-1$
                    } );
        }
    }


    /**
     * Creates the outer composite.
     * 
     * @param parent the parent
     */
    public void createComposite( Composite parent )
    {
        this.parent = parent;

        composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
        GridLayout gl = new GridLayout();
        gl.marginHeight = 2;
        gl.marginWidth = 2;
        composite.setLayout( gl );
        // Setting the default width and height of the composite to 0
        GridData compositeGridData = new GridData( SWT.NONE, SWT.NONE, false, false );
        compositeGridData.heightHint = 0;
        compositeGridData.widthHint = 0;
        composite.setLayoutData( compositeGridData );

        innerComposite = null;
    }


    /**
     * Creates the inner composite with its input fields.
     */
    private void create()
    {
        this.browserWidget.getViewer().addPostSelectionChangedListener( selectionListener );

        // Reseting the layout of the composite to be displayed correctly
        GridData compositeGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        composite.setLayoutData( compositeGridData );

        innerComposite = BaseWidgetUtils.createColumnContainer( composite, 5, 1 );

        String[] attributes = HistoryUtils.load( ATTRIBUTE_HISTORY_DIALOGSETTING_KEY );
        quickSearchAttributeCombo = BaseWidgetUtils.createCombo( innerComposite, attributes, -1, 1 );
        quickSearchAttributePP = new ListContentProposalProvider( attributes );
        new ExtendedContentAssistCommandAdapter( quickSearchAttributeCombo, new ComboContentAdapter(),
            quickSearchAttributePP, null, null, true );
        quickSearchAttributeCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                quickSearchRunButton.setEnabled( !"".equals( quickSearchAttributeCombo.getText() ) );
            }
        } );
        quickSearchAttributeCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetDefaultSelected( SelectionEvent e )
            {
                performSearch();
            }
        } );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.widthHint = 50;
        quickSearchAttributeCombo.setLayoutData( gd );

        String[] operators = new String[]
            { "=", "!=", "<=", ">=", "~=" };
        quickSearchOperatorCombo = BaseWidgetUtils.createReadonlyCombo( innerComposite, operators, 0, 1 );
        GridData data = new GridData();
        quickSearchOperatorCombo.setLayoutData( data );

        String[] values = HistoryUtils.load( VALUE_HISTORY_DIALOGSETTING_KEY );
        quickSearchValueCombo = BaseWidgetUtils.createCombo( innerComposite, values, -1, 1 );
        quickSearchValuePP = new ListContentProposalProvider( values );
        new ExtendedContentAssistCommandAdapter( quickSearchValueCombo, new ComboContentAdapter(), quickSearchValuePP,
            null, null, true );
        quickSearchValueCombo.addSelectionListener( new SelectionAdapter()
        {
            public void widgetDefaultSelected( SelectionEvent e )
            {
                performSearch();
            }
        } );
        gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.widthHint = 50;
        quickSearchValueCombo.setLayoutData( gd );

        quickSearchScopeButton = new Button( innerComposite, SWT.TOGGLE );
        quickSearchScopeButton.setToolTipText( Messages.getString( "BrowserQuickSearchWidget.ScopeOneLevelToolTip" ) ); //$NON-NLS-1$
        quickSearchScopeButton.setImage( BrowserCommonActivator.getDefault().getImage(
            BrowserCommonConstants.IMG_SUBTREE ) );
        quickSearchScopeButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                String one = Messages.getString( "BrowserQuickSearchWidget.ScopeOneLevelToolTip" ); //$NON-NLS-1$
                String sub = Messages.getString( "BrowserQuickSearchWidget.ScopeSubtreeToolTip" ); //$NON-NLS-1$
                quickSearchScopeButton.setToolTipText( quickSearchScopeButton.getSelection() ? sub : one );
            }
        } );

        quickSearchRunButton = new Button( innerComposite, SWT.PUSH );
        quickSearchRunButton.setToolTipText( Messages.getString( "BrowserQuickSearchWidget.RunQuickSearch" ) ); //$NON-NLS-1$
        quickSearchRunButton.setImage( BrowserCommonActivator.getDefault().getImage(
            BrowserCommonConstants.IMG_QUICKSEARCH ) );
        quickSearchRunButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                performSearch();
            }
        } );

        setEnabled( getSelectedEntry() != null );

        composite.layout( true, true );
        parent.layout( true, true );
    }


    private void performSearch()
    {
        if ( !quickSearchRunButton.isEnabled() )
        {
            return;
        }

        IEntry entry = getSelectedEntry();
        if ( entry == null )
        {
            return;
        }

        HistoryUtils.save( ATTRIBUTE_HISTORY_DIALOGSETTING_KEY, quickSearchAttributeCombo.getText() );
        String[] attributes = HistoryUtils.load( ATTRIBUTE_HISTORY_DIALOGSETTING_KEY );
        quickSearchAttributeCombo.setItems( attributes );
        quickSearchAttributeCombo.select( 0 );
        HistoryUtils.save( VALUE_HISTORY_DIALOGSETTING_KEY, quickSearchValueCombo.getText() );
        String[] values = HistoryUtils.load( VALUE_HISTORY_DIALOGSETTING_KEY );
        quickSearchValueCombo.setItems( values );
        quickSearchValueCombo.select( 0 );
        quickSearchValuePP.setProposals( Arrays.asList( values ) );

        IBrowserConnection conn = entry.getBrowserConnection();

        QuickSearch quickSearch = new QuickSearch( entry, conn );
        quickSearch.getSearchParameter().setScope( quickSearchScopeButton.getSelection() ? SearchScope.SUBTREE : SearchScope.ONELEVEL );

        StringBuffer filter = new StringBuffer();
        filter.append( "(" );
        if ( "!=".equals( quickSearchOperatorCombo.getText() ) )
        {
            filter.append( "!(" );
        }
        filter.append( quickSearchAttributeCombo.getText() );
        filter.append( "!=".equals( quickSearchOperatorCombo.getText() ) ? "=" : quickSearchOperatorCombo.getText() );

        // only escape '\', '(', ')', and '\u0000'
        // don't escape '*' to allow substring search
        String value = quickSearchValueCombo.getText();
        value = value.replaceAll( "\\\\", "\\\\5c" );
        value = value.replaceAll( "\u0000", "\\\\00" );
        value = value.replaceAll( "\\(", "\\\\28" );
        value = value.replaceAll( "\\)", "\\\\29" );
        filter.append( value );
        if ( "!=".equals( quickSearchOperatorCombo.getText() ) )
        {
            filter.append( ")" );
        }
        filter.append( ")" );
        quickSearch.getSearchParameter().setFilter( filter.toString() );

        // set new quick search
        conn.getSearchManager().setQuickSearch( quickSearch );

        // execute quick search
        new StudioBrowserJob( new SearchRunnable( new ISearch[]
            { quickSearch } ) ).execute();
    }


    private IEntry getSelectedEntry()
    {
        ISelection selection = browserWidget.getViewer().getSelection();
        IEntry[] entries = BrowserSelectionUtils.getEntries( selection );
        ISearch[] searches = BrowserSelectionUtils.getSearches( selection );
        if ( entries != null && entries.length == 1 )
        {
            IEntry entry = entries[0];
            return entry;
        }
        else if ( searches != null && searches.length == 1 && ( searches[0] instanceof IQuickSearch ) )
        {
            IQuickSearch quickSearch = ( IQuickSearch ) searches[0];
            IEntry entry = quickSearch.getSearchBaseEntry();
            return entry;
        }
        else
        {
            return null;
        }
    }


    /**
     * Destroys the inner widget.
     */
    private void destroy()
    {
        browserWidget.getViewer().removePostSelectionChangedListener( selectionListener );

        // Reseting the layout of the composite with a width and height set to 0
        GridData compositeGridData = new GridData( SWT.NONE, SWT.NONE, false, false );
        compositeGridData.heightHint = 0;
        compositeGridData.widthHint = 0;
        composite.setLayoutData( compositeGridData );

        innerComposite.dispose();
        innerComposite = null;

        composite.layout( true, true );
        parent.layout( true, true );
    }


    /**
     * Disposes this widget.
     */
    public void dispose()
    {
        if ( browserWidget != null )
        {
            quickSearchAttributeCombo = null;
            quickSearchOperatorCombo = null;
            quickSearchValueCombo = null;
            quickSearchRunButton = null;
            innerComposite = null;
            composite.dispose();
            composite = null;
            parent = null;
            browserWidget = null;
        }
    }


    /**
     * Enables or disables this quick search widget.
     * 
     * @param enabled true to enable this quick search widget, false to disable it
     */
    private void setEnabled( boolean enabled )
    {
        if ( composite != null && !composite.isDisposed() )
        {
            composite.setEnabled( enabled );
        }
        if ( innerComposite != null && !innerComposite.isDisposed() )
        {
            innerComposite.setEnabled( enabled );
            quickSearchAttributeCombo.setEnabled( enabled );
            quickSearchOperatorCombo.setEnabled( enabled );
            quickSearchValueCombo.setEnabled( enabled );
            quickSearchScopeButton.setEnabled( enabled );
            quickSearchRunButton.setEnabled( enabled && !"".equals( quickSearchAttributeCombo.getText() ) );

            if ( !enabled )
            {
                quickSearchAttributeCombo.setToolTipText( null );
                quickSearchOperatorCombo.setToolTipText( null );
                quickSearchValueCombo.setToolTipText( null );
                parent.setToolTipText( Messages.getString( "BrowserQuickSearchWidget.DisabledToolTipText" ) ); //$NON-NLS-1$
            }
            else
            {
                quickSearchAttributeCombo.setToolTipText( Messages
                    .getString( "BrowserQuickSearchWidget.SearchAttribute" ) ); //$NON-NLS-1$
                quickSearchOperatorCombo
                    .setToolTipText( Messages.getString( "BrowserQuickSearchWidget.SearchOperator" ) ); //$NON-NLS-1$
                quickSearchValueCombo.setToolTipText( Messages.getString( "BrowserQuickSearchWidget.SearchValue" ) ); //$NON-NLS-1$
                parent.setToolTipText( null );
            }
        }
    }


    /**
     * Activates or deactivates this quick search widget.
     *
     * @param visible true to create this quick search widget, false to destroy it
     */
    public void setActive( boolean visible )
    {
        if ( visible && innerComposite == null && composite != null )
        {
            create();
            Object input = browserWidget.getViewer().getInput();
            if ( input != null && input instanceof IBrowserConnection )
            {
                setInput( ( IBrowserConnection ) input );
            }
            else if ( input != null && input instanceof IEntry[] )
            {
                setInput( ( ( IEntry[] ) input )[0].getBrowserConnection() );
            }
            quickSearchAttributeCombo.setFocus();
        }
        else if ( !visible && innerComposite != null && composite != null )
        {
            destroy();
            browserWidget.getViewer().getTree().setFocus();
        }
    }


    /**
     * Sets the input.
     * 
     * @param connection the new input
     */
    public void setInput( IBrowserConnection connection )
    {
        if ( innerComposite != null && !innerComposite.isDisposed() )
        {
            String[] atdNames;
            if ( connection != null )
            {
                Collection<AttributeType> atds = connection.getSchema().getAttributeTypeDescriptions();
                atdNames = SchemaUtils.getNames( atds ).toArray( EMPTY );
            }
            else
            {
                atdNames = EMPTY;
                setEnabled( false );
            }
            quickSearchAttributePP.setProposals( Arrays.asList( atdNames ) );
        }
    }

}
