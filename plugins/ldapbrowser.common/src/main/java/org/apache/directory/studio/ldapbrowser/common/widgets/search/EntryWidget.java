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


import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.Dn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.dialogs.SelectEntryDialog;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.HistoryUtils;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReadEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * The EntryWidget could be used to select an entry.
 * It is composed
 * <ul>
 * <li>a combo to manually enter an Dn or to choose one from
 *     the history
 * <li>an up button to switch to the parent's Dn
 * <li>a browse button to open a {@link SelectEntryDialog}
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryWidget extends BrowserWidget
{

    /** The Dn combo. */
    private Combo dnCombo;

    /** The up button. */
    private Button upButton;

    /** The entry browse button. */
    private Button entryBrowseButton;

    /** The connection. */
    private IBrowserConnection browserConnection;

    /** The selected Dn. */
    private Dn dn;

    /** The suffix. */
    private Dn suffix;

    /** Flag indicating if using local name for the dn */
    boolean useLocalName;


    /**
     * Creates a new instance of EntryWidget.
     */
    public EntryWidget()
    {
        this.browserConnection = null;
        this.dn = null;
    }


    /**
     * Creates a new instance of EntryWidget.
     *
     * @param browserConnection the connection
     * @param dn the initial Dn
     */
    public EntryWidget( IBrowserConnection browserConnection, Dn dn )
    {
        this( browserConnection, dn, null, false );
    }


    /**
     * Creates a new instance of EntryWidget.
     *
     * @param browserConnection the connection
     * @param dn the initial Dn
     * @param suffix the suffix
     * @param useLocalName true to use local name for the Dn
     */
    public EntryWidget( IBrowserConnection browserConnection, Dn dn, Dn suffix, boolean useLocalName )
    {
        this.browserConnection = browserConnection;
        this.dn = dn;
        this.suffix = suffix;
        this.useLocalName = useLocalName;
    }


    /**
     * Creates the widget.
     *
     * @param parent the parent
     */
    public void createWidget( final Composite parent )
    {

        // Dn combo
        Composite textAndUpComposite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );
        dnCombo = BaseWidgetUtils.createCombo( textAndUpComposite, new String[0], -1, 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        dnCombo.setLayoutData( gd );

        // Dn history
        String[] history = HistoryUtils.load( BrowserCommonConstants.DIALOGSETTING_KEY_DN_HISTORY );
        dnCombo.setItems( history );
        dnCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                try
                {
                    dn = new Dn( dnCombo.getText() );
                }
                catch ( LdapInvalidDnException e1 )
                {
                    dn = null;
                }

                internalSetEnabled();
                notifyListeners();
            }
        } );

        // Up button
        upButton = new Button( textAndUpComposite, SWT.PUSH );
        upButton.setToolTipText( Messages.getString( "EntryWidget.Parent" ) ); //$NON-NLS-1$
        upButton.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_PARENT ) );
        upButton.setEnabled( false );
        upButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( dn != null && DnUtils.getParent( dn ) != null )
                {
                    dn = DnUtils.getParent( dn );
                    dnChanged();
                    internalSetEnabled();
                    notifyListeners();
                }
            }
        } );

        // Browse button
        entryBrowseButton = BaseWidgetUtils.createButton( parent, Messages.getString( "EntryWidget.BrowseButton" ), 1 ); //$NON-NLS-1$
        entryBrowseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( browserConnection != null )
                {
                    // get root entry
                    IEntry rootEntry = browserConnection.getRootDSE();
                    if ( suffix != null && suffix.size() > 0 )
                    {
                        rootEntry = browserConnection.getEntryFromCache( suffix );
                        if ( rootEntry == null )
                        {
                            ReadEntryRunnable runnable = new ReadEntryRunnable( browserConnection, suffix );
                            RunnableContextRunner.execute( runnable, null, true );
                            rootEntry = runnable.getReadEntry();
                        }
                    }

                    // calculate initial Dn
                    Dn initialDn = dn;
                    if ( useLocalName && suffix != null && suffix.size() > 0 )
                    {
                        if ( initialDn != null && initialDn.size() > 0 )
                        {
                            initialDn = DnUtils.composeDn(initialDn, suffix );
                        }
                    }

                    // get initial entry
                    IEntry entry = rootEntry;
                    if ( initialDn != null && initialDn.size() > 0 )
                    {
                        entry = browserConnection.getEntryFromCache(initialDn);
                        if ( entry == null )
                        {
                            ReadEntryRunnable runnable = new ReadEntryRunnable( browserConnection, initialDn);
                            RunnableContextRunner.execute( runnable, null, true );
                            entry = runnable.getReadEntry();
                        }
                    }

                    // open dialog
                    SelectEntryDialog dialog = new SelectEntryDialog( parent.getShell(), Messages
                        .getString( "EntryWidget.SelectDN" ), rootEntry, entry ); //$NON-NLS-1$
                    dialog.open();
                    IEntry selectedEntry = dialog.getSelectedEntry();

                    // get selected Dn
                    if ( selectedEntry != null )
                    {
                        dn = selectedEntry.getDn();
                        if ( useLocalName && suffix != null && suffix.size() > 0 )
                        {
                            dn = DnUtils.getPrefixName( dn, suffix );
                        }
                        dnChanged();
                        internalSetEnabled();
                        notifyListeners();
                    }
                }
            }
        } );

        dnChanged();
        internalSetEnabled();
    }


    /**
     * Notifies that the Dn has been changed.
     */
    private void dnChanged()
    {
        if ( dnCombo != null && entryBrowseButton != null )
        {
            dnCombo.setText( dn != null ? dn.getName() : "" ); //$NON-NLS-1$
        }
    }


    /**
     * Sets the enabled state of the widget.
     *
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
        dnCombo.setEnabled( b );

        if ( b )
        {
            this.dnChanged();
        }

        internalSetEnabled();
    }


    /**
     * Internal set enabled.
     */
    private void internalSetEnabled()
    {
        upButton.setEnabled( dn != null && DnUtils.getParent( dn ) != null && dnCombo.isEnabled() );
        entryBrowseButton.setEnabled( browserConnection != null && dnCombo.isEnabled() );
    }


    /**
     * Saves dialog settings.
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserCommonConstants.DIALOGSETTING_KEY_DN_HISTORY, this.dnCombo.getText() );
    }


    /**
     * Gets the suffix Dn or <code>null</code> if not set.
     *
     * @return the suffix Dn or <code>null</code> if not set
     */
    public Dn getSuffix()
    {
        return suffix;
    }


    /**
     * Gets the Dn or <code>null</code> if the Dn isn't valid.
     *
     * @return the Dn or <code>null</code> if the Dn isn't valid
     */
    public Dn getDn()
    {
        return dn;
    }


    /**
     * Gets the browser connection.
     *
     * @return the browser connection
     */
    public IBrowserConnection getBrowserConnection()
    {
        return browserConnection;
    }


    /**
     * Sets the input.
     *
     * @param dn the Dn
     * @param browserConnection the connection
     */
    public void setInput( IBrowserConnection browserConnection, Dn dn )
    {
        setInput( browserConnection, dn, null, false );
    }


    /**
     * Sets the input.
     *
     * @param browserConnection the connection
     * @param dn the Dn
     * @param suffix the suffix
     * @param useLocalName true to use local name for the Dn
     */
    public void setInput( IBrowserConnection browserConnection, Dn dn, Dn suffix, boolean useLocalName )
    {
        if ( this.browserConnection != browserConnection || this.dn != dn || this.suffix != suffix )
        {
            this.browserConnection = browserConnection;
            this.dn = dn;
            this.suffix = suffix;
            this.useLocalName = useLocalName;
            dnChanged();
        }
    }

}
