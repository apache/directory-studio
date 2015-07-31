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
package org.apache.directory.studio.openldap.config.acl.widgets;


import java.text.ParseException;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclItem;
import org.apache.directory.studio.openldap.config.acl.model.OpenLdapAclParser;
import org.apache.directory.studio.openldap.config.acl.sourceeditor.OpenLdapAclSourceViewerConfiguration;


/**
 * This composite contains the source editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclSourceEditorComposite extends Composite
{
    /** The source editor */
    private SourceViewer sourceEditor;

    /** The source editor configuration. */
    private SourceViewerConfiguration configuration;

    /** The ACL context */
    private OpenLdapAclValueWithContext context;

    /** The ACL parser */
    private OpenLdapAclParser parser = new OpenLdapAclParser();

    /**
     * Creates a new instance of ACIItemSourceEditorComposite.
     *
     * @param parent
     * @param style
     */
    public OpenLdapAclSourceEditorComposite( Composite parent, OpenLdapAclValueWithContext context, int style )
    {
        super( parent, style );
        
        this.context = context;
        setLayout( new FillLayout() );

        createSourceEditor();
    }


    /**
     * Creates and configures the source editor.
     *
     */
    private void createSourceEditor()
    {
        // create source editor
        sourceEditor = new SourceViewer( this, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );

        // setup basic configuration
        configuration = new OpenLdapAclSourceViewerConfiguration();
        sourceEditor.configure( configuration );

        // set text font
        Font font = JFaceResources.getFont( JFaceResources.TEXT_FONT );
        sourceEditor.getTextWidget().setFont( font );

        // setup document
        IDocument document = new Document();
        sourceEditor.setDocument( document );
    }


    /**
     * Sets the input to the source editor.
     * A syntax check is performed before setting the input, an 
     * invalid syntax causes a ParseException.
     *
     * @param input the valid string representation of the ACI item
     * @throws ParseException it the syntax check fails.
     */
    public void refresh()
    {
        forceSetInput( context.getAclItem().toString() );
    }


    /**
     * Set the input to the source editor without a syntax check.
     *
     * @param input The string representation of the ACI item, may be invalid
     */
    public void forceSetInput( String input )
    {
        sourceEditor.getDocument().set( input );

        // format
        IRegion region = new Region( 0, sourceEditor.getDocument().getLength() );
        configuration.getContentFormatter( sourceEditor ).format( sourceEditor.getDocument(), region );
    }


    /**
     * Returns the string representation of the ACI item.
     * A syntax check is performed before returning the input, an 
     * invalid syntax causes a ParseException.
     *
     * @return the valid string representation of the ACI item
     * @throws ParseException it the syntax check fails.
     */
    public String getInput() throws ParseException
    {
        String input = forceGetInput();

        // strip new lines
        input = input.replaceAll( "\\n", " " ); //$NON-NLS-1$ //$NON-NLS-2$
        input = input.replaceAll( "\\r", " " ); //$NON-NLS-1$ //$NON-NLS-2$

        AclItem aclItem = parser.parse( input );

        String acl = "";
        
        if ( aclItem != null )
        {
            acl = aclItem.toString();
        }
        
        return acl;
    }


    /**
     * Returns the string representation of the ACI item without syntax check.
     * In other words only the text in the source editor is returned.
     *
     * @return the string representation of the ACI item, may be invalid
     */
    public String forceGetInput()
    {
        return sourceEditor.getDocument().get();
    }


    /**
     * Sets the context.
     * 
     * @param context the context
     */
    public void setContext( OpenLdapAclValueWithContext context )
    {
    }


    /**
     * Formats the content.
     */
    public void format()
    {
        IRegion region = new Region( 0, sourceEditor.getDocument().getLength() );
        configuration.getContentFormatter( sourceEditor ).format( sourceEditor.getDocument(), region );
    }
}
