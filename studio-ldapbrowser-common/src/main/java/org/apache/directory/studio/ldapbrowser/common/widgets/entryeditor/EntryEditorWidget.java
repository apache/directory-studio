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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import org.apache.directory.studio.ldapbrowser.common.widgets.ViewFormWidget;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;


/**
 * The EntryEditorWidget is a reusable widget to display and edit the attributes of an entry.
 * It is used by 
 * {@link org.apache.directory.studio.ldapbrowser.ui.editors.entry.EntryEditor}, 
 * {@link org.apache.directory.studio.ldapbrowser.common.dialogs.MultivaluedDialog}, 
 * {@link org.apache.directory.studio.ldapbrowser.common.dialogs.LdifEntryEditorDialog} and 
 * {@link org.apache.directory.studio.ldapbrowser.common.wizards.NewEntryAttributesWizardPage}. 
 * 
 * It provides a context menu and a local toolbar with actions to
 * manage attributes. Further there is an instant search feature to filter 
 * the visible attributes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorWidget extends ViewFormWidget
{

    /** The configuration. */
    private EntryEditorWidgetConfiguration configuration;

    /** The quick filter widget. */
    private EntryEditorWidgetQuickFilterWidget quickFilterWidget;

    /** The tree. */
    private Tree tree;

    /** The viewer. */
    private TreeViewer viewer;


    /**
     * Creates a new instance of EntryEditorWidget.
     * 
     * @param configuration the configuration
     */
    public EntryEditorWidget( EntryEditorWidgetConfiguration configuration )
    {
        this.configuration = configuration;
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContent( Composite parent )
    {
        quickFilterWidget = new EntryEditorWidgetQuickFilterWidget( configuration.getFilter(), this );
        quickFilterWidget.createComposite( parent );

        // create tree widget and viewer
        tree = new Tree( parent, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
            | SWT.HIDE_SELECTION );
        GridData data = new GridData( GridData.FILL_BOTH );
        data.widthHint = 450;
        data.heightHint = 250;
        tree.setLayoutData( data );
        tree.setHeaderVisible( true );
        tree.setLinesVisible( true );
        viewer = new TreeViewer( tree );
        viewer.setUseHashlookup( true );

        // set tree columns
        for ( int i = 0; i < EntryEditorWidgetTableMetadata.COLUM_NAMES.length; i++ )
        {
            TreeColumn column = new TreeColumn( tree, SWT.LEFT, i );
            column.setText( EntryEditorWidgetTableMetadata.COLUM_NAMES[i] );
            column.setWidth( 200 );
            column.setResizable( true );

        }
        viewer.setColumnProperties( EntryEditorWidgetTableMetadata.COLUM_NAMES );
        tree.addControlListener( new ControlAdapter()
        {
            public void controlResized( ControlEvent e )
            {
                if ( tree.getClientArea().width > 0 )
                {
                    int width = tree.getClientArea().width - 2 * tree.getBorderWidth();
                    if ( tree.getVerticalBar().isVisible() )
                    {
                        width -= tree.getVerticalBar().getSize().x;
                    }
                    tree.getColumn( EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX ).setWidth(
                        width - tree.getColumn( EntryEditorWidgetTableMetadata.KEY_COLUMN_INDEX ).getWidth() );
                }
            }
        } );

        // setup sorter, filter and layout
        configuration.getSorter().connect( viewer );
        configuration.getFilter().connect( viewer );
        configuration.getPreferences().connect( viewer );

        // setup providers
        viewer.setContentProvider( configuration.getContentProvider( this ) );
        viewer.setLabelProvider( configuration.getLabelProvider( viewer ) );

        // set table cell editors
        viewer.setCellModifier( configuration.getCellModifier( viewer ) );
        CellEditor[] editors = new CellEditor[EntryEditorWidgetTableMetadata.COLUM_NAMES.length];
        viewer.setCellEditors( editors );

        return tree;

    }


    /**
     * Sets the focus to the tree viewer.
     */
    public void setFocus()
    {
        viewer.getTree().setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( viewer != null )
        {
            configuration.dispose();
            configuration = null;

            if ( quickFilterWidget != null )
            {
                quickFilterWidget.dispose();
                quickFilterWidget = null;
            }

            tree.dispose();
            tree = null;
            viewer = null;
        }

        super.dispose();
    }


    /**
     * Gets the viewer.
     * 
     * @return the viewer
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }


    /**
     * Gets the quick filter widget.
     * 
     * @return the quick filter widget
     */
    public EntryEditorWidgetQuickFilterWidget getQuickFilterWidget()
    {
        return quickFilterWidget;
    }


    /**
     * Enables or disables this widget.
     *
     * @param enabled true to enable this widget, false to disable this widget
     */
    public void setEnabled( boolean enabled )
    {
        tree.setEnabled( enabled );
    }

}
