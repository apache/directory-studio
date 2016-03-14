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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.swt.widgets.Composite;


/**
 * This class implements the page to select the target file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportLogsToWizardPage extends ExportBaseToPage
{

    /** The extensions used by LDIF files */
    private static final String[] EXTENSIONS = new String[]
        { "*.ldif", "*" }; //$NON-NLS-1$ //$NON-NLS-2$


    /**
     * Creates a new instance of ExportModificationLogsToWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public ExportLogsToWizardPage( String pageName, ExportBaseWizard wizard )
    {
        super( pageName, wizard );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_EXPORT_LDIF_WIZARD ) );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        final Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        super.createControl( composite );
    }


    /**
     * {@inheritDoc}
     */
    protected String[] getExtensions()
    {
        return EXTENSIONS;
    }


    /**
     * {@inheritDoc}
     */
    protected String getFileType()
    {
        return Messages.getString( "ExportLogsToWizardPage.Log" ); //$NON-NLS-1$
    }

}
