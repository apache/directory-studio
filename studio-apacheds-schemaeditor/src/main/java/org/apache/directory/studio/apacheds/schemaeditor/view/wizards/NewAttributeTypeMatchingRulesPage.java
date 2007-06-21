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
package org.apache.directory.studio.apacheds.schemaeditor.view.wizards;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the Matching Rules WizardPage of the NewAttributeTypeWizard.
 * <p>
 * It is used to let the user enter matching rules information about the
 * attribute type he wants to create (equality, ordering, substring).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewAttributeTypeMatchingRulesPage extends WizardPage
{
    /**
     * Creates a new instance of NewAttributeTypeMatchingRulesPage.
     */
    public NewAttributeTypeMatchingRulesPage()
    {
        super( "MatchingRules" );
        setTitle( "Matching Rules" );
        setDescription( "Please specify the matching rules (equality, ordering and substring) to use for the attribute type." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_ATTRIBUTE_TYPE_NEW_WIZARD ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Matching Rules Group
        Group matchingRulesGroup = new Group( composite, SWT.NONE );
        matchingRulesGroup.setText( "Matching Rules" );
        matchingRulesGroup.setLayout( new GridLayout( 2, false ) );
        matchingRulesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Equality
        Label equalityLabel = new Label( matchingRulesGroup, SWT.NONE );
        equalityLabel.setText( "Equality" );
        Combo equalityCombo = new Combo( matchingRulesGroup, SWT.NONE );
        equalityCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Ordering
        Label orderingLabel = new Label( matchingRulesGroup, SWT.NONE );
        orderingLabel.setText( "Ordering" );
        Combo orderingCombo = new Combo( matchingRulesGroup, SWT.NONE );
        orderingCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Substring
        Label substringLabel = new Label( matchingRulesGroup, SWT.NONE );
        substringLabel.setText( "Substring" );
        Combo substringCombo = new Combo( matchingRulesGroup, SWT.NONE );
        substringCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        setControl( composite );
    }
}
