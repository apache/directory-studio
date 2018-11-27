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


import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerAdapter;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.io.OpenLdapSchemaFileExporter;
import org.apache.directory.studio.schemaeditor.view.widget.SchemaSourceViewer;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


/**
 * This class is the Source Code Page of the Schema Editor.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaEditorSourceCodePage extends FormPage
{
    /** The page ID */
    public static final String ID = SchemaEditor.ID + "sourceCode"; //$NON-NLS-1$

    /** The flag to indicate if the page has been initialized */
    private boolean initialized = false;

    /** The associated schema */
    private Schema schema;

    // UI Field
    private SchemaSourceViewer schemaSourceViewer;

    // Listerner
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerAdapter()
    {
        /**
         * {@inheritDoc}
         */
        public void attributeTypeAdded( AttributeType at )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void attributeTypeModified( AttributeType at )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void attributeTypeRemoved( AttributeType at )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void matchingRuleAdded( MatchingRule mr )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void matchingRuleModified( MatchingRule mr )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void matchingRuleRemoved( MatchingRule mr )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void objectClassAdded( ObjectClass oc )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void objectClassModified( ObjectClass oc )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void objectClassRemoved( ObjectClass oc )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void syntaxAdded( LdapSyntax syntax )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void syntaxModified( LdapSyntax syntax )
        {
            refreshUI();
        }


        /**
         * {@inheritDoc}
         */
        public void syntaxRemoved( LdapSyntax syntax )
        {
            refreshUI();
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
        super( editor, ID, Messages.getString( "SchemaEditorSourceCodePage.SourceCode" ) ); //$NON-NLS-1$
        Activator.getDefault().getSchemaHandler().addListener( schemaHandlerListener );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        schema = ( ( SchemaEditor ) getEditor() ).getSchema();

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

        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( form, PluginConstants.PLUGIN_ID + "." + "schema_editor" ); //$NON-NLS-1$ //$NON-NLS-2$

        initialized = true;
    }


    /**
     * Fills in the fields of the User Interface.
     */
    private void fillInUiFields()
    {
        schemaSourceViewer.getDocument().set( OpenLdapSchemaFileExporter.toSourceCode( schema ) );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        Activator.getDefault().getSchemaHandler().removeListener( schemaHandlerListener );

        super.dispose();
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        if ( initialized )
        {
            fillInUiFields();
        }
    }
}
