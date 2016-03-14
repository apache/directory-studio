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
package org.apache.directory.studio.openldap.config.editor.dialogs.overlays;


import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.apache.directory.studio.openldap.common.ui.widgets.EntryWidget;
import org.apache.directory.studio.openldap.config.editor.dialogs.AbstractOverlayDialogConfigurationBlock;
import org.apache.directory.studio.openldap.config.editor.dialogs.OverlayDialog;
import org.apache.directory.studio.openldap.config.model.overlay.OlcPPolicyConfig;


/**
 * This class implements a block for the configuration of the Password Policy Log overlay.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyOverlayConfigurationBlock extends AbstractOverlayDialogConfigurationBlock<OlcPPolicyConfig>
{
    // UI widgets
    private EntryWidget defaultPolicyEntryWidget;
    private Button forwardUpdatesCheckbox;
    private Button hashCleartextCheckbox;
    private Button useLockoutCheckbox;


    public PasswordPolicyOverlayConfigurationBlock( OverlayDialog dialog )
    {
        super( dialog );
        setOverlay( new OlcPPolicyConfig() );
    }


    public PasswordPolicyOverlayConfigurationBlock( OverlayDialog dialog, IBrowserConnection browserConnection,
        OlcPPolicyConfig overlay )
    {
        super( dialog, browserConnection );

        if ( overlay == null )
        {
            overlay = new OlcPPolicyConfig();
        }

        setOverlay( overlay );
    }


    /**
     * {@inheritDoc}
     */
    public void createBlockContent( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // Default Policy
        BaseWidgetUtils.createLabel( composite, "Default Policy:", 1 );
        defaultPolicyEntryWidget = new EntryWidget( browserConnection, Dn.EMPTY_DN );
        defaultPolicyEntryWidget.createWidget( composite );
        defaultPolicyEntryWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );

        // Forward Updates
        forwardUpdatesCheckbox = BaseWidgetUtils.createCheckbox( composite, "Forward Updates", 3 );

        // Hash Cleartext
        hashCleartextCheckbox = BaseWidgetUtils.createCheckbox( composite, "Hash Cleartext", 3 );

        // Use Lockout
        useLockoutCheckbox = BaseWidgetUtils.createCheckbox( composite, "Use Lockout", 3 );
    }


    /**
     * {@inheritDoc}
     */
    public void refresh()
    {
        if ( overlay != null )
        {
            // Default Policy
            defaultPolicyEntryWidget.setInput( overlay.getOlcPPolicyDefault() );

            // Forward Updates
            Boolean forwardUpdates = overlay.getOlcPPolicyForwardUpdates();

            if ( forwardUpdates != null )
            {
                forwardUpdatesCheckbox.setSelection( forwardUpdates.booleanValue() );
            }
            else
            {
                forwardUpdatesCheckbox.setSelection( false );
            }

            // Hash Cleartext
            Boolean hashCleartext = overlay.getOlcPPolicyHashCleartext();

            if ( hashCleartext != null )
            {
                hashCleartextCheckbox.setSelection( hashCleartext.booleanValue() );
            }
            else
            {
                hashCleartextCheckbox.setSelection( false );
            }

            // Use Lockout
            Boolean useLockout = overlay.getOlcPPolicyUseLockout();

            if ( useLockout != null )
            {
                useLockoutCheckbox.setSelection( useLockout.booleanValue() );
            }
            else
            {
                useLockoutCheckbox.setSelection( false );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void save()
    {
        if ( overlay != null )
        {
            // Default Policy
            overlay.setOlcPPolicyDefault( defaultPolicyEntryWidget.getDn() );

            // Forward Updates
            overlay.setOlcPPolicyForwardUpdates( forwardUpdatesCheckbox.getSelection() );

            // Hash Cleartext
            overlay.setOlcPPolicyHashCleartext( hashCleartextCheckbox.getSelection() );

            // Use Lockout
            overlay.setOlcPPolicyUseLockout( useLockoutCheckbox.getSelection() );
        }

        // Saving dialog settings
        defaultPolicyEntryWidget.saveDialogSettings();
    }
}
