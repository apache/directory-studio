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
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;

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


public class ObjectClassDescriptionDetailsPage extends SchemaDetailsPage
{

    private Section mainSection;

    private Text numericOidText;

    private Text nameText;

    private Text descText;

    private Text kindText;

    private Section superclassesSection;

    private Hyperlink[] superLinks;

    private Section subclassesSection;

    private Hyperlink[] subLinks;

    private Section mustSection;

    private Hyperlink[] mustLinks;

    private Section maySection;

    private Hyperlink[] mayLinks;


    public ObjectClassDescriptionDetailsPage( SchemaBrowser schemaBrowser, FormToolkit toolkit )
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

        mustSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        mustSection.setText( "MUST Attributes" );
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
        maySection.setText( "MAY Attributes" );
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

        superclassesSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        superclassesSection.setText( "Superclasses" );
        superclassesSection.marginWidth = 0;
        superclassesSection.marginHeight = 0;
        superclassesSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( superclassesSection );
        superclassesSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        subclassesSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        subclassesSection.setText( "Subclasses" );
        subclassesSection.marginWidth = 0;
        subclassesSection.marginHeight = 0;
        subclassesSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( subclassesSection );
        subclassesSection.addExpansionListener( new ExpansionAdapter()
        {
            public void expansionStateChanged( ExpansionEvent e )
            {
                detailForm.reflow( true );
            }
        } );

        super.createRawSection();

    }


    public void objectClassDescriptionSelected( ObjectClassDescription ocd )
    {
        this.createMainContent( ocd );

        this.createSuperclassContents( ocd );
        this.createSubclassContents( ocd );

        this.createMustContents( ocd );
        this.createMayContents( ocd );

        super.createRawContents( ocd );

        this.detailForm.reflow( true );
        this.detailForm.redraw();
    }


    private void createMainContent( ObjectClassDescription ocd )
    {

        // int labelWidth = 100;

        if ( mainSection.getClient() != null )
        {
            if ( mainSection.getClient() instanceof Composite )
            {
                Composite client = ( Composite ) mainSection.getClient();
                if ( client.getChildren() != null && client.getChildren().length > 0 )
                {
                    // labelWidth = client.getChildren()[0].getSize().x;
                }
            }
            mainSection.getClient().dispose();
        }

        Composite mainClient = toolkit.createComposite( mainSection, SWT.WRAP );
        GridLayout mainLayout = new GridLayout( 2, false );
        mainClient.setLayout( mainLayout );
        mainSection.setClient( mainClient );

        if ( ocd != null )
        {
            toolkit.createLabel( mainClient, "Numeric OID:", SWT.NONE );
            numericOidText = toolkit.createText( mainClient, getNonNullString( ocd.getNumericOID() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient, "Objectclass names:", SWT.NONE );
            nameText = toolkit.createText( mainClient, getNonNullString( ocd.toString() ), SWT.NONE );
            nameText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            nameText.setEditable( false );

            toolkit.createLabel( mainClient, "Descripton:", SWT.NONE );
            descText = toolkit.createText( mainClient, getNonNullString( ocd.getDesc() ), SWT.WRAP | SWT.MULTI );
            GridData gd = new GridData( GridData.FILL_HORIZONTAL );
            gd.widthHint = detailForm.getForm().getSize().x - 100 - 60;
            descText.setLayoutData( gd );
            descText.setEditable( false );

            String kind = "";
            if ( ocd.isStructural() )
                kind = "structural";
            else if ( ocd.isAbstract() )
                kind = "abstract";
            else if ( ocd.isAuxiliary() )
                kind = "auxiliary";
            if ( ocd.isObsolete() )
                kind += " (obsolete)";
            toolkit.createLabel( mainClient, "Objectclass kind:", SWT.NONE );
            kindText = toolkit.createText( mainClient, getNonNullString( kind ), SWT.NONE );
            kindText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            kindText.setEditable( false );

        }

        mainSection.layout();
    }


    private void createMustContents( ObjectClassDescription ocd )
    {
        if ( mustSection.getClient() != null )
        {
            mustSection.getClient().dispose();
        }

        Composite mustClient = toolkit.createComposite( mustSection, SWT.WRAP );
        mustClient.setLayout( new GridLayout() );
        mustSection.setClient( mustClient );

        if ( ocd != null )
        {
            String[] names = ocd.getMustAttributeTypeDescriptionNamesTransitive();
            if ( names != null && names.length > 0 )
            {
                mustSection.setText( "MUST Attributes (" + names.length + ")" );
                mustLinks = new Hyperlink[names.length];
                for ( int i = 0; i < names.length; i++ )
                {
                    if ( ocd.getSchema().hasAttributeTypeDescription( names[i] ) )
                    {
                        AttributeTypeDescription mustAtd = ocd.getSchema().getAttributeTypeDescription( names[i] );
                        mustLinks[i] = toolkit.createHyperlink( mustClient, mustAtd.toString(), SWT.WRAP );
                        mustLinks[i].setHref( mustAtd );
                        mustLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        mustLinks[i].setUnderlined( true );
                        mustLinks[i].setEnabled( true );
                        mustLinks[i].addHyperlinkListener( new HyperlinkAdapter()
                        {
                            public void linkActivated( HyperlinkEvent e )
                            {
                                SchemaBrowser.select( e.getHref() );
                            }
                        } );
                    }
                    else
                    {
                        mustLinks[i] = toolkit.createHyperlink( mustClient, names[i], SWT.WRAP );
                        mustLinks[i].setHref( null );
                        mustLinks[i].setUnderlined( false );
                        mustLinks[i].setEnabled( false );
                    }
                }
            }
            else
            {
                mustSection.setText( "MUST Attributes (0)" );
                mustLinks = new Hyperlink[0];
                Text mustText = toolkit.createText( mustClient, getNonNullString( null ), SWT.NONE );
                mustText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mustText.setEditable( false );
            }
        }
        else
        {
            mustSection.setText( "MUST Attributes" );
        }

        mustSection.layout();
    }


    private void createMayContents( ObjectClassDescription ocd )
    {
        if ( maySection.getClient() != null )
        {
            maySection.getClient().dispose();
        }

        Composite mayClient = toolkit.createComposite( maySection, SWT.WRAP );
        mayClient.setLayout( new GridLayout() );
        maySection.setClient( mayClient );

        if ( ocd != null )
        {
            String[] names = ocd.getMayAttributeTypeDescriptionNamesTransitive();
            if ( names != null && names.length > 0 )
            {
                maySection.setText( "MAY Attributes (" + names.length + ")" );
                mayLinks = new Hyperlink[names.length];
                for ( int i = 0; i < names.length; i++ )
                {
                    if ( ocd.getSchema().hasAttributeTypeDescription( names[i] ) )
                    {
                        AttributeTypeDescription mayAtd = ocd.getSchema().getAttributeTypeDescription( names[i] );
                        mayLinks[i] = toolkit.createHyperlink( mayClient, mayAtd.toString(), SWT.WRAP );
                        mayLinks[i].setHref( mayAtd );
                        mayLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        mayLinks[i].setUnderlined( true );
                        mayLinks[i].setEnabled( true );
                        mayLinks[i].addHyperlinkListener( new HyperlinkAdapter()
                        {
                            public void linkActivated( HyperlinkEvent e )
                            {
                                SchemaBrowser.select( e.getHref() );

                            }
                        } );
                    }
                    else
                    {
                        mayLinks[i] = toolkit.createHyperlink( mayClient, names[i], SWT.WRAP );
                        mayLinks[i].setHref( null );
                        mayLinks[i].setUnderlined( false );
                        mayLinks[i].setEnabled( false );
                    }
                }
            }
            else
            {
                maySection.setText( "MAY Attributes (0)" );
                mayLinks = new Hyperlink[0];
                Text mayText = toolkit.createText( mayClient, getNonNullString( null ), SWT.NONE );
                mayText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mayText.setEditable( false );
            }
        }
        else
        {
            maySection.setText( "MAY Attributes" );
        }
        maySection.layout();
    }


    private void createSubclassContents( ObjectClassDescription ocd )
    {

        if ( subclassesSection.getClient() != null )
        {
            subclassesSection.getClient().dispose();
        }

        Composite subClient = toolkit.createComposite( subclassesSection, SWT.WRAP );
        subClient.setLayout( new GridLayout() );
        subclassesSection.setClient( subClient );

        if ( ocd != null )
        {
            ObjectClassDescription[] subOCDs = ocd.getSubObjectClassDescriptions();
            if ( subOCDs != null && subOCDs.length > 0 )
            {
                subclassesSection.setText( "Subclasses (" + subOCDs.length + ")" );
                subLinks = new Hyperlink[subOCDs.length];
                for ( int i = 0; i < subOCDs.length; i++ )
                {
                    subLinks[i] = toolkit.createHyperlink( subClient, subOCDs[i].toString(), SWT.WRAP );
                    subLinks[i].setHref( subOCDs[i] );
                    subLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    subLinks[i].setUnderlined( true );
                    subLinks[i].setEnabled( true );
                    subLinks[i].addHyperlinkListener( new HyperlinkAdapter()
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
                subclassesSection.setText( "Subclasses (0)" );
                subLinks = new Hyperlink[0];
                Text derivedText = toolkit.createText( subClient, getNonNullString( null ), SWT.NONE );
                derivedText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                derivedText.setEditable( false );
            }
        }
        else
        {
            subclassesSection.setText( "Subclasses" );
        }

        subclassesSection.layout();

    }


    private void createSuperclassContents( ObjectClassDescription ocd )
    {

        if ( superclassesSection.getClient() != null )
        {
            superclassesSection.getClient().dispose();
        }

        Composite superClient = toolkit.createComposite( superclassesSection, SWT.WRAP );
        superClient.setLayout( new GridLayout() );
        superclassesSection.setClient( superClient );

        if ( ocd != null )
        {

            String[] names = ocd.getSuperiorObjectClassDescriptionNames();
            if ( names != null && names.length > 0 )
            {
                superclassesSection.setText( "Superclasses (" + names.length + ")" );
                Composite supClient = toolkit.createComposite( superClient, SWT.WRAP );
                GridLayout gl = new GridLayout();
                gl.marginWidth = 0;
                gl.marginHeight = 0;
                supClient.setLayout( gl );
                superLinks = new Hyperlink[names.length];
                for ( int i = 0; i < names.length; i++ )
                {
                    if ( ocd.getSchema().hasObjectClassDescription( names[i] ) )
                    {
                        ObjectClassDescription supOcd = ocd.getSchema().getObjectClassDescription( names[i] );
                        superLinks[i] = toolkit.createHyperlink( supClient, supOcd.toString(), SWT.WRAP );
                        superLinks[i].setHref( supOcd );
                        superLinks[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        superLinks[i].setUnderlined( true );
                        superLinks[i].setEnabled( true );
                        superLinks[i].addHyperlinkListener( new HyperlinkAdapter()
                        {
                            public void linkActivated( HyperlinkEvent e )
                            {
                                SchemaBrowser.select( e.getHref() );
                            }
                        } );
                    }
                    else
                    {
                        superLinks[i] = toolkit.createHyperlink( supClient, names[i], SWT.WRAP );
                        superLinks[i].setHref( null );
                        superLinks[i].setUnderlined( false );
                        superLinks[i].setEnabled( false );
                    }
                }
            }
            else
            {
                superclassesSection.setText( "Superlasses (0)" );
                superLinks = new Hyperlink[0];
                Text superText = toolkit.createText( superClient, getNonNullString( null ), SWT.NONE );
                superText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                superText.setEditable( false );
            }
        }
        else
        {
            superclassesSection.setText( "Superclasses" );
        }

        superclassesSection.layout();

    }

}
