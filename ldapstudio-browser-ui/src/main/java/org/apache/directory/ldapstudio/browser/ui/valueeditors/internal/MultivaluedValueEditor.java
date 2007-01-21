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

package org.apache.directory.ldapstudio.browser.ui.valueeditors.internal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.ui.dialogs.MultivaluedDialog;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
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

    /** The parent composite, used to instanciate a new control */
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
     * This is a dialog editor, it doesn't create a control. 
     */
    protected Control createControl( Composite parent )
    {
        return null;
    }


    /**
     * Returns the value object stored in a member.
     */
    protected final Object doGetValue()
    {
        return this.value;
    }


    /**
     * This is a dialog editor, doesn't set focus. 
     */
    protected void doSetFocus()
    {
    }


    /**
     * Stores the value object in a member.
     */
    protected void doSetValue( Object value )
    {
        this.value = value;
    }


    /**
     * Opens the MulitvaluedDialog. Expects that an AttributeHierarchy
     * object is in value member. 
     */
    public void activate()
    {
        if ( this.getValue() != null && this.getValue() instanceof AttributeHierarchy )
        {
            AttributeHierarchy ah = ( AttributeHierarchy ) this.getValue();
            if ( ah != null )
            {
                MultivaluedDialog dialog = new MultivaluedDialog( this.parent.getShell(), ah );
                dialog.open();
            }
        }

        fireCancelEditor();
    }


    /**
     * Returns this.
     */
    public CellEditor getCellEditor()
    {
        return this;
    }


    /**
     * This implementation of getDisplayValue() returns a 
     * comma-separated list of all values. 
     */
    public String getDisplayValue( AttributeHierarchy attributeHierarchy )
    {

        List<IValue> valueList = new ArrayList<IValue>();
        for ( Iterator it = attributeHierarchy.iterator(); it.hasNext(); )
        {
            IAttribute attribute = ( IAttribute ) it.next();
            valueList.addAll( Arrays.asList( attribute.getValues() ) );
        }

        StringBuffer sb = new StringBuffer();
        if ( valueList.size() > 1 )
            sb.append( valueList.size() + " values: " );
        for ( Iterator it = valueList.iterator(); it.hasNext(); )
        {
            IValue value = ( IValue ) it.next();
            IValueEditor vp = this.valueEditorManager.getCurrentValueEditor( value );
            sb.append( vp.getDisplayValue( value ) );
            if ( it.hasNext() )
                sb.append( ", " );
        }
        return sb.toString();
    }


    /**
     * It doesn't make sense to use the MultivaluedValueEditor with a single value.
     * Returns an empty string.
     */
    public String getDisplayValue( IValue value )
    {
        return "";
    }


    /**
     * Returns the attributeHierarchy.
     */
    public Object getRawValue( AttributeHierarchy attributeHierarchy )
    {
        return attributeHierarchy;
    }


    /**
     * It doesn't make sense to use the MultivaluedValueEditor with a single value.
     * Returns null.
     */
    public Object getRawValue( IValue value )
    {
        return null;
    }


    /**
     * It doesn't make sense to use the MultivaluedValueEditor with a single value.
     * Returns null.
     */
    public Object getRawValue( IConnection connection, Object value )
    {
        return null;
    }


    /**
     * Modification is performed in the concrete single-ValueEditors.
     */
    public void modifyValue( IValue oldValue, Object newRawValue ) throws ModelModificationException
    {
    }


    /**
     * Creationg is performed in the concrete single-ValueEditors.
     */
    public void createValue( IEntry entry, String attributeName, Object newRawValue ) throws ModelModificationException
    {
    }


    /**
     * Deletion is performed in the concrete single-ValueEditors.
     */
    public void deleteAttribute( AttributeHierarchy ah ) throws ModelModificationException
    {
    }


    /**
     * Deletion is performed in the concrete single-ValueEditors.
     */
    public void deleteValue( IValue oldValue ) throws ModelModificationException
    {
    }


    /**
     * Modification is performed in the concrete single-ValueEditors. No need 
     * to return a value.
     */
    public Object getStringOrBinaryValue( Object rawValue )
    {
        return null;
    }


    /*
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor#setValueEditorName(java.lang.String)
     */
    public void setValueEditorName( String name )
    {
        this.name = name;
    }


    /*
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor#getValueEditorName()
     */
    public String getValueEditorName()
    {
        return name;
    }


    /*
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor#setValueEditorImageDescriptor(org.eclipse.jface.resource.ImageDescriptor)
     */
    public void setValueEditorImageDescriptor( ImageDescriptor imageDescriptor )
    {
        this.imageDescriptor = imageDescriptor;
    }


    /*
     * @see org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor#getValueEditorImageDescriptor()
     */
    public ImageDescriptor getValueEditorImageDescriptor()
    {
        return imageDescriptor;
    }

}
