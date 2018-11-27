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
import org.apache.directory.studio.openldap.config.model.OpenLdapConfigFormat;
import org.apache.directory.studio.openldap.config.model.OpenLdapVersion;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * A Dialog opened when a new OpenLDAP configuration is created.
 * I asks for the OpenLDAP version and file format (slapd.d or slapd.conf).
 * 
 * Here is what teh dialog looks like :
 * 
 * <pre>
 * .--------------------------------.
 * | o o o                          |
 * +--------------------------------+
 * | OpenLDAP version    [2.4.45|v] |
 * |  File Format                   |
 * | .----------------------------. |
 * | | ( ) Static (slapd.conf)    | |
 * | | (o) Dynamic (slapd.d)      | |
 * | `----------------------------' |
 * |                                |
 * |    (Cancel)            (Ok)    |
 * `--------------------------------'
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapConfigDialog extends Dialog
{
    /** The version combo. */
    private Combo versionCombo;
    
    /** The selected version */
    private OpenLdapVersion openLdapVersion;
    
    /** The static file format button */
    private Button staticButton;
    
    /** The file format*/
    private OpenLdapConfigFormat openLdapConfigFormat;

    /**
     * Creates a new instance of AttributeDialog.
     * 
     * @param parentShell the parent shell
     * @param currentAttribute the current attribute, null if none 
     * @param attributeNamesAndOids the possible attribute names and OIDs
     */
    public OpenLdapConfigDialog( Shell parentShell )
    {
        super( parentShell );
        
        // Default to 2.4.45 and dynamic
        openLdapVersion = OpenLdapVersion.VERSION_2_4_45;
        openLdapConfigFormat = OpenLdapConfigFormat.DYNAMIC;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString( "Configuration.Title" ) );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void createButtonsForButtonBar( Composite parent )
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite dialogComposite = ( Composite ) super.createDialogArea( parent );

        Composite composite = BaseWidgetUtils.createColumnContainer( dialogComposite, 2, 1 );
        BaseWidgetUtils.createLabel( composite, Messages.getString( "Configuration.Version" ), 1 ); //$NON-NLS-1$
        
        versionCombo = BaseWidgetUtils.createCombo( composite, OpenLdapVersion.getVersions(), -1, 1 );
        versionCombo.setText( OpenLdapVersion.VERSION_2_4_45.getValue() );
        
        // The forat group
        Group formatGroup = BaseWidgetUtils.createGroup( composite, Messages.getString( "Configuration.FileFormat" ), 2 );

        // The static format button
        staticButton = new Button( formatGroup, SWT.RADIO );
        staticButton.setText( Messages.getString( "Configuration.Static" ) );
        
        Button dynamicButton = new Button( formatGroup, SWT.RADIO );
        dynamicButton.setText( Messages.getString( "Configuration.Dynamic" ) );
        dynamicButton.setSelection( true );

        return dialogComposite;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed()
    {
        openLdapVersion = OpenLdapVersion.getVersion( versionCombo.getText() );
        
        if ( staticButton.getSelection() )
        {
            openLdapConfigFormat = OpenLdapConfigFormat.STATIC;
        }
        else
        {
            openLdapConfigFormat = OpenLdapConfigFormat.DYNAMIC;
        }
        
        super.okPressed();
    }


    /**
     * @return the openLdapVersion
     */
    public OpenLdapVersion getOpenLdapVersion()
    {
        return openLdapVersion;
    }


    /**
     * @param openLdapVersion the openLdapVersion to set
     */
    public void setOpenLdapVersion( OpenLdapVersion openLdapVersion )
    {
        this.openLdapVersion = openLdapVersion;
    }


    /**
     * @return the openLdapConfigFomat
     */
    public OpenLdapConfigFormat getOpenLdapConfigFormat()
    {
        return openLdapConfigFormat;
    }


    /**
     * @param openLdapConfigFomat the openLdapConfigFomat to set
     */
    public void setOpenLdapConfigFomat( OpenLdapConfigFormat openLdapConfigFormat )
    {
        this.openLdapConfigFormat = openLdapConfigFormat;
    }
}
