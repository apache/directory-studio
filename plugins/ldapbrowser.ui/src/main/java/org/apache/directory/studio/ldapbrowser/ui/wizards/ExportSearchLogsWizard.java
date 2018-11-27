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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.directory.api.util.FileUtils;
import org.apache.directory.api.util.IOUtils;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.io.jndi.LdifSearchLogger;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * This class implements the wizard for exporting the search logs.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportSearchLogsWizard extends ExportBaseWizard
{

    /** The to page, used to select the target file. */
    private ExportLogsToWizardPage toPage;


    /**
     * Creates a new instance of ExportSearchLogsWizard.
     */
    public ExportSearchLogsWizard()
    {
        super( Messages.getString( "ExportSearchLogsWizard.ExportSearchLogs" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        toPage = new ExportLogsToWizardPage( ExportLogsToWizardPage.class.getName(), this );
        addPage( toPage );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        toPage.saveDialogSettings();

        if ( search.getBrowserConnection().getConnection() != null )
        {
            try
            {
                File targetFile = new File( exportFilename );
                OutputStream os = FileUtils.openOutputStream( targetFile );

                LdifSearchLogger searchLogger = ConnectionCorePlugin.getDefault().getLdifSearchLogger();
                File[] files = searchLogger.getFiles( search.getBrowserConnection().getConnection() );
                // need to go backward through the files as the 1st file contains the newest entry
                for ( int i = files.length - 1; i >= 0; i-- )
                {
                    File file = files[i];
                    if ( file != null && file.exists() && file.canRead() )
                    {
                        InputStream is = FileUtils.openInputStream( file );
                        IOUtils.copy( is, os );
                        is.close();
                    }
                }
                os.close();
            }
            catch ( IOException e )
            {
                ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                    new Status( IStatus.ERROR, BrowserCommonConstants.PLUGIN_ID, IStatus.ERROR, Messages
                        .getString( "ExportSearchLogsWizard.CantExportSearchLogs" ), e ) ); //$NON-NLS-1$
            }
        }

        return true;
    }

}
