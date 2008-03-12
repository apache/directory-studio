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
package org.apache.directory.studio.schemaeditor.view.views;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.Project;
import org.apache.directory.studio.schemaeditor.model.ProjectType;
import org.apache.directory.studio.schemaeditor.model.Project.ProjectState;
import org.apache.directory.studio.schemaeditor.view.wrappers.ProjectWrapper;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * This class implements the LabelProvider for the ProjectsView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProjectsViewLabelProvider extends LabelProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element )
    {
        if ( element instanceof ProjectWrapper )
        {
            Project project = ( ( ProjectWrapper ) element ).getProject();
            ProjectType type = project.getType();
            switch ( type )
            {
                case OFFLINE:
                    ProjectState state = project.getState();
                    switch ( state )
                    {
                        case OPEN:
                            return Activator.getDefault().getImage( PluginConstants.IMG_PROJECT_OFFLINE );
                        case CLOSED:
                            return Activator.getDefault().getImage( PluginConstants.IMG_PROJECT_OFFLINE_CLOSED );
                    }
                case ONLINE:
                    ProjectState state2 = project.getState();
                    switch ( state2 )
                    {
                        case OPEN:
                            return Activator.getDefault().getImage( PluginConstants.IMG_PROJECT_ONLINE );
                        case CLOSED:
                            return Activator.getDefault().getImage( PluginConstants.IMG_PROJECT_ONLINE_CLOSED );
                    }
            }
        }

        // Default
        return super.getImage( element );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element )
    {
        if ( element instanceof ProjectWrapper )
        {
            ProjectWrapper projectWrapper = ( ProjectWrapper ) element;
            return projectWrapper.getProject().getName();
        }

        // Default
        return super.getText( element );
    }
}
