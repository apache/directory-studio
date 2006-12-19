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


import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.ui.actions.SelectionUtils;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


public abstract class ExportBaseWizard extends Wizard implements IExportWizard
{

    protected String exportFilename = "";

    protected ISearch search;


    public ExportBaseWizard( String title )
    {
        super();
        super.setWindowTitle( title );
        init( null, ( IStructuredSelection ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
            .getSelection() );
    }


    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        this.search = SelectionUtils.getExampleSearch( selection );
        this.search.setName( null );
        this.exportFilename = "";
    }


    public void setExportFilename( String exportFilename )
    {
        this.exportFilename = exportFilename;
    }


    public String getExportFilename()
    {
        return exportFilename;
    }


    public ISearch getSearch()
    {
        return search;
    }


    public void setSearch( ISearch search )
    {
        this.search = search;
    }

}
