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

package org.apache.directory.studio.connection.ui.actions;


import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * This action collapses all nodes of the viewer's tree, starting with the root.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CollapseAllAction extends Action
{
    protected TreeViewer viewer;


    /**
     * Creates a new instance of CollapseAllAction.
     *
     * @param viewer
     *      the attached Viewer
     */
    public CollapseAllAction( TreeViewer viewer )
    {
        super( Messages.getString("CollapseAllAction.CollapseAll"), ConnectionUIPlugin.getDefault().getImageDescriptor( ConnectionUIConstants.IMG_COLLAPSEALL ) ); //$NON-NLS-1$
        super.setToolTipText( getText() );
        super.setEnabled( true );

        this.viewer = viewer;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        this.viewer.collapseAll();
    }


    /**
     * Disposes the action delegate.
     */
    public void dispose()
    {
        this.viewer = null;
    }
}
