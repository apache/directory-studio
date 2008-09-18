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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.io.jndi.LdifModificationLogger;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * This class implements the wizard for exporting the modification logs.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportModificationLogsWizard extends ExportBaseWizard
{

    /** The to page, used to select the target file. */
    private ExportLogsToWizardPage toPage;


    /**
     * Creates a new instance of ExportModificationLogsWizard.
     */
    public ExportModificationLogsWizard()
    {
        super( "Export Modification Logs" );
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
                FileOutputStream os = FileUtils.openOutputStream( targetFile );

                LdifModificationLogger modificationLogger = ConnectionCorePlugin.getDefault()
                    .getLdifModificationLogger();
                File[] files = modificationLogger.getFiles( search.getBrowserConnection().getConnection() );
                // need to go backward through the files as the 1st file contains the newest entry
                for ( int i = files.length - 1; i >= 0; i-- )
                {
                    File file = files[i];
                    if ( file != null && file.exists() && file.canRead() )
                    {
                        FileInputStream is = FileUtils.openInputStream( file );
                        IOUtils.copy( is, os );
                        is.close();
                    }
                }
                os.close();
            }
            catch ( IOException e )
            {
                ConnectionUIPlugin.getDefault().getExceptionHandler().handleException(
                    new Status( IStatus.ERROR, BrowserCommonConstants.PLUGIN_ID, IStatus.ERROR,
                        "Can't export modification logs", e ) );
            }
        }

        return true;
    }

}
