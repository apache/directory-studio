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

package org.apache.directory.ldapstudio.browser.ui.editors.schemabrowser;


import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * The LdapSyntaxDescriptionDetailsPage displays the details of an
 * syntax description.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapSyntaxDescriptionDetailsPage extends SchemaDetailsPage
{

    /** The main section, contains oid and desc */
    private Section mainSection;

    /** The numeric oid field */
    private Text numericOidText;

    /** The description field */
    private Text descText;

    /** The used from section, contains links to attribute types */
    private Section usedFromSection;

    /** The links to attributes using the syntax */
    private Hyperlink[] usedFromLinks;


    /**
     * Creates a new instance of LdapSyntaxDescriptionDetailsPage.
     *
     * @param schemaPage the master schema page
     * @param toolkit the toolkit used to create controls
     */
    public LdapSyntaxDescriptionDetailsPage( SchemaPage schemaPage, FormToolkit toolkit )
    {
        super( schemaPage, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    public void createContents( final ScrolledForm detailForm )
    {

        this.detailForm = detailForm;
        detailForm.getBody().setLayout( new GridLayout() );

        // create main section
        mainSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        mainSection.setText( "Details" );
        mainSection.marginWidth = 0;
        mainSection.marginHeight = 0;
        mainSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( mainSection );

        // create used from section
        usedFromSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        usedFromSection.setText( "Used from" );
        usedFromSection.marginWidth = 0;
        usedFromSection.marginHeight = 0;
        usedFromSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( usedFromSection );
        usedFromSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create raw aection
        createRawSection();
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( Object input )
    {
        LdapSyntaxDescription lsd = null;
        if ( input instanceof LdapSyntaxDescription )
        {
            lsd = ( LdapSyntaxDescription ) input;
        }

        createMainContent( lsd );
        createUsedFromContents( lsd );
        createRawContents( lsd );

        detailForm.reflow( true );
    }


    /**
     * Creates the content of the main section. It is newly created
     * on every input change to ensure a proper layout of 
     * multilined descriptions. 
     *
     * @param lsd the syntax description
     */
    private void createMainContent( LdapSyntaxDescription lsd )
    {
        // dispose old content
        if ( mainSection.getClient() != null )
        {
            mainSection.getClient().dispose();
        }

        // create new client
        Composite mainClient = toolkit.createComposite( mainSection, SWT.WRAP );
        GridLayout mainLayout = new GridLayout( 2, false );
        mainClient.setLayout( mainLayout );
        mainSection.setClient( mainClient );

        // create new content
        if ( lsd != null )
        {
            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( lsd.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Descripton:", SWT.NONE );
            descText = toolkit.createText( mainClient, getNonNullString( lsd.getDesc() ), SWT.WRAP | SWT.MULTI );
            GridData gd = new GridData( GridData.FILL_HORIZONTAL );
            gd.widthHint = detailForm.getForm().getSize().x - 100 - 60;
            descText.setLayoutData( gd );
            descText.setEditable( false );
        }

        mainSection.layout();
    }


    /**
     * Creates the content of the used from section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param lsd the syntax description
     */
    private void createUsedFromContents( LdapSyntaxDescription lsd )
    {
        // dispose old content
        if ( usedFromSection.getClient() != null && !usedFromSection.getClient().isDisposed() )
        {
            usedFromSection.getClient().dispose();
        }

        // create new client
        Composite usedFromClient = toolkit.createComposite( usedFromSection, SWT.WRAP );
        usedFromClient.setLayout( new GridLayout() );
        usedFromSection.setClient( usedFromClient );

        // create content
        if ( lsd != null )
        {
            AttributeTypeDescription[] usedFromATDs = lsd.getUsedFromAttributeTypeDescription();
            if ( usedFromATDs != null && usedFromATDs.length > 0 )
            {
                usedFromSection.setText( "Used from (" + usedFromATDs.length + ")" );
                usedFromLinks = new Hyperlink[usedFromATDs.length];
                for ( int i = 0; i < usedFromATDs.length; i++ )
                {
                    usedFromLinks[i] = toolkit.createHyperlink( usedFromClient, usedFromATDs[i].toString(), SWT.WRAP );
                    usedFromLinks[i].setHref( usedFromATDs[i] );
                    usedFromLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    usedFromLinks[i].setUnderlined( true );
                    usedFromLinks[i].setEnabled( true );
                    usedFromLinks[i].addHyperlinkListener( this );
                }
            }
            else
            {
                usedFromSection.setText( "Used from (0)" );
                usedFromLinks = new Hyperlink[0];
                Text usedFromText = toolkit.createText( usedFromClient, getNonNullString( null ), SWT.NONE );
                usedFromText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                usedFromText.setEditable( false );
            }
        }
        else
        {
            usedFromSection.setText( "Used from" );
        }

        usedFromSection.layout();
    }

}
