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


import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.dialogs.SelectEntryDialog;
import org.apache.directory.studio.ldapbrowser.common.jobs.RunnableContextJobAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.HistoryUtils;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReadEntryJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.DnUtils;
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
 * <li>a combo to manually enter an DN or to choose one from
 *     the history
 * <li>an up button to switch to the parent's DN
 * <li>a browse button to open a {@link SelectEntryDialog}
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryWidget extends BrowserWidget
{

    /** The DN combo. */
    private Combo dnCombo;

    /** The up button. */
    private Button upButton;

    /** The entry browse button. */
    private Button entryBrowseButton;

    /** The connection. */
    private IBrowserConnection browserConnection;

    /** The selected DN. */
    private LdapDN dn;

    /** The suffix. */
    private LdapDN suffix;


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
     * @param dn the initial DN
     */
    public EntryWidget( IBrowserConnection browserConnection, LdapDN dn )
    {
        this( browserConnection, dn, null );
    }


    /**
     * Creates a new instance of EntryWidget.
     *
     * @param browserConnection the connection
     * @param dn the initial DN
     * @param suffix the suffix
     */
    public EntryWidget( IBrowserConnection browserConnection, LdapDN dn, LdapDN suffix )
    {
        this.browserConnection = browserConnection;
        this.dn = dn;
        this.suffix = suffix;
    }


    /**
     * Creates the widget.
     *
     * @param parent the parent
     */
    public void createWidget( final Composite parent )
    {

        // DN combo
        Composite textAndUpComposite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );
        dnCombo = BaseWidgetUtils.createCombo( textAndUpComposite, new String[0], -1, 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        dnCombo.setLayoutData( gd );

        // DN history
        String[] history = HistoryUtils.load( BrowserCommonConstants.DIALOGSETTING_KEY_DN_HISTORY );
        dnCombo.setItems( history );
        dnCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                try
                {
                    dn = new LdapDN( dnCombo.getText() );
                }
                catch ( InvalidNameException e1 )
                {
                    dn = null;
                }

                internalSetEnabled();
                notifyListeners();
            }
        } );

        // Up button
        upButton = new Button( textAndUpComposite, SWT.PUSH );
        upButton.setToolTipText( "Parent" );
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
        entryBrowseButton = BaseWidgetUtils.createButton( parent, "Br&owse...", 1 );
        entryBrowseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( browserConnection != null )
                {
                    // get root entry
                    IEntry rootEntry = browserConnection.getRootDSE();
                    if( suffix != null && suffix.size() > 0 )
                    {
                        rootEntry = browserConnection.getEntryFromCache( suffix );
                        if ( rootEntry == null )
                        {
                            ReadEntryJob job = new ReadEntryJob( browserConnection, suffix );
                            RunnableContextJobAdapter.execute( job );
                            rootEntry = job.getReadEntry();
                        }
                    }

                    // calculate initial DN
                    LdapDN initialDN = dn;
                    if( suffix != null && suffix.size() > 0 )
                    {
                        if( initialDN != null && initialDN.size() > 0 )
                        {
                            initialDN = DnUtils.composeDn( initialDN, suffix );
                        }
                    }

                    // get initial entry
                    IEntry entry = rootEntry;
                    if ( initialDN != null && initialDN.size() > 0 )
                    {
                        entry = browserConnection.getEntryFromCache( initialDN );
                        if ( entry == null )
                        {
                            ReadEntryJob job = new ReadEntryJob( browserConnection, initialDN );
                            RunnableContextJobAdapter.execute( job );
                            entry = job.getReadEntry();
                        }
                    }


                    // open dialog
                    SelectEntryDialog dialog = new SelectEntryDialog( parent.getShell(), "Select DN", rootEntry, entry );
                    dialog.open();
                    IEntry selectedEntry = dialog.getSelectedEntry();

                    // get selected DN
                    if ( selectedEntry != null )
                    {
                        dn = selectedEntry.getDn();
                        if( suffix != null && suffix.size() > 0 )
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
     * Notifies that the DN has been changed.
     */
    private void dnChanged()
    {
        if ( dnCombo != null && entryBrowseButton != null )
        {
            dnCombo.setText( dn != null ? dn.getUpName() : "" );
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
     * Gets the suffix DN or <code>null</code> if not set.
     *
     * @return the suffix DN or <code>null</code> if not set
     */
    public LdapDN getSuffix()
    {
        return suffix;
    }


    /**
     * Gets the DN or <code>null</code> if the DN isn't valid.
     *
     * @return the DN or <code>null</code> if the DN isn't valid
     */
    public LdapDN getDn()
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
     * @param dn the DN
     * @param browserConnection the connection
     */
    public void setInput( IBrowserConnection browserConnection, LdapDN dn )
    {
        setInput( browserConnection, dn, null );
    }


    /**
     * Sets the input.
     *
     * @param browserConnection the connection
     * @param dn the DN
     * @param suffix the suffix
     */
    public void setInput( IBrowserConnection browserConnection, LdapDN dn, LdapDN suffix )
    {
        if ( this.browserConnection != browserConnection || this.dn != dn || this.suffix != suffix )
        {
            this.browserConnection = browserConnection;
            this.dn = dn;
            this.suffix = suffix;
            dnChanged();
        }
    }

}
