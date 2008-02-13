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

package org.apache.directory.studio.connection.ui.wizards;


import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.ui.ConnectionParameterPage;
import org.apache.directory.studio.connection.ui.ConnectionParameterPageModifyListener;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * NewConnectionWizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewConnectionWizardPage extends WizardPage implements ConnectionParameterPageModifyListener
{

    /** The wizard. */
    private NewConnectionWizard wizard;

    /** The page. */
    private ConnectionParameterPage page;


    /**
     * Creates a new instance of NewConnectionWizard.
     * 
     * @param page the page
     * @param wizard the wizard
     */
    public NewConnectionWizardPage( NewConnectionWizard wizard, ConnectionParameterPage page )
    {
        super( page.getPageName() );
        setTitle( page.getPageName() );
        setDescription( page.getPageDescription() );
        setImageDescriptor( ConnectionUIPlugin.getDefault().getImageDescriptor(
            ConnectionUIConstants.IMG_CONNECTION_WIZARD ) );
        setPageComplete( false );

        page.setRunnableContext( getContainer() );

        this.wizard = wizard;
        this.page = page;
    }


    /**
     * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if ( visible )
        {
            page.setFocus();
        }
    }


    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        page.init( composite, this, null );
        setControl( composite );
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPageModifyListener#connectionParameterPageModified()
     */
    public void connectionParameterPageModified()
    {
        //only one of the messages can be shown
        //warning messages are more important 
        //than info messages
        if ( page.getMessage() != null )
        {
            setMessage( page.getMessage() );
        }
        else if ( page.getInfoMessage() != null )
        {
            setMessage( page.getInfoMessage() );
        }
        else
        {
            setMessage( null );
        }
        setErrorMessage( page.getErrorMessage() );
        setPageComplete( page.isValid() );

        if ( getContainer() != null && getContainer().getCurrentPage() != null )
        {
            getContainer().updateButtons();
        }
    }


    /**
     * @see org.apache.directory.studio.connection.ui.ConnectionParameterPageModifyListener#getTestConnectionParameters()
     */
    public ConnectionParameter getTestConnectionParameters()
    {
        return wizard.getTestConnectionParameters();
    }

}