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
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleDescription;

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


public class MatchingRuleDescriptionDetailsPage extends SchemaDetailsPage
{

    private Section mainSection;

    private Section flagSection;

    private Section syntaxSection;

    private Section usedFromSection;

    private Text numericOidText;

    private Text nameText;

    private Text descText;

    private Label isObsoleteText;

    private Text syntaxText;

    private Hyperlink syntaxLink;

    private Hyperlink[] usedFromLinks;


    public MatchingRuleDescriptionDetailsPage( SchemaBrowser schemaBrowser, FormToolkit toolkit )
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

        syntaxSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        syntaxSection.setText( "Syntax" );
        syntaxSection.marginWidth = 0;
        syntaxSection.marginHeight = 0;
        syntaxSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( syntaxSection );

        Composite syntaxClient = toolkit.createComposite( syntaxSection, SWT.WRAP );
        GridLayout syntaxLayout = new GridLayout();
        syntaxLayout.numColumns = 2;
        syntaxLayout.marginWidth = 0;
        syntaxLayout.marginHeight = 0;
        syntaxClient.setLayout( syntaxLayout );
        syntaxSection.setClient( syntaxClient );

        toolkit.createLabel( syntaxClient, "Syntax OID:", SWT.NONE );
        syntaxLink = toolkit.createHyperlink( syntaxClient, "", SWT.WRAP );
        syntaxLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        syntaxLink.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                SchemaBrowser.select( e.getHref() );
            }
        } );

        toolkit.createLabel( syntaxClient, "Syntax Description:", SWT.NONE );
        syntaxText = toolkit.createText( syntaxClient, "", SWT.NONE );
        syntaxText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        syntaxText.setEditable( false );

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


    public void matchingRuleDescriptionSelected( MatchingRuleDescription mrd )
    {
        this.createMainContent( mrd );

        isObsoleteText.setEnabled( mrd != null && mrd.isObsolete() );

        String lsdOid = null;
        LdapSyntaxDescription lsd = null;
        if ( mrd != null )
        {
            lsdOid = mrd.getSyntaxDescriptionNumericOID();
            if ( lsdOid != null && mrd.getSchema().hasLdapSyntaxDescription( lsdOid ) )
            {
                lsd = mrd.getSchema().getLdapSyntaxDescription( lsdOid );
            }
        }
        syntaxLink.setText( getNonNullString( lsd != null ? lsd.getNumericOID() : lsdOid ) );
        syntaxLink.setHref( lsd );
        syntaxLink.setUnderlined( lsd != null );
        syntaxLink.setEnabled( lsd != null );
        syntaxText.setText( getNonNullString( lsd != null ? lsd.getDesc() : null ) );
        syntaxSection.layout();

        this.createUsedFromContents( mrd );

        super.createRawContents( mrd );

        this.usedFromSection.redraw();
        this.usedFromSection.update();
        this.usedFromSection.layout();
        this.detailForm.reflow( true );
    }


    private void createMainContent( MatchingRuleDescription mrd )
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

        if ( mrd != null )
        {
            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( mrd.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Matching rule names:", SWT.NONE );
            nameText = toolkit.createText( mainClient, getNonNullString( mrd.toString() ), SWT.NONE );
            nameText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            nameText.setEditable( false );

            toolkit.createLabel( mainClient, "Descripton:", SWT.NONE );
            descText = toolkit.createText( mainClient, getNonNullString( mrd.getDesc() ), SWT.WRAP | SWT.MULTI );
            GridData gd = new GridData( GridData.FILL_HORIZONTAL );
            gd.widthHint = detailForm.getForm().getSize().x - labelWidth - 60;
            descText.setLayoutData( gd );
            descText.setEditable( false );
        }

        mainSection.layout();
    }


    private void createUsedFromContents( MatchingRuleDescription mrd )
    {

        if ( usedFromSection.getClient() != null )
        {
            usedFromSection.getClient().dispose();
        }

        Composite usedFromClient = toolkit.createComposite( usedFromSection, SWT.WRAP );
        usedFromClient.setLayout( new GridLayout() );
        usedFromSection.setClient( usedFromClient );

        if ( mrd != null )
        {
            AttributeTypeDescription[] usedFromATDs = mrd.getUsedFromAttributeTypeDescriptions();
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
