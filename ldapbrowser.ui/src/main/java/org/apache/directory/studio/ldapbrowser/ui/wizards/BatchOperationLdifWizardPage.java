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


import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyEvent;
import org.apache.directory.studio.ldapbrowser.common.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldifeditor.widgets.LdifEditorWidget;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class BatchOperationLdifWizardPage extends WizardPage implements WidgetModifyListener
{

    private static final String LDIF_DN_PREFIX = "dn: cn=dummy" + BrowserCoreConstants.LINE_SEPARATOR; //$NON-NLS-1$

    private static final String LDIF_INITIAL = "changetype: modify" + BrowserCoreConstants.LINE_SEPARATOR; //$NON-NLS-1$

    private BatchOperationWizard wizard;

    private LdifEditorWidget ldifEditorWidget;


    public BatchOperationLdifWizardPage( String pageName, BatchOperationWizard wizard )
    {
        super( pageName );
        super.setTitle( Messages.getString( "BatchOperationLdifWizardPage.LDIFFragment" ) ); //$NON-NLS-1$
        super.setDescription( Messages.getString( "BatchOperationLdifWizardPage.PleaseEnterLDIFFragment" ) ); //$NON-NLS-1$
        // super.setImageDescriptor(BrowserUIPlugin.getDefault().getImageDescriptor(BrowserUIConstants.IMG_ENTRY_WIZARD));
        super.setPageComplete( false );

        this.wizard = wizard;
    }


    public void dispose()
    {
        ldifEditorWidget.dispose();
        super.dispose();
    }


    private void validate()
    {

        LdifFile model = ldifEditorWidget.getLdifModel();
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


    public boolean isPageComplete()
    {

        if ( wizard.getTypePage().getOperationType() != BatchOperationTypeWizardPage.OPERATION_TYPE_CREATE_LDIF )
        {
            return true;
        }

        return super.isPageComplete();
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        ldifEditorWidget = new LdifEditorWidget( null, LDIF_DN_PREFIX + LDIF_INITIAL, true );
        ldifEditorWidget.createWidget( composite );
        ldifEditorWidget.addWidgetModifyListener( this );

        ldifEditorWidget.getSourceViewer().getTextWidget().addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( e.start < LDIF_DN_PREFIX.length() || e.end < LDIF_DN_PREFIX.length() )
                {
                    e.doit = false;
                }
            }
        } );

        validate();

        setControl( composite );
    }


    public String getLdifFragment()
    {
        return ldifEditorWidget.getLdifModel().toRawString().replaceAll( LDIF_DN_PREFIX, "" ); //$NON-NLS-1$
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        validate();
    }

}