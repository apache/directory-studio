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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.ldifeditor.editor.LdifEditor;
import org.eclipse.swt.widgets.Composite;


/**
 * An entry editor that uses LDIF format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MultiTabLdifEntryEditor extends LdifEditor
{

    public MultiTabLdifEntryEditor()
    {
        super();

        // use our own document provider that saves changes to the directory
        setDocumentProvider( new MultiTabLdifEntryEditorDocumentProvider() );
    }


    @Override
    public void createPartControl( Composite parent )
    {
        // don't show the tool bar
        showToolBar = false;

        super.createPartControl( parent );
    }


    @Override
    public boolean isSaveAsAllowed()
    {
        // Allowing "Save As..." requires an IPathEditorInput.
        // Would makes things much more complex, maybe we could add this later.
        return false;
    }

}
