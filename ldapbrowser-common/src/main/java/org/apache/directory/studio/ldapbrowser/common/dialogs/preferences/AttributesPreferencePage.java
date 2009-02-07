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

package org.apache.directory.studio.ldapbrowser.common.dialogs.preferences;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The AttributesPreferencePage contains general settings for attributes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Button showDecoratedValuesButton;


    /**
     * Creates a new instance of AttributesPreferencePage.
     */
    public AttributesPreferencePage()
    {
        super( Messages.getString("AttributesPreferencePage.Attributes") ); //$NON-NLS-1$
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString("AttributesPreferencePage.GeneralSettings") ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        showDecoratedValuesButton = BaseWidgetUtils.createCheckbox( composite, Messages.getString("AttributesPreferencePage.ShowDecoratedValues"), 1 ); //$NON-NLS-1$
        showDecoratedValuesButton.setSelection( !getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES,
            !showDecoratedValuesButton.getSelection() );
        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        showDecoratedValuesButton.setSelection( !getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );
        super.performDefaults();
    }

}
