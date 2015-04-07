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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.apache.directory.studio.openldap.config.editor.dialogs.overlays.AccessLogOverlayConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.overlays.AuditLogOverlayConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.overlays.MemberOfOverlayConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.overlays.PasswordPolicyOverlayConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.overlays.ReferentialIntegrityOverlayConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.overlays.RewriteRemapOverlayConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.overlays.SyncProvOverlayConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.overlays.ValueSortingOverlayConfigurationBlock;
import org.apache.directory.studio.openldap.config.model.OlcOverlayConfig;
import org.apache.directory.studio.openldap.config.model.overlay.OlcAccessLogConfig;
import org.apache.directory.studio.openldap.config.model.overlay.OlcAuditlogConfig;
import org.apache.directory.studio.openldap.config.model.overlay.OlcMemberOf;
import org.apache.directory.studio.openldap.config.model.overlay.OlcPPolicyConfig;
import org.apache.directory.studio.openldap.config.model.overlay.OlcRefintConfig;
import org.apache.directory.studio.openldap.config.model.overlay.OlcRwmConfig;
import org.apache.directory.studio.openldap.config.model.overlay.OlcSyncProvConfig;
import org.apache.directory.studio.openldap.config.model.overlay.OlcValSortConfig;


/**
 * The OverlayDialog is used to edit the configuration of an overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OverlayDialog extends Dialog
{
    /** The instance */
    private OverlayDialog instance;

    /** The flag to allow the overlay type selection */
    private boolean allowOverlayTypeSelection;

    /** The overlay configuration */
    private OlcOverlayConfig overlay;

    /** The configuration block */
    private OverlayDialogConfigurationBlock<? extends OlcOverlayConfig> configurationBlock;

    /** The connection */
    private IBrowserConnection browserConnection;

    // UI widgets
    private Combo overlayTypeCombo;
    private ComboViewer overlayTypeComboViewer;
    private Composite configurationComposite;
    private Composite configurationInnerComposite;

    // Listeners
    private ISelectionChangedListener overlayTypeComboViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            OverlayType type = ( OverlayType ) ( ( StructuredSelection ) overlayTypeComboViewer.getSelection() )
                .getFirstElement();

            switch ( type )
            {
                case AUDIT_LOG:
                    overlay = new OlcAuditlogConfig();
                    configurationBlock = new AuditLogOverlayConfigurationBlock( instance, ( OlcAuditlogConfig ) overlay );
                    break;
                case MEMBER_OF:
                    overlay = new OlcMemberOf();
                    configurationBlock = new MemberOfOverlayConfigurationBlock( instance, browserConnection,
                        ( OlcMemberOf ) overlay );
                    break;
                case PASSWORD_POLICY:
                    overlay = new OlcPPolicyConfig();
                    configurationBlock = new PasswordPolicyOverlayConfigurationBlock( instance, browserConnection,
                        ( OlcPPolicyConfig ) overlay );
                    break;
                case REFERENTIAL_INTEGRITY:
                    overlay = new OlcRefintConfig();
                    configurationBlock = new ReferentialIntegrityOverlayConfigurationBlock( instance,
                        browserConnection, ( OlcRefintConfig ) overlay );
                    break;
                case REWRITE_REMAP:
                    overlay = new OlcRwmConfig();
                    configurationBlock = new RewriteRemapOverlayConfigurationBlock( instance,
                        browserConnection, ( OlcRwmConfig ) overlay );
                    break;
                case SYNC_PROV:
                    overlay = new OlcSyncProvConfig();
                    configurationBlock = new SyncProvOverlayConfigurationBlock( instance, ( OlcSyncProvConfig ) overlay );
                    break;
                case VALUE_SORTING:
                    overlay = new OlcValSortConfig();
                    configurationBlock = new ValueSortingOverlayConfigurationBlock( instance, browserConnection,
                        ( OlcValSortConfig ) overlay );
                    break;
                case ACCESS_LOG:
                default:
                    overlay = new OlcAccessLogConfig();
                    configurationBlock = new AccessLogOverlayConfigurationBlock( instance, browserConnection,
                        ( OlcAccessLogConfig ) overlay );
                    break;
            }

            refreshOverlayContent();
            autoresizeDialog();
        }
    };


    /**
     * Creates a new instance of OverlayDialog.
     * 
     * @param parentShell the parent shell
     */
    public OverlayDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        instance = this;
    }


    /**
     * Creates a new instance of OverlayDialog.
     * 
     * @param parentShell the parent shell
     * @param allowOverlayTypeSelection the flag to allow the overlay type selection
     */
    public OverlayDialog( Shell parentShell, boolean allowOverlayTypeSelection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        instance = this;
        this.allowOverlayTypeSelection = allowOverlayTypeSelection;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( getDialogText() );
    }


    /**
     * Gets the dialog text.
     *
     * @return the dialog text
     */
    private String getDialogText()
    {
        if ( overlay != null )
        {
            return NLS.bind( "{0} Overlay Configuration", getOverlayDisplayName( overlay ) );
        }
        else
        {
            return "Overlay Configuration";
        }
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        if ( configurationBlock != null )
        {
            configurationBlock.save();
        }

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        // Checking if we need to show the overlay type selection
        if ( allowOverlayTypeSelection )
        {
            createOverlayTypeSelection( composite );

            BaseWidgetUtils.createSeparator( composite, 1 );
        }

        // Creating the configuration composites
        configurationComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 1 );
        createConfigurationInnerComposite();

        // Checking for empty overlay
        if ( overlay == null )
        {
            // Assigning a default one
            overlay = new OlcAccessLogConfig();

            // Select the correct value on the combo viewer (if required)
            if ( allowOverlayTypeSelection )
            {
                overlayTypeComboViewer.setSelection( new StructuredSelection( OverlayType.ACCESS_LOG ) );
            }
        }

        // Initializing the dialog with the overlay
        initWithOverlay();

        // Adding the listener on the combo viewer  (if required)
        if ( allowOverlayTypeSelection )
        {
            overlayTypeComboViewer.addSelectionChangedListener( overlayTypeComboViewerSelectionChangedListener );
        }

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Creates the UI widgets for the overlay type selection.
     *
     * @param parent the parent composite
     */
    private void createOverlayTypeSelection( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );
        BaseWidgetUtils.createLabel( composite, "Type:", 1 );

        overlayTypeCombo = new Combo( composite, SWT.READ_ONLY | SWT.SINGLE );
        overlayTypeCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        overlayTypeComboViewer = new ComboViewer( overlayTypeCombo );
        overlayTypeComboViewer.setContentProvider( new ArrayContentProvider() );
        overlayTypeComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof OverlayType )
                {
                    OverlayType overlayType = ( OverlayType ) element;

                    return getOverlayDisplayName( overlayType );
                }

                return super.getText( element );
            }
        } );
        OverlayType[] databaseTypes = new OverlayType[]
            {
                OverlayType.ACCESS_LOG,
                OverlayType.AUDIT_LOG,
                OverlayType.MEMBER_OF,
                OverlayType.PASSWORD_POLICY,
                OverlayType.REFERENTIAL_INTEGRITY,
                OverlayType.REWRITE_REMAP,
                OverlayType.SYNC_PROV,
                OverlayType.VALUE_SORTING
        };
        overlayTypeComboViewer.setInput( databaseTypes );
    }


    /**
     * Gets the overlay display name.
     *
     * @param overlayType the overlay type
     * @return the display name
     */
    public static String getOverlayDisplayName( OverlayType overlayType )
    {
        switch ( overlayType )
        {
            case ACCESS_LOG:
                return "Access Log";
            case AUDIT_LOG:
                return "Audit Log";
            case MEMBER_OF:
                return "Member Of";
            case PASSWORD_POLICY:
                return "Password Policy";
            case REFERENTIAL_INTEGRITY:
                return "Referential Integrity";
            case REWRITE_REMAP:
                return "Rewrite/Remap";
            case SYNC_PROV:
                return "Sync Prov (Replication)";
            case VALUE_SORTING:
                return "Value Sorting";
        }

        return "Unknown";
    }


    public static String getOverlayDisplayName( OlcOverlayConfig overlay )
    {
        return getOverlayDisplayName( getOverlayType( overlay ) );
    }


    /**
     * Gets the overlay type.
     *
     * @param overlay the overlay
     * @return the overlay type
     */
    public static OverlayType getOverlayType( OlcOverlayConfig overlay )
    {
        if ( overlay instanceof OlcAccessLogConfig )
        {
            return OverlayType.ACCESS_LOG;
        }
        else if ( overlay instanceof OlcAuditlogConfig )
        {
            return OverlayType.AUDIT_LOG;
        }
        else if ( overlay instanceof OlcMemberOf )
        {
            return OverlayType.MEMBER_OF;
        }
        else if ( overlay instanceof OlcPPolicyConfig )
        {
            return OverlayType.PASSWORD_POLICY;
        }
        else if ( overlay instanceof OlcRefintConfig )
        {
            return OverlayType.REFERENTIAL_INTEGRITY;
        }
        else if ( overlay instanceof OlcRwmConfig )
        {
            return OverlayType.REWRITE_REMAP;
        }
        else if ( overlay instanceof OlcSyncProvConfig )
        {
            return OverlayType.SYNC_PROV;
        }
        else if ( overlay instanceof OlcValSortConfig )
        {
            return OverlayType.VALUE_SORTING;
        }

        return null;
    }


    /**
     * Creates the configuration inner composite.
     */
    private void createConfigurationInnerComposite()
    {
        configurationInnerComposite = BaseWidgetUtils.createColumnContainer( configurationComposite, 1, 1 );
    }


    /**
     * Disposes the configuration inner composite.
     */
    private void disposeConfigurationInnerComposite()
    {
        if ( configurationInnerComposite != null )
        {
            configurationInnerComposite.dispose();
            configurationInnerComposite = null;
        }
    }


    /**
     * Initializes the dialog with the overlay.
     */
    private void initWithOverlay()
    {
        if ( overlay instanceof OlcAccessLogConfig )
        {
            configurationBlock = new AccessLogOverlayConfigurationBlock( this, browserConnection,
                ( OlcAccessLogConfig ) overlay );
        }
        else if ( overlay instanceof OlcAuditlogConfig )
        {
            configurationBlock = new AuditLogOverlayConfigurationBlock( this, ( OlcAuditlogConfig ) overlay );
        }
        else if ( overlay instanceof OlcMemberOf )
        {
            configurationBlock = new MemberOfOverlayConfigurationBlock( this, browserConnection,
                ( OlcMemberOf ) overlay );
        }
        else if ( overlay instanceof OlcPPolicyConfig )
        {
            configurationBlock = new PasswordPolicyOverlayConfigurationBlock( this, browserConnection,
                ( OlcPPolicyConfig ) overlay );
        }
        else if ( overlay instanceof OlcRefintConfig )
        {
            configurationBlock = new ReferentialIntegrityOverlayConfigurationBlock( this, browserConnection,
                ( OlcRefintConfig ) overlay );
        }
        else if ( overlay instanceof OlcRwmConfig )
        {
            configurationBlock = new RewriteRemapOverlayConfigurationBlock( this, browserConnection,
                ( OlcRwmConfig ) overlay );
        }
        else if ( overlay instanceof OlcSyncProvConfig )
        {
            configurationBlock = new SyncProvOverlayConfigurationBlock( this, ( OlcSyncProvConfig ) overlay );
        }
        else if ( overlay instanceof OlcValSortConfig )
        {
            configurationBlock = new ValueSortingOverlayConfigurationBlock( this, browserConnection,
                ( OlcValSortConfig ) overlay );
        }

        refreshOverlayContent();
    }


    /**
     * Gets the overlay.
     * 
     * @return the overlay
     */
    public OlcOverlayConfig getOverlay()
    {
        return overlay;
    }


    /**
     * Sets the overlay.
     * 
     * @param overlay the overlay to set
     */
    public void setOverlay( OlcOverlayConfig overlay )
    {
        this.overlay = overlay;
    }


    /**
     * Calls the pack() method on the current shell, which forces the dialog to be resized.
     */
    private void autoresizeDialog()
    {
        this.getShell().pack();
    }


    /**
     * Refreshes the overlay content.
     */
    private void refreshOverlayContent()
    {
        // Disposing existing configuration inner composite and creating a new one
        disposeConfigurationInnerComposite();
        createConfigurationInnerComposite();

        // Displaying the specific settings
        configurationBlock.createBlockContent( configurationInnerComposite );
        configurationBlock.refresh();
        configurationComposite.layout();

        // Changing the dialog title
        getShell().setText( getDialogText() );
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
     * Sets the browser connection.
     *
     * @param browserConnection the browser connection
     */
    public void setBrowserConnection( IBrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;
    }
}
