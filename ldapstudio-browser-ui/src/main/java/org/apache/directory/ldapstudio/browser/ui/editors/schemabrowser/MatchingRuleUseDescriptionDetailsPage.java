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
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleUseDescription;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


public class MatchingRuleUseDescriptionDetailsPage extends SchemaDetailsPage
{

    private Section mainSection;

    private Section flagSection;

    private Section appliesSection;

    private Text numericOidText;

    private Hyperlink nameLink;

    private Text descText;

    private Label isObsoleteText;

    private Hyperlink[] appliesLinks;


    public MatchingRuleUseDescriptionDetailsPage( SchemaBrowser schemaBrowser, FormToolkit toolkit )
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

        flagSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        flagSection.setText( "Flags" );
        flagSection.marginWidth = 0;
        flagSection.marginHeight = 0;
        flagSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( flagSection );

        Composite flagClient = toolkit.createComposite( flagSection, SWT.WRAP );
        GridLayout flagLayout = new GridLayout();
        flagLayout.numColumns = 1;
        flagLayout.marginWidth = 0;
        flagLayout.marginHeight = 0;
        flagClient.setLayout( flagLayout );
        flagSection.setClient( flagClient );

        isObsoleteText = toolkit.createLabel( flagClient, "Obsolete", SWT.CHECK );
        isObsoleteText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        isObsoleteText.setEnabled( false );

        appliesSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        appliesSection.setText( "Applies" );
        appliesSection.marginWidth = 0;
        appliesSection.marginHeight = 0;
        appliesSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( appliesSection );
        appliesSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        super.createRawSection();
    }


    public void matchingRuleUseDescriptionSelected( MatchingRuleUseDescription mrud )
    {
        this.createMainContent( mrud );
        isObsoleteText.setEnabled( mrud != null && mrud.isObsolete() );
        this.createAppliesContents( mrud );
        super.createRawContents( mrud );

        this.detailForm.reflow( true );
    }


    private void createMainContent( MatchingRuleUseDescription mrud )
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

        if ( mrud != null )
        {
            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( mrud.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Matching rule names:", SWT.NONE );
            nameLink = toolkit.createHyperlink( mainClient, "", SWT.WRAP );
            nameLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            nameLink.addHyperlinkListener( new HyperlinkAdapter()
            {
                public void linkActivated( HyperlinkEvent e )
                {
                    SchemaBrowser.select( e.getHref() );
                }
            } );

            MatchingRuleDescription mrd = mrud.getSchema().hasMatchingRuleDescription( mrud.getNumericOID() ) ? mrud
                .getSchema().getMatchingRuleDescription( mrud.getNumericOID() ) : null;
            nameLink.setText( getNonNullString( mrd != null ? mrd.toString() : mrud.toString() ) );
            nameLink.setHref( mrd );
            nameLink.setUnderlined( mrd != null );
            nameLink.setEnabled( mrd != null );

            toolkit.createLabel( mainClient, "Descripton:", SWT.NONE );
            descText = toolkit.createText( mainClient, getNonNullString( mrud.getDesc() ), SWT.WRAP | SWT.MULTI );
            GridData gd = new GridData( GridData.FILL_HORIZONTAL );
            gd.widthHint = detailForm.getForm().getSize().x - labelWidth - 60;
            descText.setLayoutData( gd );
            descText.setEditable( false );
        }

        mainSection.layout();
    }


    private void createAppliesContents( MatchingRuleUseDescription mrud )
    {
        if ( appliesSection.getClient() != null )
        {
            appliesSection.getClient().dispose();
        }

        Composite appliesClient = toolkit.createComposite( appliesSection, SWT.WRAP );
        appliesClient.setLayout( new GridLayout() );
        appliesSection.setClient( appliesClient );

        if ( mrud != null )
        {
            String[] names = mrud.getAppliesAttributeTypeDescriptionOIDs();
            if ( names != null && names.length > 0 )
            {
                appliesSection.setText( "Applies (" + names.length + ")" );
                appliesLinks = new Hyperlink[names.length];
                for ( int i = 0; i < names.length; i++ )
                {
                    if ( mrud.getSchema().hasAttributeTypeDescription( names[i] ) )
                    {
                        AttributeTypeDescription appliesAtd = mrud.getSchema().getAttributeTypeDescription( names[i] );
                        appliesLinks[i] = toolkit.createHyperlink( appliesClient, appliesAtd.toString(), SWT.WRAP );
                        appliesLinks[i].setHref( appliesAtd );
                        appliesLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        appliesLinks[i].setUnderlined( true );
                        appliesLinks[i].setEnabled( true );
                        appliesLinks[i].addHyperlinkListener( new HyperlinkAdapter()
                        {
                            public void linkActivated( HyperlinkEvent e )
                            {
                                SchemaBrowser.select( e.getHref() );
                            }
                        } );
                    }
                    else
                    {
                        appliesLinks[i] = toolkit.createHyperlink( appliesClient, names[i], SWT.WRAP );
                        appliesLinks[i].setHref( null );
                        appliesLinks[i].setUnderlined( false );
                        appliesLinks[i].setEnabled( false );
                    }
                }
            }
            else
            {
                appliesSection.setText( "Applies (0)" );
                appliesLinks = new Hyperlink[0];
                Text usedFromText = toolkit.createText( appliesClient, getNonNullString( null ), SWT.NONE );
                usedFromText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                usedFromText.setEditable( false );
            }
        }
        else
        {
            appliesSection.setText( "Applies" );
        }

        appliesSection.layout();
    }

}
