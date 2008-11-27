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
import java.util.List;

import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.syntax.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.osgi.util.NLS;
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
 * The ObjectClassDescriptionDetailsPage displays the details of an
 * object class description.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassDescriptionDetailsPage extends SchemaDetailsPage
{

    /** The main section, contains oid, names, desc and kind */
    private Section mainSection;

    /** The numeric oid field */
    private Text numericOidText;

    /** The names field */
    private Text namesText;

    /** The description field */
    private Text descText;

    /** The kind field */
    private Text kindText;

    /** The section with links to superior object classes */
    private Section superclassesSection;

    /** The section with links to derived object classes */
    private Section subclassesSection;

    /** The section with links to must attribute types */
    private Section mustSection;

    /** The section with links to may attribute types */
    private Section maySection;


    /**
     * Creates a new instance of ObjectClassDescriptionDetailsPage.
     *
     * @param schemaPage the master schema page
     * @param toolkit the toolkit used to create controls
     */
    public ObjectClassDescriptionDetailsPage( SchemaPage schemaPage, FormToolkit toolkit )
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
        mainSection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.Details" ) ); //$NON-NLS-1$
        mainSection.marginWidth = 0;
        mainSection.marginHeight = 0;
        mainSection.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        toolkit.createCompositeSeparator( mainSection );

        // create must section
        mustSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        mustSection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.MustAttributes" ) ); //$NON-NLS-1$
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

        // create may section
        maySection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        maySection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.MayAttributes" ) ); //$NON-NLS-1$
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

        // create superior section
        superclassesSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        superclassesSection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.Superclasses" ) ); //$NON-NLS-1$
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

        // create subclasses section
        subclassesSection = toolkit.createSection( detailForm.getBody(), Section.TWISTIE );
        subclassesSection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.Subclasses" ) ); //$NON-NLS-1$
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

        // create raw section
        createRawSection();
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( Object input )
    {
        ObjectClassDescription ocd = null;
        if ( input instanceof ObjectClassDescription )
        {
            ocd = ( ObjectClassDescription ) input;
        }

        // create main content
        this.createMainContent( ocd );

        // create contents of dynamic sections
        this.createSuperclassContents( ocd );
        this.createSubclassContents( ocd );
        this.createMustContents( ocd );
        this.createMayContents( ocd );
        super.createRawContents( ocd );

        this.detailForm.reflow( true );
    }


    /**
     * Creates the content of the main section. It is newly created
     * on every input change to ensure a proper layout of 
     * multilined descriptions. 
     *
     * @param ocd the object class description
     */
    private void createMainContent( ObjectClassDescription ocd )
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
        if ( ocd != null )
        {
            toolkit.createLabel( mainClient,
                Messages.getString( "ObjectClassDescriptionDetailsPage.NumericOID" ), SWT.NONE ); //$NON-NLS-1$
            numericOidText = toolkit.createText( mainClient, getNonNullString( ocd.getNumericOid() ), SWT.NONE );
            numericOidText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            numericOidText.setEditable( false );

            toolkit.createLabel( mainClient,
                Messages.getString( "ObjectClassDescriptionDetailsPage.ObjectclassNames" ), SWT.NONE ); //$NON-NLS-1$
            namesText = toolkit.createText( mainClient, getNonNullString( SchemaUtils.toString( ocd ) ), SWT.NONE );
            namesText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            namesText.setEditable( false );

            toolkit.createLabel( mainClient,
                Messages.getString( "ObjectClassDescriptionDetailsPage.Description" ), SWT.NONE ); //$NON-NLS-1$
            descText = toolkit.createText( mainClient, getNonNullString( ocd.getDescription() ), SWT.WRAP | SWT.MULTI );
            GridData gd = new GridData( GridData.FILL_HORIZONTAL );
            gd.widthHint = detailForm.getForm().getSize().x - 100 - 60;
            descText.setLayoutData( gd );
            descText.setEditable( false );

            String kind = ""; //$NON-NLS-1$
            switch ( ocd.getKind() )
            {
                case STRUCTURAL:
                    kind = Messages.getString( "ObjectClassDescriptionDetailsPage.Structural" ); //$NON-NLS-1$
                    break;
                case ABSTRACT:
                    kind = Messages.getString( "ObjectClassDescriptionDetailsPage.Abstract" ); //$NON-NLS-1$
                    break;
                case AUXILIARY:
                    kind = Messages.getString( "ObjectClassDescriptionDetailsPage.Auxiliary" ); //$NON-NLS-1$
                    break;
            }
            if ( ocd.isObsolete() )
            {
                kind += Messages.getString( "ObjectClassDescriptionDetailsPage.Obsolete" ); //$NON-NLS-1$
            }
            toolkit.createLabel( mainClient,
                Messages.getString( "ObjectClassDescriptionDetailsPage.ObjectclassKind" ), SWT.NONE ); //$NON-NLS-1$
            kindText = toolkit.createText( mainClient, getNonNullString( kind ), SWT.NONE );
            kindText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            kindText.setEditable( false );
        }

        mainSection.layout();
    }


    /**
     * Creates the content of the must section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param ocd the object class description
     */
    private void createMustContents( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( mustSection.getClient() != null )
        {
            mustSection.getClient().dispose();
        }

        // create new client
        Composite mustClient = toolkit.createComposite( mustSection, SWT.WRAP );
        mustClient.setLayout( new GridLayout() );
        mustSection.setClient( mustClient );

        // create new content
        if ( ocd != null )
        {
            Collection<String> names = SchemaUtils.getMustAttributeTypeDescriptionNamesTransitive( ocd, getSchema() );
            if ( names != null && names.size() > 0 )
            {
                mustSection
                    .setText( NLS
                        .bind(
                            Messages.getString( "ObjectClassDescriptionDetailsPage.MustAttributesCount" ), new int[] { names.size() } ) ); //$NON-NLS-1$
                for ( String name : names )
                {
                    if ( getSchema().hasAttributeTypeDescription( name ) )
                    {
                        AttributeTypeDescription mustAtd = getSchema().getAttributeTypeDescription( name );
                        Hyperlink mustLink = toolkit.createHyperlink( mustClient, SchemaUtils.toString( mustAtd ),
                            SWT.WRAP );
                        mustLink.setHref( mustAtd );
                        mustLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        mustLink.setUnderlined( true );
                        mustLink.setEnabled( true );
                        mustLink.addHyperlinkListener( this );
                    }
                    else
                    {
                        Hyperlink mustLink = toolkit.createHyperlink( mustClient, name, SWT.WRAP );
                        mustLink.setHref( null );
                        mustLink.setUnderlined( false );
                        mustLink.setEnabled( false );
                    }
                }
            }
            else
            {
                mustSection.setText( NLS.bind( Messages
                    .getString( "ObjectClassDescriptionDetailsPage.MustAttributesCount" ), new int[] { 0 } ) ); //$NON-NLS-1$
                Text mustText = toolkit.createText( mustClient, getNonNullString( null ), SWT.NONE );
                mustText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mustText.setEditable( false );
            }
        }
        else
        {
            mustSection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.MustAttributes" ) ); //$NON-NLS-1$
        }

        mustSection.layout();
    }


    /**
     * Creates the content of the may section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param ocd the object class description
     */
    private void createMayContents( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( maySection.getClient() != null )
        {
            maySection.getClient().dispose();
        }

        // create new client
        Composite mayClient = toolkit.createComposite( maySection, SWT.WRAP );
        mayClient.setLayout( new GridLayout() );
        maySection.setClient( mayClient );

        // create new content
        if ( ocd != null )
        {
            Collection<String> names = SchemaUtils.getMayAttributeTypeDescriptionNamesTransitive( ocd, getSchema() );
            if ( names != null && names.size() > 0 )
            {
                maySection.setText( NLS.bind( Messages
                    .getString( "ObjectClassDescriptionDetailsPage.MayAttributesCount" ), new int[] { names.size() } ) ); //$NON-NLS-1$
                for ( String name : names )
                {
                    if ( getSchema().hasAttributeTypeDescription( name ) )
                    {
                        AttributeTypeDescription mayAtd = getSchema().getAttributeTypeDescription( name );
                        Hyperlink mayLink = toolkit.createHyperlink( mayClient, SchemaUtils.toString( mayAtd ),
                            SWT.WRAP );
                        mayLink.setHref( mayAtd );
                        mayLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        mayLink.setUnderlined( true );
                        mayLink.setEnabled( true );
                        mayLink.addHyperlinkListener( this );
                    }
                    else
                    {
                        Hyperlink mayLink = toolkit.createHyperlink( mayClient, name, SWT.WRAP );
                        mayLink.setHref( null );
                        mayLink.setUnderlined( false );
                        mayLink.setEnabled( false );
                    }
                }
            }
            else
            {
                maySection.setText( NLS.bind( Messages
                    .getString( "ObjectClassDescriptionDetailsPage.MayAttributesCount" ), new int[] { 0 } ) ); //$NON-NLS-1$
                Text mayText = toolkit.createText( mayClient, getNonNullString( null ), SWT.NONE );
                mayText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                mayText.setEditable( false );
            }
        }
        else
        {
            maySection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.MayAttributes" ) ); //$NON-NLS-1$
        }
        maySection.layout();
    }


    /**
     * Creates the content of the sub classes section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param ocd the object class description
     */
    private void createSubclassContents( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( subclassesSection.getClient() != null )
        {
            subclassesSection.getClient().dispose();
        }

        // create new client
        Composite subClient = toolkit.createComposite( subclassesSection, SWT.WRAP );
        subClient.setLayout( new GridLayout() );
        subclassesSection.setClient( subClient );

        // create new content
        if ( ocd != null )
        {
            List<ObjectClassDescription> subOcds = SchemaUtils.getSubObjectClassDescriptions( ocd, getSchema() );
            if ( subOcds != null && subOcds.size() > 0 )
            {
                subclassesSection.setText( NLS.bind( Messages
                    .getString( "ObjectClassDescriptionDetailsPage.SubclassesCount" ), new int[] { subOcds.size() } ) ); //$NON-NLS-1$
                for ( ObjectClassDescription subOcd : subOcds )
                {
                    Hyperlink subLink = toolkit.createHyperlink( subClient, SchemaUtils.toString( subOcd ), SWT.WRAP );
                    subLink.setHref( subOcd );
                    subLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                    subLink.setUnderlined( true );
                    subLink.setEnabled( true );
                    subLink.addHyperlinkListener( this );
                }
            }
            else
            {
                subclassesSection.setText( NLS.bind( Messages
                    .getString( "ObjectClassDescriptionDetailsPage.SubclassesCount" ), new int[] { 0 } ) ); //$NON-NLS-1$
                Text derivedText = toolkit.createText( subClient, getNonNullString( null ), SWT.NONE );
                derivedText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                derivedText.setEditable( false );
            }
        }
        else
        {
            subclassesSection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.Subclasses" ) ); //$NON-NLS-1$
        }

        subclassesSection.layout();
    }


    /**
     * Creates the content of the super classes section. 
     * It is newly created on every input change because the content
     * of this section is dynamic.
     *
     * @param ocd the object class description
     */
    private void createSuperclassContents( ObjectClassDescription ocd )
    {
        // dispose old content
        if ( superclassesSection.getClient() != null )
        {
            superclassesSection.getClient().dispose();
        }

        // create new client
        Composite superClient = toolkit.createComposite( superclassesSection, SWT.WRAP );
        superClient.setLayout( new GridLayout() );
        superclassesSection.setClient( superClient );

        // create new content
        if ( ocd != null )
        {
            List<String> names = ocd.getSuperiorObjectClasses();
            if ( names != null && names.size() > 0 )
            {
                superclassesSection.setText( NLS.bind( Messages
                    .getString( "ObjectClassDescriptionDetailsPage.SuperclassesCount" ), new int[] { names.size() } ) ); //$NON-NLS-1$
                Composite supClient = toolkit.createComposite( superClient, SWT.WRAP );
                GridLayout gl = new GridLayout();
                gl.marginWidth = 0;
                gl.marginHeight = 0;
                supClient.setLayout( gl );
                for ( String name : names )
                {
                    if ( getSchema().hasObjectClassDescription( name ) )
                    {
                        ObjectClassDescription supOcd = getSchema().getObjectClassDescription( name );
                        Hyperlink superLink = toolkit.createHyperlink( supClient, SchemaUtils.toString( supOcd ),
                            SWT.WRAP );
                        superLink.setHref( supOcd );
                        superLink.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                        superLink.setUnderlined( true );
                        superLink.setEnabled( true );
                        superLink.addHyperlinkListener( this );
                    }
                    else
                    {
                        Hyperlink superLink = toolkit.createHyperlink( supClient, name, SWT.WRAP );
                        superLink.setHref( null );
                        superLink.setUnderlined( false );
                        superLink.setEnabled( false );
                    }
                }
            }
            else
            {
                superclassesSection.setText( NLS.bind( Messages
                    .getString( "ObjectClassDescriptionDetailsPage.SuperclassesCount" ), new int[] { 0 } ) ); //$NON-NLS-1$
                Text superText = toolkit.createText( superClient, getNonNullString( null ), SWT.NONE );
                superText.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
                superText.setEditable( false );
            }
        }
        else
        {
            superclassesSection.setText( Messages.getString( "ObjectClassDescriptionDetailsPage.Superclasses" ) ); //$NON-NLS-1$
        }

        superclassesSection.layout();
    }

}
