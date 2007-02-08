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


import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaElement;
import org.apache.directory.ldapstudio.schemas.model.SchemaElementListener;
import org.apache.directory.ldapstudio.schemas.model.Schema.SchemaType;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemaSourceViewer;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;
import org.apache.directory.server.core.tools.schema.OpenLdapSchemaParser;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class is the Source Code Page of the Object Class Editor
 */
public class ObjectClassFormEditorSourceCodePage extends FormPage implements SchemaElementListener
{
    private ObjectClass modifiedObjectClass;
    private SchemaSourceViewer schemaSourceViewer;
    
    private boolean canLeaveThePage = true;


    /**
     * Default constructor
     * @param editor
     * @param id
     * @param title
     */
    public ObjectClassFormEditorSourceCodePage( FormEditor editor, String id, String title )
    {
        super( editor, id, title );
        setTitleImage( AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, IImageKeys.ATTRIBUTE_TYPE_NEW_WIZARD ).createImage() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        form.getBody().setLayout( layout );
        toolkit.paintBordersFor( form.getBody() );

        modifiedObjectClass = ( ( ObjectClassFormEditor ) getEditor() ).getModifiedObjectClass();
        modifiedObjectClass.addListener( this );

        // SOURCE CODE Field
        schemaSourceViewer = new SchemaSourceViewer( form.getBody(), null, null, false, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.heightHint = 10;
        schemaSourceViewer.getTextWidget().setLayoutData( gd );
        if ( modifiedObjectClass.getOriginatingSchema().type == SchemaType.coreSchema )
        {
            schemaSourceViewer.setEditable( false );
        }
        schemaSourceViewer.getTextWidget().addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                canLeaveThePage = true;
                try
                {
                    ( (ObjectClassFormEditor) getEditor() ).setDirty( true );
                    OpenLdapSchemaParser parser = new OpenLdapSchemaParser();
                    parser.parse( schemaSourceViewer.getTextWidget().getText() );
                    
                    List objectclasses = parser.getObjectClassTypes();
                    if ( objectclasses.size() != 1 )
                    {
                        // Throw an exception and return
                    }
                    else
                    {
                        updateObjectClass( ( ObjectClassLiteral ) objectclasses.get( 0 ) );
                    }
                }
                catch ( IOException e1 )
                {
                    canLeaveThePage = false;
                }
                catch ( ParseException exception )
                {
                    canLeaveThePage = false;
                    System.err.println( exception.getMessage() );
                }
            }
        });
        //toolkit.createLabel( form.getBody(), "" );
        
        // set text font
        Font font = JFaceResources.getFont( JFaceResources.TEXT_FONT );
        schemaSourceViewer.getTextWidget().setFont( font );
        
        IDocument document = new Document();
        schemaSourceViewer.setDocument( document );
        schemaSourceViewer.getAnnotationModel().connect( document );

        // Initialization from the "input" object class
        fillInUiFields();
    }


    private void fillInUiFields()
    {
        // SOURCE CODE Field
        schemaSourceViewer.getDocument().set( modifiedObjectClass.write() );
    }
    
    
    public boolean canLeaveThePage()
    {
        return canLeaveThePage;
    }


    public void schemaElementChanged( SchemaElement originatingSchemaElement, LDAPModelEvent e )
    {
        modifiedObjectClass.removeListener( this );
        fillInUiFields();
        modifiedObjectClass.addListener( this );
    }
    
    private void updateObjectClass( ObjectClassLiteral ocl )
    {
        modifiedObjectClass.removeListener( this );
        modifiedObjectClass.setClassType( ocl.getClassType() );
        modifiedObjectClass.setDescription( ocl.getDescription() );
        modifiedObjectClass.setMay( ocl.getMay() );
        modifiedObjectClass.setMust( ocl.getMust() );
        modifiedObjectClass.setNames( ocl.getNames() );
        modifiedObjectClass.setObsolete( ocl.isObsolete() );
        modifiedObjectClass.setOid( ocl.getOid() );
        modifiedObjectClass.setSuperiors( ocl.getSuperiors() );
        modifiedObjectClass.addListener( this );
    }
}
