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

package org.apache.directory.studio.ldapbrowser.common.wizards;


import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.naming.InvalidNameException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.name.AttributeTypeAndValue;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.DnBuilderWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.ListContentProposalProvider;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReadEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * The NewEntryDnWizardPage is used to compose the new entry's 
 * distinguished name.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewEntryDnWizardPage extends WizardPage implements WidgetModifyListener
{

    /** The wizard. */
    private NewEntryWizard wizard;

    /** The DN builder widget. */
    private DnBuilderWidget dnBuilderWidget;

    /** The context entry DN combo. */
    private Combo contextEntryDnCombo;

    /** The content proposal adapter for the context entry DN combo. */
    private ContentProposalAdapter contextEntryDnComboCPA;


    /**
     * Creates a new instance of NewEntryDnWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public NewEntryDnWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        setTitle( Messages.getString( "NewEntryDnWizardPage.DistinguishedName" ) ); //$NON-NLS-1$
        if ( wizard.isNewContextEntry() )
        {
            setDescription( Messages.getString( "NewEntryDnWizardPage.EnterDN" ) ); //$NON-NLS-1$
        }
        else
        {
            setDescription( Messages.getString( "NewEntryDnWizardPage.SelectParent" ) ); //$NON-NLS-1$
        }
        setImageDescriptor( BrowserCommonActivator.getDefault().getImageDescriptor(
            BrowserCommonConstants.IMG_ENTRY_WIZARD ) );
        setPageComplete( false );

        this.wizard = wizard;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( dnBuilderWidget != null )
        {
            dnBuilderWidget.removeWidgetModifyListener( this );
            dnBuilderWidget.dispose();
            dnBuilderWidget = null;
        }
        super.dispose();
    }


    /**
     * Validates the input fields.
     */
    private void validate()
    {
        if ( wizard.isNewContextEntry() && !"".equals( contextEntryDnCombo.getText() ) //$NON-NLS-1$
            && LdapDN.isValid( contextEntryDnCombo.getText() ) )
        {
            setPageComplete( true );
            saveState();
        }
        else if ( !wizard.isNewContextEntry() && dnBuilderWidget.getRdn() != null
            && dnBuilderWidget.getParentDn() != null )
        {
            setPageComplete( true );
            saveState();
        }
        else
        {
            setPageComplete( false );
        }
    }


    /**
     * Initializes the DN builder widget with the DN of 
     * the prototype entry. Called when this page becomes visible.
     */
    private void loadState()
    {
        DummyEntry newEntry = wizard.getPrototypeEntry();

        if ( wizard.isNewContextEntry() )
        {
            IAttribute attribute = wizard.getSelectedConnection().getRootDSE().getAttribute(
                SchemaConstants.NAMING_CONTEXTS_AT );
            if ( attribute != null )
            {
                String[] values = attribute.getStringValues();

                // content proposals
                contextEntryDnComboCPA.setContentProposalProvider( new ListContentProposalProvider( values ) );

                // fill namingContext values into combo 
                contextEntryDnCombo.setItems( values );

                // preset combo text
                if ( Arrays.asList( values ).contains( newEntry.getDn().getUpName() ) )
                {
                    contextEntryDnCombo.setText( newEntry.getDn().getUpName() );
                }
            }
        }
        else
        {
            Collection<AttributeTypeDescription> atds = SchemaUtils.getAllAttributeTypeDescriptions( newEntry );
            String[] attributeNames = SchemaUtils.getNames( atds ).toArray( ArrayUtils.EMPTY_STRING_ARRAY );

            LdapDN parentDn = null;
            if ( wizard.getSelectedEntry() != null && newEntry.getDn().equals( wizard.getSelectedEntry().getDn() )
                && DnUtils.getParent( newEntry.getDn() ) != null )
            {
                parentDn = DnUtils.getParent( newEntry.getDn() );
            }
            else if ( wizard.getSelectedEntry() != null )
            {
                parentDn = wizard.getSelectedEntry().getDn();
            }
            else if ( DnUtils.getParent( newEntry.getDn() ) != null )
            {
                parentDn = DnUtils.getParent( newEntry.getDn() );
            }

            Rdn rdn = newEntry.getRdn();

            dnBuilderWidget.setInput( wizard.getSelectedConnection(), attributeNames, rdn, parentDn );
        }
    }


    /**
     * Saves the DN of the DN builder widget to the prototype entry.
     */
    private void saveState()
    {
        DummyEntry newEntry = wizard.getPrototypeEntry();

        try
        {
            EventRegistry.suspendEventFiringInCurrentThread();

            // remove old RDN
            if ( newEntry.getRdn().size() > 0 )
            {
                Iterator<AttributeTypeAndValue> atavIterator = newEntry.getRdn().iterator();
                while ( atavIterator.hasNext() )
                {
                    AttributeTypeAndValue atav = atavIterator.next();
                    IAttribute attribute = newEntry.getAttribute( atav.getUpType() );
                    if ( attribute != null )
                    {
                        IValue[] values = attribute.getValues();
                        for ( int v = 0; v < values.length; v++ )
                        {
                            if ( values[v].getStringValue().equals( atav.getNormValue().getString() ) )
                            {
                                attribute.deleteValue( values[v] );
                            }
                        }

                        // If we have removed all the values of the attribute,
                        // then we also need to remove this attribute from the
                        // entry.
                        // This test has been added to fix DIRSTUDIO-222
                        if ( attribute.getValueSize() == 0 )
                        {
                            newEntry.deleteAttribute( attribute );
                        }
                    }
                }
            }

            // set new DN
            LdapDN dn;
            if ( wizard.isNewContextEntry() )
            {
                try
                {
                    dn = new LdapDN( contextEntryDnCombo.getText() );
                }
                catch ( InvalidNameException e )
                {
                    dn = LdapDN.EMPTY_LDAPDN;
                }
            }
            else
            {
                dn = DnUtils.composeDn( dnBuilderWidget.getRdn(), dnBuilderWidget.getParentDn() );
            }
            newEntry.setDn( dn );

            // add new RDN
            if ( dn.getRdn().size() > 0 )
            {
                Iterator<AttributeTypeAndValue> atavIterator = dn.getRdn().iterator();
                while ( atavIterator.hasNext() )
                {
                    AttributeTypeAndValue atav = atavIterator.next();
                    IAttribute rdnAttribute = newEntry.getAttribute( atav.getUpType() );
                    if ( rdnAttribute == null )
                    {
                        rdnAttribute = new Attribute( newEntry, atav.getUpType() );
                        newEntry.addAttribute( rdnAttribute );
                    }
                    Object rdnValue = atav.getNormValue().getString();
                    String[] stringValues = rdnAttribute.getStringValues();
                    if ( !Arrays.asList( stringValues ).contains( rdnValue ) )
                    {
                        rdnAttribute.addValue( new Value( rdnAttribute, rdnValue ) );
                    }
                }
            }

        }
        finally
        {
            EventRegistry.resumeEventFiringInCurrentThread();
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation initializes DN builder widghet with the
     * DN of the protoype entry.
     */
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if ( visible )
        {
            loadState();
            validate();

            if ( wizard.isNewContextEntry() )
            {
                contextEntryDnCombo.setFocus();
            }
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation just checks if this page is complete. It 
     * doesn't call {@link #getNextPage()} to avoid unneeded 
     * invokings of {@link ReadEntryRunnable}s.
     */
    public boolean canFlipToNextPage()
    {
        return isPageComplete();
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation invokes a {@link ReadEntryRunnable} to check if an
     * entry with the composed DN already exists.
     */
    public IWizardPage getNextPage()
    {
        if ( !wizard.isNewContextEntry() )
        {
            dnBuilderWidget.validate();

            Rdn rdn = dnBuilderWidget.getRdn();
            LdapDN parentDn = dnBuilderWidget.getParentDn();
            final LdapDN dn = DnUtils.composeDn( rdn, parentDn );

            // check if parent exists
            ReadEntryRunnable readEntryRunnable1 = new ReadEntryRunnable( wizard.getSelectedConnection(), parentDn );
            RunnableContextRunner.execute( readEntryRunnable1, getContainer(), false );
            IEntry parentEntry = readEntryRunnable1.getReadEntry();
            if ( parentEntry == null )
            {
                getShell().getDisplay().syncExec( new Runnable()
                {
                    public void run()
                    {
                        MessageDialog
                            .openError( getShell(),
                                Messages.getString( "NewEntryDnWizardPage.Error" ), //$NON-NLS-1$
                                NLS
                                    .bind(
                                        Messages.getString( "NewEntryDnWizardPage.ParentDoesNotExist" ), dnBuilderWidget.getParentDn().toString() ) ); //$NON-NLS-1$
                    }
                } );
                return null;
            }

            // check that new entry does not exists yet 
            ReadEntryRunnable readEntryRunnable2 = new ReadEntryRunnable( wizard.getSelectedConnection(), dn );
            RunnableContextRunner.execute( readEntryRunnable2, getContainer(), false );
            IEntry entry = readEntryRunnable2.getReadEntry();
            if ( entry != null )
            {
                getShell().getDisplay().syncExec( new Runnable()
                {
                    public void run()
                    {
                        MessageDialog
                            .openError(
                                getShell(),
                                Messages.getString( "NewEntryDnWizardPage.Error" ), NLS.bind( Messages.getString( "NewEntryDnWizardPage.EntryAlreadyExists" ), dn.toString() ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } );
                return null;
            }
        }
        else
        {
            try
            {
                final LdapDN dn = new LdapDN( contextEntryDnCombo.getText() );

                // check that new entry does not exists yet 
                ReadEntryRunnable readEntryRunnable2 = new ReadEntryRunnable( wizard.getSelectedConnection(), dn );
                RunnableContextRunner.execute( readEntryRunnable2, getContainer(), false );
                IEntry entry = readEntryRunnable2.getReadEntry();
                if ( entry != null )
                {
                    getShell().getDisplay().syncExec( new Runnable()
                    {
                        public void run()
                        {
                            MessageDialog
                                .openError(
                                    getShell(),
                                    Messages.getString( "NewEntryDnWizardPage.Error" ), NLS.bind( Messages.getString( "NewEntryDnWizardPage.EntryAlreadyExists" ), dn.toString() ) ); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    } );
                    return null;
                }
            }
            catch ( InvalidNameException e )
            {
                return null;
            }
        }

        return super.getNextPage();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        if ( wizard.isNewContextEntry() )
        {
            // the combo
            Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );
            contextEntryDnCombo = BaseWidgetUtils.createCombo( composite, ArrayUtils.EMPTY_STRING_ARRAY, 0, 1 );
            contextEntryDnCombo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    validate();
                }
            } );

            // attach content proposal behavior
            contextEntryDnComboCPA = new ContentProposalAdapter( contextEntryDnCombo, new ComboContentAdapter(), null,
                null, null );
            contextEntryDnComboCPA.setFilterStyle( ContentProposalAdapter.FILTER_NONE );
            contextEntryDnComboCPA.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );

            setControl( composite );
        }
        else
        {
            dnBuilderWidget = new DnBuilderWidget( true, true );
            dnBuilderWidget.addWidgetModifyListener( this );
            Composite composite = dnBuilderWidget.createContents( parent );
            setControl( composite );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    /**
     * Saves the dialogs settings.
     */
    public void saveDialogSettings()
    {
        if ( !wizard.isNewContextEntry() )
        {
            dnBuilderWidget.saveDialogSettings();
        }
    }

}