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
package org.apache.directory.studio.schemaeditor.view.preferences;


import java.util.Comparator;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.model.io.SchemaConnector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * This class implements the Preference page for the Plugin
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PluginPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    /**
     * Creates a new instance of PluginPreferencePage.
     *
     */
    public PluginPreferencePage()
    {
        super();
        setPreferenceStore( Activator.getDefault().getPreferenceStore() );
        setDescription( Messages.getString( "PluginPreferencePage.GeneralSettings" ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        //        // SchemaConnectors Group
        //        Group schemaConnectorsGroup = new Group( composite, SWT.NONE );
        //        schemaConnectorsGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        //        schemaConnectorsGroup.setLayout( new GridLayout( 2, true ) );
        //        schemaConnectorsGroup.setText( "Schema Connectors" );
        //
        //        // Available Schema Connectors Label
        //        Label availableSchemaConnectorsLabel = new Label( schemaConnectorsGroup, SWT.NONE );
        //        availableSchemaConnectorsLabel.setText( "Available Connectors:" );
        //
        //        // Description Label
        //        Label descriptionLabel = new Label( schemaConnectorsGroup, SWT.NONE );
        //        descriptionLabel.setText( "Description:" );
        //        // SchemaConnectors TableViewer
        //        final TableViewer schemaConnectorsTableViewer = new TableViewer( schemaConnectorsGroup, SWT.BORDER | SWT.SINGLE
        //            | SWT.FULL_SELECTION );
        //        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        //        gridData.heightHint = 125;
        //        schemaConnectorsTableViewer.getTable().setLayoutData( gridData );
        //        schemaConnectorsTableViewer.setContentProvider( new ArrayContentProvider() );
        //        schemaConnectorsTableViewer.setLabelProvider( new LabelProvider()
        //        {
        //            public String getText( Object element )
        //            {
        //                return ( ( SchemaConnector ) element ).getName();
        //            }
        //
        //
        //            public Image getImage( Object element )
        //            {
        //                return Activator.getDefault().getImage( PluginConstants.IMG_SCHEMA_CONNECTOR );
        //            }
        //        } );
        //
        //        schemaConnectorsTableViewer.setComparator( new ViewerComparator( new Comparator<String>()
        //        {
        //            public int compare( String o1, String o2 )
        //            {
        //                if ( ( o1 != null ) && ( o2 != null ) )
        //                {
        //                    return o1.compareToIgnoreCase( o2 );
        //                }
        //
        //                // Default
        //                return 0;
        //            }
        //        } ) );
        //
        //        //      schemaConnectorsTableViewer.setComparator( new ViewerComparator( new Comparator<SchemaConnector>()
        //        //      {
        //        //          public int compare( SchemaConnector o1, SchemaConnector o2 )
        //        //          {
        //        //              String name1 = o1.getName();
        //        //              String name2 = o2.getName();
        //        //
        //        //              if ( ( name1 != null ) && ( name2 != null ) )
        //        //              {
        //        //                  return name1.compareToIgnoreCase( name2 );
        //        //              }
        //        //
        //        //              // Default
        //        //              return 0;
        //        //          }
        //        //      } ) );
        //        schemaConnectorsTableViewer.setInput( PluginUtils.getSchemaConnectors() );
        //
        //        // Description Text
        //        final Text descriptionText = new Text( schemaConnectorsGroup, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY );
        //        descriptionText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        //
        //        schemaConnectorsTableViewer.addSelectionChangedListener( new ISelectionChangedListener()
        //        {
        //            public void selectionChanged( SelectionChangedEvent event )
        //            {
        //                SchemaConnector schemaConnector = ( SchemaConnector ) ( ( StructuredSelection ) schemaConnectorsTableViewer
        //                    .getSelection() ).getFirstElement();
        //
        //                if ( schemaConnector != null )
        //                {
        //                    descriptionText.setText( schemaConnector.getDescription() );
        //                }
        //            }
        //        } );

        return parent;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init( IWorkbench workbench )
    {
        // Nothing to do
    }
}
