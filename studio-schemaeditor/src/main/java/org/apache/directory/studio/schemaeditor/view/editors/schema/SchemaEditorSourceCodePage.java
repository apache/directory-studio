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

package org.apache.directory.studio.schemaeditor.view.editors.schema;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaListener;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;
import org.apache.directory.studio.schemaeditor.model.io.OpenLdapSchemaFileExporter;
import org.apache.directory.studio.schemaeditor.view.widget.SchemaSourceViewer;
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
 * This class is the Source Code Page of the Schema Editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaEditorSourceCodePage extends FormPage
{
    /** The page ID */
    public static final String ID = SchemaEditor.ID + "sourceCode"; //$NON-NLS-1$

    /** The page title */
    public static final String TITLE = "Source Code";

    /** The associated schema */
    private Schema schema;

    // UI Field
    private SchemaSourceViewer schemaSourceViewer;

    // Listerner
    private SchemaListener schemaListener = new SchemaListener()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#attributeTypeAdded(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#attributeTypeModified(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeModified( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#attributeTypeRemoved(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#matchingRuleAdded(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleAdded( MatchingRuleImpl mr )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#matchingRuleModified(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleModified( MatchingRuleImpl mr )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#matchingRuleRemoved(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleRemoved( MatchingRuleImpl mr )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#objectClassAdded(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassAdded( ObjectClassImpl oc )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#objectClassModified(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassModified( ObjectClassImpl oc )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#objectClassRemoved(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassRemoved( ObjectClassImpl oc )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#syntaxAdded(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxAdded( SyntaxImpl syntax )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#syntaxModified(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxModified( SyntaxImpl syntax )
        {
            fillInUiFields();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaListener#syntaxRemoved(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxRemoved( SyntaxImpl syntax )
        {
            fillInUiFields();
        }
    };


    /**
     * Creates a new instance of SchemaFormEditorSourceCodePage.
     * 
     * @param editor
     *      the associated editor
     */
    public SchemaEditorSourceCodePage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        schema = ( ( SchemaEditor ) getEditor() ).getSchema();
        Activator.getDefault().getSchemaHandler().addListener( schema, schemaListener );

        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout();
        form.getBody().setLayout( layout );
        toolkit.paintBordersFor( form.getBody() );

        // SOURCE CODE Field
        schemaSourceViewer = new SchemaSourceViewer( form.getBody(), null, null, false, SWT.BORDER | SWT.H_SCROLL
            | SWT.V_SCROLL );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.heightHint = 10;
        schemaSourceViewer.getTextWidget().setLayoutData( gd );
        schemaSourceViewer.getTextWidget().setEditable( false );

        // set text font
        Font font = JFaceResources.getFont( JFaceResources.TEXT_FONT );
        schemaSourceViewer.getTextWidget().setFont( font );

        IDocument document = new Document();
        schemaSourceViewer.setDocument( document );

        // Initializes the UI from the schema
        fillInUiFields();
    }


    /**
     * Fills in the fields of the User Interface.
     */
    private void fillInUiFields()
    {
        schemaSourceViewer.getDocument().set( OpenLdapSchemaFileExporter.toSourceCode( schema ) );
    }
}
