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


import java.util.Collection;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.osgi.util.NLS;
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
        mainSection.setText( Messages.getString( "MatchingRuleDescriptionDetailsPage.Details" ) ); //$NON-NLS-1$
        mainSection.marginWidth = 0;
        mainSection.marginHeight = 0;
        mainSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( mainSection );

        // create flag section
        flagSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        flagSection.setText( Messages.getString( "MatchingRuleDescriptionDetailsPage.Flags" ) ); //$NON-NLS-1$
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

        isObsoleteText = toolkit.createLabel( flagClient, Messages
            .getString( "MatchingRuleDescriptionDetailsPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        isObsoleteText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        isObsoleteText.setEnabled( false );

        // create syntax section
        syntaxSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        syntaxSection.setText( Messages.getString( "MatchingRuleDescriptionDetailsPage.Syntax" ) ); //$NON-NLS-1$
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

        toolkit.createLabel( syntaxClient,
            Messages.getString( "MatchingRuleDescriptionDetailsPage.SyntaxOID" ), SWT.NONE ); //$NON-NLS-1$
        syntaxLink = toolkit.createHyperlink( syntaxClient, "", SWT.WRAP ); //$NON-NLS-1$
        syntaxLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        syntaxLink.addHyperlinkListener( this );

        toolkit.createLabel( syntaxClient,
            Messages.getString( "MatchingRuleDescriptionDetailsPage.SyntaxDescription" ), SWT.NONE ); //$NON-NLS-1$
        syntaxDescText = toolkit.createText( syntaxClient, "", SWT.NONE ); //$NON-NLS-1$
        syntaxDescText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        syntaxDescText.setEditable( false );

        // create used from section
        usedFromSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        usedFromSection.setText( Messages.getString( "MatchingRuleDescriptionDetailsPage.UsedFrom" ) ); //$NON-NLS-1$
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
        MatchingRule mrd = null;
        if ( input instanceof MatchingRule )
        {
            mrd = ( MatchingRule ) input;
        }

        // create main content
        createMainContent( mrd );

        // set flag
        isObsoleteText.setEnabled( mrd != null && mrd.isObsolete() );

        // set syntax content
        String lsdOid = null;
        LdapSyntax lsd = null;
        if ( mrd != null )
        {
            Schema schema = getSchema();
            lsdOid = mrd.getSyntaxOid();
            if ( lsdOid != null && schema.hasLdapSyntaxDescription( lsdOid ) )
            {
                lsd = schema.getLdapSyntaxDescription( lsdOid );
            }
        }
        syntaxLink.setText( getNonNullString( lsd != null ? lsd.getOid() : lsdOid ) );
        syntaxLink.setHref( lsd );
        syntaxLink.setUnderlined( lsd != null );
        syntaxLink.setEnabled( lsd != null );
        syntaxDescText.setText( getNonNullString( lsd != null ? lsd.getDescription() : null ) );
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
    private void createMainContent( MatchingRule mrd )
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
            toolkit.createLabel( mainClient,
                Messages.getString( "MatchingRuleDescriptionDetailsPage.NumericOID" ), SWT.NONE ); //$NON-NLS-1$
            numericOidText = toolkit.createText( mainClient, getNonNullString( mrd.getOid() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient,
                Messages.getString( "MatchingRuleDescriptionDetailsPage.MatchingRule" ), SWT.NONE ); //$NON-NLS-1$
            namesText = toolkit.createText( mainClient, getNonNullString( SchemaUtils.toString( mrd ) ), SWT.NONE );
            namesText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            namesText.setEditable( false );

            toolkit.createLabel( mainClient,
                Messages.getString( "MatchingRuleDescriptionDetailsPage.Description" ), SWT.NONE ); //$NON-NLS-1$
            descText = toolkit.createText( mainClient, getNonNullString( mrd.getDescription() ), SWT.WRAP | SWT.MULTI );
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
    private void createUsedFromContents( MatchingRule mrd )
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
            Collection<AttributeType> usedFromATDs = SchemaUtils.getUsedFromAttributeTypeDescriptions( mrd,
                getSchema() );
            if ( usedFromATDs != null && usedFromATDs.size() > 0 )
            {
                usedFromSection
                    .setText( NLS
                        .bind(
                            Messages.getString( "MatchingRuleDescriptionDetailsPage.UsedFromCount" ), new Object[] { usedFromATDs.size() } ) ); //$NON-NLS-1$
                for ( AttributeType atd : usedFromATDs )
                {
                    Hyperlink usedFromLink = toolkit.createHyperlink( usedFromClient, SchemaUtils.toString( atd ),
                        SWT.WRAP );
                    usedFromLink.setHref( atd );
                    usedFromLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    usedFromLink.setUnderlined( true );
                    usedFromLink.setEnabled( true );
                    usedFromLink.addHyperlinkListener( this );
                }
            }
            else
            {
                usedFromSection.setText( NLS.bind( Messages
                    .getString( "MatchingRuleDescriptionDetailsPage.UsedFromCount" ), new Object[] { 0 } ) ); //$NON-NLS-1$
                Text usedFromText = toolkit.createText( usedFromClient, getNonNullString( null ), SWT.NONE );
                usedFromText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                usedFromText.setEditable( false );
            }
        }
        else
        {
            usedFromSection.setText( Messages.getString( "MatchingRuleDescriptionDetailsPage.UsedFrom" ) ); //$NON-NLS-1$
        }

        usedFromSection.layout();
    }

}
