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


import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.MatchingRuleDescription;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * The MatchingRuleDescriptionDetailsPage displays the details of an
 * matching rule description.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MatchingRuleDescriptionDetailsPage extends SchemaDetailsPage
{

    /** The main section, contains oid, names and desc */
    private Section mainSection;

    /** The numeric oid field */
    private Text numericOidText;

    /** The names field */
    private Text namesText;

    /** The description field */
    private Text descText;

    /** The flag section, contains obsolete */
    private Section flagSection;

    /** The obsolete field */
    private Label isObsoleteText;

    /** The syntax section, contains syntax description and a link to the syntax */
    private Section syntaxSection;

    /** The syntax description field */
    private Text syntaxDescText;

    /** The link to the syntax */
    private Hyperlink syntaxLink;

    /** The used from section, contains links to attribute types */
    private Section usedFromSection;

    /** The links to attribute types using the matching rule */
    private Hyperlink[] usedFromLinks;


    /**
     * Creates a new instance of MatchingRuleDescriptionDetailsPage.
     *
     * @param schemaPage the master schema page
     * @param toolkit the toolkit used to create controls
     */
    public MatchingRuleDescriptionDetailsPage( SchemaPage scheamPage, FormToolkit toolkit )
    {
        super( scheamPage, toolkit );
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

        // create flag section
        flagSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        flagSection.setText( "Flags" );
        flagSection.marginWidth = 0;
        flagSection.marginHeight = 0;
        flagSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( flagSection );

        // create flag content
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

        // create syntax section
        syntaxSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        syntaxSection.setText( "Syntax" );
        syntaxSection.marginWidth = 0;
        syntaxSection.marginHeight = 0;
        syntaxSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( syntaxSection );

        // create syntax content
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
        syntaxLink.addHyperlinkListener( this );

        toolkit.createLabel( syntaxClient, "Syntax Description:", SWT.NONE );
        syntaxDescText = toolkit.createText( syntaxClient, "", SWT.NONE );
        syntaxDescText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        syntaxDescText.setEditable( false );

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

        // create raw section
        createRawSection();
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( Object input )
    {
        MatchingRuleDescription mrd = null;
        if ( input instanceof MatchingRuleDescription )
        {
            mrd = ( MatchingRuleDescription ) input;
        }

        // create main content
        createMainContent( mrd );

        // set flag
        isObsoleteText.setEnabled( mrd != null && mrd.isObsolete() );

        // set syntax content
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
        syntaxDescText.setText( getNonNullString( lsd != null ? lsd.getDesc() : null ) );
        syntaxSection.layout();

        // create contents of dynamic sections
        createUsedFromContents( mrd );
        createRawContents( mrd );

        detailForm.reflow( true );
    }


    /**
     * Creates the content of the main section. It is newly created
     * on every input change to ensure a proper layout of 
     * multilined descriptions. 
     *
     * @param mrd the matching rule description
     */
    private void createMainContent( MatchingRuleDescription mrd )
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
        if ( mrd != null )
        {
            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( mrd.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Matching rule names:", SWT.NONE );
            namesText = toolkit.createText( mainClient, getNonNullString( mrd.toString() ), SWT.NONE );
            namesText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            namesText.setEditable( false );

            toolkit.createLabel( mainClient, "Descripton:", SWT.NONE );
            descText = toolkit.createText( mainClient, getNonNullString( mrd.getDesc() ), SWT.WRAP | SWT.MULTI );
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
     * @param mrd the matching rule description
     */
    private void createUsedFromContents( MatchingRuleDescription mrd )
    {
        // dispose old content
        if ( usedFromSection.getClient() != null )
        {
            usedFromSection.getClient().dispose();
        }

        // create new client
        Composite usedFromClient = toolkit.createComposite( usedFromSection, SWT.WRAP );
        usedFromClient.setLayout( new GridLayout() );
        usedFromSection.setClient( usedFromClient );

        // create new content
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
