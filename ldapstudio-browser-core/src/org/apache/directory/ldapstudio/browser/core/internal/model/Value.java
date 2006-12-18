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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.RDNPart;
import org.apache.directory.ldapstudio.browser.core.utils.LdifUtils;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * Default implementation of IValue.
 */
public class Value implements IValue
{

    private static final long serialVersionUID = -9039209604742682740L;

    private IAttribute attribute;

    private Object rawValue;


    protected Value()
    {
    }


    public Value( IAttribute attribute, Object rawValue ) throws ModelModificationException
    {
        this.init( attribute, rawValue );

        if ( rawValue == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_value );
        }
    }


    public Value( IAttribute attribute ) throws ModelModificationException
    {
        this.init( attribute, null );
    }


    private void init( IAttribute attribute, Object rawValue ) throws ModelModificationException
    {
        if ( attribute == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_attribute );
        }

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


    public IAttribute getAttribute()
    {
        return this.attribute;
    }


    public Object getRawValue()
    {
        return this.rawValue;
    }


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


    public boolean isString()
    {
        return this.rawValue == EMPTY_STRING_VALUE || this.attribute.isString();
    }


    public boolean isBinary()
    {
        return this.rawValue == EMPTY_BINARY_VALUE || this.attribute.isBinary();
    }


    public boolean isEmpty()
    {
        return this.rawValue == EMPTY_STRING_VALUE || this.rawValue == EMPTY_BINARY_VALUE;
    }


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


    public int hashCode()
    {
        return rawValue.hashCode();
    }


    public String toString()
    {
        return attribute + ":" + ( this.isString() ? this.getStringValue() : "BINARY" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    public Object getAdapter( Class adapter )
    {
        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IConnection.class )
        {
            return this.getConnection();
        }
        if ( adapter == IEntry.class )
        {
            return this.getEntry();
        }
        if ( adapter == IAttribute.class )
        {
            return this.getAttribute();
        }
        if ( adapter == IValue.class )
        {
            return this;
        }
        return null;
    }


    public IConnection getConnection()
    {
        return this.attribute.getEntry().getConnection();
    }


    public IEntry getEntry()
    {
        return this.attribute.getEntry();
    }


    public IValue getValue()
    {
        return this;
    }


    public boolean isRdnPart()
    {
        RDNPart[] parts = getAttribute().getEntry().getRdn().getParts();
        for ( int p = 0; p < parts.length; p++ )
        {
            if ( getAttribute().getDescription().equals( parts[p].getName() )
                && getStringValue().equals( parts[p].getValue() ) )
            {
                return true;
            }
        }
        return false;
    }
}
