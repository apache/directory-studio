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
import org.apache.directory.ldapstudio.browser.common.filtereditor.FilterSourceViewerConfiguration;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class FilterDialog extends Dialog
{

    public static final String DIALOG_TITLE = "Filter Editor";

    private String title;

    private IConnection connection;

    private SourceViewer sourceViewer;

    private FilterSourceViewerConfiguration configuration;

    private LdapFilterParser parser;

    private String filter;


    public FilterDialog( Shell parentShell, String title, String filter, IConnection connection )
    {
        super( parentShell );
        this.title = title;
        this.filter = filter;
        this.connection = connection;
        this.parser = new LdapFilterParser();
        setShellStyle( SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE );
    }


    public String getFilter()
    {
        return this.filter;
    }


    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( this.title != null ? this.title : DIALOG_TITLE );
        newShell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_FILTER_EDITOR ) );
    }


    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            // this.filter = sourceViewer.getDocument().get();
            this.parser.parse( sourceViewer.getDocument().get() );
            this.filter = this.parser.getModel().toString();
        }
        else if ( buttonId == 987654321 )
        {
            IRegion region = new Region( 0, sourceViewer.getDocument().getLength() );
            configuration.getContentFormatter( sourceViewer ).format( sourceViewer.getDocument(), region );
        }

        // call super implementation
        super.buttonPressed( buttonId );
    }


    protected Control createButtonBar( Composite parent )
    {
        Composite composite = ( Composite ) super.createButtonBar( parent );
        super.createButton( composite, 987654321, "Format", false );
        return composite;
    }


    protected Control createDialogArea( Composite parent )
    {
        // Composite composite = parent;
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        gd.heightHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        // create and configure source viewer
        sourceViewer = new SourceViewer( composite, new VerticalRuler( 0 ), SWT.H_SCROLL | SWT.V_SCROLL );
        sourceViewer.getControl().setLayoutData( new GridData( GridData.FILL_BOTH ) );
        configuration = new FilterSourceViewerConfiguration( this.sourceViewer, this.parser, this.connection );
        sourceViewer.configure( configuration );

        // set document
        IDocument document = new Document( this.filter );
        sourceViewer.setDocument( document );

        // preformat
        IRegion region = new Region( 0, sourceViewer.getDocument().getLength() );
        configuration.getContentFormatter( sourceViewer ).format( sourceViewer.getDocument(), region );

        sourceViewer.getTextWidget().setFocus();

        return composite;
    }


    protected boolean canHandleShellCloseEvent()
    {
        // proposal popup is opened, don't close dialog!
        return super.canHandleShellCloseEvent();
    }

}
