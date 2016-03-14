/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.widgets.composites;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AbstractAclWhoClauseCryptoStrength;
import org.apache.directory.studio.openldap.config.acl.widgets.AclWhoClauseSsfValuesEnum;


/**
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 *
 * @param <C>
 */
public class AbstractWhoClauseCryptoStrengthComposite<C extends AbstractAclWhoClauseCryptoStrength> extends
    AbstractWhoClauseComposite<C>
{
    /** The array of SSF who clause values */
    private static final AclWhoClauseSsfValuesEnum[] aclWhoClauseSsfValues = new AclWhoClauseSsfValuesEnum[]
        {
            AclWhoClauseSsfValuesEnum.ANY,
            AclWhoClauseSsfValuesEnum.FORTY,
            AclWhoClauseSsfValuesEnum.FIFTY_SIX,
            AclWhoClauseSsfValuesEnum.SIXTY_FOUR,
            AclWhoClauseSsfValuesEnum.ONE_TWENTY_HEIGHT,
            AclWhoClauseSsfValuesEnum.ONE_SIXTY_FOUR,
            AclWhoClauseSsfValuesEnum.TWO_FIFTY_SIX,
            AclWhoClauseSsfValuesEnum.CUSTOM
    };

    /** The SSF values combo viewer */
    private ComboViewer ssfValuesComboViewer;

    /** The custom SSF value spinner */
    private Spinner customSsfValueSpinner;

    /** The current SSF value */
    private AclWhoClauseSsfValuesEnum currentSsfValue;


    /**
     * Creates a new instance of AbstractWhoClauseCryptoStrengthComposite.
     *
     * @param clause the clause
     * @param visualEditorComposite the visual editor composite
     */
    public AbstractWhoClauseCryptoStrengthComposite( OpenLdapAclValueWithContext context, C clause, Composite visualEditorComposite )
    {
        super( context, clause, visualEditorComposite );
    }


    public Composite createComposite( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );

        // SSF Value Label
        BaseWidgetUtils.createLabel( composite, "SSF Value:", 1 );

        // SSF Values Combo Viewer
        ssfValuesComboViewer = new ComboViewer( BaseWidgetUtils.createReadonlyCombo(
            composite, new String[0], -1, 1 ) );
        ssfValuesComboViewer.getCombo().setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false ) );
        ssfValuesComboViewer.setContentProvider( new ArrayContentProvider() );
        ssfValuesComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof AclWhoClauseSsfValuesEnum )
                {
                    AclWhoClauseSsfValuesEnum value = ( AclWhoClauseSsfValuesEnum ) element;
                    switch ( value )
                    {
                        case ANY:
                            return "1 (Any)";
                        case FORTY:
                            return "40";
                        case FIFTY_SIX:
                            return "56";
                        case SIXTY_FOUR:
                            return "64";
                        case ONE_TWENTY_HEIGHT:
                            return "128";
                        case ONE_SIXTY_FOUR:
                            return "164";
                        case TWO_FIFTY_SIX:
                            return "256";
                        case CUSTOM:
                            return "Custom";
                    }
                }

                return super.getText( element );
            }
        } );
        ssfValuesComboViewer.setInput( aclWhoClauseSsfValues );
        ssfValuesComboViewer.setSelection( new StructuredSelection( aclWhoClauseSsfValues[0] ) );
        ssfValuesComboViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                // Getting the selected who clause
                AclWhoClauseSsfValuesEnum ssfValue = ( AclWhoClauseSsfValuesEnum ) ( ( StructuredSelection ) ssfValuesComboViewer
                    .getSelection() ).getFirstElement();

                // Only changing the UI when the clause is different
                if ( currentSsfValue != ssfValue )
                {
                    // Storing the current value
                    currentSsfValue = ssfValue;

                    // Making the spinner hidden/visible (depending on the choice
                    customSsfValueSpinner.setVisible( AclWhoClauseSsfValuesEnum.CUSTOM.equals( currentSsfValue ) );

                    // Refreshing the layout of the parent composite
                    visualEditorComposite.layout( true, true );
                }
            }
        } );

        // Custom SSF Value Spinner
        customSsfValueSpinner = new Spinner( composite, SWT.BORDER );
        customSsfValueSpinner.setMinimum( 1 );
        customSsfValueSpinner.setTextLimit( 4 );
        customSsfValueSpinner.setVisible( false );
        customSsfValueSpinner.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                System.out.println( customSsfValueSpinner.getSelection() );
            }
        } );

        return composite;
    }
}
