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

package org.apache.directory.studio.ldapbrowser.common.dialogs.preferences;


import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The BrowserPreferencePage contains general settings for the browser view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ViewsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    /**
     * Creates a new instance of ViewsPreferencePage.
     */
    public ViewsPreferencePage()
    {
        super( Messages.getString("ViewsPreferencePage.Views") ); //$NON-NLS-1$
        setDescription( Messages.getString("ViewsPreferencePage.Description") ); //$NON-NLS-1$
        
        // Removing Default and Apply buttons
        noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        // Nothing to do
        
        return parent;
    }
}
