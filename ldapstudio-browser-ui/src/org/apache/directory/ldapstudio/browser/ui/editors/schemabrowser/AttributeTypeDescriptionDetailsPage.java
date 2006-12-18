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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


public class AttributeTypeDescriptionDetailsPage extends SchemaDetailsPage
{

    private Section mainSection;

    private Text numericOidText;

    private Text nameText;

    private Text descText;

    private Text usageText;

    private Section flagSection;

    private Label singleValuedText;

    private Label isObsoleteText;

    private Label collectiveText;

    private Label noUserModificationText;

    private Section syntaxSection;

    private Text syntaxText;

    private Text lengthText;

    private Hyperlink syntaxLink;

    private Section matchSection;

    private Hyperlink equalityLink;

    private Hyperlink substringLink;

    private Hyperlink orderingLink;

    private Section otherMatchSection;

    private Hyperlink[] otherMatchLinks;

    private Section mustSection;

    private Hyperlink[] usedAsMustLinks;

    private Section maySection;

    private Hyperlink[] usedAsMayLinks;

    private Section superSection;

    private Hyperlink superLink;

    private Section subSection;

    private Hyperlink[] subAttributeTypeLinks;


    public AttributeTypeDescriptionDetailsPage( SchemaBrowser schemaBrowser, FormToolkit toolkit )
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

        toolkit.createLabel( syntaxClient, "Length:", SWT.NONE );
        lengthText = toolkit.createText( syntaxClient, "", SWT.NONE );
        lengthText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        lengthText.setEditable( false );

        matchSection = toolkit.createSection( detailForm.getBody(), SWT.NONE );
        matchSection.setText( "Matching Rules" );
        matchSection.marginWidth = 0;
        matchSection.marginHeight = 0;
        matchSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( matchSection );

        Composite matchClient = toolkit.createComposite( matchSection, SWT.WRAP );
        GridLayout matchLayout = new GridLayout();
        matchLayout.numColumns = 2;
        matchLayout.marginWidth = 0;
        matchLayout.marginHeight = 0;
        matchClient.setLayout( matchLayout );
        matchSection.setClient( matchClient );

        toolkit.createLabel( matchClient, "Equality match:", SWT.NONE );
        equalityLink = toolkit.createHyperlink( matchClient, "", SWT.WRAP );
        equalityLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        equalityLink.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                SchemaBrowser.select( e.getHref() );
            }
        } );

        toolkit.createLabel( matchClient, "Substring match:", SWT.NONE );
        substringLink = toolkit.createHyperlink( matchClient, "", SWT.WRAP );
        substringLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        substringLink.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                SchemaBrowser.select( e.getHref() );
            }
        } );

        toolkit.createLabel( matchClient, "Ordering match:", SWT.NONE );
        orderingLink = toolkit.createHyperlink( matchClient, "", SWT.WRAP );
        orderingLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        orderingLink.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                SchemaBrowser.select( e.getHref() );
            }
        } );

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

        mustSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        mustSection.setText( "Used as MUST" );
        mustSection.marginWidth = 0;
        mustSection.marginHeight = 0;
        mustSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( mustSection );
        mustSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        maySection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        maySection.setText( "Used as MAY" );
        maySection.marginWidth = 0;
        maySection.marginHeight = 0;
        maySection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( maySection );
        maySection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        superSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        superSection.setText( "Supertypes" );
        superSection.marginWidth = 0;
        superSection.marginHeight = 0;
        superSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( superSection );
        superSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        subSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        subSection.setText( "Subtypes" );
        subSection.marginWidth = 0;
        subSection.marginHeight = 0;
        subSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( subSection );
        subSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        super.createRawSection();
    }


    public void attributeTypeDescriptionSelected( AttributeTypeDescription atd )
    {
        this.createMainContent( atd );

        singleValuedText.setEnabled( atd != null && atd.isSingleValued() );
        isObsoleteText.setEnabled( atd != null && atd.isObsolete() );
        collectiveText.setEnabled( atd != null && atd.isCollective() );
        noUserModificationText.setEnabled( atd != null && atd.isNoUserModification() );
        flagSection.layout();

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
        syntaxText.setText( getNonNullString( lsd != null ? lsd.getDesc() : null ) );
        lengthText.setText( getNonNullString( lsdLength ) );
        syntaxSection.layout();

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
        matchSection.layout();

        this.createOtherMatchContent( atd );
        this.createMustContent( atd );
        this.createMayContent( atd );
        this.createSuperContent( atd );
        this.createSubContent( atd );

        super.createRawContents( atd );

        detailForm.reflow( true );
    }


    private void createMainContent( AttributeTypeDescription atd )
    {

        if ( mainSection.getClient() != null )
        {
            mainSection.getClient().dispose();
        }

        Composite mainClient = toolkit.createComposite( mainSection, SWT.WRAP );
        GridLayout mainLayout = new GridLayout( 2, false );
        mainClient.setLayout( mainLayout );
        mainSection.setClient( mainClient );

        if ( atd != null )
        {

            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( atd.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Attribute names:", SWT.NONE );
            nameText = toolkit.createText( mainClient, getNonNullString( atd.toString() ), SWT.NONE );
            nameText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            nameText.setEditable( false );

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


    private void createOtherMatchContent( AttributeTypeDescription atd )
    {

        if ( otherMatchSection.getClient() != null )
        {
            otherMatchSection.getClient().dispose();
        }

        Composite otherMatchClient = toolkit.createComposite( otherMatchSection, SWT.WRAP );
        otherMatchClient.setLayout( new GridLayout() );
        otherMatchSection.setClient( otherMatchClient );

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
                        otherMatchLinks[i].addHyperlinkListener( new HyperlinkAdapter()
                        {
                            public void linkActivated( HyperlinkEvent e )
                            {
                                SchemaBrowser.select( e.getHref() );
                            }
                        } );
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


    private void createSuperContent( AttributeTypeDescription atd )
    {

        if ( superSection.getClient() != null )
        {
            superSection.getClient().dispose();
        }

        Composite superClient = toolkit.createComposite( superSection, SWT.WRAP );
        superClient.setLayout( new GridLayout() );
        superSection.setClient( superClient );

        if ( atd != null )
        {
            String superName = atd.getSuperiorAttributeTypeDescriptionName();
            if ( superName != null )
            {
                superSection.setText( "Supertype (" + "1" + ")" );
                if ( atd.getSchema().hasAttributeTypeDescription( superName ) )
                {
                    AttributeTypeDescription supAtd = atd.getSchema().getAttributeTypeDescription( superName );
                    superLink = toolkit.createHyperlink( superClient, supAtd.toString(), SWT.WRAP );
                    superLink.setHref( supAtd );
                    superLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    superLink.setUnderlined( true );
                    superLink.setEnabled( true );
                    superLink.addHyperlinkListener( new HyperlinkAdapter()
                    {
                        public void linkActivated( HyperlinkEvent e )
                        {
                            SchemaBrowser.select( e.getHref() );
                        }
                    } );
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
                superSection.setText( "Supertype (0)" );
                superLink = null;
                Text supText = toolkit.createText( superClient, getNonNullString( null ), SWT.NONE );
                supText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                supText.setEditable( false );
            }
        }
        else
        {
            superSection.setText( "Supertype" );
        }

        superSection.layout();

    }


    private void createSubContent( AttributeTypeDescription atd )
    {

        if ( subSection.getClient() != null )
        {
            subSection.getClient().dispose();
        }

        Composite subClient = toolkit.createComposite( subSection, SWT.WRAP );
        subClient.setLayout( new GridLayout() );
        subSection.setClient( subClient );

        if ( atd != null )
        {
            AttributeTypeDescription[] subATDs = atd.getDerivedAttributeTypeDescriptions();
            if ( subATDs != null && subATDs.length > 0 )
            {
                subSection.setText( "Subtypes (" + subATDs.length + ")" );
                subAttributeTypeLinks = new Hyperlink[subATDs.length];
                for ( int i = 0; i < subATDs.length; i++ )
                {
                    subAttributeTypeLinks[i] = toolkit.createHyperlink( subClient, subATDs[i].toString(), SWT.WRAP );
                    subAttributeTypeLinks[i].setHref( subATDs[i] );
                    subAttributeTypeLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    subAttributeTypeLinks[i].setUnderlined( true );
                    subAttributeTypeLinks[i].setEnabled( true );
                    subAttributeTypeLinks[i].addHyperlinkListener( new HyperlinkAdapter()
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
                subSection.setText( "Subtypes (0)" );
                subAttributeTypeLinks = new Hyperlink[0];
                Text subText = toolkit.createText( subClient, getNonNullString( null ), SWT.NONE );
                subText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                subText.setEditable( false );
            }
        }
        else
        {
            subSection.setText( "Subtypes" );
        }

        subSection.layout();

    }


    private void createMustContent( AttributeTypeDescription atd )
    {

        if ( mustSection.getClient() != null )
        {
            mustSection.getClient().dispose();
        }

        Composite mustClient = toolkit.createComposite( mustSection, SWT.WRAP );
        mustClient.setLayout( new GridLayout() );
        mustSection.setClient( mustClient );

        if ( atd != null )
        {
            ObjectClassDescription[] usedAsMusts = atd.getUsedAsMust();
            if ( usedAsMusts != null && usedAsMusts.length > 0 )
            {
                mustSection.setText( "Used as MUST (" + usedAsMusts.length + ")" );
                usedAsMustLinks = new Hyperlink[usedAsMusts.length];
                for ( int i = 0; i < usedAsMusts.length; i++ )
                {
                    usedAsMustLinks[i] = toolkit.createHyperlink( mustClient, usedAsMusts[i].toString(), SWT.WRAP );
                    usedAsMustLinks[i].setHref( usedAsMusts[i] );
                    usedAsMustLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    usedAsMustLinks[i].setUnderlined( true );
                    usedAsMustLinks[i].setEnabled( true );
                    usedAsMustLinks[i].addHyperlinkListener( new HyperlinkAdapter()
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
                mustSection.setText( "Used as MUST (0)" );
                usedAsMustLinks = new Hyperlink[0];
                Text mustText = toolkit.createText( mustClient, getNonNullString( null ), SWT.NONE );
                mustText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mustText.setEditable( false );
            }
        }
        else
        {
            mustSection.setText( "Used as MUST" );
        }

        mustSection.layout();

    }


    private void createMayContent( AttributeTypeDescription atd )
    {

        if ( maySection.getClient() != null )
        {
            maySection.getClient().dispose();
        }

        Composite mayClient = toolkit.createComposite( maySection, SWT.WRAP );
        mayClient.setLayout( new GridLayout() );
        maySection.setClient( mayClient );

        if ( atd != null )
        {
            ObjectClassDescription[] usedAsMays = atd.getUsedAsMay();
            if ( usedAsMays != null && usedAsMays.length > 0 )
            {
                maySection.setText( "Used as MAY (" + usedAsMays.length + ")" );
                usedAsMayLinks = new Hyperlink[usedAsMays.length];
                for ( int i = 0; i < usedAsMays.length; i++ )
                {
                    usedAsMayLinks[i] = toolkit.createHyperlink( mayClient, usedAsMays[i].toString(), SWT.WRAP );
                    usedAsMayLinks[i].setHref( usedAsMays[i] );
                    usedAsMayLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    usedAsMayLinks[i].setUnderlined( true );
                    usedAsMayLinks[i].setEnabled( true );
                    usedAsMayLinks[i].addHyperlinkListener( new HyperlinkAdapter()
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
                maySection.setText( "Used as MAY (0)" );
                usedAsMayLinks = new Hyperlink[0];
                Text mayText = toolkit.createText( mayClient, getNonNullString( null ), SWT.NONE );
                mayText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mayText.setEditable( false );
            }
        }
        else
        {
            maySection.setText( "Used as MAY" );
        }

        maySection.layout();
    }

}
