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
package org.apache.directory.studio.ldapbrowser.ui.dialogs.preferences;


import org.apache.directory.studio.entryeditors.EntryEditorExtension;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The entry editors preference page contains settings 
 * for the Entry Editors.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    // UI fields
    private TableViewer entryEditorsTableViewer;
    private Button upButton;
    private Button downButton;


    /**
     * Creates a new instance of EntryEditorsPreferencePage.
     */
    public EntryEditorsPreferencePage()
    {
        super( "Entry Editors" );
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( "Description" );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Entry Editors Label
        Label entryEditorsLabel = new Label( composite, SWT.NONE );
        entryEditorsLabel.setText( "Entry Editors:" );

        // Entry Editors Composite
        Composite entryEditorsComposite = new Composite( composite, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        gl.marginHeight = gl.marginWidth = 0;
        entryEditorsComposite.setLayout( gl );
        entryEditorsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SchemaConnectors TableViewer
        entryEditorsTableViewer = new TableViewer( entryEditorsComposite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        gridData.heightHint = 125;
        entryEditorsTableViewer.getTable().setLayoutData( gridData );
        entryEditorsTableViewer.setContentProvider( new ArrayContentProvider() );
        entryEditorsTableViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                return ( ( EntryEditorExtension ) element ).getName();
            }


            public Image getImage( Object element )
            {
                return ( ( EntryEditorExtension ) element ).getIcon().createImage();
            }
        } );

        entryEditorsTableViewer.setInput( BrowserUIPlugin.getDefault().getEntryEditorManager()
            .getEntryEditorExtensions() );

        // Up Button
        upButton = new Button( entryEditorsComposite, SWT.PUSH );
        upButton.setText( "Up" );
        upButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Down Button
        downButton = new Button( entryEditorsComposite, SWT.PUSH );
        downButton.setText( "Down" );
        downButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );

        // Description Label
        Label descriptionLabel = new Label( composite, SWT.NONE );
        descriptionLabel.setText( "Description:" );

        // Description Text
        final Text descriptionText = new Text( composite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY );
        gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData.heightHint = 27;
        descriptionText.setLayoutData( gridData );

        entryEditorsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                EntryEditorExtension schemaConnector = ( EntryEditorExtension ) ( ( StructuredSelection ) entryEditorsTableViewer
                    .getSelection() ).getFirstElement();

                if ( schemaConnector != null )
                {
                    descriptionText.setText( schemaConnector.getDescription() );
                }
            }
        } );

        return parent;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        super.performDefaults();
    }
}