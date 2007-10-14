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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.RDNPart;
import org.apache.directory.studio.ldapbrowser.core.utils.LdifUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * Default implementation of IValue.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Value implements IValue
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = -9039209604742682740L;

    /** The attribute this value belongs to */
    private IAttribute attribute;

    /** The raw value, either a String or a byte[] */
    private Object rawValue;


    /**
     * Creates a new instance of Value.
     *
     * @param attribute the attribute this value belongs to 
     * @param rawValue the raw value, either a String or a byte[]
     */
    public Value( IAttribute attribute, Object rawValue )
    {
        this.init( attribute, rawValue );
        assert rawValue != null;
    }


    /**
     * Creates a new instance of Value with an empty value.
     *
     * @param attribute the attribute this value belongs to
     */
    public Value( IAttribute attribute )
    {
        this.init( attribute, null );
    }


    /**
     * Initializes this Value.
     *
     * @param attribute the attribute this value belongs to 
     * @param rawValue the raw value, either a String or a byte[] or null 
     */
    private void init( IAttribute attribute, Object rawValue )
    {
        assert attribute != null;

        this.attribute = attribute;

        if ( rawValue == null )
        {
            if ( attribute.isString() )
            {
                this.rawValue = IValue.EMPTY_STRING_VALUE;
            }
            else
            {
                this.rawValue = IValue.EMPTY_BINARY_VALUE;
            }
        }
        else
        {
            this.rawValue = rawValue;
        }
    }


    /**
     * {@inheritDoc}
     */
    public IAttribute getAttribute()
    {
        return this.attribute;
    }


    /**
     * {@inheritDoc}
     */
    public Object getRawValue()
    {
        return this.rawValue;
    }


    /**
     * {@inheritDoc}
     */
    public String getStringValue()
    {

        if ( this.rawValue == EMPTY_STRING_VALUE )
        {
            return EMPTY_STRING_VALUE.getStringValue();
        }
        else if ( this.rawValue == EMPTY_BINARY_VALUE )
        {
            return EMPTY_BINARY_VALUE.getStringValue();
        }
        else if ( this.rawValue instanceof String )
        {
            return ( String ) this.rawValue;
        }
        else if ( this.rawValue instanceof byte[] )
        {
            return LdifUtils.utf8decode( ( byte[] ) this.rawValue );
        }
        else
        {
            return "UNKNOWN";
        }
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getBinaryValue()
    {
        if ( this.rawValue == EMPTY_STRING_VALUE )
        {
            return EMPTY_STRING_VALUE.getBinaryValue();
        }
        else if ( this.rawValue == EMPTY_BINARY_VALUE )
        {
            return EMPTY_BINARY_VALUE.getBinaryValue();
        }
        else if ( this.rawValue instanceof byte[] )
        {
            return ( byte[] ) this.rawValue;
        }
        else if ( this.rawValue instanceof String )
        {
            return LdifUtils.utf8encode( ( String ) this.rawValue );
        }
        else
        {
            return LdifUtils.utf8encode( "UNKNOWN" );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isString()
    {
        return this.rawValue == EMPTY_STRING_VALUE || this.attribute.isString();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isBinary()
    {
        return this.rawValue == EMPTY_BINARY_VALUE || this.attribute.isBinary();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return this.rawValue == EMPTY_STRING_VALUE || this.rawValue == EMPTY_BINARY_VALUE;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o )
    {
        // check argument
        if ( o == null || !( o instanceof IValue ) )
        {
            return false;
        }
        IValue vc = ( IValue ) o;

        // compare attributes
        if ( !vc.getAttribute().equals( this.getAttribute() ) )
        {
            return false;
        }

        // compare values
        if ( this.isEmpty() && vc.isEmpty() )
        {
            return true;
        }
        else if ( this.isBinary() && vc.isBinary() )
        {
            return Utils.equals( this.getBinaryValue(), vc.getBinaryValue() );
        }
        else if ( this.isString() && vc.isString() )
        {
            return ( this.getStringValue().equals( vc.getStringValue() ) );
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return rawValue.hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return attribute + ":" + ( this.isString() ? this.getStringValue() : "BINARY" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {
        Class<?> clazz = ( Class<?> ) adapter;
        if ( clazz.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( clazz.isAssignableFrom( Connection.class ) )
        {
            return getAttribute().getEntry().getBrowserConnection().getConnection();
        }
        if ( clazz.isAssignableFrom( IBrowserConnection.class ) )
        {
            return getAttribute().getEntry().getBrowserConnection();
        }
        if ( clazz.isAssignableFrom( IEntry.class ) )
        {
            return getAttribute().getEntry();
        }
        if ( clazz.isAssignableFrom( IAttribute.class ) )
        {
            return getAttribute();
        }
        if ( clazz.isAssignableFrom( IValue.class ) )
        {
            return this;
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isRdnPart()
    {
        RDNPart[] parts = getAttribute().getEntry().getRdn().getParts();
        for ( int p = 0; p < parts.length; p++ )
        {
            if ( getAttribute().getDescription().equals( parts[p].getType() )
                && getStringValue().equals( parts[p].getValue() ) )
            {
                return true;
            }
        }
        return false;
    }

}
