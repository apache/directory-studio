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

package org.apache.directory.studio.openldap.common.ui.widgets;


import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.HistoryUtils;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.dialogs.SelectEntryDialog;
import org.apache.directory.studio.ldapbrowser.common.widgets.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.search.Messages;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReadEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;


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
    /** The connection. */
    private IBrowserConnection browserConnection;

    /** The flag to show the "None" checkbox or not */
    private boolean showNoneCheckbox;

    /** The selected Dn. */
    private Dn dn;

    /** The enabled state */
    private boolean enabled = true;

    // UI widgets
    private Composite composite;
    private Button noneCheckbox;
    private Combo dnCombo;
    private Button entryBrowseButton;

    // Listeners
    private SelectionAdapter noneCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            noneCheckboxSelected( noneCheckbox.getSelection() );
            notifyListeners();
        }
    };
    private ModifyListener dnComboListener = new ModifyListener()
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
    };
    private SelectionAdapter entryBrowseButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            if ( browserConnection != null )
            {
                // get root entry
                IEntry rootEntry = browserConnection.getRootDSE();

                // get initial entry
                IEntry entry = rootEntry;
                if ( dn != null && dn.size() > 0 )
                {
                    entry = browserConnection.getEntryFromCache( dn );
                    if ( entry == null )
                    {
                        ReadEntryRunnable runnable = new ReadEntryRunnable( browserConnection, dn );
                        RunnableContextRunner.execute( runnable, null, true );
                        entry = runnable.getReadEntry();
                    }
                }

                // open dialog
                SelectEntryDialog dialog = new SelectEntryDialog( entryBrowseButton.getShell(), Messages
                    .getString( "EntryWidget.SelectDN" ), rootEntry, entry ); //$NON-NLS-1$
                dialog.open();
                IEntry selectedEntry = dialog.getSelectedEntry();

                // get selected Dn
                if ( selectedEntry != null )
                {
                    dn = selectedEntry.getDn();
                    dnChanged();
                    internalSetEnabled();
                    notifyListeners();
                }
            }
        }
    };


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
     */
    public EntryWidget( IBrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;
    }


    /**
     * Creates a new instance of EntryWidget.
     *
     * @param browserConnection the connection
     * @param dn the initial Dn
     */
    public EntryWidget( IBrowserConnection browserConnection, Dn dn )
    {
        this.browserConnection = browserConnection;
        this.dn = dn;
    }


    /**
     * Creates a new instance of EntryWidget.
     *
     * @param browserConnection the connection
     * @param dn the initial Dn
     * @param showNoneButton the flag to show the "None" checkbox
     */
    public EntryWidget( IBrowserConnection browserConnection, Dn dn, boolean showNoneCheckbox )
    {
        this.browserConnection = browserConnection;
        this.dn = dn;
        this.showNoneCheckbox = showNoneCheckbox;
    }


    /**
     * Creates the widget.
     *
     * @param parent the parent
     */
    public void createWidget( Composite parent )
    {
        createWidget( parent, null );
    }


    /**
     * Creates the widget.
     *
     * @param parent the parent
     * @param toolkit the toolkit
     */
    public void createWidget( Composite parent, FormToolkit toolkit )
    {
        // Composite
        if ( toolkit != null )
        {
            composite = toolkit.createComposite( parent );
        }
        else
        {
            composite = new Composite( parent, SWT.NONE );
        }
        GridLayout compositeGridLayout = new GridLayout( getNumberOfColumnsForComposite(), false );
        compositeGridLayout.marginHeight = compositeGridLayout.marginWidth = 0;
        compositeGridLayout.verticalSpacing = 0;
        composite.setLayout( compositeGridLayout );

        // None Checbox
        if ( showNoneCheckbox )
        {
            if ( toolkit != null )
            {
                noneCheckbox = toolkit.createButton( composite, "None", SWT.CHECK );
            }
            else
            {
                noneCheckbox = BaseWidgetUtils.createCheckbox( composite, "None", 1 );
            }
        }

        // Dn combo
        dnCombo = BaseWidgetUtils.createCombo( composite, new String[0], -1, 1 );
        if ( toolkit != null )
        {
            toolkit.adapt( dnCombo );
        }
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 50;
        dnCombo.setLayoutData( gd );

        // Dn history
        String[] history = HistoryUtils.load( BrowserCommonActivator.getDefault().getDialogSettings(),
            BrowserCommonConstants.DIALOGSETTING_KEY_DN_HISTORY );
        dnCombo.setItems( history );

        // Browse button
        if ( toolkit != null )
        {
            entryBrowseButton = toolkit.createButton( composite,
                Messages.getString( "EntryWidget.BrowseButton" ), SWT.PUSH ); //$NON-NLS-1$
        }
        else
        {
            entryBrowseButton = BaseWidgetUtils.createButton( composite,
                Messages.getString( "EntryWidget.BrowseButton" ), 1 ); //$NON-NLS-1$

        }

        dnChanged();
        internalSetEnabled();
        addListeners();
    }


    /**
     * Adds the listeners
     */
    private void addListeners()
    {
        if ( showNoneCheckbox )
        {
            noneCheckbox.addSelectionListener( noneCheckboxListener );
        }

        dnCombo.addModifyListener( dnComboListener );
        entryBrowseButton.addSelectionListener( entryBrowseButtonListener );
    }


    /**
     * Removes the listeners
     */
    private void removeListeners()
    {
        if ( showNoneCheckbox )
        {
            noneCheckbox.removeSelectionListener( noneCheckboxListener );
        }

        dnCombo.removeModifyListener( dnComboListener );
        entryBrowseButton.removeSelectionListener( entryBrowseButtonListener );
    }


    /**
     * Gets the number of columns for the composite.
     *
     * @return the number of columns for the composite
     */
    private int getNumberOfColumnsForComposite()
    {
        if ( showNoneCheckbox )
        {
            return 3;
        }
        else
        {
            return 2;
        }
    }


    /**
     * Notifies that the Dn has been changed.
     */
    private void dnChanged()
    {
        if ( dnCombo != null && entryBrowseButton != null )
        {
            if ( showNoneCheckbox )
            {
                boolean noneSelected = ( dn == null );
                noneCheckbox.setSelection( noneSelected );
                noneCheckboxSelected( noneSelected );
            }

            dnCombo.setText( dn != null ? dn.getName() : "" ); //$NON-NLS-1$
        }
    }


    /**
     * This method is called when the "None" checkbox is clicked.
     */
    private void noneCheckboxSelected( boolean state )
    {
        dnCombo.setEnabled( !state );
        entryBrowseButton.setEnabled( !state );
    }


    /**
     * Sets the enabled state of the widget.
     *
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;

        if ( enabled )
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
        if ( showNoneCheckbox )
        {
            noneCheckbox.setEnabled( enabled );

            if ( dn == null )
            {
                dnCombo.setEnabled( false );
                entryBrowseButton.setEnabled( false );
            }
            else
            {
                dnCombo.setEnabled( enabled );
                entryBrowseButton.setEnabled( ( browserConnection != null ) && enabled );
            }
        }
        else
        {
            dnCombo.setEnabled( enabled );
            entryBrowseButton.setEnabled( ( browserConnection != null ) && enabled );
        }
    }


    /**
     * Saves dialog settings.
     */
    public void saveDialogSettings()
    {
        HistoryUtils.save( BrowserCommonActivator.getDefault().getDialogSettings(),
            BrowserCommonConstants.DIALOGSETTING_KEY_DN_HISTORY, this.dnCombo.getText() );
    }


    /**
     * Gets the Dn or <code>null</code> if the Dn isn't valid.
     *
     * @return the Dn or <code>null</code> if the Dn isn't valid
     */
    public Dn getDn()
    {
        if ( showNoneCheckbox && noneCheckbox.getSelection() )
        {
            return null;
        }

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
     */
    public void setInput( Dn dn )
    {
        if ( this.dn != dn )
        {
            this.dn = dn;
            removeListeners();
            dnChanged();
            addListeners();
        }
    }


    /**
     * Returns the primary control associated with this widget.
     *
     * @return the primary control associated with this widget.
     */
    public Control getControl()
    {
        return composite;
    }
}
