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


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.filtereditor.FilterSourceViewerConfiguration;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
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


/**
 * Dialog to edit a filter in a text source viewer with syntax highlighting
 * and content assistent. It also provides a button to format the filter.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterDialog extends Dialog
{

    /** The default dialog title. */
    private static final String DIALOG_TITLE = "Filter Editor";

    /** The button ID for the format button. */
    private static final int FORMAT_BUTTON_ID = 987654321;

    /** The dialog title. */
    private String title;

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The source viewer. */
    private SourceViewer sourceViewer;

    /** The filter source viewer configuration. */
    private FilterSourceViewerConfiguration configuration;

    /** The filter parser. */
    private LdapFilterParser parser;

    /** The filter. */
    private String filter;


    /**
     * Creates a new instance of FilterDialog.
     * 
     * @param parentShell the parent shell
     * @param title the title
     * @param filter the initial filter
     * @param brwoserConnection the browser connection
     */
    public FilterDialog( Shell parentShell, String title, String filter, IBrowserConnection brwoserConnection )
    {
        super( parentShell );
        this.title = title;
        this.filter = filter;
        this.browserConnection = brwoserConnection;
        this.parser = new LdapFilterParser();
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
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( title != null ? title : DIALOG_TITLE );
        newShell.setImage( BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_FILTER_EDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed( int buttonId )
    {
        if ( buttonId == IDialogConstants.OK_ID )
        {
            parser.parse( sourceViewer.getDocument().get() );
            filter = parser.getModel().toString();
        }
        else if ( buttonId == FORMAT_BUTTON_ID )
        {
            IRegion region = new Region( 0, sourceViewer.getDocument().getLength() );
            configuration.getContentFormatter( sourceViewer ).format( sourceViewer.getDocument(), region );
        }

        // call super implementation
        super.buttonPressed( buttonId );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected Control createButtonBar( Composite parent )
    {
        Composite composite = ( Composite ) super.createButtonBar( parent );
        super.createButton( composite, FORMAT_BUTTON_ID, "Format", false );
        return composite;
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
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
        configuration = new FilterSourceViewerConfiguration( parser, browserConnection );
        sourceViewer.configure( configuration );

        // set document
        IDocument document = new Document( filter );
        sourceViewer.setDocument( document );

        // preformat
        IRegion region = new Region( 0, sourceViewer.getDocument().getLength() );
        configuration.getContentFormatter( sourceViewer ).format( sourceViewer.getDocument(), region );

        // set focus to the source viewer
        sourceViewer.getTextWidget().setFocus();

        return composite;
    }

}
