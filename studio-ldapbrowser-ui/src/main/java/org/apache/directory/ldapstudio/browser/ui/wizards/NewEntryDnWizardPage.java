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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.common.jobs.RunnableContextJobAdapter;
import org.apache.directory.ldapstudio.browser.common.widgets.DnBuilderWidget;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.common.widgets.WidgetModifyListener;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Attribute;
import org.apache.directory.studio.ldapbrowser.core.internal.model.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Value;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReadEntryJob;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;
import org.apache.directory.studio.ldapbrowser.core.model.RDN;
import org.apache.directory.studio.ldapbrowser.core.model.RDNPart;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Subschema;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


/**
 * The NewEntryDnWizardPage is used to compose the new entry's 
 * distinguished name.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewEntryDnWizardPage extends WizardPage implements WidgetModifyListener
{

    /** The wizard. */
    private NewEntryWizard wizard;

    /** The DN builder widget. */
    private DnBuilderWidget dnBuilderWidget;


    /**
     * Creates a new instance of NewEntryDnWizardPage.
     * 
     * @param pageName the page name
     * @param wizard the wizard
     */
    public NewEntryDnWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        setTitle( "Distinguished Name" );
        setDescription( "Please select the parent of the new entry and enter the RDN." );
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ENTRY_WIZARD ) );
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
        if ( dnBuilderWidget.getRdn() != null && dnBuilderWidget.getParentDn() != null )
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

        Subschema subschema = newEntry.getSubschema();
        String[] attributeNames = subschema.getAllAttributeNames();

        DN parentDn = null;
        if ( newEntry.getDn().getParentDn() != null )
        {
            parentDn = newEntry.getDn().getParentDn();
        }
        else if ( wizard.getSelectedEntry() != null )
        {
            parentDn = wizard.getSelectedEntry().getDn();
        }

        RDN rdn = newEntry.getRdn();

        dnBuilderWidget.setInput( wizard.getSelectedConnection(), attributeNames, rdn, parentDn );
    }


    /**
     * Saves the DN of the DN builder widget to the prototype entry.
     */
    private void saveState()
    {
        DummyEntry newEntry = wizard.getPrototypeEntry();

        try
        {
            EventRegistry.suspendEventFireingInCurrentThread();

            // remove old RDN
            RDNPart[] oldRdnParts = newEntry.getRdn().getParts();
            for ( int i = 0; i < oldRdnParts.length; i++ )
            {
                IAttribute attribute = newEntry.getAttribute( oldRdnParts[i].getType() );
                if ( attribute != null )
                {
                    IValue[] values = attribute.getValues();
                    for ( int v = 0; v < values.length; v++ )
                    {
                        if ( values[v].getStringValue().equals( oldRdnParts[i].getUnencodedValue() ) )
                        {
                            attribute.deleteValue( values[v] );
                        }
                    }
                }
            }

            // set new DN
            DN dn = new DN( dnBuilderWidget.getRdn(), dnBuilderWidget.getParentDn() );
            newEntry.setDn( dn );

            // add new RDN
            RDNPart[] newRdnParts = dn.getRdn().getParts();
            for ( int i = 0; i < newRdnParts.length; i++ )
            {
                IAttribute rdnAttribute = newEntry.getAttribute( newRdnParts[i].getType() );
                if ( rdnAttribute == null )
                {
                    rdnAttribute = new Attribute( newEntry, newRdnParts[i].getType() );
                    newEntry.addAttribute( rdnAttribute );
                }
                String rdnValue = newRdnParts[i].getUnencodedValue();
                String[] stringValues = rdnAttribute.getStringValues();
                if ( !Arrays.asList( stringValues ).contains( rdnValue ) )
                {
                    rdnAttribute.addValue( new Value( rdnAttribute, rdnValue ) );
                }
            }

        }
        catch ( ModelModificationException e )
        {
            e.printStackTrace();
        }
        finally
        {
            EventRegistry.resumeEventFireingInCurrentThread();
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
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation just checks if this page is complete. It 
     * doesn't call {@link #getNextPage()} to avoid unneeded 
     * invokings of {@link ReadEntryJob}s.
     */
    public boolean canFlipToNextPage()
    {
        return isPageComplete();
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation invokes a {@link ReadEntryJob} to check if an
     * entry with the composed DN already exists.
     */
    public IWizardPage getNextPage()
    {

        dnBuilderWidget.validate();
        final RDN[] rdns = new RDN[]
            { dnBuilderWidget.getRdn() };
        final DN[] parentDns = new DN[]
            { dnBuilderWidget.getParentDn() };
        final DN dn = new DN( rdns[0], parentDns[0] );

        // check if parent exists or new entry already exists
        ReadEntryJob readEntryJob1 = new ReadEntryJob( wizard.getSelectedConnection(), parentDns[0] );
        RunnableContextJobAdapter.execute( readEntryJob1, getContainer(), false );
        IEntry parentEntry = readEntryJob1.getReadEntry();
        if ( parentEntry == null )
        {
            getShell().getDisplay().syncExec( new Runnable()
            {
                public void run()
                {
                    MessageDialog.openError( getShell(), "Error", "Parent " + dnBuilderWidget.getParentDn().toString()
                        + " doesn't exists" );
                }
            } );
            return null;
        }
        ReadEntryJob readEntryJob2 = new ReadEntryJob( wizard.getSelectedConnection(), dn );
        RunnableContextJobAdapter.execute( readEntryJob2, getContainer(), false );
        IEntry entry = readEntryJob2.getReadEntry();
        if ( entry != null )
        {
            getShell().getDisplay().syncExec( new Runnable()
            {
                public void run()
                {
                    MessageDialog.openError( getShell(), "Error", "Entry " + dn.toString() + " already exists" );
                }
            } );
            return null;
        }

        return super.getNextPage();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        dnBuilderWidget = new DnBuilderWidget( true, true );
        dnBuilderWidget.addWidgetModifyListener( this );
        Composite composite = dnBuilderWidget.createContents( parent );

        setControl( composite );
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
        dnBuilderWidget.saveDialogSettings();
    }

}