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

package org.apache.directory.ldapstudio.schemas.view.editors;


import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemaSourceViewer;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class is the Source Code Page of the Schema Editor
 */
public class SchemaFormEditorSourceCodePage extends FormPage
{
    private Schema schema;
    private SchemaSourceViewer schemaSourceViewer;


    /**
     * Default constructor
     * @param editor
     * @param id
     * @param title
     */
    public SchemaFormEditorSourceCodePage( FormEditor editor, String id, String title )
    {
        super( editor, id, title );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout();
        form.getBody().setLayout( layout );
        toolkit.paintBordersFor( form.getBody() );

        // Getting the input and the objectClass
        SchemaFormEditorInput input = ( SchemaFormEditorInput ) getEditorInput();
        schema = input.getSchema();

        // SOURCE CODE Field
        schemaSourceViewer = new SchemaSourceViewer( form.getBody(), null, null, false, SWT.H_SCROLL | SWT.V_SCROLL );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.heightHint = 10;
        schemaSourceViewer.getTextWidget().setLayoutData( gd );
        schemaSourceViewer.getTextWidget().setEditable( false );
        
        
        // set text font
        Font font = JFaceResources.getFont( JFaceResources.TEXT_FONT );
        schemaSourceViewer.getTextWidget().setFont( font );
        
        IDocument document = new Document();
        schemaSourceViewer.setDocument( document );
        
        // Initialization from the "input" object class
        initFieldsContentFromInput();
    }


    private void initFieldsContentFromInput()
    {
        // SOURCE CODE Field
        AttributeType[] attributeTypes = schema.getAttributeTypesAsArray();
        ObjectClass[] objectClasses = schema.getObjectClassesAsArray();

        StringBuffer sb = new StringBuffer();

        if ( attributeTypes.length != 0 )
        {
            for ( int i = 0; i < attributeTypes.length; i++ )
            {
                AttributeType attributeType = attributeTypes[i];
                sb.append( attributeType.write() );
                sb.append( "\n" ); //$NON-NLS-1$
            }
        }
        if ( objectClasses.length != 0 )
        {
            for ( int i = 0; i < objectClasses.length; i++ )
            {
                ObjectClass objectClass = objectClasses[i];
                sb.append( objectClass.write() );
                sb.append( "\n" ); //$NON-NLS-1$
            }
        }
        
        schemaSourceViewer.getDocument().set( sb.toString() );
    }
}
