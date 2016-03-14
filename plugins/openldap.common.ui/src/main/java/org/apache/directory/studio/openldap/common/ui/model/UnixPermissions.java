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
package org.apache.directory.studio.openldap.common.ui.model;


import java.text.ParseException;
import java.util.regex.Pattern;


/**
 * The class defines an Unix Permissions.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UnixPermissions
{
    /** The pattern used to match a symbolic value (e.g. "-rw-------") */
    private static final Pattern SYMBOLIC_FORMAT_PATTERN = Pattern.compile(
        "^-(-|r)(-|w)(-|x)(-|r)(-|w)(-|x)(-|r)(-|w)(-|x)$", Pattern.CASE_INSENSITIVE );

    private boolean ownerRead;
    private boolean ownerWrite;
    private boolean ownerExecute;
    private boolean groupRead;
    private boolean groupWrite;
    private boolean groupExecute;
    private boolean othersRead;
    private boolean othersWrite;
    private boolean othersExecute;


    /**
     * Creates a new instance of UnixPermissions.
     *
     */
    public UnixPermissions()
    {
    }


    /**
     * Creates a new instance of UnixPermissions.
     *
     * @param s the string
     * @throws ParseException if an error occurs during the parsing of the string
     */
    public UnixPermissions( String s ) throws ParseException
    {
        if ( ( s != null ) && ( !s.isEmpty() ) )
        {
            // First let's trim the value
            String trimmed = s.trim();
            int integerValue = -1;

            try
            {
                integerValue = Integer.parseInt( trimmed );
            }
            catch ( NumberFormatException e )
            {
                // Silent, integerValue will be -1.
            }

            // Is it an octal value?
            if ( trimmed.startsWith( "0" ) )
            {
                if ( trimmed.length() == 4 )
                {
                    readOwnerOctalValue( trimmed.charAt( 1 ) );
                    readGroupOctalValue( trimmed.charAt( 2 ) );
                    readOthersOctalValue( trimmed.charAt( 3 ) );
                }
                else
                {
                    throw new ParseException( "Unable to recognize the format for this Unix Permissions String '" + s
                        + "'.", 0 );
                }
            }
            // Is it a decimal value?
            else if ( integerValue != -1 )
            {
                String octal = Integer.toOctalString( integerValue );

                if ( octal.length() == 1 )
                {
                    octal = "00" + octal;
                }
                else if ( octal.length() == 2 )
                {
                    octal = "0" + octal;
                }

                readOwnerOctalValue( octal.charAt( 0 ) );
                readGroupOctalValue( octal.charAt( 1 ) );
                readOthersOctalValue( octal.charAt( 2 ) );
            }
            // Is it a symbolic value?
            else if ( SYMBOLIC_FORMAT_PATTERN.matcher( trimmed ).matches() )
            {
                readOwnerSymbolicValue( trimmed.substring( 1, 4 ) );
                readGroupSymbolicValue( trimmed.substring( 4, 7 ) );
                readOthersSymbolicValue( trimmed.substring( 7, 10 ) );
            }
            else
            {
                throw new ParseException( "Unable to recognize the format for this Unix Permissions String '" + s
                    + "'.", 0 );
            }
        }
    }


    /**
     * Reads the owner octal value.
     *
     * @param ownerValue the owner value
     */
    private void readOwnerOctalValue( char ownerValue )
    {
        if ( ownerValue == '1' )
        {
            ownerExecute = true;
        }
        else if ( ownerValue == '2' )
        {
            ownerWrite = true;
        }
        else if ( ownerValue == '3' )
        {
            ownerExecute = true;
            ownerWrite = true;
        }
        else if ( ownerValue == '4' )
        {
            ownerRead = true;
        }
        else if ( ownerValue == '5' )
        {
            ownerExecute = true;
            ownerRead = true;
        }
        else if ( ownerValue == '6' )
        {
            ownerWrite = true;
            ownerRead = true;
        }
        else if ( ownerValue == '7' )
        {
            ownerExecute = true;
            ownerWrite = true;
            ownerRead = true;
        }
    }


    /**
     * Reads the group octal value.
     *
     * @param groupValue the group value
     */
    private void readGroupOctalValue( char groupValue )
    {
        if ( groupValue == '1' )
        {
            groupExecute = true;
        }
        else if ( groupValue == '2' )
        {
            groupWrite = true;
        }
        else if ( groupValue == '3' )
        {
            groupExecute = true;
            groupWrite = true;
        }
        else if ( groupValue == '4' )
        {
            groupRead = true;
        }
        else if ( groupValue == '5' )
        {
            groupExecute = true;
            groupRead = true;
        }
        else if ( groupValue == '6' )
        {
            groupWrite = true;
            groupRead = true;
        }
        else if ( groupValue == '7' )
        {
            groupExecute = true;
            groupWrite = true;
            groupRead = true;
        }
    }


    /**
     * Reads the others octal value.
     *
     * @param othersValue the others value
     */
    private void readOthersOctalValue( char othersValue )
    {
        if ( othersValue == '1' )
        {
            othersExecute = true;
        }
        else if ( othersValue == '2' )
        {
            othersWrite = true;
        }
        else if ( othersValue == '3' )
        {
            othersExecute = true;
            othersWrite = true;
        }
        else if ( othersValue == '4' )
        {
            othersRead = true;
        }
        else if ( othersValue == '5' )
        {
            othersExecute = true;
            othersRead = true;
        }
        else if ( othersValue == '6' )
        {
            othersWrite = true;
            othersRead = true;
        }
        else if ( othersValue == '7' )
        {
            othersExecute = true;
            othersWrite = true;
            othersRead = true;
        }
    }


    /**
     * Reads the owner symbolic value.
     *
     * @param ownerValue the owner value
     */
    private void readOwnerSymbolicValue( String ownerValue )
    {
        if ( ownerValue.length() == 3 )
        {
            // Read
            if ( ownerValue.charAt( 0 ) == 'r' )
            {
                ownerRead = true;
            }

            // Write
            if ( ownerValue.charAt( 1 ) == 'w' )
            {
                ownerWrite = true;
            }

            // Execute
            if ( ownerValue.charAt( 2 ) == 'x' )
            {
                ownerExecute = true;
            }
        }
    }


    /**
     * Reads the group symbolic value.
     *
     * @param groupValue the group value
     */
    private void readGroupSymbolicValue( String groupValue )
    {
        if ( groupValue.length() == 3 )
        {
            // Read
            if ( groupValue.charAt( 0 ) == 'r' )
            {
                groupRead = true;
            }

            // Write
            if ( groupValue.charAt( 1 ) == 'w' )
            {
                groupWrite = true;
            }

            // Execute
            if ( groupValue.charAt( 2 ) == 'x' )
            {
                groupExecute = true;
            }
        }
    }


    /**
     * Reads the others symbolic value.
     *
     * @param othersValue the others value
     */
    private void readOthersSymbolicValue( String othersValue )
    {
        if ( othersValue.length() == 3 )
        {
            // Read
            if ( othersValue.charAt( 0 ) == 'r' )
            {
                othersRead = true;
            }

            // Write
            if ( othersValue.charAt( 1 ) == 'w' )
            {
                othersWrite = true;
            }

            // Execute
            if ( othersValue.charAt( 2 ) == 'x' )
            {
                othersExecute = true;
            }
        }
    }


    /**
     * Gets the integer value.
     *
     * @return the integer value
     */
    public Integer getDecimalValue()
    {
        return Integer.parseInt( getOctalValue(), 8 );
    }


    /**
     * Gets the octal value.
     * 
     * @return the octal value
     */
    public String getOctalValue()
    {
        int value = 0;

        // Owner Read
        if ( ownerRead )
        {
            value = value + 400;
        }

        // Owner Write
        if ( ownerWrite )
        {
            value = value + 200;
        }

        // Owner Execute
        if ( ownerExecute )
        {
            value = value + 100;
        }

        // Group Read
        if ( groupRead )
        {
            value = value + 40;
        }

        // Group Write
        if ( groupWrite )
        {
            value = value + 20;
        }

        // Group Execute
        if ( groupExecute )
        {
            value = value + 10;
        }

        // Others Read
        if ( othersRead )
        {
            value = value + 4;
        }

        // Others Write
        if ( othersWrite )
        {
            value = value + 2;
        }

        // Others Execute
        if ( othersExecute )
        {
            value = value + 1;
        }

        // Adding zeros before returning the value
        if ( value < 10 )
        {
            return "000" + value;
        }
        else if ( value < 100 )
        {
            return "00" + value;
        }
        else if ( value < 1000 )
        {
            return "0" + value;
        }
        else
        {
            return "" + value;
        }
    }


    /**
     * Gets the symbolic value (no type included).
     * 
     * @return the symbolic value
     */
    public String getSymbolicValue()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( '-' );

        // Owner Read
        if ( ownerRead )
        {
            sb.append( 'r' );
        }
        else
        {
            sb.append( '-' );
        }

        // Owner Write
        if ( ownerWrite )
        {
            sb.append( 'w' );
        }
        else
        {
            sb.append( '-' );
        }

        // Owner Execute
        if ( ownerExecute )
        {
            sb.append( 'x' );
        }
        else
        {
            sb.append( '-' );
        }

        // Group Read
        if ( groupRead )
        {
            sb.append( 'r' );
        }
        else
        {
            sb.append( '-' );
        }

        // Group Write
        if ( groupWrite )
        {
            sb.append( 'w' );
        }
        else
        {
            sb.append( '-' );
        }

        // Group Execute
        if ( groupExecute )
        {
            sb.append( 'x' );
        }
        else
        {
            sb.append( '-' );
        }

        // Others Read
        if ( othersRead )
        {
            sb.append( 'r' );
        }
        else
        {
            sb.append( '-' );
        }

        // Others Write
        if ( othersWrite )
        {
            sb.append( 'w' );
        }
        else
        {
            sb.append( '-' );
        }

        // Others Execute
        if ( othersExecute )
        {
            sb.append( 'x' );
        }
        else
        {
            sb.append( '-' );
        }

        return sb.toString();
    }


    public boolean isGroupExecute()
    {
        return groupExecute;
    }


    public boolean isGroupRead()
    {
        return groupRead;
    }


    public boolean isGroupWrite()
    {
        return groupWrite;
    }


    public boolean isOthersExecute()
    {
        return othersExecute;
    }


    public boolean isOthersRead()
    {
        return othersRead;
    }


    public boolean isOthersWrite()
    {
        return othersWrite;
    }


    public boolean isOwnerExecute()
    {
        return ownerExecute;
    }


    public boolean isOwnerRead()
    {
        return ownerRead;
    }


    public boolean isOwnerWrite()
    {
        return ownerWrite;
    }


    public void setGroupExecute( boolean groupExecute )
    {
        this.groupExecute = groupExecute;
    }


    public void setGroupRead( boolean groupRead )
    {
        this.groupRead = groupRead;
    }


    public void setGroupWrite( boolean groupWrite )
    {
        this.groupWrite = groupWrite;
    }


    public void setOthersExecute( boolean othersExecute )
    {
        this.othersExecute = othersExecute;
    }


    public void setOthersRead( boolean othersRead )
    {
        this.othersRead = othersRead;
    }


    public void setOthersWrite( boolean othersWrite )
    {
        this.othersWrite = othersWrite;
    }


    public void setOwnerExecute( boolean ownerExecute )
    {
        this.ownerExecute = ownerExecute;
    }


    public void setOwnerRead( boolean ownerRead )
    {
        this.ownerRead = ownerRead;
    }


    public void setOwnerWrite( boolean ownerWrite )
    {
        this.ownerWrite = ownerWrite;
    }
}
