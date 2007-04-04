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
package org.apache.directory.ldapstudio.apacheds.configuration.editor;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;


/**
 * This class implements the Server Configuration Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerConfigurationEditor extends FormEditor
{
    /** The Editor ID */
    public static final String ID = "org.apache.directory.ldapstudio.apacheds.configuration.editor";

    // The Pages
    private FormPage basicPage;
    private FormPage limitsPage;
    private FormPage optionsPage;
    private FormPage partitionsPage;
    private FormPage interceptorsPage;
    private FormPage extendedOperationsPage;

    
    @Override
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.init( site, input );
        
        setPartName( input.getName() );
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages()
    {
        basicPage = new BasicPage( this );
        limitsPage = new LimitsPage( this );
        optionsPage = new OptionsPage( this );
        partitionsPage = new PartitionsPage( this );
        interceptorsPage = new InterceptorsPage( this );
        extendedOperationsPage = new ExtendedOperationsPage( this );

        try
        {
            addPage( basicPage );
            addPage( limitsPage );
            addPage( optionsPage );
            addPage( partitionsPage );
            addPage( interceptorsPage );
            addPage( extendedOperationsPage );
        }
        catch ( PartInitException e )
        {
            // TODO Add an eclipse logger.
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor )
    {
        // TODO Auto-generated method stub
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     */
    public void doSaveAs()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }
}
