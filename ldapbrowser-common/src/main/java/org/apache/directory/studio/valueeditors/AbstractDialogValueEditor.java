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

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * Abstract base class for value editors that handle values
 * in a dialog. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractDialogValueEditor extends CellEditor implements IValueEditor
{

    /** The value to handle */
    private Object value;

    /** The shell, used to open the editor */
    private Shell shell;

    /** The name of this value editor */
    private String name;

    /** The image of this value editor */
    private ImageDescriptor imageDescriptor;


    /**
     * 
     * Creates a new instance of AbstractDialogEditor.
     */
    protected AbstractDialogValueEditor()
    {
    }


    /**
     * Returns true if the user wishes to show raw values rather than
     * user-friendly values. If true the getDisplayValue() methods 
     * should not modify the value.
     *
     * @return true if raw values should be displayed
     */
    protected boolean showRawValues()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore()
            .getBoolean( BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation simple returns itself.
     */
    public CellEditor getCellEditor()
    {
        return this;
    }


    /**
     * {@inheritDoc}
     * 
     * This is a dialog editor, it doesn't create a control. 
     * It just extracts and saves the shell reference from parent.
     */
    protected final Control createControl( Composite parent )
    {
        this.shell = parent.getShell();
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * This is a dialog editor, doesn't set focus. 
     */
    protected final void doSetFocus()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * Returns the value object stored in a member.
     */
    protected final Object doGetValue()
    {
        return this.value;
    }


    /**
     * {@inheritDoc}
     * 
     * Stores the value object in a member.
     */
    protected final void doSetValue( Object value )
    {
        if ( value != null && value instanceof IValue.EmptyValue )
        {
            IValue.EmptyValue emptyValue = ( IValue.EmptyValue ) value;
            if ( emptyValue.isBinary() )
                value = emptyValue.getBinaryValue();
            else
                value = emptyValue.getStringValue();
        }
        this.value = value;
    }


    /**
     * {@inheritDoc}
     * 
     * The activate method is called from the JFace framework
     * to start editing. 
     */
    public final void activate()
    {
        boolean save = this.openDialog( shell );
        //doSetValue( newValue );
        if ( !save || this.value == null )
        {
            this.value = null;
            fireCancelEditor();
        }
        else
        {
            fireApplyEditorValue();
            deactivate();
        }
    }


    /**
     * Opens the edit dialog. 
     * Call getValue() to get the current value to edit. 
     * Call setValue() to set the new value after editing. 
     *
     * @param shell The shell to use to open the dialog
     * @return true if the new value should be stored, false
     *         to cancel the editor.
     */
    protected abstract boolean openDialog( Shell shell );


    /**
     * Returns a raw value that represents an empty value.
     * 
     * @param attribute the attribute
     * @return a raw value that represents an empty value
     */
    protected abstract Object getEmptyRawValue( IAttribute attribute );


    /**
     * {@inheritDoc}
     * 
     * This implementation of getDisplayValue() returns a 
     * comma-separated list of all values. 
     */
    public String getDisplayValue( AttributeHierarchy attributeHierarchy )
    {
        if ( attributeHierarchy == null )
        {
            return "NULL"; //$NON-NLS-1$
        }

        List<IValue> valueList = new ArrayList<IValue>();
        for ( Iterator it = attributeHierarchy.iterator(); it.hasNext(); )
        {
            IAttribute attribute = ( IAttribute ) it.next();
            valueList.addAll( Arrays.asList( attribute.getValues() ) );
        }

        StringBuffer sb = new StringBuffer();
        for ( Iterator<IValue> it = valueList.iterator(); it.hasNext(); )
        {
            IValue value = it.next();
            sb.append( getDisplayValue( value ) );
            if ( it.hasNext() )
                sb.append( ", " ); //$NON-NLS-1$
        }
        return sb.toString();
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation calls getEmptyRawValue(IAttribute) if there are no values
     * in attributeHierarchy and getRawValue(IValue) if attributeHierarchy
     * contains exactly one value. Otherwise null is returned.
     */
    public Object getRawValue( AttributeHierarchy attributeHierarchy )
    {
        if ( attributeHierarchy == null )
        {
            return null;
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 0 )
        {
            return getEmptyRawValue( attributeHierarchy.getAttribute() );
        }
        else if ( attributeHierarchy.size() == 1 && attributeHierarchy.getAttribute().getValueSize() == 1 )
        {
            return getRawValue( attributeHierarchy.getAttribute().getValues()[0] );
        }
        else
        {
            return null;
        }
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
