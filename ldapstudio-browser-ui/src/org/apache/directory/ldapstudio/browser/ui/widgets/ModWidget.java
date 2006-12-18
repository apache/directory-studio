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

package org.apache.directory.ldapstudio.browser.ui.widgets;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class ModWidget implements ModifyListener
{

    private Schema schema;

    private Shell shell;

    private Composite modComposite;

    private ArrayList modGroupList;

    private int modCompositeHeight = -1;

    private String ldif;


    public ModWidget( Schema schema )
    {
        this.schema = schema;
        this.modGroupList = new ArrayList();
        this.ldif = null;
    }


    public void dispose()
    {
    }


    public String getLdif()
    {
        return this.ldif;
    }


    public Composite createContents( Composite parent )
    {
        this.shell = parent.getShell();

        modComposite = BaseWidgetUtils.createColumnContainer( parent, 3, 1 );
        addModGroup( this.modComposite, 0 );

        return modComposite;
    }


    public void modifyText( ModifyEvent e )
    {
        this.validate();
    }

    private List listeners;


    public void addPropertyChangeListener( IPropertyChangeListener listener )
    {
        if ( listeners == null )
            listeners = new ArrayList();

        if ( !listeners.contains( listener ) )
            listeners.add( listener );
    }


    public void removePropertyChangeListener( IPropertyChangeListener listener )
    {
        if ( listeners == null )
            return;

        if ( listeners.contains( listener ) )
            listeners.remove( listener );
    }


    private void fire( String property, Object oldValue, Object newValue )
    {
        if ( listeners == null )
            return;

        for ( Iterator it = listeners.iterator(); it.hasNext(); )
        {
            ( ( IPropertyChangeListener ) it.next() ).propertyChange( new PropertyChangeEvent( this, property,
                oldValue, newValue ) );
        }
    }


    public void validate()
    {

        for ( int i = 0; i < this.modGroupList.size(); i++ )
        {
            ModGroup modGroup = ( ModGroup ) this.modGroupList.get( i );
            if ( this.modGroupList.size() > 1 )
            {
                modGroup.modDeleteButton.setEnabled( true );
            }
            else
            {
                modGroup.modDeleteButton.setEnabled( false );
            }
            for ( int k = 0; k < modGroup.valueLineList.size(); k++ )
            {
                ValueLine valueLine = ( ValueLine ) modGroup.valueLineList.get( k );
                if ( modGroup.valueLineList.size() > 1 )
                {
                    valueLine.valueDeleteButton.setEnabled( true );
                }
                else
                {
                    valueLine.valueDeleteButton.setEnabled( false );
                }
            }
        }

        fire( "ldif", null, null );
    }


    private void addModGroup( Composite modComposite, int index )
    {

        ModGroup[] modGroups = ( ModGroup[] ) modGroupList.toArray( new ModGroup[modGroupList.size()] );

        if ( modGroups.length > 0 )
        {
            for ( int i = 0; i < modGroups.length; i++ )
            {
                ModGroup oldModGroup = modGroups[i];

                // remember values
                String oldType = oldModGroup.modType.getText();
                String oldAttribute = oldModGroup.modAttribute.getText();
                String[] oldValues = new String[oldModGroup.valueLineList.size()];
                for ( int k = 0; k < oldValues.length; k++ )
                {
                    oldValues[k] = ( ( ValueLine ) oldModGroup.valueLineList.get( k ) ).valueText.getText();
                }

                // delete old
                oldModGroup.modGroup.dispose();
                oldModGroup.modAddButton.dispose();
                oldModGroup.modDeleteButton.dispose();
                modGroupList.remove( oldModGroup );

                // add new
                ModGroup newModGroup = createModGroup( modComposite );
                modGroupList.add( newModGroup );

                // restore values
                newModGroup.modType.setText( oldType );
                newModGroup.modAttribute.setText( oldAttribute );
                deleteValueLine( newModGroup, 0 );
                for ( int k = 0; k < oldValues.length; k++ )
                {
                    addValueLine( newModGroup, k );
                    ValueLine newValueLine = ( ValueLine ) newModGroup.valueLineList.get( k );
                    newValueLine.valueText.setText( oldValues[k] );
                }

                // check
                if ( index == i + 1 )
                {
                    ModGroup modGroup = createModGroup( modComposite );
                    modGroupList.add( modGroup );

                }
            }
        }
        else
        {
            ModGroup modGroup = createModGroup( modComposite );
            modGroupList.add( modGroup );
        }
    }


    private ModGroup createModGroup( final Composite modComposite )
    {
        final ModGroup modGroup = new ModGroup();

        // this.connection.getSchema().getAttributeTypeDescriptionNames()
        modGroup.modGroup = BaseWidgetUtils.createGroup( modComposite, "", 1 );
        Composite modGroupComposite = BaseWidgetUtils.createColumnContainer( modGroup.modGroup, 2, 1 );
        modGroup.modType = BaseWidgetUtils.createCombo( modGroupComposite, new String[]
            { "add", "replace", "delete" }, 0, 1 );
        modGroup.modType.addModifyListener( this );
        String[] attributeDescriptions = schema.getAttributeTypeDescriptionNames();
        Arrays.sort( attributeDescriptions );
        modGroup.modAttribute = BaseWidgetUtils.createCombo( modGroupComposite, attributeDescriptions, -1, 1 );
        modGroup.modAttribute.addModifyListener( this );

        modGroup.modAddButton = new Button( modComposite, SWT.PUSH );
        modGroup.modAddButton.setText( "  +   " );
        modGroup.modAddButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = modGroupList.size();
                for ( int i = 0; i < modGroupList.size(); i++ )
                {
                    ModGroup modGroup = ( ModGroup ) modGroupList.get( i );
                    if ( modGroup.modAddButton == e.widget )
                    {
                        index = i + 1;
                    }
                }
                addModGroup( modComposite, index );

                Point shellSize = shell.getSize();
                Point modCompositeSize = modComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
                int newModCompositeSize = modCompositeSize.y;
                shell.setSize( shellSize.x, shellSize.y + newModCompositeSize - modCompositeHeight );
                modComposite.layout( true, true );
                shell.layout( true, true );
                modCompositeHeight = newModCompositeSize;

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        modGroup.modDeleteButton = new Button( modComposite, SWT.PUSH );
        modGroup.modDeleteButton.setText( "  \u2212  " ); // \u2013
        modGroup.modDeleteButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < modGroupList.size(); i++ )
                {
                    ModGroup modGroup = ( ModGroup ) modGroupList.get( i );
                    if ( modGroup.modDeleteButton == e.widget )
                    {
                        index = i;
                    }
                }
                deleteModGroup( modComposite, index );

                Point shellSize = shell.getSize();
                Point groupSize = modComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
                int newModCompositeSize = groupSize.y;
                shell.setSize( shellSize.x, shellSize.y + newModCompositeSize - modCompositeHeight );
                modComposite.layout( true, true );
                shell.layout( true, true );
                modCompositeHeight = newModCompositeSize;

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        addValueLine( modGroup, 0 );

        return modGroup;
    }


    private void deleteModGroup( Composite modComposite, int index )
    {
        ModGroup modGroup = ( ModGroup ) modGroupList.remove( index );
        if ( modGroup != null )
        {
            modGroup.modGroup.dispose();
            modGroup.modAddButton.dispose();
            modGroup.modDeleteButton.dispose();
        }
    }


    private void addValueLine( ModGroup modGroup, int index )
    {

        ValueLine[] valueLines = ( ValueLine[] ) modGroup.valueLineList.toArray( new ValueLine[modGroup.valueLineList
            .size()] );

        if ( valueLines.length > 0 )
        {
            for ( int i = 0; i < valueLines.length; i++ )
            {
                ValueLine oldValueLine = valueLines[i];

                // remember values
                String oldValue = oldValueLine.valueText.getText();

                // delete old
                oldValueLine.valueComposite.dispose();
                modGroup.valueLineList.remove( oldValueLine );

                // add new
                ValueLine newValueLine = createValueLine( modGroup );
                modGroup.valueLineList.add( newValueLine );

                // restore value
                newValueLine.valueText.setText( oldValue );

                // check
                if ( index == i + 1 )
                {
                    ValueLine valueLine = createValueLine( modGroup );
                    modGroup.valueLineList.add( valueLine );
                }
            }
        }
        else
        {
            ValueLine valueLine = createValueLine( modGroup );
            modGroup.valueLineList.add( valueLine );
        }
    }


    private ValueLine createValueLine( final ModGroup modGroup )
    {
        final ValueLine valueLine = new ValueLine();

        // this.connection.getSchema().getAttributeTypeDescriptionNames()
        valueLine.valueComposite = BaseWidgetUtils.createColumnContainer( modGroup.modGroup, 3, 1 );
        valueLine.valueText = BaseWidgetUtils.createText( valueLine.valueComposite, "", 1 );
        valueLine.valueText.addModifyListener( this );

        valueLine.valueAddButton = new Button( valueLine.valueComposite, SWT.PUSH );
        valueLine.valueAddButton.setText( "  +   " );
        valueLine.valueAddButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = modGroup.valueLineList.size();
                for ( int i = 0; i < modGroup.valueLineList.size(); i++ )
                {
                    ValueLine valueLine = ( ValueLine ) modGroup.valueLineList.get( i );
                    if ( valueLine.valueAddButton == e.widget )
                    {
                        index = i + 1;
                    }
                }
                addValueLine( modGroup, index );

                Point shellSize = shell.getSize();
                Point modCompositeSize = modComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
                int newModCompositeSize = modCompositeSize.y;
                shell.setSize( shellSize.x, shellSize.y + newModCompositeSize - modCompositeHeight );
                modComposite.layout( true, true );
                shell.layout( true, true );
                modCompositeHeight = newModCompositeSize;

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        valueLine.valueDeleteButton = new Button( valueLine.valueComposite, SWT.PUSH );
        valueLine.valueDeleteButton.setText( "  \u2212  " ); // \u2013
        valueLine.valueDeleteButton.addSelectionListener( new SelectionListener()
        {
            public void widgetSelected( SelectionEvent e )
            {
                int index = 0;
                for ( int i = 0; i < modGroup.valueLineList.size(); i++ )
                {
                    ValueLine valueLine = ( ValueLine ) modGroup.valueLineList.get( i );
                    if ( valueLine.valueDeleteButton == e.widget )
                    {
                        index = i;
                    }
                }
                deleteValueLine( modGroup, index );

                Point shellSize = shell.getSize();
                Point groupSize = modComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT, true );
                int newModCompositeSize = groupSize.y;
                shell.setSize( shellSize.x, shellSize.y + newModCompositeSize - modCompositeHeight );
                modComposite.layout( true, true );
                shell.layout( true, true );
                modCompositeHeight = newModCompositeSize;

                validate();
            }


            public void widgetDefaultSelected( SelectionEvent e )
            {
            }
        } );

        return valueLine;
    }


    private void deleteValueLine( ModGroup modGroup, int index )
    {
        ValueLine valueLine = ( ValueLine ) modGroup.valueLineList.remove( index );
        if ( valueLine != null )
        {
            valueLine.valueComposite.dispose();
        }
    }


    public String getLdifFragment()
    {

        StringBuffer sb = new StringBuffer();
        sb.append( "changetype: modify" ).append( BrowserCoreConstants.LINE_SEPARATOR );

        ModGroup[] modGroups = ( ModGroup[] ) modGroupList.toArray( new ModGroup[modGroupList.size()] );

        if ( modGroups.length > 0 )
        {
            for ( int i = 0; i < modGroups.length; i++ )
            {
                ModGroup modGroup = modGroups[i];

                // get values
                String type = modGroup.modType.getText();
                String attribute = modGroup.modAttribute.getText();
                String[] values = new String[modGroup.valueLineList.size()];
                for ( int k = 0; k < values.length; k++ )
                {
                    values[k] = ( ( ValueLine ) modGroup.valueLineList.get( k ) ).valueText.getText();
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

    public class ModGroup
    {
        public Group modGroup;

        public Combo modType;

        public Combo modAttribute;

        public Button modAddButton;

        public Button modDeleteButton;

        public ArrayList valueLineList = new ArrayList();;
    }

    public class ValueLine
    {
        public Composite valueComposite;

        public Text valueText;

        public Button valueAddButton;

        public Button valueDeleteButton;
    }

}
