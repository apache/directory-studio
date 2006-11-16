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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class is the Source Code Page of the Attribute Type Editor
 */
public class AttributeTypeFormEditorSourceCodePage extends FormPage
{
    private AttributeType attributeType;
    private Text sourceCode_text;


    /**
     * Default constructor
     * @param editor
     * @param id
     * @param title
     */
    public AttributeTypeFormEditorSourceCodePage( FormEditor editor, String id, String title )
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
        AttributeTypeFormEditorInput input = ( AttributeTypeFormEditorInput ) getEditorInput();
        attributeType = ( AttributeType ) input.getAttributeType();

        // SOURCE CODE Field
        sourceCode_text = toolkit.createText( form.getBody(), "", SWT.MULTI ); //$NON-NLS-1$
        sourceCode_text.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        sourceCode_text.setEditable( false );

        // Initialization from the "input" object class
        initFieldsContentFromInput();
    }


    private void initFieldsContentFromInput()
    {
        // SOURCE CODE Field
        sourceCode_text.setText( attributeType.write() );
    }


    /**
     * Refreshes the source code field of the page
     */
    public void refresh()
    {
        if ( sourceCode_text != null )
        {
            initFieldsContentFromInput();
        }
    }
}
