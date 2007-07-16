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
package org.apache.directory.studio.apacheds.schemaeditor.view.views;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.model.Project;
import org.apache.directory.studio.apacheds.schemaeditor.model.Project.ProjectType;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ProjectsViewRoot;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ProjectWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the ContentProvider for the ProblemsView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProjectsViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** The associated viewer */
    private TableViewer viewer;


    /**
     * Creates a new instance of ProblemsViewContentProvider.
     *
     * @param viewer
     *      the associated TableViewer
     */
    public ProjectsViewContentProvider( TableViewer viewer )
    {
        this.viewer = viewer;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        return getChildren( inputElement );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        // Nothing to do.
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do.
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        List<TreeNode> children = null;

        if ( parentElement instanceof ProjectsViewRoot )
        {
            children = new ArrayList<TreeNode>();

            children.add( new ProjectWrapper( new Project( ProjectType.APACHE_DIRECTORY_SERVER,
                "Apache DS" ) ) );

            children.add( new ProjectWrapper( new Project( ProjectType.OFFLINE, "Open LDAP" ) ) );
        }
        else if ( parentElement instanceof ProjectWrapper )
        {
            children = new ArrayList<TreeNode>();
        }
        return children.toArray();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        if ( element instanceof TreeNode )
        {
            return ( ( TreeNode ) element ).getParent();
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof TreeNode )
        {
            return ( ( TreeNode ) element ).hasChildren();
        }

        // Default
        return false;
    }
}
