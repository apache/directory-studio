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

package org.apache.directory.ldapstudio.schemas.view.editors.schema;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;


/**
 * This class is the Schema Editor main class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaFormEditor extends FormEditor
{
    /** The logger */
    private static Logger logger = Logger.getLogger( SchemaFormEditor.class );

    /** The ID of the Editor */
    public static final String ID = Activator.PLUGIN_ID + ".view.schemaformeditor"; //$NON-NLS-1$

    /** The Overview Page */
    private SchemaFormEditorOverviewPage overview;

    /** The Source Code page */
    private SchemaFormEditorSourceCodePage sourceCode;

    /** The associated schema */
    private Schema schema;


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        setSite( site );
        setInput( input );
        setPartName( input.getName() );

        schema = ( ( SchemaFormEditorInput ) getEditorInput() ).getSchema();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages()
    {
        try
        {
            overview = new SchemaFormEditorOverviewPage( this );
            addPage( overview );
            sourceCode = new SchemaFormEditorSourceCodePage( this );
            addPage( sourceCode );
        }
        catch ( PartInitException e )
        {
            logger.debug( "error when adding pages" ); //$NON-NLS-1$
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave( IProgressMonitor monitor )
    {
        // There's nothing to save
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


    /**
     * Gets the associated schema.
     *
     * @return
     *      the associated schema
     */
    public Schema getSchema()
    {
        return schema;
    }
}
