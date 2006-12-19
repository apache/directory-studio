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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


public class LdapSyntaxDescriptionDetailsPage extends SchemaDetailsPage
{

    private Section mainSection;

    private Section usedFromSection;

    private Text numericOidText;

    private Text descText;

    private Hyperlink[] usedFromLinks;


    public LdapSyntaxDescriptionDetailsPage( SchemaBrowser schemaBrowser, FormToolkit toolkit )
    {
        super( schemaBrowser, toolkit );
    }


    public void createContents( final ScrolledForm detailForm )
    {

        this.detailForm = detailForm;
        detailForm.getBody().setLayout( new GridLayout() );

        mainSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        mainSection.setText( "Details" );
        mainSection.marginWidth = 0;
        mainSection.marginHeight = 0;
        mainSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( mainSection );

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

        super.createRawSection();
    }


    public void ldapSyntacDescriptionSelected( LdapSyntaxDescription lsd )
    {
        if ( this.detailForm != null && !this.detailForm.isDisposed() )
        {
            this.createMainContent( lsd );
            this.createUsedFromContents( lsd );
            super.createRawContents( lsd );

            this.detailForm.reflow( true );
        }
    }


    private void createMainContent( LdapSyntaxDescription lsd )
    {

        int labelWidth = 100;

        if ( mainSection.getClient() != null )
        {
            if ( mainSection.getClient() instanceof Composite )
            {
                Composite client = ( Composite ) mainSection.getClient();
                if ( client.getChildren() != null && client.getChildren().length > 0 )
                {
                    labelWidth = client.getChildren()[0].getSize().x;
                }
            }
            mainSection.getClient().dispose();
        }

        Composite mainClient = toolkit.createComposite( mainSection, SWT.WRAP );
        GridLayout mainLayout = new GridLayout( 2, false );
        mainClient.setLayout( mainLayout );
        mainSection.setClient( mainClient );

        if ( lsd != null )
        {
            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( lsd.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Descripton:", SWT.NONE );
            descText = toolkit.createText( mainClient, getNonNullString( lsd.getDesc() ), SWT.WRAP | SWT.MULTI );
            GridData gd = new GridData( GridData.FILL_HORIZONTAL );
            gd.widthHint = detailForm.getForm().getSize().x - labelWidth - 60;
            descText.setLayoutData( gd );
            descText.setEditable( false );
        }

        mainSection.layout();

    }


    private void createUsedFromContents( LdapSyntaxDescription lsd )
    {
        if ( usedFromSection.getClient() != null && !usedFromSection.getClient().isDisposed() )
        {
            usedFromSection.getClient().dispose();
        }

        Composite usedFromClient = toolkit.createComposite( usedFromSection, SWT.WRAP );
        usedFromClient.setLayout( new GridLayout() );
        usedFromSection.setClient( usedFromClient );

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
                    usedFromLinks[i].addHyperlinkListener( new HyperlinkAdapter()
                    {
                        public void linkActivated( HyperlinkEvent e )
                        {
                            SchemaBrowser.select( e.getHref() );
                        }
                    } );
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
