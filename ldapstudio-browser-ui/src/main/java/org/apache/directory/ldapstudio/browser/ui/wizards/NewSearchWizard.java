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


import org.apache.directory.ldapstudio.browser.ui.search.SearchPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;


public class NewSearchWizard extends Wizard implements INewWizard
{

    private IWorkbenchWindow window;


    public NewSearchWizard()
    {
    }


    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        window = workbench.getActiveWorkbenchWindow();
    }


    public void dispose()
    {
        window = null;
    }


    public static String getId()
    {
        return NewSearchWizard.class.getName();
    }


    public boolean performFinish()
    {
        NewSearchUI.openSearchDialog( window, SearchPage.getId() );
        return true;
    }

}
