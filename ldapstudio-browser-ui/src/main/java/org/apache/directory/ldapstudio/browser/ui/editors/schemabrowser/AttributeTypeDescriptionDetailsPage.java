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
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
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
 * The AttributeTypeDescriptionDetailsPage displays the details of an
 * attribute type description.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeDescriptionDetailsPage extends SchemaDetailsPage
{

    /** The main section, contains oid, names, desc and usage */
    private Section mainSection;

    /** The numeric oid field */
    private Text numericOidText;

    /** The names field */
    private Text namesText;

    /** The description field */
    private Text descText;

    /** The usage field */
    private Text usageText;

    /** The flag section, contains sv, obsolete, collective and read-only */
    private Section flagSection;

    /** The single-valued field */
    private Label singleValuedText;

    /** The obsolete field */
    private Label isObsoleteText;

    /** The collective field */
    private Label collectiveText;

    /** The no-user-modification field */
    private Label noUserModificationText;

    /** The syntax section, contains syntax description, lenth and a link to the syntax */ 
    private Section syntaxSection;

    /** The syntax description field */
    private Text syntaxDescText;

    /** The syntax length field */
    private Text lengthText;

    /** The link to the syntax */
    private Hyperlink syntaxLink;

    /** The matching rules section, contains links to matching rules */
    private Section matchingRulesSection;

    /** The link to the equality matching rule */
    private Hyperlink equalityLink;

    /** The link to the substring matching rule */
    private Hyperlink substringLink;

    /** The link to the ordering matching rule */
    private Hyperlink orderingLink;

    /** The section with other matching rules */
    private Section otherMatchSection;

    /** The links to other matching rules applicaple to the selected attribute */
    private Hyperlink[] otherMatchLinks;

    /** The section with links to object classes using the selected attribute as must */
    private Section usedAsMustSection;

    /** The links to object classes using the selected attribute as must */
    private Hyperlink[] usedAsMustLinks;

    /** The section with links to object classes using the selected attribute as may */
    private Section usedAsMaySection;

    /** The links to object classes using the selected attribute as may */
    private Hyperlink[] usedAsMayLinks;

    /** The section with a link to the superior attribute type */
    private Section supertypeSection;

    /** The link to the superior attribute type */
    private Hyperlink superLink;

    /** The section with links to the derived attribute types */
    private Section subtypesSection;

    /** The links to derived attribute types */
    private Hyperlink[] subAttributeTypeLinks;


    /**
     * Creates a new instance of AttributeTypeDescriptionDetailsPage.
     *
     * @param schemaPage the master schema page
     * @param toolkit the toolkit used to create controls
     */
    public AttributeTypeDescriptionDetailsPage( SchemaPage schemaPage, FormToolkit toolkit )
    {
        super( schemaPage, toolkit );
    }


    /**
     * {@inheritDoc}
     */
    protected void createContents( final ScrolledForm detailForm )
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

        // create flags content
        Composite flagClient = toolkit.createComposite( flagSection, SWT.WRAP );
        GridLayout flagLayout = new GridLayout();
        flagLayout.numColumns = 4;
        flagLayout.marginWidth = 0;
        flagLayout.marginHeight = 0;
        flagClient.setLayout( flagLayout );
        flagSection.setClient( flagClient );

        singleValuedText = toolkit.createLabel( flagClient, "Single valued", SWT.CHECK );
        singleValuedText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        singleValuedText.setEnabled( false );

        noUserModificationText = toolkit.createLabel( flagClient, "Read only", SWT.CHECK );
        noUserModificationText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        noUserModificationText.setEnabled( false );

        collectiveText = toolkit.createLabel( flagClient, "Collective", SWT.CHECK );
        collectiveText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        collectiveText.setEnabled( false );

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

        toolkit.createLabel( syntaxClient, "Length:", SWT.NONE );
        lengthText = toolkit.createText( syntaxClient, "", SWT.NONE );
        lengthText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        lengthText.setEditable( false );

        // create matching rules section
        matchingRulesSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        matchingRulesSection.setText( "Matching Rules" );
        matchingRulesSection.marginWidth = 0;
        matchingRulesSection.marginHeight = 0;
        matchingRulesSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( matchingRulesSection );

        // create matching rules content
        Composite matchClient = toolkit.createComposite( matchingRulesSection, SWT.WRAP );
        GridLayout matchLayout = new GridLayout();
        matchLayout.numColumns = 2;
        matchLayout.marginWidth = 0;
        matchLayout.marginHeight = 0;
        matchClient.setLayout( matchLayout );
        matchingRulesSection.setClient( matchClient );

        toolkit.createLabel( matchClient, "Equality match:", SWT.NONE );
        equalityLink = toolkit.createHyperlink( matchClient, "", SWT.WRAP );
        equalityLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        equalityLink.addHyperlinkListener( this );

        toolkit.createLabel( matchClient, "Substring match:", SWT.NONE );
        substringLink = toolkit.createHyperlink( matchClient, "", SWT.WRAP );
        substringLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        substringLink.addHyperlinkListener( this );

        toolkit.createLabel( matchClient, "Ordering match:", SWT.NONE );
        orderingLink = toolkit.createHyperlink( matchClient, "", SWT.WRAP );
        orderingLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        orderingLink.addHyperlinkListener( this );

        // create other matching rules section
        otherMatchSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        otherMatchSection.setText( "Other Matching Rules" );
        otherMatchSection.marginWidth = 0;
        otherMatchSection.marginHeight = 0;
        otherMatchSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( otherMatchSection );
        otherMatchSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create used as must section
        usedAsMustSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        usedAsMustSection.setText( "Used as MUST" );
        usedAsMustSection.marginWidth = 0;
        usedAsMustSection.marginHeight = 0;
        usedAsMustSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( usedAsMustSection );
        usedAsMustSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create used as may section
        usedAsMaySection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        usedAsMaySection.setText( "Used as MAY" );
        usedAsMaySection.marginWidth = 0;
        usedAsMaySection.marginHeight = 0;
        usedAsMaySection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( usedAsMaySection );
        usedAsMaySection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create supertype section
        supertypeSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        supertypeSection.setText( "Supertype" );
        supertypeSection.marginWidth = 0;
        supertypeSection.marginHeight = 0;
        supertypeSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( supertypeSection );
        supertypeSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        // create subtypes section
        subtypesSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        subtypesSection.setText( "Subtypes" );
        subtypesSection.marginWidth = 0;
        subtypesSection.marginHeight = 0;
        subtypesSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( subtypesSection );
        subtypesSection.addExpansionListener( new ExpansionAdapter()
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
        AttributeTypeDescription atd = null;
        if ( input instanceof AttributeTypeDescription )
        {
            atd = ( AttributeTypeDescription ) input;
        }

        // create main content
        createMainContent( atd );

        // set flags
        singleValuedText.setEnabled( atd != null && atd.isSingleValued() );
        isObsoleteText.setEnabled( atd != null && atd.isObsolete() );
        collectiveText.setEnabled( atd != null && atd.isCollective() );
        noUserModificationText.setEnabled( atd != null && atd.isNoUserModification() );
        flagSection.layout();

        // set syntax content
        String lsdOid = null;
        LdapSyntaxDescription lsd = null;
        String lsdLength = null;
        if ( atd != null )
        {
            lsdOid = atd.getSyntaxDescriptionNumericOIDTransitive();
            if ( lsdOid != null && atd.getSchema().hasLdapSyntaxDescription( lsdOid ) )
            {
                lsd = atd.getSchema().getLdapSyntaxDescription( lsdOid );
            }
            lsdLength = atd.getSyntaxDescriptionLengthTransitive();
        }
        syntaxLink.setText( getNonNullString( lsd != null ? lsd.getNumericOID() : lsdOid ) );
        syntaxLink.setHref( lsd );
        syntaxLink.setUnderlined( lsd != null );
        syntaxLink.setEnabled( lsd != null );
        syntaxDescText.setText( getNonNullString( lsd != null ? lsd.getDesc() : null ) );
        lengthText.setText( getNonNullString( lsdLength ) );
        syntaxSection.layout();

        // set matching rules content
        String emrOid = null;
        MatchingRuleDescription emr = null;
        if ( atd != null )
        {
            emrOid = atd.getEqualityMatchingRuleDescriptionOIDTransitive();
            if ( emrOid != null && atd.getSchema().hasMatchingRuleDescription( emrOid ) )
            {
                emr = atd.getSchema().getMatchingRuleDescription( emrOid );
            }
        }
        equalityLink.setText( getNonNullString( emr != null ? emr.toString() : emrOid ) );
        equalityLink.setHref( emr );
        equalityLink.setUnderlined( emr != null );
        equalityLink.setEnabled( emr != null );

        String smrOid = null;
        MatchingRuleDescription smr = null;
        if ( atd != null )
        {
            smrOid = atd.getSubstringMatchingRuleDescriptionOIDTransitive();
            if ( smrOid != null && atd.getSchema().hasMatchingRuleDescription( smrOid ) )
            {
                smr = atd.getSchema().getMatchingRuleDescription( smrOid );
            }
        }
        substringLink.setText( getNonNullString( smr != null ? smr.toString() : smrOid ) );
        substringLink.setHref( smr );
        substringLink.setUnderlined( smr != null );
        substringLink.setEnabled( smr != null );

        String omrOid = null;
        MatchingRuleDescription omr = null;
        if ( atd != null )
        {
            omrOid = atd.getOrderingMatchingRuleDescriptionOIDTransitive();
            if ( omrOid != null && atd.getSchema().hasMatchingRuleDescription( omrOid ) )
            {
                omr = atd.getSchema().getMatchingRuleDescription( omrOid );
            }
        }
        orderingLink.setText( getNonNullString( omr != null ? omr.toString() : omrOid ) );
        orderingLink.setHref( omr );
        orderingLink.setUnderlined( omr != null );
        orderingLink.setEnabled( omr != null );
        matchingRulesSection.layout();

        // create contents of dynamic sections
        createOtherMatchContent( atd );
        createUsedAsMustContent( atd );
        createUsedAsMayContent( atd );
        createSupertypeContent( atd );
        createSubtypesContent( atd );
        createRawContents( atd );

        detailForm.reflow( true );
    }


    /**
     * Creates the content of the main section. It is newly created
     * on every input change to ensure a proper layout of 
     * multilined descriptions. 
     *
     * @param atd the attribute type description
     */
    private void createMainContent( AttributeTypeDescription atd )
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
        if ( atd != null )
        {
            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( atd.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Attribute names:", SWT.NONE );
            namesText = toolkit.createText( mainClient, getNonNullString( atd.toString() ), SWT.NONE );
            namesText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            namesText.setEditable( false );

            toolkit.createLabel( mainClient, "Descripton:", SWT.WRAP );
            descText = toolkit.createText( mainClient, getNonNullString( atd.getDesc() ), SWT.WRAP | SWT.MULTI );
            GridData gd = new GridData( GridData.FILL_HORIZONTAL );
            gd.widthHint = detailForm.getForm().getSize().x - 100 - 60;
            descText.setLayoutData( gd );
            descText.setEditable( false );

            toolkit.createLabel( mainClient, "Usage:", SWT.NONE );
            usageText = toolkit.createText( mainClient, getNonNullString( atd.getUsage() ), SWT.NONE );
            usageText.setLayoutData( new GridData( GridData.GRAB_HORIZONTAL ) );
            usageText.setEditable( false );

        }

        mainSection.layout();
    }


    /**
     * Creates the content of the other matching rules section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param atd the attribute type description
     */
    private void createOtherMatchContent( AttributeTypeDescription atd )
    {
        // dispose old content
        if ( otherMatchSection.getClient() != null )
        {
            otherMatchSection.getClient().dispose();
        }

        // create new client
        Composite otherMatchClient = toolkit.createComposite( otherMatchSection, SWT.WRAP );
        otherMatchClient.setLayout( new GridLayout() );
        otherMatchSection.setClient( otherMatchClient );

        // create new content, either links to other matching rules 
        // or a dash if no other matching rules exist.
        if ( atd != null )
        {
            String[] names = atd.getOtherMatchingRuleDescriptionNames();
            if ( names != null && names.length > 0 )
            {
                otherMatchSection.setText( "Other Matching Rules (" + names.length + ")" );
                otherMatchLinks = new Hyperlink[names.length];
                for ( int i = 0; i < names.length; i++ )
                {
                    if ( atd.getSchema().hasMatchingRuleDescription( names[i] ) )
                    {
                        MatchingRuleDescription mrd = atd.getSchema().getMatchingRuleDescription( names[i] );
                        otherMatchLinks[i] = toolkit.createHyperlink( otherMatchClient, mrd.toString(), SWT.WRAP );
                        otherMatchLinks[i].setHref( mrd );
                        otherMatchLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        otherMatchLinks[i].setUnderlined( true );
                        otherMatchLinks[i].setEnabled( true );
                        otherMatchLinks[i].addHyperlinkListener( this );
                    }
                    else
                    {
                        otherMatchLinks[i] = toolkit.createHyperlink( otherMatchClient, names[i], SWT.WRAP );
                        otherMatchLinks[i].setHref( null );
                        otherMatchLinks[i].setUnderlined( false );
                        otherMatchLinks[i].setEnabled( false );
                    }
                }
            }
            else
            {
                otherMatchSection.setText( "Other Matching Rules (0)" );
                otherMatchLinks = new Hyperlink[0];
                Text otherText = toolkit.createText( otherMatchClient, getNonNullString( null ), SWT.NONE );
                otherText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                otherText.setEditable( false );
            }
        }
        else
        {
            otherMatchSection.setText( "Other Matching Rules" );
        }

        otherMatchSection.layout();
    }


    /**
     * Creates the content of the supertype section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param atd the attribute type description
     */
    private void createSupertypeContent( AttributeTypeDescription atd )
    {
        // dispose old content
        if ( supertypeSection.getClient() != null )
        {
            supertypeSection.getClient().dispose();
        }

        // create new client
        Composite superClient = toolkit.createComposite( supertypeSection, SWT.WRAP );
        superClient.setLayout( new GridLayout() );
        supertypeSection.setClient( superClient );

        // create new content, either a link to the superior attribute type
        // or a dash if no supertype exists.
        if ( atd != null )
        {
            String superName = atd.getSuperiorAttributeTypeDescriptionName();
            if ( superName != null )
            {
                supertypeSection.setText( "Supertype (" + "1" + ")" );
                if ( atd.getSchema().hasAttributeTypeDescription( superName ) )
                {
                    AttributeTypeDescription supAtd = atd.getSchema().getAttributeTypeDescription( superName );
                    superLink = toolkit.createHyperlink( superClient, supAtd.toString(), SWT.WRAP );
                    superLink.setHref( supAtd );
                    superLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    superLink.setUnderlined( true );
                    superLink.setEnabled( true );
                    superLink.addHyperlinkListener( this );
                }
                else
                {
                    superLink = toolkit.createHyperlink( superClient, superName, SWT.WRAP );
                    superLink.setHref( null );
                    superLink.setUnderlined( false );
                    superLink.setEnabled( false );
                }
            }
            else
            {
                supertypeSection.setText( "Supertype (0)" );
                superLink = null;
                Text supText = toolkit.createText( superClient, getNonNullString( null ), SWT.NONE );
                supText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                supText.setEditable( false );
            }
        }
        else
        {
            supertypeSection.setText( "Supertype" );
        }

        supertypeSection.layout();
    }


    /**
     * Creates the content of the subtypes. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param atd the attribute type description
     */
    private void createSubtypesContent( AttributeTypeDescription atd )
    {
        // dispose old content
        if ( subtypesSection.getClient() != null )
        {
            subtypesSection.getClient().dispose();
        }

        // create new client
        Composite subClient = toolkit.createComposite( subtypesSection, SWT.WRAP );
        subClient.setLayout( new GridLayout() );
        subtypesSection.setClient( subClient );

        // create new content, either links to subtypes or a dash if no subtypes exist.
        if ( atd != null )
        {
            AttributeTypeDescription[] subATDs = atd.getDerivedAttributeTypeDescriptions();
            if ( subATDs != null && subATDs.length > 0 )
            {
                subtypesSection.setText( "Subtypes (" + subATDs.length + ")" );
                subAttributeTypeLinks = new Hyperlink[subATDs.length];
                for ( int i = 0; i < subATDs.length; i++ )
                {
                    subAttributeTypeLinks[i] = toolkit.createHyperlink( subClient, subATDs[i].toString(), SWT.WRAP );
                    subAttributeTypeLinks[i].setHref( subATDs[i] );
                    subAttributeTypeLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    subAttributeTypeLinks[i].setUnderlined( true );
                    subAttributeTypeLinks[i].setEnabled( true );
                    subAttributeTypeLinks[i].addHyperlinkListener( this );
                }
            }
            else
            {
                subtypesSection.setText( "Subtypes (0)" );
                subAttributeTypeLinks = new Hyperlink[0];
                Text subText = toolkit.createText( subClient, getNonNullString( null ), SWT.NONE );
                subText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                subText.setEditable( false );
            }
        }
        else
        {
            subtypesSection.setText( "Subtypes" );
        }

        subtypesSection.layout();

    }


    /**
     * Creates the content of the used as must section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param atd the attribute type description
     */
    private void createUsedAsMustContent( AttributeTypeDescription atd )
    {
        // dispose old content
        if ( usedAsMustSection.getClient() != null )
        {
            usedAsMustSection.getClient().dispose();
        }

        // create new client
        Composite mustClient = toolkit.createComposite( usedAsMustSection, SWT.WRAP );
        mustClient.setLayout( new GridLayout() );
        usedAsMustSection.setClient( mustClient );

        // create new content, either links to objectclasses or a dash
        if ( atd != null )
        {
            ObjectClassDescription[] usedAsMusts = atd.getUsedAsMust();
            if ( usedAsMusts != null && usedAsMusts.length > 0 )
            {
                usedAsMustSection.setText( "Used as MUST (" + usedAsMusts.length + ")" );
                usedAsMustLinks = new Hyperlink[usedAsMusts.length];
                for ( int i = 0; i < usedAsMusts.length; i++ )
                {
                    usedAsMustLinks[i] = toolkit.createHyperlink( mustClient, usedAsMusts[i].toString(), SWT.WRAP );
                    usedAsMustLinks[i].setHref( usedAsMusts[i] );
                    usedAsMustLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    usedAsMustLinks[i].setUnderlined( true );
                    usedAsMustLinks[i].setEnabled( true );
                    usedAsMustLinks[i].addHyperlinkListener( this );
                }
            }
            else
            {
                usedAsMustSection.setText( "Used as MUST (0)" );
                usedAsMustLinks = new Hyperlink[0];
                Text mustText = toolkit.createText( mustClient, getNonNullString( null ), SWT.NONE );
                mustText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mustText.setEditable( false );
            }
        }
        else
        {
            usedAsMustSection.setText( "Used as MUST" );
        }

        usedAsMustSection.layout();

    }


    /**
     * Creates the content of the used as may section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param atd the attribute type description
     */
    private void createUsedAsMayContent( AttributeTypeDescription atd )
    {
        // dispose old content
        if ( usedAsMaySection.getClient() != null )
        {
            usedAsMaySection.getClient().dispose();
        }

        // create new client
        Composite mayClient = toolkit.createComposite( usedAsMaySection, SWT.WRAP );
        mayClient.setLayout( new GridLayout() );
        usedAsMaySection.setClient( mayClient );

        // create new content, either links to objectclasses or a dash
        if ( atd != null )
        {
            ObjectClassDescription[] usedAsMays = atd.getUsedAsMay();
            if ( usedAsMays != null && usedAsMays.length > 0 )
            {
                usedAsMaySection.setText( "Used as MAY (" + usedAsMays.length + ")" );
                usedAsMayLinks = new Hyperlink[usedAsMays.length];
                for ( int i = 0; i < usedAsMays.length; i++ )
                {
                    usedAsMayLinks[i] = toolkit.createHyperlink( mayClient, usedAsMays[i].toString(), SWT.WRAP );
                    usedAsMayLinks[i].setHref( usedAsMays[i] );
                    usedAsMayLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    usedAsMayLinks[i].setUnderlined( true );
                    usedAsMayLinks[i].setEnabled( true );
                    usedAsMayLinks[i].addHyperlinkListener( this );
                }
            }
            else
            {
                usedAsMaySection.setText( "Used as MAY (0)" );
                usedAsMayLinks = new Hyperlink[0];
                Text mayText = toolkit.createText( mayClient, getNonNullString( null ), SWT.NONE );
                mayText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mayText.setEditable( false );
            }
        }
        else
        {
            usedAsMaySection.setText( "Used as MAY" );
        }

        usedAsMaySection.layout();
    }

}
