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

package org.apache.directory.ldapstudio.browser.ui.valueeditors;


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
import org.apache.directory.ldapstudio.browser.core.utils.LdifUtils;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.TextValueEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;


/**
 * 
 * Abstract base class for value editors that handle string values
 * withing the table or tree control. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractInPlaceStringValueEditor extends TextCellEditor implements IValueEditor
{

    /** 
     * @deprecated will be removed soon. Just used to delegate
     *             createValue(), deleteValue() and modifyValue().
     */
    private TextValueEditor delegate;

    /** The name of this value editor */
    private String name;

    /** The image of this value editor */
    private ImageDescriptor imageDescriptor;


    /**
     * Creates a new instance of AbstractInPlaceStringValueEditor.
     */
    protected AbstractInPlaceStringValueEditor()
    {
        super();
        this.delegate = new TextValueEditor();
    }


    /**
     * Returns true if the user wishes to show raw values rather than
     * user-friendly values. If true the getDisplayValue() methods 
     * shouldnot modify the value.
     *
     * @return true if raw values should be displayed
     */
    protected boolean showRawValues()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SHOW_RAW_VALUES );
    }


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
            return "NULL";
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
                sb.append( ", " );
        }
        return sb.toString();
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation just returns the raw value
     */
    public String getDisplayValue( IValue value )
    {
        Object obj = this.getRawValue( value );
        return obj == null ? "NULL" : obj.toString();
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns IValue.EMPTY_xx_VALUE if there are no values
     * in attributeHierarchy or calls getRawValue(IValue) if attributeHierarchy
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
            if ( attributeHierarchy.getAttribute().isString() )
            {
                return IValue.EMPTY_STRING_VALUE;
            }
            else
            {
                return IValue.EMPTY_BINARY_VALUE;
            }
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
     * 
     * This implementation returns the string value 
     * of the given value. 
     */
    public Object getRawValue( IValue value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value.isString() )
        {
            return value.getStringValue();
        }
        else if ( value.isBinary() )
        {
            return isEditable( value.getBinaryValue() ) ? value.getStringValue() : null;
        }
        else
        {
            return null;
        }
    }


    /**
     * Small helper.
     */
    private boolean isEditable( byte[] b )
    {
        if ( b == null )
        {
            return false;
        }

        for ( int i = 0; i < b.length; i++ )
        {
            if ( !( b[i] == '\n' || b[i] == '\r' || ( b[i] >= '\u0020' && b[i] <= '\u007F' ) ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns the value itself if it is
     * of type byte[] or a byte[] with the UTF-8 encoded string
     * value if it is of type String.  
     */
    public Object getRawValue( IConnection connection, Object value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value instanceof String )
        {
            return value;
        }
        else if ( value instanceof byte[] )
        {
            String s = LdifUtils.utf8decode( ( byte[] ) value );
            for ( int i = 0; i < s.length(); i++ )
            {
                if ( Character.isISOControl( s.charAt( i ) ) && s.charAt( i ) != '\n' && s.charAt( i ) != '\r' )
                {
                    return null;
                }
            }
            return s;
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always return the string value
     * as String.
     */
    public Object getStringOrBinaryValue( Object rawValue )
    {
        if ( rawValue == null )
        {
            return null;
        }
        else if ( rawValue instanceof String )
        {
            return rawValue;
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public CellEditor getCellEditor()
    {
        return this;
    }


    /**
     * {@inheritDoc}
     */
    protected Object doGetValue()
    {
        return "".equals( text.getText() ) ? null : text.getText();
    }


    /**
     * {@inheritDoc}
     */
    protected void doSetValue( Object value )
    {
        if ( value != null && value instanceof IValue.EmptyValue )
        {
            value = ( ( IValue.EmptyValue ) value ).getStringValue();
        }
        super.doSetValue( value );
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


    /**
     * {@inheritDoc}
     */
    public final void createValue( IEntry entry, String attributeDescription, Object newRawValue )
        throws ModelModificationException
    {
        delegate.createValue( entry, attributeDescription, newRawValue );
    }


    /**
     * {@inheritDoc}
     */
    public final void deleteAttribute( AttributeHierarchy ah ) throws ModelModificationException
    {
        delegate.deleteAttribute( ah );
    }


    /**
     * {@inheritDoc}
     */
    public final void deleteValue( IValue oldValue ) throws ModelModificationException
    {
        delegate.deleteValue( oldValue );
    }


    /**
     * {@inheritDoc}
     */
    public final void modifyValue( IValue oldValue, Object newRawValue ) throws ModelModificationException
    {
        delegate.modifyValue( oldValue, newRawValue );
    }

}
