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


import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.osgi.util.NLS;


/**
 * 
 * Abstract base class for value editors that handle binary values
 * in a dialog. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractDialogBinaryValueEditor extends AbstractDialogValueEditor
{

    /**
     * Creates a new instance of AbstractDialogBinaryValueEditor.
     */
    protected AbstractDialogBinaryValueEditor()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation of getDisplayValue just returns a note,
     * that the value is binary and the size of the data.
     */
    public String getDisplayValue( IValue value )
    {
        if ( showRawValues() )
        {
            return getPrintableString( value );
        }
        else
        {
            if ( value == null )
            {
                return "NULL"; //$NON-NLS-1$
            }
            else if ( value.isBinary() )
            {
                byte[] data = value.getBinaryValue();
                return NLS.bind( Messages.getString("AbstractDialogBinaryValueEditor.BinaryDateNBytes"), data.length ); //$NON-NLS-1$
            }
            else
            {
                return Messages.getString("AbstractDialogBinaryValueEditor.InvalidData"); //$NON-NLS-1$
            }
        }
    }


    /**
     * Helper method, returns a printable string if the value
     * is binary.
     * 
     * @param value the value
     * 
     * @return the printable string
     */
    public static String getPrintableString( IValue value )
    {
        if ( value == null )
        {
            return "NULL"; //$NON-NLS-1$
        }
        else if ( value.isBinary() )
        {
            byte[] data = value.getBinaryValue();
            StringBuffer sb = new StringBuffer();
            for ( int i = 0; data != null && i < data.length && i < 128; i++ )
            {
                if ( data[i] > 32 && data[i] < 127 )
                    sb.append( ( char ) data[i] );
                else
                    sb.append( '.' );
            }
            return sb.toString();
        }
        else if ( value.isString() )
        {
            return value.getStringValue();
        }
        else
        {
            return "NULL"; //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns IValue.EMPTY_BINARY_VALUE if
     * the attribute is binary.
     */
    protected Object getEmptyRawValue( IAttribute attribute )
    {
        if ( attribute.isBinary() )
        {
            return IValue.EMPTY_BINARY_VALUE;
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns the binary (byte[]) value 
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
            return value.getBinaryValue();
        }
        else if ( value.isBinary() )
        {
            return value.getBinaryValue();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always return the binary value
     * as byte[].
     */
    public Object getStringOrBinaryValue( Object rawValue )
    {
        if ( rawValue == null )
        {
            return null;
        }
        else if ( rawValue instanceof byte[] )
        {
            return rawValue;
        }
        else
        {
            return null;
        }
    }

}
