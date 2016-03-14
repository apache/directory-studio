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

package org.apache.directory.studio.ldifparser.model.lines;


import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.LdifUtils;


public class LdifControlLine extends LdifValueLineBase
{
    private String rawCriticality;

    private String rawControlValueType;

    private String rawControlValue;


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
        return getNonNull( rawCriticality );
    }


    public String getUnfoldedCriticality()
    {
        return unfold( getRawCriticality() );
    }


    public boolean isCritical()
    {
        return getUnfoldedCriticality().endsWith( "true" ); //$NON-NLS-1$
    }


    public String getRawControlValueType()
    {
        return getNonNull( rawControlValueType );
    }


    public String getUnfoldedControlValueType()
    {
        return unfold( getRawControlValueType() );
    }


    public String getRawControlValue()
    {
        return getNonNull( rawControlValue );
    }


    public String getUnfoldedControlValue()
    {
        return unfold( getRawControlValue() );
    }


    public String toRawString()
    {
        return getRawControlSpec() + getRawControlType() + getRawOid() + getRawCriticality()
            + getRawControlValueType() + getRawControlValue() + getRawNewLine();
    }


    public boolean isValid()
    {
        return getUnfoldedControlSpec().length() > 0
            && getUnfoldedControlType().length() > 0
            && getUnfoldedOid().length() > 0
            && ( rawCriticality == null || getUnfoldedCriticality().endsWith( "true" ) || this //$NON-NLS-1$
                .getUnfoldedCriticality().endsWith( "false" ) ) //$NON-NLS-1$
            && ( ( rawControlValueType == null && rawControlValue == null ) || ( rawControlValueType != null && rawControlValue != null ) )
            && getUnfoldedNewLine().length() > 0;
    }


    public String getInvalidString()
    {
        if ( getUnfoldedControlSpec().length() == 0 )
        {
            return "Missing 'control'";
        }
        else if ( getUnfoldedOid().length() == 0 )
        {
            return "Missing OID";
        }
        else if ( ( rawCriticality != null && !getUnfoldedCriticality().endsWith( "true" ) && !this //$NON-NLS-1$
            .getUnfoldedCriticality().endsWith( "false" ) ) ) //$NON-NLS-1$
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
        if ( isControlValueTypeSafe() )
        {
            return getUnfoldedControlValue();
        }
        else if ( isControlValueTypeBase64() )
        {
            return LdifUtils.base64decodeToByteArray( getUnfoldedControlValue() );
        }
        else
        {
            return null;
        }
    }


    public boolean isControlValueTypeBase64()
    {
        return getUnfoldedControlValueType().startsWith( "::" ); //$NON-NLS-1$
    }


    public boolean isControlValueTypeSafe()
    {
        return getUnfoldedControlValueType().startsWith( ":" ) && !isControlValueTypeBase64(); //$NON-NLS-1$
    }


    public static LdifControlLine create( String oid, String criticality, String controlValue )
    {
        if ( LdifUtils.mustEncode( controlValue ) )
        {
            return create( oid, criticality, LdifUtils.utf8encode( controlValue ) );
        }
        else
        {
            LdifControlLine controlLine = new LdifControlLine( 0, "control", ":", oid, criticality, //$NON-NLS-1$ //$NON-NLS-2$
                controlValue != null ? ":" : null, controlValue != null ? controlValue : null, //$NON-NLS-1$
                LdifParserConstants.LINE_SEPARATOR );
            return controlLine;
        }
    }


    public static LdifControlLine create( String oid, String criticality, byte[] controlValue )
    {
        LdifControlLine controlLine = new LdifControlLine( 0, "control", ":", oid, criticality, controlValue != null //$NON-NLS-1$ //$NON-NLS-2$
            && controlValue.length > 0 ? "::" : null, controlValue != null && controlValue.length > 0 ? LdifUtils //$NON-NLS-1$
            .base64encode( controlValue ) : null, LdifParserConstants.LINE_SEPARATOR );
        return controlLine;
    }


    public static LdifControlLine create( String oid, boolean isCritical, String controlValue )
    {
        return create( oid, isCritical ? " true" : " false", controlValue ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    public static LdifControlLine create( String oid, boolean isCritical, byte[] controlValue )
    {
        return create( oid, isCritical ? " true" : " false", controlValue ); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
