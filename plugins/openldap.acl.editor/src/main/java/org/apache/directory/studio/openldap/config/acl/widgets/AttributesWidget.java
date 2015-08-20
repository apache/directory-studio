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
package org.apache.directory.studio.openldap.config.acl.widgets;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPlugin;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPluginConstants;
import org.apache.directory.studio.openldap.config.acl.model.AclAttribute;
import org.apache.directory.studio.openldap.config.acl.model.AclAttributeStyleEnum;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseAttributes;
import org.apache.directory.studio.openldap.config.acl.wrapper.AclAttributeDecorator;
import org.apache.directory.studio.openldap.config.acl.wrapper.AclAttributeWrapper;


/**
 * A widget used to create an AclWhatClause Attribute :
 * 
 * <pre>
 * ...
 * | .--------------------------------------------------------. |
 * | | Attribute list :                                       | |
 * | | +-------------------------------------------+          | |
 * | | | abc                                       | (Add)    | |
 * | | | !def                                      | (Edit)   | |
 * | | | entry                                     | (Delete) | |
 * | | +-------------------------------------------+          | |
 * | | Val : [ ]  MatchingRule : [ ] Style : [--------------] | |
 * | | Value : [////////////////////////////////////////////] | |
 * | `--------------------------------------------------------' |
 * ...
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributesWidget extends AbstractWidget
{
    /** The Attributes table */
    private TableWidget<AclAttributeWrapper> attributeTable;
    
    /** The WhatAttributes clause */
    private AclWhatClauseAttributes aclWhatClauseAttributes;
    
    /** The checkbox for the Val */
    private Button valButton;
    
    /** The checkbox for the matchingrule */
    private Button matchingRuleButton;
    
    /** The style combo */
    private Combo styleCombo;
    
    /** The Value Text */
    private Text valueText;
    
    /** The initial attributes. */
    private String[] initialAttributes;

    /** The proposal provider */
    private AttributesWidgetContentProposalProvider proposalProvider;

    /** The proposal adapter*/
    private ContentProposalAdapter proposalAdapter;

    /** The label provider for the proposal adapter */
    private LabelProvider labelProvider = new LabelProvider()
    {
        public String getText( Object element )
        {
            if ( element instanceof IContentProposal )
            {
                IContentProposal proposal = ( IContentProposal ) element;
                return proposal.getLabel() == null ? proposal.getContent() : proposal.getLabel();
            }

            return super.getText( element );
        };


        public Image getImage( Object element )
        {
            if ( element instanceof AttributeTypeContentProposal )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ATD );
            }
            else if ( element instanceof ObjectClassContentProposal )
            {
                return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_OCD );
            }
            else if ( element instanceof KeywordContentProposal )
            {
                return OpenLdapAclEditorPlugin.getDefault().getImage( OpenLdapAclEditorPluginConstants.IMG_KEYWORD );
            }

            return super.getImage( element );
        }
    };

    /** The verify listener which doesn't allow white spaces*/
    private VerifyListener verifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            // Not allowing white spaces
            if ( Character.isWhitespace( e.character ) )
            {
                e.doit = false;
            }
        }
    };
    

    /** The modify listener */
    private ModifyListener modifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            notifyListeners();
        }
    };
    
    
    /** The Val button listener */
    private SelectionAdapter valButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent event )
        {
            // If the Val Button is selected, then the MatchingRule Button, 
            // the Style Combo and the value Text must be enabled
            boolean valSelected = valButton.getSelection();

            matchingRuleButton.setEnabled( valSelected );
            styleCombo.setEnabled( valSelected );
            valueText.setEnabled( valSelected );
            aclWhatClauseAttributes.setVal( valSelected );
            
            // TODO : disable the OK button if Val is set and there is no value
        }
    };
    
    
    /** The MatchingRule button listener */
    private SelectionAdapter matchingRuleButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent event )
        {
            aclWhatClauseAttributes.setMatchingRule( matchingRuleButton.getSelection() );
        }
    };
    
    
    /** The style combo listener */
    private SelectionAdapter styleComboListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent event )
        {
            aclWhatClauseAttributes.setStyle( AclAttributeStyleEnum.getStyle( styleCombo.getText() ) );
        }
    };
    
    
    /**
     * Creates the widget.
     * <pre>
     * Attribute list :
     * +-------------------------------------------+
     * | abc                                       | (Add)   
     * | !def                                      | (Edit)  
     * | entry                                     | (Delete)
     * +-------------------------------------------+         
     * Val : [ ]  MatchingRule : [ ] Style : [--------------]
     * Value : [////////////////////////////////////////////]
     * </pre>
     * 
     * @param parent the parent
     */
    public void createWidget( Composite parent, IBrowserConnection connection, AclWhatClauseAttributes clause )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 4, 1 );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
        gd.horizontalSpan = 1;
        gd.widthHint = 30;
        composite.setLayoutData( gd );

        // The Attribute table
        BaseWidgetUtils.createLabel( composite, "Attributes list :", 4 );
        AclAttributeDecorator decorator = new AclAttributeDecorator( composite.getShell(), connection );
        attributeTable = new TableWidget<AclAttributeWrapper>( decorator );
        attributeTable.createWidgetWithEdit( composite, null );
        attributeTable.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 4, 3 ) );
        //attributeTable.addWidgetModifyListener( attributeTableListener );
        
        // The Val
        valButton = BaseWidgetUtils.createCheckbox( composite, "Val", 1 );
        valButton.addSelectionListener( valButtonListener );
        
        // The MatchingRule
        matchingRuleButton = BaseWidgetUtils.createCheckbox( composite, "MatchingRule", 1 );
        matchingRuleButton.setEnabled( false );
        matchingRuleButton.addSelectionListener( matchingRuleButtonListener );
        
        // The style
        BaseWidgetUtils.createLabel( composite, "Style :", 1 );
        styleCombo = BaseWidgetUtils.createCombo( composite, AclAttributeStyleEnum.getNames(), 9, 1 );
        styleCombo.setEnabled( false );
        styleCombo.addSelectionListener( styleComboListener );

        // The value
        BaseWidgetUtils.createLabel( composite, "Value :", 1 );
        valueText = BaseWidgetUtils.createText( composite, "", 3 );
        valueText.setEnabled( false );
        //valueText.addModifyListener( valueTextListener );
        
        initWidget( clause );
    }


    /**
     * Initialize the widget with the current value
     */
    private void initWidget( AclWhatClauseAttributes clause )
    {
        aclWhatClauseAttributes = clause;
        
        // Update the table
        setAttributes( clause.getAttributes() );
        
        // The Val button is always enabled
        valButton.setEnabled( true );
        
        if ( clause.hasVal() )
        {
            matchingRuleButton.setEnabled( clause.hasMatchingRule() );
            styleCombo.setEnabled( false );
            styleCombo.setText( clause.getStyle().getName() );
            valueText.setEnabled( false );
            valueText.setText( CommonUIUtils.getTextValue( clause.getValue() ) );
        }
        else
        {
            matchingRuleButton.setEnabled( false );
            styleCombo.setEnabled( false );
            valueText.setEnabled( false );
        }
    }
    
    
    /**
     * Sets the initial attributes.
     * 
     * @param aclAttributes the initial attributes
     */
    private void setAttributes( List<AclAttribute> aclAttributes )
    {
        List<AclAttributeWrapper> aclAttributeWrappers = new ArrayList<AclAttributeWrapper>( aclAttributes.size() );
        
        for ( AclAttribute aclAttribute: aclAttributes )
        {
            AclAttributeWrapper aclAttributeWrapper = new AclAttributeWrapper( aclAttribute );
            aclAttributeWrappers.add( aclAttributeWrapper );
        }
        
        attributeTable.setElements( aclAttributeWrappers );
    }


    /**
     * Sets the enabled state of the widget.
     * 
     * @param b true to enable the widget, false to disable the widget
     */
    public void setEnabled( boolean b )
    {
    }


    /**
     * Gets the attributes.
     * 
     * @return the attributes
     */
    public List<AclAttribute> getAttributes()
    {
        List<AclAttributeWrapper> elementList = attributeTable.getElements();
        
        List<AclAttribute> result = new ArrayList<AclAttribute>( elementList.size() );
        
        for ( AclAttributeWrapper element : elementList )
        {
            result.add( element.getAclAttribute() );
        }
        
        return result;
    }
}
