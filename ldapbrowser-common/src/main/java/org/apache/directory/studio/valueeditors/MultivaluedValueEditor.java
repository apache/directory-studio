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

package org.apache.directory.studio.valueeditors;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.dialogs.MultivaluedDialog;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;


/**
 * Special ValueEditor to handle attributes with multiple values in a dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MultivaluedValueEditor extends CellEditor implements IValueEditor
{

    /** The value to handle */
    private Object value;

    /** The parent composite, used to instantiate a new control */
    private Composite parent;

    /** The name of this value editor */
    private String name;

    /** The image of this value editor */
    private ImageDescriptor imageDescriptor;

    /** The value editor manager, used to get proper value editors */
    protected ValueEditorManager valueEditorManager;


    /**
     * Creates a new instance of MultivaluedValueEditor.
     *
     * @param parent the parent composite
     * @param valueEditorManager the value editor manager, used to get
     *                           proper value editors
     */
    public MultivaluedValueEditor( Composite parent, ValueEditorManager valueEditorManager )
    {
        super( parent );
        this.parent = parent;
        this.valueEditorManager = valueEditorManager;
    }


    /**
     * {@inheritDoc}
     * 
     * This is a dialog editor, it doesn't create a control. 
     */
    protected Control createControl( Composite parent )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * Returns the value object stored in a member.
     */
    protected final Object doGetValue()
    {
        return value;
    }


    /**
     * {@inheritDoc}
     * 
     * This is a dialog editor, doesn't set focus. 
     */
    protected void doSetFocus()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * Stores the value object in a member.
     */
    protected void doSetValue( Object value )
    {
        this.value = value;
    }


    /**
     * {@inheritDoc}
     * 
     * Opens the MulitvaluedDialog. Expects that an AttributeHierarchy
     * object is in value member. 
     */
    public void activate()
    {
        if ( getValue() != null && getValue() instanceof AttributeHierarchy )
        {
            AttributeHierarchy ah = ( AttributeHierarchy ) getValue();
            if ( ah != null )
            {
                MultivaluedDialog dialog = new MultivaluedDialog( parent.getShell(), ah );
                dialog.open();
            }
        }

        fireCancelEditor();
    }


    /**
     * {@inheritDoc}
     * 
     * Returns this.
     */
    public CellEditor getCellEditor()
    {
        return this;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation of getDisplayValue() returns a 
     * comma-separated list of all values. 
     */
    public String getDisplayValue( AttributeHierarchy attributeHierarchy )
    {
        List<IValue> valueList = new ArrayList<IValue>();
        for ( IAttribute attribute : attributeHierarchy )
        {
            valueList.addAll( Arrays.asList( attribute.getValues() ) );
        }

        StringBuffer sb = new StringBuffer();
        if ( valueList.size() > 1 )
        {
            sb.append( NLS.bind( Messages.getString( "EntryValueEditor.n_values" ), valueList.size() ) ); //$NON-NLS-1$
        }
        for ( Iterator<IValue> it = valueList.iterator(); it.hasNext(); )
        {
            IValue value = it.next();
            IValueEditor vp = valueEditorManager.getCurrentValueEditor( value.getAttribute().getEntry(), value
                .getAttribute().getDescription() );
            sb.append( vp.getDisplayValue( value ) );
            if ( it.hasNext() )
            {
                sb.append( ", " ); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }


    /**
     * {@inheritDoc}
     * 
     * It doesn't make sense to use the MultivaluedValueEditor with a single value.
     * Returns an empty string.
     */
    public String getDisplayValue( IValue value )
    {
        return ""; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     * 
     * Returns the attributeHierarchy.
     */
    public Object getRawValue( AttributeHierarchy attributeHierarchy )
    {
        return attributeHierarchy;
    }


    /**
     * {@inheritDoc}
     * 
     * It doesn't make sense to use the MultivaluedValueEditor with a single value.
     * Returns null.
     */
    public Object getRawValue( IValue value )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * Modification is performed in the concrete single-ValueEditors. No need 
     * to return a value.
     */
    public Object getStringOrBinaryValue( Object rawValue )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void setValueEditorName( String name )
    {
        this.name = name;
    }


    /**
     * {@inheritDoc}
     */
    public String getValueEditorName()
    {
        return name;
    }


    /**
     * {@inheritDoc}
     */
    public void setValueEditorImageDescriptor( ImageDescriptor imageDescriptor )
    {
        this.imageDescriptor = imageDescriptor;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getValueEditorImageDescriptor()
    {
        return imageDescriptor;
    }

}
