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

package org.apache.directory.studio.ldapbrowser.common.dialogs;


import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.widgets.DnBuilderWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * A dialog to enter the new Rdn of an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameEntryDialog extends Dialog implements WidgetModifyListener
{

    /** The "Delete old Rdn" dialog setting . */
    private static final String DELETE_OLD_RDN_DIALOGSETTING_KEY = RenameEntryDialog.class.getName() + ".deleteOldRdn"; //$NON-NLS-1$

    /** The dialog title. */
    private static final String DIALOG_TITLE = Messages.getString( "RenameEntryDialog.RenameEntry" ); //$NON-NLS-1$

    /** The entry to rename. */
    private IEntry entry;

    /** The dn builder widget. */
    private DnBuilderWidget dnBuilderWidget;

    /** The ok button. */
    private Button okButton;

    /** The new rdn. */
    private Rdn rdn;


    /**
     * Creates a new instance of RenameEntryDialog.
     * 
     * @param parentShell the parent shell
     * @param entry the entry
     */
    public RenameEntryDialog( Shell parentShell, IEntry entry )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.entry = entry;
        this.rdn = null;

        if ( BrowserCommonActivator.getDefault().getDialogSettings().get( DELETE_OLD_RDN_DIALOGSETTING_KEY ) == null )
        {
            BrowserCommonActivator.getDefault().getDialogSettings().put( DELETE_OLD_RDN_DIALOGSETTING_KEY, true );
        }
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    public boolean close()
    {
        dnBuilderWidget.removeWidgetModifyListener( this );
        dnBuilderWidget.dispose();
        return super.close();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        rdn = dnBuilderWidget.getRdn();
        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    /**     
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 3 / 2;
        composite.setLayoutData( gd );

        BaseWidgetUtils.createLabel( composite, Messages.getString( "RenameEntryDialog.RenameEntryDescription" ), 1 ); //$NON-NLS-1$

        dnBuilderWidget = new DnBuilderWidget( true, false );
        dnBuilderWidget.addWidgetModifyListener( this );
        dnBuilderWidget.createContents( composite );
        Collection<AttributeType> allAtds = SchemaUtils.getAllAttributeTypeDescriptions( entry );
        String[] allAttributeNames = SchemaUtils.getNames( allAtds ).toArray( ArrayUtils.EMPTY_STRING_ARRAY );
        dnBuilderWidget.setInput( entry.getBrowserConnection(), allAttributeNames, entry.getRdn(), null );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener#widgetModified(org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent)
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        if ( okButton != null )
        {
            okButton.setEnabled( dnBuilderWidget.getRdn() != null );
        }
    }


    /**
     * Gets the rdn.
     * 
     * @return the rdn
     */
    public Rdn getRdn()
    {
        return rdn;
    }

}
