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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.studio.ldapbrowser.common.actions.SelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


/**
 * This class is a base implementation of the export wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class ExportBaseWizard extends Wizard implements IExportWizard
{

    /** The export filename. */
    protected String exportFilename = "";

    /** The search. */
    protected ISearch search;


    /**
     * Creates a new instance of ExportBaseWizard.
     * 
     * @param title the title
     */
    public ExportBaseWizard( String title )
    {
        super();
        setWindowTitle( title );
        init( null, ( IStructuredSelection ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
            .getSelection() );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        search = SelectionUtils.getExampleSearch( selection );
        search.setName( null );
        exportFilename = "";
    }


    /**
     * Sets the export filename.
     * 
     * @param exportFilename the export filename
     */
    public void setExportFilename( String exportFilename )
    {
        this.exportFilename = exportFilename;
    }


    /**
     * Gets the export filename.
     * 
     * @return the export filename
     */
    public String getExportFilename()
    {
        return exportFilename;
    }


    /**
     * Gets the search.
     * 
     * @return the search
     */
    public ISearch getSearch()
    {
        return search;
    }


    /**
     * Sets the search.
     * 
     * @param search the search
     */
    public void setSearch( ISearch search )
    {
        this.search = search;
    }

}
