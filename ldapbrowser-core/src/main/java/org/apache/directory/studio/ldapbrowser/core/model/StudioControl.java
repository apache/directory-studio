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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.io.Serializable;

import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;


/**
 * The StudioControl class represents a LDAP control as defined in RFC 4511
 * <pre>
 * Control ::= SEQUENCE {
 *     controlType             LDAPOID,
 *     criticality             BOOLEAN DEFAULT FALSE,
 *     controlValue            OCTET STRING OPTIONAL }
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioControl implements Serializable
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = -1289018814649849178L;

    /**
     * The subentries control as defined in RFC 3672.
     */
    public static final StudioControl SUBENTRIES_CONTROL = new StudioControl( "Subentries", "1.3.6.1.4.1.4203.1.10.1",
        false, new byte[]
            { 0x01, 0x01, ( byte ) 0xFF } );

    /**
     * The Manage DSA IT control as defined in RFC 3296.
     */
    public static final StudioControl MANAGEDSAIT_CONTROL = new StudioControl( "Manage DSA IT",
        "2.16.840.1.113730.3.4.2", false, null );

    /**
     * The Tree Delete control as defined in draft-armijo-ldap-treedelete-02.
     */
    public static final StudioControl TREEDELETE_CONTROL = new StudioControl( "Tree Delete", "1.2.840.113556.1.4.805",
        false, null );

    /** The symbolic name. */
    protected String name;

    /** The oid. */
    protected String oid;

    /** The critical. */
    protected boolean critical;

    /** The control value. */
    protected byte[] controlValue;


    /**
     * Creates a new instance of Control.
     */
    public StudioControl()
    {
    }


    /**
     * Creates a new instance of Control.
     *
     * @param name the symbolic name
     * @param oid the oid
     * @param critical the criticality
     * @param controlValue the control value
     */
    public StudioControl( String name, String oid, boolean critical, byte[] controlValue )
    {
        super();
        this.name = name == null ? "" : name;
        this.oid = oid;
        this.critical = critical;
        this.controlValue = controlValue;
    }


    /**
     * Gets the control value.
     * 
     * @return the control value
     */
    public byte[] getControlValue()
    {
        return controlValue;
    }


    /**
     * Gets the oid.
     * 
     * @return the oid
     */
    public String getOid()
    {
        return oid;
    }


    /**
     * Checks if is critical.
     * 
     * @return true, if is critical
     */
    public boolean isCritical()
    {
        return critical;
    }


    /**
     * Gets the symbolic name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        if ( oid == null )
        {
            return "";
        }

        LdifControlLine line = LdifControlLine.create( getOid(), isCritical(), getControlValue() );
        String s = line.toRawString();
        s = s.substring( line.getRawControlSpec().length(), s.length() );
        s = s.substring( line.getRawControlType().length(), s.length() );
        s = s.substring( 0, s.length() - line.getRawNewLine().length() );

        //System.out.println( s );

        return s;
    }


    /**
     * Sets the control value.
     * 
     * @param controlValue the control value
     */
    public void setControlValue( byte[] controlValue )
    {
        this.controlValue = controlValue;
    }


    /**
     * Sets the critical.
     * 
     * @param critical the critical
     */
    public void setCritical( boolean critical )
    {
        this.critical = critical;
    }


    /**
     * Sets the symbolic name.
     * 
     * @param name the name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Sets the oid.
     * 
     * @param oid the oid
     */
    public void setOid( String oid )
    {
        this.oid = oid;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + toString().hashCode();
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj == null || !( obj instanceof StudioControl ) )
        {
            return false;
        }
        StudioControl other = ( StudioControl ) obj;

        return this.toString().equals( other.toString() );
    }

}
