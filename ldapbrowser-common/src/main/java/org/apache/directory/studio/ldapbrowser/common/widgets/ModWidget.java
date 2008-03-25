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

package org.apache.directory.studio.ldapbrowser.common.widgets;


import java.util.ArrayList;
import java.util.Arrays;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * The ModWidget provides input elements to define an LDAP modify 
 * operation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModWidget extends BrowserWidget implements ModifyListener
{

    /** The schema with the possible attribute types */
    private Schema schema;

    /** The shell */
    private Shell shell;

    /** The composite that contains the ModSpecs */
    private Composite modComposite;

    /** The list of ModSpecs */
    private ArrayList<ModSpec> modSpecList;

    /** The resulting LDIF */
    private String ldif;


    /**
     * Creates a new instance of ModWidget.
     *
     * @param schema the schema with the possible attribute types
     */
    public ModWidget( Schema schema )
    {
        this.schema = schema;
        this.modSpecList = new ArrayList<ModSpec>();
        this.ldif = null;
    }


    /**
     * Disposes this widget.
     */
    public void dispose()
    {
    }


    /**
     * Gets the ldif.
     * 
     * @return the ldif
     */
    public String getLdif()
    {
        return ldif;
    }


    /**
     * Creates the contents.
     * 
     * @param parent the parent composite
     * 
     * @return the created composite
     */
    public Composite createContents( Composite parent )
    {
        shell = parent.getShell();

        modComposite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        addModSpec( modComposite, 0 );

        return modComposite;
    }


    /**
     * {@inheritDoc}
     */
    public void modifyText( ModifyEvent e )
    {
        validate();
    }


    /**
     * Validates the input elements.
     */
    public void validate()
    {
        for ( int i = 0; i < modSpecList.size(); i++ )
        {
            ModSpec modSpec = ( ModSpec ) modSpecList.get( i );
            if ( modSpecList.size() > 1 )
            {
                modSpec.modDeleteButton.setEnabled( true );
            }
            else
            {
                modSpec.modDeleteButton.setEnabled( false );
            }
            for ( int k = 0; k < modSpec.valueLineList.size(); k++ )
            {
                ValueLine valueLine = ( ValueLine ) modSpec.valueLineList.get( k );
                if ( modSpec.valueLineList.size() > 1 )
                {
                    valueLine.valueDeleteButton.setEnabled( true );
                }
                else
                {
                    valueLine.valueDeleteButton.setEnabled( false );
                }
            }
        }

        notifyListeners();
    }


    /**
     * Adds a modification spec at the given index.
     * 
     * @param modComposite the composite
     * @param index the index
     */
    private void addModSpec( Composite modComposite, int index )
    {

        ModSpec[] modSpecs = ( ModSpec[] ) modSpecList.toArray( new ModSpec[modSpecList.size()] );

        if ( modSpecs.length > 0 )
        {
            for ( int i = 0; i < modSpecs.length; i++ )
            {
                ModSpec oldModSpec = modSpecs[i];

                // remember values
                String oldType = oldModSpec.modType.getText();
                String oldAttribute = oldModSpec.modAttributeCombo.getText();
                String[] oldValues = new String[oldModSpec.valueLineList.size()];
                for ( int k = 0; k < oldValues.length; k++ )
                {
                    oldValues[k] = ( ( ValueLine ) oldModSpec.valueLineList.get( k ) ).valueText.getText();
                }

                // delete old
                oldModSpec.modGroup.dispose();
                oldModSpec.modAddButton.dispose();
                oldModSpec.modDeleteButton.dispose();
                modSpecList.remove( oldModSpec );

                // add new
                ModSpec newModSpec = createModSpec( modComposite );
                modSpecList.add( newModSpec );

                // restore values
                newModSpec.modType.setText( oldType );
                newModSpec.modAttributeCombo.setText( oldAttribute );
                deleteValueLine( newModSpec, 0 );
                for ( int k = 0; k < oldValues.length; k++ )
                {
                    addValueLine( newModSpec, k );
                    ValueLine newValueLine = ( ValueLine ) newModSpec.valueLineList.get( k );
                    newValueLine.valueText.setText( oldValues[k] );
                }

                // check
                if ( index == i + 1 )
                {
                    ModSpec modSpec = createModSpec( modComposite );
                    modSpecList.add( modSpec );
                }
            }
        }
        else
        {
            ModSpec modSpec = createModSpec( modComposite );
            modSpecList.add( modSpec );
        }

        shell.layout( true, true );
    }


    /**
     * Creates and returns a modification spec.
     * 
     * @param modComposite the composite
     * 
     * @return the created modification spec
     */
    private ModSpec createModSpec( final Composite modComposite )
    {
        final ModSpec modSpec = new ModSpec();

        modSpec.modGroup = BaseWidgetUtils.createGroup( modComposite, "", 1 );
        Composite modSpecComposite = BaseWidgetUtils.createColumnContainer( modSpec.modGroup, 2, 1 );
        modSpec.modType = BaseWidgetUtils.createCombo( modSpecComposite, new String[]
            { "add", "replace", "delete" }, 0, 1 );
        modSpec.modType.addModifyListener( this );
        String[] attributeDescriptions = SchemaUtils.getNamesAsArray( schema.getAttributeTypeDescriptions() );
        Arrays.sort( attributeDescriptions );

        // attribute combo with field decoration
        final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_CONTENT_PROPOSAL );
        modSpec.modAttributeComboField = new DecoratedField( modSpecComposite, SWT.NONE, new IControlCreator()
        {
            public Control createControl( Composite parent, int style )
            {
                Combo combo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );
                combo.setVisibleItemCount( 20 );
                return combo;
            }
        } );
        modSpec.modAttributeComboField.addFieldDecoration( fieldDecoration, SWT.TOP | SWT.LEFT, true );
        modSpec.modAttributeComboField.getLayoutControl().setLayoutData(
            new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        modSpec.modAttributeCombo = ( Combo ) modSpec.modAttributeComboField.getControl();
        modSpec.modAttributeCombo.setItems( attributeDescriptions );
        modSpec.modAttributeCombo.addModifyListener( this );

        // content proposal adapter
        modSpec.modAttributeCPA = new ContentProposalAdapter( modSpec.modAttributeCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( attributeDescriptions ), null, null );
        modSpec.modAttributeCPA.setFilterStyle( ContentProposalAdapter.FILTER_NONE );
        modSpec.modAttributeCPA.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );

        // add button with listener
        modSpec.modAddButton = new Button( modComposite, SWT.PUSH );
        modSpec.modAddButton.setText( "  +   " );
        modSpec.modAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = modSpecList.size();
                for ( int i = 0; i < modSpecList.size(); i++ )
                {
                    ModSpec modSpec = modSpecList.get( i );
                    if ( modSpec.modAddButton == e.widget )
                    {
                        index = i + 1;
                    }
                }

                addModSpec( modComposite, index );

                validate();
            }
        } );

        // delete button with listener
        modSpec.modDeleteButton = new Button( modComposite, SWT.PUSH );
        modSpec.modDeleteButton.setText( "  \u2212  " ); // \u2013
        modSpec.modDeleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < modSpecList.size(); i++ )
                {
                    ModSpec modSpec = modSpecList.get( i );
                    if ( modSpec.modDeleteButton == e.widget )
                    {
                        index = i;
                    }
                }

                deleteModSpec( modComposite, index );

                validate();
            }
        } );

        addValueLine( modSpec, 0 );

        return modSpec;
    }


    /**
     * Delets a modification spec.
     *
     * @param modComposite the composite
     * @param index the index
     */
    private void deleteModSpec( Composite modComposite, int index )
    {
        ModSpec modSpec = modSpecList.remove( index );
        if ( modSpec != null )
        {
            modSpec.modGroup.dispose();
            modSpec.modAddButton.dispose();
            modSpec.modDeleteButton.dispose();

            if ( !modComposite.isDisposed() )
            {
                shell.layout( true, true );
            }
        }
    }


    /**
     * Adds a value line to the given modification spec.
     * 
     * @param modSpec the modification spec
     * @param index the index
     */
    private void addValueLine( ModSpec modSpec, int index )
    {

        ValueLine[] valueLines = modSpec.valueLineList.toArray( new ValueLine[modSpec.valueLineList.size()] );

        if ( valueLines.length > 0 )
        {
            for ( int i = 0; i < valueLines.length; i++ )
            {
                ValueLine oldValueLine = valueLines[i];

                // remember values
                String oldValue = oldValueLine.valueText.getText();

                // delete old
                oldValueLine.valueComposite.dispose();
                modSpec.valueLineList.remove( oldValueLine );

                // add new
                ValueLine newValueLine = createValueLine( modSpec );
                modSpec.valueLineList.add( newValueLine );

                // restore value
                newValueLine.valueText.setText( oldValue );

                // check
                if ( index == i + 1 )
                {
                    ValueLine valueLine = createValueLine( modSpec );
                    modSpec.valueLineList.add( valueLine );
                }
            }
        }
        else
        {
            ValueLine valueLine = createValueLine( modSpec );
            modSpec.valueLineList.add( valueLine );
        }

        shell.layout( true, true );
    }


    /**
     * Creates the value line.
     * 
     * @param modSpec the modification spec
     * 
     * @return the value line
     */
    private ValueLine createValueLine( final ModSpec modSpec )
    {
        final ValueLine valueLine = new ValueLine();

        // text field
        valueLine.valueComposite = BaseWidgetUtils.createColumnContainer( modSpec.modGroup, 3, 1 );
        valueLine.valueText = BaseWidgetUtils.createText( valueLine.valueComposite, "", 1 );
        valueLine.valueText.addModifyListener( this );

        // add button with listener
        valueLine.valueAddButton = new Button( valueLine.valueComposite, SWT.PUSH );
        valueLine.valueAddButton.setText( "  +   " );
        valueLine.valueAddButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = modSpec.valueLineList.size();
                for ( int i = 0; i < modSpec.valueLineList.size(); i++ )
                {
                    ValueLine valueLine = modSpec.valueLineList.get( i );
                    if ( valueLine.valueAddButton == e.widget )
                    {
                        index = i + 1;
                    }
                }

                addValueLine( modSpec, index );

                validate();
            }
        } );

        // delete button with listener
        valueLine.valueDeleteButton = new Button( valueLine.valueComposite, SWT.PUSH );
        valueLine.valueDeleteButton.setText( "  \u2212  " ); // \u2013
        valueLine.valueDeleteButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < modSpec.valueLineList.size(); i++ )
                {
                    ValueLine valueLine = modSpec.valueLineList.get( i );
                    if ( valueLine.valueDeleteButton == e.widget )
                    {
                        index = i;
                    }
                }

                deleteValueLine( modSpec, index );

                validate();
            }
        } );

        return valueLine;
    }


    /**
     * Delete value line.
     * 
     * @param modSpec the mod spec
     * @param index the index
     */
    private void deleteValueLine( ModSpec modSpec, int index )
    {
        ValueLine valueLine = ( ValueLine ) modSpec.valueLineList.remove( index );
        if ( valueLine != null )
        {
            valueLine.valueComposite.dispose();

            if ( !modComposite.isDisposed() )
            {
                shell.layout( true, true );
            }
        }
    }


    /**
     * Gets the LDIF fragment.
     * 
     * @return the LDIF fragment
     */
    public String getLdifFragment()
    {

        StringBuffer sb = new StringBuffer();
        sb.append( "changetype: modify" ).append( BrowserCoreConstants.LINE_SEPARATOR );

        ModSpec[] modSpecs = ( ModSpec[] ) modSpecList.toArray( new ModSpec[modSpecList.size()] );

        if ( modSpecs.length > 0 )
        {
            for ( int i = 0; i < modSpecs.length; i++ )
            {
                ModSpec modSpec = modSpecs[i];

                // get values
                String type = modSpec.modType.getText();
                String attribute = modSpec.modAttributeCombo.getText();
                String[] values = new String[modSpec.valueLineList.size()];
                for ( int k = 0; k < values.length; k++ )
                {
                    values[k] = ( ( ValueLine ) modSpec.valueLineList.get( k ) ).valueText.getText();
                }

                // build ldif
                sb.append( type ).append( ": " ).append( attribute ).append( BrowserCoreConstants.LINE_SEPARATOR );
                for ( int k = 0; k < values.length; k++ )
                {
                    if ( values[k].length() > 0 )
                    {
                        sb.append( attribute ).append( ": " ).append( values[k] ).append(
                            BrowserCoreConstants.LINE_SEPARATOR );
                    }
                }
                sb.append( "-" ).append( BrowserCoreConstants.LINE_SEPARATOR );
                // sb.append(BrowserCoreConstants.NEWLINE);
            }
        }

        return sb.toString();
    }

    /**
     * The Class ModSpec is a wrapper for all input elements
     * of an modification. It contains a combo for the modify
     * operation, a combo for the attribute to modify, 
     * value lines and + and - buttons to add and remove 
     * other modifications. It looks like this:
     * <pre>
     * ----------------------------------
     * | operation v | attribute type v |--------
     * ------------------------ --------| + | - |
     * | value                  | + | - |--------
     * ----------------------------------
     * </pre>
     */
    private class ModSpec
    {

        /** The mod group. */
        private Group modGroup;

        /** The mod type. */
        private Combo modType;

        /** The modification attribute field. */
        private DecoratedField modAttributeComboField;

        /** The modification attribute. */
        private Combo modAttributeCombo;

        /** The modification content proposal adapter */
        private ContentProposalAdapter modAttributeCPA;

        /** The mod add button. */
        private Button modAddButton;

        /** The mod delete button. */
        private Button modDeleteButton;

        /** The value line list. */
        private ArrayList<ValueLine> valueLineList = new ArrayList<ValueLine>();;
    }

    /**
     * The Class ValueLine is a wrapper for all input elements
     * of an value line. It contains an input field for the value
     * and + and - buttons to add and remove other value lines. 
     * It looks like this:
     * <pre>
     * -------------------------------------
     * | value                     | + | - |
     * -------------------------------------
     * </pre>
     */
    private class ValueLine
    {

        /** The value composite. */
        private Composite valueComposite;

        /** The value text. */
        private Text valueText;

        /** The value add button. */
        private Button valueAddButton;

        /** The value delete button. */
        private Button valueDeleteButton;
    }

}
