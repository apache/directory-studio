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

package org.apache.directory.ldapstudio.browser.view.views;


import org.apache.directory.ldapstudio.browser.model.Connection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class implements the Connection Wizard
 */
public class ConnectionWizard extends Wizard
{
    public static final String CONNECTIONS_PREFS = "connections_prefs";

    public enum ConnectionWizardType
    {
        NEW, EDIT
    };

    private ConnectionWizardType type;

    /** The Connection Information Page */
    private ConnectionWizardInformationPage cwip;


    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        cwip = new ConnectionWizardInformationPage();
        addPage( cwip );
        setWindowTitle( "Connection" );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish()
    {
        // Saving the connection
        cwip.saveConnection();

        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
    public boolean canFinish()
    {
        return cwip.canFinish();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#needsPreviousAndNextButtons()
     */
    @Override
    public boolean needsPreviousAndNextButtons()
    {
        return false;
    }


    /**
     * Gets the Connection
     * @return the  Connection
     */
    public Connection getConnection()
    {
        return cwip.getConnection();
    }


    /**
     * Gets the currently selected Connection
     * @return the currently selected Connection, null if no Connection is selected
     */
    public void setConnection( Connection connection )
    {
        cwip.setConnection( connection );
    }


    /**
     * Gets the Connection Type
     * @return the Connection Type
     */
    public ConnectionWizardType getType()
    {
        return type;
    }


    /**
     * Sets the Connection Type 
     * @param type the Connection Type
     */
    public void setType( ConnectionWizardType type )
    {
        this.type = type;
    }
}
