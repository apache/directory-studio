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

package org.apache.directory.ldapstudio.browser.common.dialogs;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.common.widgets.search.FilterWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * This dialog is used to enter a LDAP filter.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterWidgetDialog extends Dialog
{

    /** The title */
    private String title;

    /** The connection, used for attribute completion. */
    private IConnection connection;

    /** The filter widget. */
    private FilterWidget filterWidget;

    /** The filter. */
    private String filter;

    /** The error message label. */
    private Label errorMessageLabel;


    /**
     * Creates a new instance of FilterWidgetDialog.
     * 
     * @param parentShell the parent shell
     * @param title the dialog's title
     * @param filter the inital filter
     * @param connection the connection, used for attribute completion
     */
    public FilterWidgetDialog( Shell parentShell, String title, String filter, IConnection connection )
    {
        super( parentShell );
        this.title = title;
        this.filter = filter;
        this.connection = connection;
        setShellStyle( SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
    }


    /**
     * Gets the filter.
     * 
     * @return the filter
     */
    public String getFilter()
    {
        return filter;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( title );
        newShell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_FILTER_EDITOR ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            filter = filterWidget.getFilter();
            filterWidget.saveDialogSettings();
        }

        // call super implementation
        super.buttonPressed( buttonId );
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        Composite inner = new Composite( composite, SWT.NONE );
        GridLayout gridLayout = new GridLayout( 2, false );
        inner.setLayout( gridLayout );
        gd = new GridData( GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        inner.setLayoutData( gd );

        filterWidget = new FilterWidget( connection, filter != null ? filter : "" );
        filterWidget.createWidget( inner );
        filterWidget.setFocus();
        filterWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        errorMessageLabel = BaseWidgetUtils.createLabel( inner, "Please enter a valid filter.", 2 );

        validate();

        return composite;
    }


    /**
     * Validates the filter.
     */
    protected void validate()
    {
        if ( getButton( IDialogConstants.OK_ID ) != null )
        {
            getButton( IDialogConstants.OK_ID ).setEnabled( filterWidget.getFilter() != null );
        }
        errorMessageLabel.setText( filterWidget.getFilter() == null ? "Please enter a valid filter." : "" );
    }

}
