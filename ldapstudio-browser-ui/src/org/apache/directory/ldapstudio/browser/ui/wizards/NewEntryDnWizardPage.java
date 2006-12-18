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

import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.internal.model.DummyEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Value;
import org.apache.directory.ldapstudio.browser.core.jobs.ReadEntryJob;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.RDN;
import org.apache.directory.ldapstudio.browser.core.model.RDNPart;
import org.apache.directory.ldapstudio.browser.core.model.schema.Subschema;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.jobs.RunnableContextJobAdapter;
import org.apache.directory.ldapstudio.browser.ui.widgets.DnBuilderWidget;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyListener;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


public class NewEntryDnWizardPage extends WizardPage implements WidgetModifyListener
{

    private NewEntryWizard wizard;

    private DnBuilderWidget dnBuilderWidget;


    public NewEntryDnWizardPage( String pageName, NewEntryWizard wizard )
    {
        super( pageName );
        super.setTitle( "Distinguished Name" );
        super.setDescription( "Please select the parent of the new entry and enter the RDN." );
        super
            .setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_ENTRY_WIZARD ) );
        super.setPageComplete( false );

        this.wizard = wizard;
    }


    public void dispose()
    {
        if ( this.dnBuilderWidget != null )
        {
            this.dnBuilderWidget.removeWidgetModifyListener( this );
            this.dnBuilderWidget.dispose();
            this.dnBuilderWidget = null;
        }
        super.dispose();
    }


    private void validate()
    {
        if ( this.dnBuilderWidget.getRdn() != null && this.dnBuilderWidget.getParentDn() != null )
        {
            super.setPageComplete( true );
            saveState();
        }
        else
        {
            super.setPageComplete( false );
        }
    }


    private void loadState()
    {

        DummyEntry newEntry = this.wizard.getNewEntry();

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

        this.dnBuilderWidget.setInput( wizard.getSelectedConnection(), attributeNames, rdn, parentDn );
    }


    private void saveState()
    {
        DummyEntry newEntry = wizard.getNewEntry();

        try
        {
            EventRegistry.suspendEventFireingInCurrentThread();

            // remove old RDN
            RDNPart[] oldRdnParts = newEntry.getRdn().getParts();
            for ( int i = 0; i < oldRdnParts.length; i++ )
            {
                IAttribute attribute = newEntry.getAttribute( oldRdnParts[i].getName() );
                if ( attribute != null )
                {
                    IValue[] values = attribute.getValues();
                    for ( int v = 0; v < values.length; v++ )
                    {
                        if ( values[v].getStringValue().equals( oldRdnParts[i].getUnencodedValue() ) )
                        {
                            attribute.deleteValue( values[v], wizard );
                        }
                    }
                }
            }

            // set new DN
            DN dn = new DN( this.dnBuilderWidget.getRdn(), this.dnBuilderWidget.getParentDn() );
            newEntry.setDn( dn );

            // add new RDN
            RDNPart[] newRdnParts = dn.getRdn().getParts();
            for ( int i = 0; i < newRdnParts.length; i++ )
            {
                IAttribute rdnAttribute = newEntry.getAttribute( newRdnParts[i].getName() );
                if ( rdnAttribute == null )
                {
                    rdnAttribute = new Attribute( newEntry, newRdnParts[i].getName() );
                    newEntry.addAttribute( rdnAttribute, wizard );
                }
                String rdnValue = newRdnParts[i].getUnencodedValue();
                String[] stringValues = rdnAttribute.getStringValues();
                if ( !Arrays.asList( stringValues ).contains( rdnValue ) )
                {
                    rdnAttribute.addValue( new Value( rdnAttribute, rdnValue ), wizard );
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


    public void setVisible( boolean visible )
    {
        super.setVisible( visible );

        if ( visible )
        {
            loadState();
            validate();
        }
    }


    public boolean canFlipToNextPage()
    {
        return isPageComplete();
    }


    public IWizardPage getNextPage()
    {

        this.dnBuilderWidget.validate();
        final RDN[] rdns = new RDN[]
            { this.dnBuilderWidget.getRdn() };
        final DN[] parentDns = new DN[]
            { this.dnBuilderWidget.getParentDn() };
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


    public IWizardPage getPreviousPage()
    {
        return super.getPreviousPage();
    }


    public void createControl( Composite parent )
    {
        this.dnBuilderWidget = new DnBuilderWidget( true, true );
        this.dnBuilderWidget.addWidgetModifyListener( this );
        Composite composite = this.dnBuilderWidget.createContents( parent );

        setControl( composite );
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    public void saveDialogSettings()
    {
        this.dnBuilderWidget.saveDialogSettings();
    }

}