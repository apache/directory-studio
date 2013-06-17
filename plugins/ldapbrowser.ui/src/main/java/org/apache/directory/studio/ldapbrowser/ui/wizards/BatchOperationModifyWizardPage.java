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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import org.apache.directory.studio.ldapbrowser.common.widgets.ModWidget;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.parser.LdifParser;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * This class implements the "Modify" page for the Batch Operation Wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BatchOperationModifyWizardPage extends WizardPage implements WidgetModifyListener
{
    /** The wizard */
    private BatchOperationWizard wizard;

    /** The ModWidget */
    private ModWidget modWidget;


    /**
     * Creates a new instance of BatchOperationModifyWizardPage.
     *
     * @param pageName the name of the page
     * @param wizard the wizard
     */
    public BatchOperationModifyWizardPage( String pageName, BatchOperationWizard wizard )
    {
        super( pageName );
        super.setTitle( Messages.getString( "BatchOperationModifyWizardPage.DefineModification" ) ); //$NON-NLS-1$
        super.setDescription( Messages.getString( "BatchOperationModifyWizardPage.PleaseDefineModifications" ) ); //$NON-NLS-1$
        // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ENTRY_WIZARD));
        super.setPageComplete( false );

        this.wizard = wizard;
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        modWidget = new ModWidget( wizard.getConnection() != null ? wizard.getConnection().getSchema()
            : Schema.DEFAULT_SCHEMA );
        modWidget.createContents( composite );
        modWidget.addWidgetModifyListener( this );

        validate();

        setControl( composite );

    }


    /**
     * Gets the LDIF fragment.
     *
     * @return the LDIF fragment
     */
    public String getLdifFragment()
    {
        return modWidget.getLdifFragment();
    }


    /**
     * {@inheritDoc}
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }


    /**
     * Validates the page
     */
    private void validate()
    {
        String dummyLdif = "dn: cn=dummy" + BrowserCoreConstants.LINE_SEPARATOR + modWidget.getLdifFragment(); //$NON-NLS-1$
        
        LdifFile model = new LdifParser().parse( dummyLdif );
        
        LdifContainer[] containers = model.getContainers();
        
        if ( containers.length == 0 )
        {
            setPageComplete( false );
            return;
        }
        
        for ( int i = 0; i < containers.length; i++ )
        {
            if ( !containers[i].isValid() )
            {
                setPageComplete( false );
                return;
            }
        }

        setPageComplete( true );
    }
}
