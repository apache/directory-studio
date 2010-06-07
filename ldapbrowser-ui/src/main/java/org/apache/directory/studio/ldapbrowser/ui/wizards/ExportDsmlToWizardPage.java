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


import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.wizards.ExportDsmlWizard.ExportDsmlWizardSaveAsType;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


/**
 * This class implements the page to select the target DSML file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportDsmlToWizardPage extends ExportBaseToPage
{
    /** The associated wizard */
    private ExportDsmlWizard wizard;

    /** The extensions used by DSML files*/
    private static final String[] EXTENSIONS = new String[]
        { "*.xml", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$


    /**
     * Creates a new instance of ExportDsmlToWizardPage.
     *
     * @param pageName
     *          the name of the page
     * @param wizard
     *          the wizard the page is attached to
     */
    public ExportDsmlToWizardPage( String pageName, ExportDsmlWizard wizard )
    {
        super( pageName, wizard );
        this.wizard = wizard;
        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_EXPORT_DSML_WIZARD ) );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        final Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        super.createControl( composite );

        Composite saveAsOuterComposite = BaseWidgetUtils.createColumnContainer( composite, 1, 3 );
        Group saveAsGroup = BaseWidgetUtils.createGroup( saveAsOuterComposite, Messages
            .getString( "ExportDsmlToWizardPage.SaveAs" ), 1 ); //$NON-NLS-1$
        Composite saveAsComposite = BaseWidgetUtils.createColumnContainer( saveAsGroup, 2, 1 );

        Button saveAsDsmlResponseButton = BaseWidgetUtils.createRadiobutton( saveAsComposite, Messages
            .getString( "ExportDsmlToWizardPage.DSMLResponse" ), 2 ); //$NON-NLS-1$
        saveAsDsmlResponseButton.setSelection( true );
        wizard.setSaveAsType( ExportDsmlWizardSaveAsType.RESPONSE );
        saveAsDsmlResponseButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                wizard.setSaveAsType( ExportDsmlWizardSaveAsType.RESPONSE );
            }
        } );
        BaseWidgetUtils.createRadioIndent( saveAsComposite, 1 );
        BaseWidgetUtils.createWrappedLabel( saveAsComposite, Messages
            .getString( "ExportDsmlToWizardPage.SearchSaveAsResponse" ), 1 ); //$NON-NLS-1$

        Button saveAsDsmlRequestButton = BaseWidgetUtils.createRadiobutton( saveAsComposite, Messages
            .getString( "ExportDsmlToWizardPage.DSMLRequest" ), 2 ); //$NON-NLS-1$
        saveAsDsmlRequestButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                wizard.setSaveAsType( ExportDsmlWizardSaveAsType.REQUEST );
            }
        } );
        BaseWidgetUtils.createRadioIndent( saveAsComposite, 1 );
        BaseWidgetUtils.createWrappedLabel( saveAsComposite, Messages
            .getString( "ExportDsmlToWizardPage.SearchSaveAsRequest" ), 1 ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected String[] getExtensions()
    {
        return EXTENSIONS;
    }


    /**
     * {@inheritDoc}
     */
    protected String getFileType()
    {
        return Messages.getString( "ExportDsmlToWizardPage.DSML" ); //$NON-NLS-1$
    }
}
