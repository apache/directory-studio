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
package org.apache.directory.studio.schemaeditor.view.wizards;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.ProjectType;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;


/**
 * This class represents the page to select merged elements of the MergeSchemasWizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MergeSchemasSelectionWizardPage extends AbstractWizardPage
{
    /** The selected projects */
    private Project[] selectedProjects = new Project[0];

    // UI Fields
    private CheckboxTreeViewer projectsTreeViewer;


    /**
     * Creates a new instance of MergeSchemasSelectionWizardPage.
     */
    protected MergeSchemasSelectionWizardPage()
    {
        super( "MergeSchemasSelectionWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "MergeSchemasSelectionWizardPage.ImportSchemasFromProjects" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "MergeSchemasSelectionWizardPage.PleaseSelectElements" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_SCHEMAS_IMPORT_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Projects TreeViewer
        Label projectsLabel = new Label( composite, SWT.NONE );
        projectsLabel.setText( Messages.getString( "MergeSchemasSelectionWizardPage.SelectElements" ) ); //$NON-NLS-1$
        projectsLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );
        projectsTreeViewer = new CheckboxTreeViewer( new Tree( composite, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION ) );
        GridData projectsTableViewerGridData = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 1 );
        projectsTableViewerGridData.widthHint = 450;
        projectsTableViewerGridData.heightHint = 250;
        projectsTreeViewer.getTree().setLayoutData( projectsTableViewerGridData );
        projectsTreeViewer.setContentProvider( new ITreeContentProvider()
        {
            public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
            {
            }


            public void dispose()
            {
            }


            public Object[] getElements( Object inputElement )
            {
                return getChildren( inputElement );
            }


            public boolean hasChildren( Object element )
            {
                return getChildren( element ).length > 0;
            }


            public Object getParent( Object element )
            {
                return null;
            }


            public Object[] getChildren( Object parentElement )
            {
                if ( parentElement instanceof List<?> )
                {
                    return ( ( List<?> ) parentElement ).toArray();
                }
                if ( parentElement instanceof Project )
                {
                    Project project = ( Project ) parentElement;
                    List<Schema> schemas = project.getSchemaHandler().getSchemas();
                    return schemas.toArray();
                }
                if ( parentElement instanceof Schema )
                {
                    Schema schema = ( Schema ) parentElement;
                    Object[] children = new Object[]
                        { new AttributeTypeFolder( schema ), new ObjectClassFolder( schema ) };
                    return children;
                }
                if ( parentElement instanceof AttributeTypeFolder )
                {
                    AttributeTypeFolder folder = ( AttributeTypeFolder ) parentElement;
                    List<AttributeTypeImpl> attributeTypes = folder.schema.getAttributeTypes();
                    return attributeTypes.toArray();
                }
                if ( parentElement instanceof ObjectClassFolder )
                {
                    ObjectClassFolder folder = ( ObjectClassFolder ) parentElement;
                    List<ObjectClassImpl> objectClasses = folder.schema.getObjectClasses();
                    return objectClasses.toArray();
                }

                return new Object[0];
            }
        } );
        projectsTreeViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof Project )
                {
                    return ( ( Project ) element ).getName();
                }
                else if ( element instanceof Schema )
                {
                    return ( ( Schema ) element ).getName();
                }
                else if ( element instanceof ObjectClassFolder )
                {
                    return Messages.getString( "MergeSchemasSelectionWizardPage.ObjectClasses" ); //$NON-NLS-1$
                }
                else if ( element instanceof AttributeTypeFolder )
                {
                    return Messages.getString( "MergeSchemasSelectionWizardPage.AttributeTypes" ); //$NON-NLS-1$
                }
                else if ( element instanceof AttributeType )
                {
                    AttributeType at = ( ( AttributeType ) element );
                    List<String> names = at.getNames();
                    if ( ( names != null ) && ( names.size() > 0 ) )
                    {
                        return names.get(0);
                    }
                    else
                    {
                        return at.getOid();
                    }
                }
                else if ( element instanceof ObjectClass )
                {
                    ObjectClass oc = ( ( ObjectClass ) element );
                    List<String> names = oc.getNames();
                    if ( ( names != null ) && ( names.size() > 0 ) )
                    {
                        return names.get(0);
                    }
                    else
                    {
                        return oc.getOid();
                    }
                }

                // Default
                return super.getText( element );
            }


            public Image getImage( Object element )
            {
                if ( element instanceof Project )
                {
                    ProjectType type = ( ( Project ) element ).getType();
                    switch ( type )
                    {
                        case OFFLINE:
                            return Activator.getDefault().getImage( PluginConstants.IMG_PROJECT_OFFLINE_CLOSED );
                        case ONLINE:
                            return Activator.getDefault().getImage( PluginConstants.IMG_PROJECT_ONLINE_CLOSED );
                    }
                }
                else if ( element instanceof Schema )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_SCHEMA );
                }
                else if ( element instanceof ObjectClassFolder )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_FOLDER_OC );
                }
                else if ( element instanceof AttributeTypeFolder )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_FOLDER_AT );
                }
                else if ( element instanceof AttributeType )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_ATTRIBUTE_TYPE );
                }
                else if ( element instanceof ObjectClass )
                {
                    return Activator.getDefault().getImage( PluginConstants.IMG_OBJECT_CLASS );
                }

                // Default
                return super.getImage( element );
            }
        } );
        projectsTreeViewer.setSorter( new ViewerSorter() );
        projectsTreeViewer.addCheckStateListener( new ICheckStateListener()
        {
            /**
             * Notifies of a change to the checked state of an element.
             *
             * @param event
             *      event object describing the change
             */
            public void checkStateChanged( CheckStateChangedEvent event )
            {
                dialogChanged();
            }
        } );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {
        // Filling the Schemas table
        List<Project> projects = new ArrayList<Project>();
        projects.addAll( Activator.getDefault().getProjectsHandler().getProjects() );
        Collections.sort( projects, new Comparator<Project>()
        {
            public int compare( Project o1, Project o2 )
            {
                return o1.getName().compareToIgnoreCase( o2.getName() );
            }

        } );
        projectsTreeViewer.setInput( projects );

        // Setting the selected projects
        projectsTreeViewer.setCheckedElements( selectedProjects );

        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Schemas table
        if ( projectsTreeViewer.getCheckedElements().length == 0 )
        {
            displayErrorMessage( Messages.getString( "MergeSchemasSelectionWizardPage.ErrorNoElementsSelected" ) ); //$NON-NLS-1$
            return;
        }

        displayErrorMessage( null );
    }


    /**
     * Gets the selected objects.
     *
     * @return
     *      the selected objects
     */
    public Object[] getSelectedObjects()
    {
        Object[] selectedObjects = projectsTreeViewer.getCheckedElements();
        return selectedObjects;
    }


    /**
     * Sets the selected projects.
     *
     * @param projects
     *      the projects
     */
    public void setSelectedProjects( Project[] projects )
    {
        selectedProjects = projects;
    }

    class ObjectClassFolder
    {
        Schema schema;


        public ObjectClassFolder( Schema schema )
        {
            this.schema = schema;
        }
    }

    class AttributeTypeFolder
    {
        Schema schema;


        public AttributeTypeFolder( Schema schema )
        {
            this.schema = schema;
        }
    }
}
