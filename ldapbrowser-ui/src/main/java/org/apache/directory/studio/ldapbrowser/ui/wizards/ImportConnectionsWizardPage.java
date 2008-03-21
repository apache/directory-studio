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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import java.io.File;

import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.widgets.FileBrowserWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


/**
 * This class implements the page used to select the data to export to LDIF.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportConnectionsWizardPage extends WizardPage
{
    private FileBrowserWidget fileBrowserWidget;


    protected ImportConnectionsWizardPage()
    {
        super( ImportConnectionsWizardPage.class.getName() );
        setTitle( "Import Connections" );
        setDescription( "Import connections from local file system." );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_IMPORT_CONNECTIONS_WIZARD ) );
        setPageComplete( false );
    }


    public void createControl( Composite parent )
    {
        // Main Composite
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // From File
        BaseWidgetUtils.createLabel( composite, "From file:", 1 );
        fileBrowserWidget = new FileBrowserWidget( "Choose a file", new String[]
            { "lbc" }, FileBrowserWidget.TYPE_OPEN );
        fileBrowserWidget.createWidget( composite );
        fileBrowserWidget.addWidgetModifyListener( new WidgetModifyListener()
        {
            public void widgetModified( WidgetModifyEvent event )
            {
                validate();
            }
        } );

        setControl( composite );
    }


    /**
     * Validates this page. This method is responsible for displaying errors, 
     * as well as enabling/disabling the "Finish" button
     */
    private void validate()
    {
        boolean ok = true;
        File file = new File( fileBrowserWidget.getFilename() );
        if ( "".equals( fileBrowserWidget.getFilename() ) )
        {
            setErrorMessage( null );
            ok = false;
        }
        else if ( !file.exists() )
        {
            setErrorMessage( "Selected file does not exist." );
            ok = false;
        }
        else if ( file.isDirectory() )
        {
            setErrorMessage( "Selected file is not a file." );
            ok = false;
        }
        else if ( file.exists() && !file.canRead() )
        {
            setErrorMessage( "Selected file is not readable." );
            ok = false;
        }

        if ( ok )
        {
            setErrorMessage( null );
        }

        setPageComplete( ok );
    }


    /**
     * Gets the export file name.
     * 
     * @return
     *      the export file name
     */
    public String getImportFileName()
    {
        return fileBrowserWidget.getFilename();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        fileBrowserWidget.saveDialogSettings();
    }
}
