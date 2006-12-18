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

package org.apache.directory.ldapstudio.browser.core.model;


import java.io.Serializable;

import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifControlLine;


public class Control implements Serializable
{

    private static final long serialVersionUID = -1289018814649849178L;

    public static final Control SUBENTRIES_CONTROL = new Control( "Subentries Control", "1.3.6.1.4.1.4203.1.10.1",
        false, new byte[]
            { 0x01, 0x01, ( byte ) 0xFF } );

    private String name;

    private String oid;

    private boolean critical;

    private transient byte[] controlValue;


    public Control()
    {

    }


    public Control( String name, String oid, boolean critical, byte[] controlValue )
    {
        super();
        this.name = name == null ? "" : name;
        this.oid = oid;
        this.critical = critical;
        this.controlValue = controlValue;
    }


    // public static Control parseControl(String controlLdif) throws
    // ParseException {
    //		
    // if("".equals(controlLdif)) {
    // return NONE_CONTOL;
    // }
    //		
    // try {
    // String ldif =
    // "dn: cn=dummy" +
    // BrowserCoreConstants.LINE_SEPARATOR +
    // "control: " +
    // controlLdif +
    // BrowserCoreConstants.LINE_SEPARATOR
    // ;
    //			
    // LdifParser parser = new LdifParser();
    // LdifFile model = parser.parse(ldif);
    // LdifPart part = model.getLastContainer().getLastPart();
    // LdifControlLine ldifControlLine = (LdifControlLine)part;
    // if(!ldifControlLine.isValid()) {
    // throw new Exception(ldifControlLine.getInvalidString());
    // }
    //			
    // Control control = new Control("", ldifControlLine.getUnfoldedOid(),
    // ldifControlLine.isCritical(),
    // ldifControlLine.getControlValueAsBinary());
    // return control;
    // }
    // catch (Exception e) {
    // throw new ParseException(e.getMessage(), 0);
    // }
    // }

    public byte[] getControlValue()
    {
        return controlValue;
    }


    public String getOid()
    {
        return oid;
    }


    public boolean isCritical()
    {
        return critical;
    }


    public String getName()
    {
        return name;
    }


    public String toString()
    {

        if ( oid == null )
        {
            return "";
        }

        LdifControlLine line = LdifControlLine.create( getOid(), isCritical() ? " true" : " false", getControlValue() );
        String s = line.toRawString();
        s = s.substring( line.getRawControlSpec().length(), s.length() );
        s = s.substring( line.getRawControlType().length(), s.length() );
        s = s.substring( 0, s.length() - line.getRawNewLine().length() );

        // System.out.println(s);

        return s;
    }


    public void setControlValue( byte[] controlValue )
    {
        this.controlValue = controlValue;
    }


    public void setCritical( boolean critical )
    {
        this.critical = critical;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public void setOid( String oid )
    {
        this.oid = oid;
    }


    public boolean equals( Object obj )
    {
        if ( obj == null || !( obj instanceof Control ) )
        {
            return false;
        }
        Control other = ( Control ) obj;

        return this.toString().equals( other.toString() );
    }

}
