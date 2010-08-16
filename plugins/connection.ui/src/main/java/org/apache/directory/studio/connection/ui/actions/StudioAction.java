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


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


/**
 * This abstract class must be extended by each Action related to the Browser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class StudioAction implements IWorkbenchWindowActionDelegate
{
    /** The selected Connections */
    private Connection[] selectedConnections;

    /** The selected connection folders */
    private ConnectionFolder[] selectedConnectionFolders;

    /** The input */
    private Object input;


    /**
     * Creates a new instance of BrowserAction.
     */
    protected StudioAction()
    {
        this.init();
    }


    /**
     * Gets the style.
     * 
     * @return the style
     */
    public int getStyle()
    {
        return Action.AS_PUSH_BUTTON;
    }


    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        this.init();
    }


    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        this.run();
    }


    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        setSelectedConnections( SelectionUtils.getConnections( selection ) );
        setSelectedConnectionFolders( SelectionUtils.getConnectionFolders( selection ) );

        action.setEnabled( this.isEnabled() );
        action.setText( this.getText() );
        action.setToolTipText( this.getText() );
    }


    /**
     * Returns the text for this action.
     * <p>
     * This method is associated with the <code>TEXT</code> property;
     * property change events are reported when its value changes.
     * </p>
     *
     * @return the text, or <code>null</code> if none
     */
    public abstract String getText();


    /**
     * Returns the image for this action as an image descriptor.
     * <p>
     * This method is associated with the <code>IMAGE</code> property;
     * property change events are reported when its value changes.
     * </p>
     *
     * @return the image, or <code>null</code> if this action has no image
     */
    public abstract ImageDescriptor getImageDescriptor();


    /**
     * Returns the command identifier.
     *
     * @return
     *      the command identifier
     */
    public abstract String getCommandId();


    /**
     * Returns whether this action is enabled.
     * <p>
     * This method is associated with the <code>ENABLED</code> property;
     * property change events are reported when its value changes.
     * </p>
     *
     * @return <code>true</code> if enabled, and
     *   <code>false</code> if disabled
     */
    public abstract boolean isEnabled();


    /**
     * Runs this action.
     * Each action implementation must define the steps needed to carry out this action.
     * The default implementation of this method in <code>Action</code>
     * does nothing.
     */
    public abstract void run();


    /**
     * Returns weather this action is checked.
     * The default implementations returns false.
     *
     * @return
     */
    public boolean isChecked()
    {
        return false;
    }


    /**
     * Initializes this action
     */
    private void init()
    {
        this.selectedConnections = new Connection[0];
        this.selectedConnectionFolders = new ConnectionFolder[0];

        this.input = null;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        this.selectedConnections = new Connection[0];
        this.selectedConnectionFolders = new ConnectionFolder[0];

        this.input = null;
    }


    /**
     * Returns the current active shell
     *
     * @return
     *      the current active shell
     */
    protected Shell getShell()
    {
        return PlatformUI.getWorkbench().getDisplay().getActiveShell();
    }


    /**
     * Gets the selected Connections.
     *
     * @return
     *      the selected Connections
     */
    public Connection[] getSelectedConnections()
    {
        return selectedConnections;
    }


    /**
     * Sets the selected Connections.
     *
     * @param selectedConnections
     *      the selected Connections to set
     */
    public void setSelectedConnections( Connection[] selectedConnections )
    {
        this.selectedConnections = selectedConnections;
    }


    /**
     * Gets the selected connection folders.
     *
     * @return
     *      the selected connection folders
     */
    public ConnectionFolder[] getSelectedConnectionFolders()
    {
        return selectedConnectionFolders;
    }


    /**
     * Sets the selected connection folders.
     *
     * @param selectedConnectionFolders
     *      the selected connections folders to set
     */
    public void setSelectedConnectionFolders( ConnectionFolder[] selectedConnectionFolders )
    {
        this.selectedConnectionFolders = selectedConnectionFolders;
    }


    /**
     * Gets the input.
     *
     * @return
     *      the input
     */
    public Object getInput()
    {
        return input;
    }


    /**
     * Sets the input.
     *
     * @param input
     *      the input to set
     */
    public void setInput( Object input )
    {
        this.input = input;
    }

}
