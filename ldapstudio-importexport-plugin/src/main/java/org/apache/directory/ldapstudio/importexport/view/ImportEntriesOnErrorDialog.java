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

package org.apache.directory.ldapstudio.importexport.view;

import java.util.List;

import org.apache.directory.ldapstudio.importexport.Messages;
import org.apache.directory.ldapstudio.importexport.Plugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * This class implement a dialog that is shown when an import has errors.
 * It shows all the entries successfully imported and the entries that
 * could not be imported.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportEntriesOnErrorDialog extends Dialog
{
    private List<String> addedEntries;
    private List<String> errorEntries;
    
    private Table entriesSuccess_table;
    private Table entriesError_table;

    
    /**
     * Default constructor
     * @param parentShell
     */
    public ImportEntriesOnErrorDialog( Shell parentShell, List<String> addedEntries, List<String> errorEntries )
    {
        super( parentShell );
        this.addedEntries = addedEntries;
        this.errorEntries = errorEntries;
    }
    
    /**
     * Default constructor
     * @param parentShell
     */
    public ImportEntriesOnErrorDialog( Shell parentShell )
    {
        super( parentShell );
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText(Messages.getString("ImportEntriesOnErrorDialog.Import_error")); //$NON-NLS-1$
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, true);
        composite.setLayout(layout);
        
        Composite header = new Composite(composite, SWT.NONE);
        GridLayout layout2 = new GridLayout(2, false);
        header.setLayout( layout2 );
        header.setLayoutData( new GridData(GridData.FILL, SWT.NONE, true, false, 2, 1));
      
        Canvas canvas = new Canvas(header,SWT.NO_REDRAW_RESIZE);
        canvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                e.gc.drawImage(PlatformUI.getWorkbench().getDisplay().getSystemImage(SWT.ICON_ERROR),0,0);
            }
        });
        GridData gridDatImage = new GridData(SWT.NONE, SWT.NONE, false, false);
        gridDatImage.heightHint = 45;
        gridDatImage.minimumHeight = 45;
        gridDatImage.widthHint = 60;
        gridDatImage.minimumWidth = 60;
        canvas.setLayoutData( gridDatImage );
        
        Label errorMessage_label = new Label(header, SWT.NONE);
        errorMessage_label.setText( Messages.getString("ImportEntriesOnErrorDialog.Some_entries_could_not_be_imported") ); //$NON-NLS-1$
        errorMessage_label.setLayoutData(new GridData( SWT.NONE, SWT.NONE, false, false));
        
        Label filler = new Label(composite, SWT.NONE);
        filler.setLayoutData(new GridData( GridData.FILL, SWT.NONE, true, false, 2, 1 ));
        
        Label entriesSuccess_label = new Label(composite, SWT.NONE);
        entriesSuccess_label.setText(Messages.getString("ImportEntriesOnErrorDialog.Entries_successfully_imported")); //$NON-NLS-1$
        entriesSuccess_label.setLayoutData(new GridData(GridData.FILL, SWT.NONE, true, false));
        
        Label entriesError_label = new Label(composite, SWT.NONE);
        entriesError_label.setText(Messages.getString("ImportEntriesOnErrorDialog.Entries_on_error")); //$NON-NLS-1$
        entriesError_label.setLayoutData(new GridData(GridData.FILL, SWT.NONE, true, false));
               
        entriesSuccess_table = new Table(composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
                SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        gridData.heightHint = 150;
        gridData.minimumHeight = 150;
        gridData.widthHint = 250;
        gridData.minimumWidth = 250;
        entriesSuccess_table.setLayoutData(gridData);
        
        entriesError_table = new Table( composite, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
            | SWT.FULL_SELECTION | SWT.HIDE_SELECTION );
        gridData = new GridData( GridData.FILL, GridData.FILL, true, true );
        gridData.heightHint = 150;
        gridData.minimumHeight = 150;
        gridData.widthHint = 250;
        gridData.minimumWidth = 250;
        entriesError_table.setLayoutData( gridData );
        
        // Tables initialization
        initTables();
        
        return composite;
    }
    
    /**
     * Initializes the tables for the entries that were successfully added and the entries
     * that were on error.
     */
    private void initTables()
    {
        // Entries successfully imported
        for ( int i = 0; i < addedEntries.size(); i++ )
        {
            String  entryName = addedEntries.get( i );
            TableItem item = new TableItem( entriesSuccess_table, SWT.NONE );
            item.setText( entryName );
            item.setImage( AbstractUIPlugin.imageDescriptorFromPlugin(Plugin.ID, ImageKeys.ENTRY_ADDED).createImage() );
        }
        
        // Entries on error
        for ( int i = 0; i < errorEntries.size(); i++ )
        {
            String  entryName = errorEntries.get( i );
            TableItem item = new TableItem( entriesError_table, SWT.NONE );
            item.setText( entryName );
            item.setImage( AbstractUIPlugin.imageDescriptorFromPlugin(Plugin.ID, ImageKeys.ENTRY_ERROR).createImage() );
        }
    }

    protected void createButtonsForButtonBar(Composite parent)
    {
        createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
    }
}
