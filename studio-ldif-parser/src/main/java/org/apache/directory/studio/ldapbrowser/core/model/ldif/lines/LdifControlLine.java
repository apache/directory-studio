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

package org.apache.directory.studio.ldapbrowser.core.model.ldif.lines;


import org.apache.directory.studio.ldapbrowser.core.utils.LdifUtils;
import org.apache.directory.studio.ldifparser.LdifParserConstants;


public class LdifControlLine extends LdifValueLineBase
{

    private static final long serialVersionUID = -3961159214439218610L;

    private String rawCriticality;

    private String rawControlValueType;

    private String rawControlValue;


    protected LdifControlLine()
    {
    }


    public LdifControlLine( int offset, String rawControlSpec, String rawControlType, String rawOid,
        String rawCriticality, String rawControlValueType, String rawControlValue, String rawNewLine )
    {
        super( offset, rawControlSpec, rawControlType, rawOid, rawNewLine );
        this.rawCriticality = rawCriticality;
        this.rawControlValueType = rawControlValueType;
        this.rawControlValue = rawControlValue;
    }


    public String getRawControlSpec()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedControlSpec()
    {
        return super.getUnfoldedLineStart();
    }


    public String getRawControlType()
    {
        return super.getRawValueType();
    }


    public String getUnfoldedControlType()
    {
        return super.getUnfoldedValueType();
    }


    public String getRawOid()
    {
        return super.getRawValue();
    }


    public String getUnfoldedOid()
    {
        return super.getUnfoldedValue();
    }


    public String getRawCriticality()
    {
        return getNonNull( this.rawCriticality );
    }


    public String getUnfoldedCriticality()
    {
        return unfold( this.getRawCriticality() );
    }


    public boolean isCritical()
    {
        return this.getUnfoldedCriticality().endsWith( "true" );
    }


    public String getRawControlValueType()
    {
        return getNonNull( this.rawControlValueType );
    }


    public String getUnfoldedControlValueType()
    {
        return unfold( this.getRawControlValueType() );
    }


    public String getRawControlValue()
    {
        return getNonNull( this.rawControlValue );
    }


    public String getUnfoldedControlValue()
    {
        return unfold( this.getRawControlValue() );
    }


    public String toRawString()
    {
        return this.getRawControlSpec() + this.getRawControlType() + this.getRawOid() + this.getRawCriticality()
            + this.getRawControlValueType() + this.getRawControlValue() + this.getRawNewLine();
    }


    public boolean isValid()
    {
        return this.getUnfoldedControlSpec().length() > 0
            && this.getUnfoldedControlType().length() > 0
            && this.getUnfoldedOid().length() > 0
            && ( this.rawCriticality == null || this.getUnfoldedCriticality().endsWith( "true" ) || this
                .getUnfoldedCriticality().endsWith( "false" ) )
            && ( ( this.rawControlValueType == null && this.rawControlValue == null ) || ( this.rawControlValueType != null && this.rawControlValue != null ) )
            && this.getUnfoldedNewLine().length() > 0;
    }


    public String getInvalidString()
    {
        if ( this.getUnfoldedControlSpec().length() == 0 )
        {
            return "Missing 'control'";
        }
        else if ( this.getUnfoldedOid().length() == 0 )
        {
            return "Missing OID";
        }
        else if ( ( this.rawCriticality != null && !this.getUnfoldedCriticality().endsWith( "true" ) && !this
            .getUnfoldedCriticality().endsWith( "false" ) ) )
        {
            return "Invalid criticality, must be 'true' or 'false'";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    /**
     * 
     * @return the binary representation of the control value, may be null
     */
    public final byte[] getControlValueAsBinary()
    {
        Object o = getControlValueAsObject();
        if ( o instanceof String )
        {
            return LdifUtils.utf8encode( ( String ) o );
        }
        else if ( o instanceof byte[] )
        {
            return ( byte[] ) o;
        }
        else
        {
            return new byte[0];
        }
    }


    public final Object getControlValueAsObject()
    {
        if ( this.isControlValueTypeSafe() )
        {
            return this.getUnfoldedControlValue();
        }
        else if ( this.isControlValueTypeBase64() )
        {
            return LdifUtils.base64decodeToByteArray( this.getUnfoldedControlValue() );
        }
        else
        {
            return null;
        }
    }


    public boolean isControlValueTypeBase64()
    {
        return this.getUnfoldedControlValueType().startsWith( "::" );
    }


    public boolean isControlValueTypeSafe()
    {
        return this.getUnfoldedControlValueType().startsWith( ":" ) && !this.isControlValueTypeBase64();
    }


    public static LdifControlLine create( String oid, String criticality, String controlValue )
    {
        if ( LdifUtils.mustEncode( controlValue ) )
        {
            return create( oid, criticality, LdifUtils.utf8encode( controlValue ) );
        }
        else
        {
            LdifControlLine controlLine = new LdifControlLine( 0, "control", ":", oid, criticality,
                controlValue != null ? ":" : null, controlValue != null ? controlValue : null,
                    LdifParserConstants.LINE_SEPARATOR );
            return controlLine;
        }
    }


    public static LdifControlLine create( String oid, String criticality, byte[] controlValue )
    {
        LdifControlLine controlLine = new LdifControlLine( 0, "control", ":", oid, criticality, controlValue != null
            && controlValue.length > 0 ? "::" : null, controlValue != null && controlValue.length > 0 ? LdifUtils
            .base64encode( controlValue ) : null, LdifParserConstants.LINE_SEPARATOR );
        return controlLine;
    }

}
