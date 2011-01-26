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
package org.apache.directory.studio.schemaeditor.view.wizards;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;


/**
 * This class represents the Matching Rules WizardPage of the NewAttributeTypeWizard.
 * <p>
 * It is used to let the user enter matching rules information about the
 * attribute type he wants to create (equality, ordering, substring).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewAttributeTypeMatchingRulesWizardPage extends WizardPage
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The LabelProvider */
    private LabelProvider labelProvider = new LabelProvider()
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        public String getText( Object element )
        {
            if ( element instanceof MatchingRule )
            {
                MatchingRule mr = ( MatchingRule ) element;

                String name = mr.getName();
                if ( name != null )
                {
                    return NLS
                        .bind(
                            Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.NameOID" ), new String[] { name, mr.getOid() } ); //$NON-NLS-1$
                }
                else
                {
                    return NLS
                        .bind(
                            Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.NoneOID" ), new String[] { mr.getOid() } ); //$NON-NLS-1$
                }
            }

            return super.getText( element );
        }
    };

    // UI fields
    private ComboViewer equalityComboViewer;
    private ComboViewer orderingComboViewer;
    private ComboViewer substringComboViewer;


    /**
     * Creates a new instance of NewAttributeTypeMatchingRulesWizardPage.
     */
    public NewAttributeTypeMatchingRulesWizardPage()
    {
        super( "NewAttributeTypeMatchingRulesWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.MatchingRules" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.PleaseSpecifiyMatchingRules" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_ATTRIBUTE_TYPE_NEW_WIZARD ) );

        schemaHandler = Activator.getDefault().getSchemaHandler();
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
        matchingRulesGroup.setText( Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.MatchingRules" ) ); //$NON-NLS-1$
        matchingRulesGroup.setLayout( new GridLayout( 2, false ) );
        matchingRulesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 3, 1 ) );

        // Equality
        Label equalityLabel = new Label( matchingRulesGroup, SWT.NONE );
        equalityLabel.setText( Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.Equality" ) ); //$NON-NLS-1$
        Combo equalityCombo = new Combo( matchingRulesGroup, SWT.READ_ONLY );
        equalityCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        equalityComboViewer = new ComboViewer( equalityCombo );
        equalityComboViewer.setContentProvider( new ArrayContentProvider() );
        equalityComboViewer.setLabelProvider( labelProvider );

        // Ordering
        Label orderingLabel = new Label( matchingRulesGroup, SWT.NONE );
        orderingLabel.setText( Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.Ordering" ) ); //$NON-NLS-1$
        Combo orderingCombo = new Combo( matchingRulesGroup, SWT.READ_ONLY );
        orderingCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        orderingComboViewer = new ComboViewer( orderingCombo );
        orderingComboViewer.setContentProvider( new ArrayContentProvider() );
        orderingComboViewer.setLabelProvider( labelProvider );

        // Substring
        Label substringLabel = new Label( matchingRulesGroup, SWT.NONE );
        substringLabel.setText( Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.Substring" ) ); //$NON-NLS-1$
        Combo substringCombo = new Combo( matchingRulesGroup, SWT.READ_ONLY );
        substringCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        substringComboViewer = new ComboViewer( substringCombo );
        substringComboViewer.setContentProvider( new ArrayContentProvider() );
        substringComboViewer.setLabelProvider( labelProvider );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI fields.
     */
    @SuppressWarnings("unchecked")
    private void initFields()
    {
        if ( schemaHandler != null )
        {
            // Getting the matching rules
            List<Object> matchingRules = new ArrayList( schemaHandler.getMatchingRules() );
            // Adding the (None) matching rule
            String none = Messages.getString( "NewAttributeTypeMatchingRulesWizardPage.None" ); //$NON-NLS-1$
            matchingRules.add( none );

            // Sorting the matching rules
            Collections.sort( matchingRules, new Comparator<Object>()
            {

                public int compare( Object o1, Object o2 )
                {
                    if ( ( o1 instanceof MatchingRule ) && ( o2 instanceof MatchingRule ) )
                    {
                        List<String> o1Names = ( ( MatchingRule ) o1 ).getNames();
                        List<String> o2Names = ( ( MatchingRule ) o2 ).getNames();

                        // Comparing the First Name
                        if ( ( o1Names != null ) && ( o2Names != null ) )
                        {
                            if ( ( o1Names.size() > 0 ) && ( o2Names.size() > 0 ) )
                            {
                                return o1Names.get( 0 ).compareToIgnoreCase( o2Names.get( 0 ) );
                            }
                            else if ( ( o1Names.size() == 0 ) && ( o2Names.size() > 0 ) )
                            {
                                return "".compareToIgnoreCase( o2Names.get( 0 ) ); //$NON-NLS-1$
                            }
                            else if ( ( o1Names.size() > 0 ) && ( o2Names.size() == 0 ) )
                            {
                                return o1Names.get( 0 ).compareToIgnoreCase( "" ); //$NON-NLS-1$
                            }
                        }
                        else if ( ( o1 instanceof String ) && ( o2 instanceof MatchingRule ) )
                        {
                            return Integer.MIN_VALUE;
                        }
                        else if ( ( o1 instanceof MatchingRule ) && ( o2 instanceof String ) )
                        {
                            return Integer.MAX_VALUE;
                        }
                    }

                    // Default
                    return o1.toString().compareToIgnoreCase( o2.toString() );
                }
            } );

            // Setting the input
            equalityComboViewer.setInput( matchingRules );
            orderingComboViewer.setInput( matchingRules );
            substringComboViewer.setInput( matchingRules );

            // Selecting the None matching rules
            equalityComboViewer.setSelection( new StructuredSelection( none ) );
            orderingComboViewer.setSelection( new StructuredSelection( none ) );
            substringComboViewer.setSelection( new StructuredSelection( none ) );
        }
    }


    /**
     * Gets the value of the equality matching rule.
     *
     * @return
     *      the value of the equality matching rule
     */
    public String getEqualityMatchingRuleValue()
    {
        Object selection = ( ( StructuredSelection ) equalityComboViewer.getSelection() ).getFirstElement();

        if ( selection instanceof MatchingRule )
        {
            MatchingRule mr = ( ( MatchingRule ) selection );

            List<String> names = mr.getNames();
            if ( ( names != null ) && ( names.size() > 0 ) )
            {
                return mr.getName();
            }
            else
            {
                return mr.getOid();
            }
        }

        return null;
    }


    /**
     * Gets the value of the ordering matching rule.
     *
     * @return
     *      the value of the ordering matching rule
     */
    public String getOrderingMatchingRuleValue()
    {
        Object selection = ( ( StructuredSelection ) orderingComboViewer.getSelection() ).getFirstElement();

        if ( selection instanceof MatchingRule )
        {
            MatchingRule mr = ( ( MatchingRule ) selection );

            List<String> names = mr.getNames();
            if ( ( names != null ) && ( names.size() > 0 ) )
            {
                return mr.getName();
            }
            else
            {
                return mr.getOid();
            }
        }

        return null;
    }


    /**
     * Gets the value of the substring matching rule.
     *
     * @return
     *      the value of the substring matching rule
     */
    public String getSubstringMatchingRuleValue()
    {
        Object selection = ( ( StructuredSelection ) substringComboViewer.getSelection() ).getFirstElement();

        if ( selection instanceof MatchingRule )
        {
            MatchingRule mr = ( ( MatchingRule ) selection );

            List<String> names = mr.getNames();
            if ( ( names != null ) && ( names.size() > 0 ) )
            {
                return mr.getName();
            }
            else
            {
                return mr.getOid();
            }
        }

        return null;
    }
}
