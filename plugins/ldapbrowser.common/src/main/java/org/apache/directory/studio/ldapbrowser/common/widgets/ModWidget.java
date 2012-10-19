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

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.widgets.ExtendedContentAssistCommandAdapter;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;


/**
 * The ModWidget provides input elements to define an LDAP modify 
 * operation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModWidget extends BrowserWidget implements ModifyListener
{
    /** The scrolled composite */
    private ScrolledComposite scrolledComposite;

    /** The composite that contains the ModSpecs */
    private Composite composite;

    /** The list of ModSpecs */
    private ArrayList<ModSpec> modSpecList = new ArrayList<ModSpec>();

    /** The list content proposal provider */
    private ListContentProposalProvider listContentProposalProvider;

    /** The resulting LDIF */
    private String ldif;


    /**
     * Creates a new instance of ModWidget.
     *
     * @param schema the schema with the possible attribute types
     */
    public ModWidget( Schema schema )
    {
        String[] attributeDescriptions = SchemaUtils.getNamesAsArray( schema.getAttributeTypeDescriptions() );
        Arrays.sort( attributeDescriptions );
        listContentProposalProvider = new ListContentProposalProvider( attributeDescriptions );
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
        // Creating the scrolled composite containing all UI
        scrolledComposite = new ScrolledComposite( parent, SWT.H_SCROLL | SWT.V_SCROLL );
        scrolledComposite.setLayout( new GridLayout() );
        scrolledComposite.setExpandHorizontal( true );
        scrolledComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Creating the composite
        composite = BaseWidgetUtils.createColumnContainer( scrolledComposite, 3, 1 );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        scrolledComposite.setContent( composite );

        addInitialModSpec();

        validate( false );

        return scrolledComposite;
    }


    /**
     * {@inheritDoc}
     */
    public void modifyText( ModifyEvent e )
    {
        validate( true );
    }


    /**
     * Validates the input elements.
     */
    public void validate( boolean notifyListeners )
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

        if ( notifyListeners )
        {
            notifyListeners();
        }
    }


    /**
     * Adds an initial modification spec.
     *
     */
    private void addInitialModSpec()
    {
        addModSpec( 0 );
    }


    /**
     * Adds a modification spec at the given index.
     * 
     * @param index the index
     */
    private void addModSpec( int index )
    {
        // Getting the array of modification specs
        ModSpec[] modSpecs = ( ModSpec[] ) modSpecList.toArray( new ModSpec[modSpecList.size()] );

        // Adding a new modification spec
        ModSpec newModSpec = createModSpec( true );
        modSpecList.add( newModSpec );

        if ( modSpecs.length > 0 )
        {
            for ( int i = index; i < modSpecs.length; i++ )
            {
                ModSpec modSpec = modSpecs[i];

                // That's a trick to relocate the modification spec
                // beneath the newly created one
                modSpec.modGroup.setParent( scrolledComposite );
                modSpec.modAddButton.setParent( scrolledComposite );
                modSpec.modDeleteButton.setParent( scrolledComposite );
                modSpec.modGroup.setParent( composite );
                modSpec.modAddButton.setParent( composite );
                modSpec.modDeleteButton.setParent( composite );

                // Same trick to update the id in the list
                modSpecList.remove( modSpec );
                modSpecList.add( modSpec );
            }
        }

        composite.setSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    }


    /**
     * Creates and returns a modification spec.
     *
     * @param addFirstValueLine if a first value line should added
     * @return the created modification spec
     */
    private ModSpec createModSpec( boolean addFirstValueLine )
    {
        ModSpec modSpec = new ModSpec();

        modSpec.modGroup = BaseWidgetUtils.createGroup( composite, "", 1 ); //$NON-NLS-1$
        modSpec.modGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        Composite modSpecComposite = BaseWidgetUtils.createColumnContainer( modSpec.modGroup, 2, 1 );
        modSpec.modType = BaseWidgetUtils.createReadonlyCombo( modSpecComposite, new String[]
            { "add", "replace", "delete" }, 0, 1 ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        modSpec.modType.setLayoutData( new GridData() );
        modSpec.modType.addModifyListener( this );

        // attribute combo with field decoration and content proposal
        modSpec.modAttributeCombo = BaseWidgetUtils.createCombo( modSpecComposite, new String[0], -1, 1 );
        new ExtendedContentAssistCommandAdapter( modSpec.modAttributeCombo, new ComboContentAdapter(),
            listContentProposalProvider, null, null, true );

        // add button with listener
        modSpec.modAddButton = new Button( composite, SWT.PUSH );
        modSpec.modAddButton.setText( "  +   " ); //$NON-NLS-1$
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

                addModSpec( index );

                validate( true );
            }
        } );

        // delete button with listener
        modSpec.modDeleteButton = new Button( composite, SWT.PUSH );
        modSpec.modDeleteButton.setText( "  \u2212  " ); //$NON-NLS-1$
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

                deleteModSpec( index );

                validate( true );
            }
        } );

        if ( addFirstValueLine )
        {
            addValueLine( modSpec, 0, false );
        }

        return modSpec;
    }


    /**
     * Delets a modification spec.
     *
     * @param index the index
     */
    private void deleteModSpec( int index )
    {
        ModSpec modSpec = modSpecList.remove( index );
        if ( modSpec != null )
        {
            modSpec.modGroup.dispose();
            modSpec.modAddButton.dispose();
            modSpec.modDeleteButton.dispose();

            composite.setSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        }
    }


    /**
     * Adds a value line to the given modification spec.
     * 
     * @param modSpec the modification spec
     * @param index the index
     * @param boolean the flag to update the size
     */
    private void addValueLine( ModSpec modSpec, int index, boolean updateSize )
    {
        ValueLine[] valueLines = modSpec.valueLineList.toArray( new ValueLine[modSpec.valueLineList.size()] );

        ValueLine newValueLine = createValueLine( modSpec );
        modSpec.valueLineList.add( newValueLine );

        if ( valueLines.length > 0 )
        {
            for ( int i = index; i < valueLines.length; i++ )
            {
                ValueLine valueLine = valueLines[i];

                // That's a trick to relocate the value line
                // beneath the newly created one
                Composite parentComposite = valueLine.valueComposite.getParent();
                valueLine.valueComposite.setParent( scrolledComposite );
                valueLine.valueComposite.setParent( parentComposite );

                // Same trick to update the id in the list
                modSpec.valueLineList.remove( valueLine );
                modSpec.valueLineList.add( valueLine );
            }
        }

        if ( updateSize )
        {
            composite.setSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        }
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
        valueLine.valueText = BaseWidgetUtils.createText( valueLine.valueComposite, "", 1 ); //$NON-NLS-1$
        valueLine.valueText.addModifyListener( this );

        // add button with listener
        valueLine.valueAddButton = new Button( valueLine.valueComposite, SWT.PUSH );
        valueLine.valueAddButton.setText( "  +   " ); //$NON-NLS-1$
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

                addValueLine( modSpec, index, true );

                validate( true );
            }
        } );

        // delete button with listener
        valueLine.valueDeleteButton = new Button( valueLine.valueComposite, SWT.PUSH );
        valueLine.valueDeleteButton.setText( "  \u2212  " ); //$NON-NLS-1$
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

                validate( true );
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

            composite.setSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
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
        sb.append( "changetype: modify" ).append( BrowserCoreConstants.LINE_SEPARATOR ); //$NON-NLS-1$

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
                sb.append( type ).append( ": " ).append( attribute ).append( BrowserCoreConstants.LINE_SEPARATOR ); //$NON-NLS-1$
                for ( int k = 0; k < values.length; k++ )
                {
                    if ( values[k].length() > 0 )
                    {
                        sb.append( attribute ).append( ": " ).append( values[k] ).append( //$NON-NLS-1$
                            BrowserCoreConstants.LINE_SEPARATOR );
                    }
                }
                sb.append( "-" ).append( BrowserCoreConstants.LINE_SEPARATOR ); //$NON-NLS-1$
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

        /** The modification attribute. */
        private Combo modAttributeCombo;

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
