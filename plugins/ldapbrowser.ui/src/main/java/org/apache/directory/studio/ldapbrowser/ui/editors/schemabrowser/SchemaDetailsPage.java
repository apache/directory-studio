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

package org.apache.directory.studio.ldapbrowser.ui.editors.schemabrowser;


import java.util.List;

import org.apache.directory.api.ldap.model.schema.AbstractSchemaObject;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * A base implementation used from all schema detail pages.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class SchemaDetailsPage implements IHyperlinkListener
{

    /** The raw section, displays the schema attibute value */
    protected Section rawSection;

    /** The text with the schema attribute value */
    protected Text rawText;

    /** The toolkit used to create controls */
    protected FormToolkit toolkit;

    /** The master schema page */
    protected SchemaPage schemaPage;

    /** The detail page form */
    protected ScrolledForm detailForm;


    /**
     * Creates a new instance of SchemaDetailsPage.
     *
     * @param schemaPage the master schema page
     * @param toolkit the toolkit used to create controls
     */
    protected SchemaDetailsPage( SchemaPage schemaPage, FormToolkit toolkit )
    {
        this.schemaPage = schemaPage;
        this.toolkit = toolkit;
    }


    /**
     * Disposes this details page.
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void linkActivated( HyperlinkEvent e )
    {
        Object obj = e.getHref();
        if ( obj instanceof AbstractSchemaObject )
        {
            schemaPage.getSchemaBrowser().setInput(
                new SchemaBrowserInput( schemaPage.getConnection(), ( AbstractSchemaObject ) obj ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void linkEntered( HyperlinkEvent e )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void linkExited( HyperlinkEvent e )
    {
    }


    /**
     * Sets the input of this details page.
     *
     * @param input the input
     */
    public abstract void setInput( Object input );


    /**
     * Creates the contents of the details page.
     *
     * @param detailForm the parent
     */
    protected abstract void createContents( final ScrolledForm detailForm );


    /**
     * Creates the raw content section.
     */
    protected void createRawSection()
    {
        rawSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        rawSection.setText( Messages.getString( "SchemaDetailsPage.RawSchemaDefinition" ) ); //$NON-NLS-1$
        rawSection.marginWidth = 0;
        rawSection.marginHeight = 0;
        rawSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( rawSection );
        rawSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );
    }


    /**
     * Creates the contents of the raw section.
     *
     * @param schemaPart the schema part to display
     */
    protected void createRawContents( AbstractSchemaObject asd )
    {

        if ( rawSection.getClient() != null && !rawSection.getClient().isDisposed() )
        {
            rawSection.getClient().dispose();
        }

        Composite client = toolkit.createComposite( rawSection, SWT.WRAP );
        client.setLayout( new GridLayout() );
        rawSection.setClient( client );

        if ( asd != null )
        {
            rawText = toolkit.createText( client, getNonNullString( SchemaUtils.getLdifLine( asd ) ), SWT.WRAP
                | SWT.MULTI );
            GridData gd2 = new GridData( GridData.FILL_HORIZONTAL );
            gd2.widthHint = detailForm.getForm().getSize().x - 100 - 60;
            // detailForm.getForm().getVerticalBar().getSize().x
            // gd2.widthHint = 10;
            rawText.setLayoutData( gd2 );
            rawText.setEditable( false );
        }

        rawSection.layout();

    }


    /**
     * Gets the schema.
     * 
     * @return the schema
     */
    protected Schema getSchema()
    {
        return schemaPage.getConnection().getSchema();
    }


    /**
     * Helper method, return a dash "-" if the given string is null. 
     *
     * @param s the string
     * @return the given string or a dash "-" if the given string is null.
     */
    protected String getNonNullString( String s )
    {
        return s == null ? "-" : s; //$NON-NLS-1$
    }


    /**
     * Helper method, return a dash "-" if the given string is null. 
     *
     * @param s the string
     * @return the given string or a dash "-" if the given string is null.
     */
    private String getNonNullString( List<String> s )
    {
        if ( s == null || s.isEmpty() )
        {
            return "-"; //$NON-NLS-1$
        }

        return s.get( 0 );
    }

}
