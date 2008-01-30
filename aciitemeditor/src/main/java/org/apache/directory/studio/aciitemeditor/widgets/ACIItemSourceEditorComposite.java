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
package org.apache.directory.studio.aciitemeditor.widgets;


import java.text.ParseException;

import org.apache.directory.shared.ldap.aci.ACIItem;
import org.apache.directory.shared.ldap.aci.ACIItemParser;
import org.apache.directory.studio.aciitemeditor.ACIItemValueWithContext;
import org.apache.directory.studio.aciitemeditor.Activator;
import org.apache.directory.studio.aciitemeditor.sourceeditor.ACISourceViewerConfiguration;
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


/**
 * This composite contains the source editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemSourceEditorComposite extends Composite
{

    /** The source editor */
    private SourceViewer sourceEditor;

    /** The source editor configuration. */
    private SourceViewerConfiguration configuration;


    /**
     * Creates a new instance of ACIItemSourceEditorComposite.
     *
     * @param parent
     * @param style
     */
    public ACIItemSourceEditorComposite( Composite parent, int style )
    {
        super( parent, style );
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
        configuration = new ACISourceViewerConfiguration();
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
    public void setInput( String input ) throws ParseException
    {
        ACIItemParser parser = Activator.getDefault().getACIItemParser();
        parser.parse( input );

        forceSetInput( input );
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

        ACIItemParser parser = Activator.getDefault().getACIItemParser();
        ACIItem aciItem = parser.parse( input );

        String aci = "";
        if ( aciItem != null )
        {
            aci = aciItem.toString();
        }
        return aci;
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
    public void setContext( ACIItemValueWithContext context )
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
