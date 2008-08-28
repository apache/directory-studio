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

package org.apache.directory.studio.ldapbrowser.ui.dialogs.properties;


import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * This page shows some info about the selected Attribute.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributePropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{

    /** The attribute name text. */
    private Text attributeNameText;

    /** The attribute type text. */
    private Text attributeTypeText;

    /** The attribute values text. */
    private Text attributeValuesText;

    /** The attribute size text. */
    private Text attributeSizeText;

    /** The atd oid text. */
    private Text atdOidText;

    /** The atd names text. */
    private Text atdNamesText;

    /** The atd desc text. */
    private Text atdDescText;

    /** The atd usage text. */
    private Text atdUsageText;

    /** The single valued flag. */
    private Button singleValuedFlag;

    /** The collective flag. */
    private Button collectiveFlag;

    /** The obsolete flag. */
    private Button obsoleteFlag;

    /** The no user modification flag. */
    private Button noUserModificationFlag;

    /** The equality matching rule text. */
    private Text equalityMatchingRuleText;

    /** The substring matching rule text. */
    private Text substringMatchingRuleText;

    /** The ordering matching rule text. */
    private Text orderingMatchingRuleText;

    /** The syntax oid text. */
    private Text syntaxOidText;

    /** The syntax desc text. */
    private Text syntaxDescText;

    /** The syntax length text. */
    private Text syntaxLengthText;


    /**
     * Creates a new instance of AttributePropertyPage.
     */
    public AttributePropertyPage()
    {
        super();
        super.noDefaultAndApplyButton();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        Composite mainGroup = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );

        BaseWidgetUtils.createLabel( mainGroup, "Description:", 1 );
        attributeNameText = BaseWidgetUtils.createLabeledText( mainGroup, "", 1 );
        GridData attributeNameTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        attributeNameTextGridData.widthHint = 300;
        attributeNameText.setLayoutData( attributeNameTextGridData );

        BaseWidgetUtils.createLabel( mainGroup, "Type:", 1 );
        attributeTypeText = BaseWidgetUtils.createLabeledText( mainGroup, "", 1 );
        GridData attributeTypeTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        attributeTypeTextGridData.widthHint = 300;
        attributeTypeText.setLayoutData( attributeTypeTextGridData );

        BaseWidgetUtils.createLabel( mainGroup, "Number of Values:", 1 );
        attributeValuesText = BaseWidgetUtils.createLabeledText( mainGroup, "", 1 );
        GridData attributeValuesTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        attributeValuesTextGridData.widthHint = 300;
        attributeValuesText.setLayoutData( attributeValuesTextGridData );

        BaseWidgetUtils.createLabel( mainGroup, "Attribute Size:", 1 );
        attributeSizeText = BaseWidgetUtils.createLabeledText( mainGroup, "", 1 );
        GridData attributeSizeTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        attributeSizeTextGridData.widthHint = 300;
        attributeSizeText.setLayoutData( attributeSizeTextGridData );

        Group atdGroup = BaseWidgetUtils.createGroup( composite, "Attribute Type", 1 );
        Composite atdComposite = BaseWidgetUtils.createColumnContainer( atdGroup, 2, 1 );

        BaseWidgetUtils.createLabel( atdComposite, "Numeric OID:", 1 );
        atdOidText = BaseWidgetUtils.createLabeledText( atdComposite, "", 1 );
        GridData atdOidTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        atdOidTextGridData.widthHint = 300;
        atdOidText.setLayoutData( atdOidTextGridData );

        BaseWidgetUtils.createLabel( atdComposite, "Alternative Names:", 1 );
        atdNamesText = BaseWidgetUtils.createLabeledText( atdComposite, "", 1 );
        GridData atdNamesTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        atdNamesTextGridData.widthHint = 300;
        atdNamesText.setLayoutData( atdNamesTextGridData );

        BaseWidgetUtils.createLabel( atdComposite, "Description:", 1 );
        atdDescText = BaseWidgetUtils.createWrappedLabeledText( atdComposite, "", 1 );
        GridData atdDescTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        atdDescTextGridData.widthHint = 300;
        atdDescText.setLayoutData( atdDescTextGridData );

        BaseWidgetUtils.createLabel( atdComposite, "Usage:", 1 );
        atdUsageText = BaseWidgetUtils.createLabeledText( atdComposite, "", 1 );
        GridData atdUsageTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        atdUsageTextGridData.widthHint = 300;
        atdUsageText.setLayoutData( atdUsageTextGridData );

        Group flagsGroup = BaseWidgetUtils.createGroup( composite, "Flags", 1 );
        Composite flagsComposite = BaseWidgetUtils.createColumnContainer( flagsGroup, 4, 1 );

        singleValuedFlag = BaseWidgetUtils.createCheckbox( flagsComposite, "Single valued", 1 );
        singleValuedFlag.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                singleValuedFlag.setSelection( !singleValuedFlag.getSelection() );
            }
        } );

        noUserModificationFlag = BaseWidgetUtils.createCheckbox( flagsComposite, "Read only", 1 );
        noUserModificationFlag.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                noUserModificationFlag.setSelection( !noUserModificationFlag.getSelection() );
            }
        } );

        collectiveFlag = BaseWidgetUtils.createCheckbox( flagsComposite, "Collective", 1 );
        collectiveFlag.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                collectiveFlag.setSelection( !collectiveFlag.getSelection() );
            }
        } );

        obsoleteFlag = BaseWidgetUtils.createCheckbox( flagsComposite, "Obsolete", 1 );
        obsoleteFlag.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                obsoleteFlag.setSelection( !obsoleteFlag.getSelection() );
            }
        } );

        Group syntaxGroup = BaseWidgetUtils.createGroup( composite, "Syntax", 1 );
        Composite syntaxComposite = BaseWidgetUtils.createColumnContainer( syntaxGroup, 2, 1 );

        BaseWidgetUtils.createLabel( syntaxComposite, "Syntax OID:", 1 );
        syntaxOidText = BaseWidgetUtils.createLabeledText( syntaxComposite, "", 1 );
        GridData syntaxOidTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        syntaxOidTextGridData.widthHint = 300;
        syntaxOidText.setLayoutData( syntaxOidTextGridData );

        BaseWidgetUtils.createLabel( syntaxComposite, "Syntax Description:", 1 );
        syntaxDescText = BaseWidgetUtils.createLabeledText( syntaxComposite, "", 1 );
        GridData syntaxDescTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        syntaxDescTextGridData.widthHint = 300;
        syntaxDescText.setLayoutData( syntaxDescTextGridData );

        BaseWidgetUtils.createLabel( syntaxComposite, "Syntax Length:", 1 );
        syntaxLengthText = BaseWidgetUtils.createLabeledText( syntaxComposite, "", 1 );
        GridData syntaxLengthTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        syntaxLengthTextGridData.widthHint = 300;
        syntaxLengthText.setLayoutData( syntaxLengthTextGridData );

        Group matchingGroup = BaseWidgetUtils.createGroup( composite, "Matching Rules", 1 );
        Composite matchingComposite = BaseWidgetUtils.createColumnContainer( matchingGroup, 2, 1 );

        BaseWidgetUtils.createLabel( matchingComposite, "Equality Match:", 1 );
        equalityMatchingRuleText = BaseWidgetUtils.createLabeledText( matchingComposite, "", 1 );
        GridData equalityMatchingRuleTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        equalityMatchingRuleTextGridData.widthHint = 300;
        equalityMatchingRuleText.setLayoutData( equalityMatchingRuleTextGridData );

        BaseWidgetUtils.createLabel( matchingComposite, "Substring Match:", 1 );
        substringMatchingRuleText = BaseWidgetUtils.createLabeledText( matchingComposite, "", 1 );
        GridData substringMatchingRuleTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        substringMatchingRuleTextGridData.widthHint = 300;
        substringMatchingRuleText.setLayoutData( substringMatchingRuleTextGridData );

        BaseWidgetUtils.createLabel( matchingComposite, "Ordering Match:", 1 );
        orderingMatchingRuleText = BaseWidgetUtils.createLabeledText( matchingComposite, "", 1 );
        GridData orderingMatchingRuleTextGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        orderingMatchingRuleTextGridData.widthHint = 300;
        orderingMatchingRuleText.setLayoutData( orderingMatchingRuleTextGridData );

        IAttribute attribute = getAttribute( getElement() );
        if ( attribute != null )
        {
            Schema schema = attribute.getEntry().getBrowserConnection().getSchema();

            int bytes = 0;
            int valCount = 0;
            IValue[] allValues = attribute.getValues();
            for ( int valIndex = 0; valIndex < allValues.length; valIndex++ )
            {
                if ( !allValues[valIndex].isEmpty() )
                {
                    valCount++;
                    bytes += allValues[valIndex].getBinaryValue().length;
                }
            }

            this.setMessage( "Attribute " + attribute.getDescription() );
            attributeNameText.setText( attribute.getDescription() );
            attributeTypeText.setText( attribute.isString() ? "String" : "Binary" );
            attributeValuesText.setText( "" + valCount );
            attributeSizeText.setText( Utils.formatBytes( bytes ) );

            if ( schema.hasAttributeTypeDescription( attribute.getDescription() ) )
            {
                AttributeTypeDescription atd = schema.getAttributeTypeDescription( attribute.getDescription() );

                atdOidText.setText( atd.getNumericOid() );
                String atdNames = atd.getNames().toString();
                atdNamesText.setText( atdNames.substring( 1, atdNames.length() - 1 ) );
                atdDescText.setText( Utils.getNonNullString( atd.getDescription() ) );
                atdUsageText.setText( Utils.getNonNullString( atd.getUsage() ) );

                singleValuedFlag.setSelection( atd.isSingleValued() );
                noUserModificationFlag.setSelection( !atd.isUserModifiable() );
                collectiveFlag.setSelection( atd.isCollective() );
                obsoleteFlag.setSelection( atd.isObsolete() );

                String syntaxNumericOid = SchemaUtils.getSyntaxNumericOidTransitive( atd, schema );
                int syntaxLength = SchemaUtils.getSyntaxLengthTransitive( atd, schema );
                String syntaxDescription = syntaxNumericOid != null ? schema
                    .getLdapSyntaxDescription( syntaxNumericOid ).getDescription() : null;
                syntaxOidText.setText( Utils.getNonNullString( syntaxNumericOid ) );
                syntaxDescText.setText( Utils.getNonNullString( syntaxDescription ) );
                syntaxLengthText.setText( Utils.getNonNullString( syntaxLength > 0 ? Integer.toString( syntaxLength )
                    : null ) );

                equalityMatchingRuleText.setText( Utils.getNonNullString( SchemaUtils
                    .getEqualityMatchingRuleNameOrNumericOidTransitive( atd, schema ) ) );
                substringMatchingRuleText.setText( Utils.getNonNullString( SchemaUtils
                    .getSubstringMatchingRuleNameOrNumericOidTransitive( atd, schema ) ) );
                orderingMatchingRuleText.setText( Utils.getNonNullString( SchemaUtils
                    .getOrderingMatchingRuleNameOrNumericOidTransitive( atd, schema ) ) );
            }
        }

        return parent;
    }


    /**
     * Gets the attribute.
     * 
     * @param element the element
     * 
     * @return the attribute
     */
    private static IAttribute getAttribute( Object element )
    {
        IAttribute attribute = null;
        if ( element instanceof IAdaptable )
        {
            attribute = ( IAttribute ) ( ( IAdaptable ) element ).getAdapter( IAttribute.class );
        }
        return attribute;
    }

}
