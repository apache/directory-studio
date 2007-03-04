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


import org.apache.directory.ldapstudio.browser.core.jobs.ReadEntryJob;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.dialogs.SelectEntryDialog;
import org.apache.directory.ldapstudio.browser.ui.jobs.RunnableContextJobAdapter;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.HistoryUtils;
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
    private IConnection connection;

    /** The selected DN. */
    private DN dn;


    /**
     * Creates a new instance of EntryWidget.
     */
    public EntryWidget()
    {
        this.connection = null;
        this.dn = null;
    }


    /**
     * Creates a new instance of EntryWidget.
     *
     * @param connection the connection
     * @param dn the initial DN
     */
    public EntryWidget( IConnection connection, DN dn )
    {
        this.connection = connection;
        this.dn = dn;
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
        String[] history = HistoryUtils.load( BrowserUIConstants.DIALOGSETTING_KEY_DN_HISTORY );
        dnCombo.setItems( history );
        dnCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                if ( dnCombo.getText().length() > 0 )
                {
                    try
                    {
                        dn = new DN( dnCombo.getText() );
                    }
                    catch ( NameException e1 )
                    {
                        dn = null;
                    }
                }
                else
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
        upButton.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_PARENT ) );
        upButton.setEnabled( false );
        upButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                if ( dn != null && dn.getParentDn() != null )
                {
                    dn = dn.getParentDn();
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
                if ( connection != null )
                {
                    IEntry entry = null;
                    if ( dn != null && dn.getRdns().length > 0 )
                    {
                        entry = connection.getEntryFromCache( dn );
                        if ( entry == null )
                        {
                            ReadEntryJob job = new ReadEntryJob( connection, dn );
                            RunnableContextJobAdapter.execute( job );
                            entry = job.getReadEntry();
                        }
                    }
                    if ( entry == null && connection.getBaseDNEntries() != null
                        && connection.getBaseDNEntries().length > 0 )
                    {
                        entry = connection.getBaseDNEntries()[0];
                    }

                    if ( entry != null )
                    {
                        SelectEntryDialog dialog = new SelectEntryDialog( parent.getShell(), "Select DN", connection,
                            entry );
                        dialog.open();
                        IEntry selectedEntry = dialog.getSelectedEntry();
                        if ( selectedEntry != null )
                        {
                            dn = selectedEntry.getDn();
                            dnChanged();
                            internalSetEnabled();
                            notifyListeners();
                        }
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
            dnCombo.setText( dn != null ? dn.toString() : "" );
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
        upButton.setEnabled( dn != null && dn.getParentDn() != null && dnCombo.isEnabled() );
        entryBrowseButton.setEnabled( connection != null && dnCombo.isEnabled() );
    }


    /**
     * Saves dialog settings.
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserUIConstants.DIALOGSETTING_KEY_DN_HISTORY, this.dnCombo.getText() );
    }


    /**
     * Gets the DN.
     * 
     * @return the DN
     */
    public DN getDn()
    {
        return dn;
    }


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public IConnection getConnection()
    {
        return connection;
    }


    /**
     * Sets the input.
     * 
     * @param dn the DN
     * @param connection the connection
     */
    public void setInput( IConnection connection, DN dn )
    {
        if ( this.connection != connection || this.dn != dn )
        {
            this.connection = connection;
            this.dn = dn;
            dnChanged();
        }
    }

}
