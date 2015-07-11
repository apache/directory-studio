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
package org.apache.directory.studio.openldap.config.editor.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.openldap.common.ui.model.DnSpecStyleEnum;
import org.apache.directory.studio.openldap.common.ui.model.DnSpecTypeEnum;
import org.apache.directory.studio.openldap.common.ui.model.LimitSelectorEnum;


/**
 * This class wraps the olcLimits parameter :
 * <pre>
 * olcLimits ::= selector limit limit-e
 * selector  ::= '*' | 'anonymous' | 'users' | dnspec '=' pattern | group '=' pattern
 * dnspec ::= 'dn' type-e style-e
 * type-e ::= '.self' | '.this' | e
 * style-e ::= '.exact' | '.base' | '.one' | '.onelevel' | '.sub' | '.subtree' | '.children' | '.regex' | '.anonymous' | e
 * pattern ::= '*' | '.*' | '"' REGEXP '"'
 * group ::= 'group' group-oc
 * group-oc ::= '/' OBJECT_CLASS group-at | e
 * group-at ::= '/' ATTRIBUTE_TYPE | e
 * limit ::= 'time' time-limit | 'size' size-limit
 * time-limit ::= '.soft=' limit-value| '.hard=' time-hard | '=' limit-value
 * time-hard ::= limit-value | 'soft'
 * size-limit ::= '.soft=' limit-value | '.hard=' size-hard | '.unchecked=' | '=' limit-value
 * size-hard ::= limit-value | 'soft' | 'disable'
 * limit-value ::= INT | 'unlimited'
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LimitsWrapper implements Cloneable, Comparable<LimitsWrapper>
{
    /** Prefix, as the Limits are ordered */
    private int prefix;
    
    /** The selector */
    private LimitSelectorEnum selector;
    
    /** The pattern if the selector is a dnspec or a group */
    private String selectorPattern;
    
    /** The type if the selector is a DnSpec */ 
    private DnSpecTypeEnum dnSpecType;
    
    /** The style if the selector is a DnSpec */ 
    private DnSpecStyleEnum dnSpecStyle;
    
    /** The group ObjectClass */
    private String objectClass;
    
    /** The group AttributeType */
    private String attributeType;
    
    /** The list of limits, as Strings */
    private List<LimitWrapper> limits = new ArrayList<LimitWrapper>();
    
    /** A flag to tell if the limits is valid or not */
    private boolean isValid = true;
    
    /** A flag used when the limit is invalid */
    private static final int ERROR = -1;

    /** A flag used when the parsing is completed */
    private static final int EOL = Integer.MIN_VALUE;
    
    /**
     * Create a LimitsrWrapper instance
     */
    public LimitsWrapper()
    {
    }
    
    
    /**
     * Create a LimitsrWrapper instance from a String
     * 
     * @param limitsStr The String that contain the value
     */
    public LimitsWrapper( String limitsStr )
    {
        if ( limitsStr != null )
        {
            // use a lowercase version of the string
            String lowerCaseLimits = limitsStr.toLowerCase();
            int pos = 0;
            
            // Process the selector
            if ( lowerCaseLimits.startsWith( LimitSelectorEnum.ANY.getName() ) )
            {
                selector = LimitSelectorEnum.ANY;
                pos += LimitSelectorEnum.ANY.getName().length();
            }
            if ( lowerCaseLimits.startsWith( LimitSelectorEnum.ANONYMOUS.getName() ) )
            {
                selector = LimitSelectorEnum.ANONYMOUS;
                pos += LimitSelectorEnum.ANONYMOUS.getName().length();
            }
            else if ( lowerCaseLimits.startsWith( LimitSelectorEnum.USERS.getName() ) )
            {
                selector = LimitSelectorEnum.USERS;
                pos += LimitSelectorEnum.USERS.getName().length();
            }
            else if ( lowerCaseLimits.startsWith( LimitSelectorEnum.DNSPEC.getName() ) )
            {
                selector = LimitSelectorEnum.DNSPEC;
                pos += LimitSelectorEnum.DNSPEC.getName().length();
                
                // parse the type
                pos = parseDnSpec( lowerCaseLimits, pos );
                
                if ( pos == ERROR )
                {
                    isValid = false;
                }
            }
            else if ( lowerCaseLimits.startsWith( LimitSelectorEnum.GROUP.getName() ) )
            {
                selector = LimitSelectorEnum.GROUP;
                pos += LimitSelectorEnum.GROUP.getName().length();
                
                pos = parseGroup( lowerCaseLimits, pos );
                
                if ( pos == ERROR )
                {
                    // This is an error
                    isValid = false;
                }
            }
            
            // Process the limits, only if the selector was valid
            if ( isValid )
            {
                boolean noLimit = true;
                
                while ( pos >= 0 )
                {
                    pos = parseLimit( lowerCaseLimits, pos );
                    
                    if ( noLimit )
                    {
                        if ( pos == EOL )
                        {
                            // We must have at least one limit
                            {
                                isValid = false;
                                break;
                            }
                        }
                        else
                        {
                            noLimit = false;
                        }
                    }
                
                    if ( pos == ERROR )
                    {
                        isValid = false;
                        break;
                    }
                }
            }
        }
    }
    
    
    /**
     * Parse the DNSpec part : 
     * <pre>
     * dnspec ::= 'dn' type-e style-e '=' pattern
     * type-e ::= '.self' | '.this' | e
     * style-e ::= '.exact' | '.base' | '.one' | '.onelevel' | '.sub' | '.subtree' | '.children' | '.regex' | '.anonymous' | e
     * </pre>
     */
    private int parseDnSpec( String str, int pos )
    {
        // The type
        if ( str.startsWith( ".self", pos ) )
        {
            dnSpecType = DnSpecTypeEnum.SELF;
            pos += 5;
        }
        else if ( str.startsWith( ".this", pos ) )
        {
            dnSpecType = DnSpecTypeEnum.THIS;
            pos += 5;
        }
        
        // The style
        if ( str.startsWith( ".exact", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.EXACT;
            pos += 6;
        }
        else if ( str.startsWith( ".base", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.BASE;
            pos += 5;
        }
        else if ( str.startsWith( ".onelevel", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.ONE_LEVEL;
            pos += 9;
        }
        else if ( str.startsWith( ".one", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.ONE_LEVEL;
            pos += 4;
        }
        else if ( str.startsWith( ".subtree", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.SUBTREE;
            pos += 8;
        }
        else if ( str.startsWith( ".sub", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.SUBTREE;
            pos += 4;
        }
        else if ( str.startsWith( ".children", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.CHILDREN;
            pos += 9;
        }
        else if ( str.startsWith( ".regex", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.REGEXP;
            pos += 6;
        }
        else if ( str.startsWith( ".anonymous", pos ) )
        {
            dnSpecStyle = DnSpecStyleEnum.ANONYMOUS;
            pos += 10;
        }
        
        // The pattern
        return parsePattern( str, pos );
    }

    
    /**
     * Parse the group part :
     * <pre>
     * group ::= 'group' group-oc '=' pattern
     * group-oc ::= '/' OBJECT_CLASS group-at | e
     * group-at ::= '/' ATTRIBUTE_TYPE | e
     * </pre>
     */
    private int parseGroup( String str, int pos )
    {
        // Check if we have an ObjectClass
        if ( Strings.isCharASCII( str, pos, '/' ) )
        {
            int i = pos + 1;
            
            for ( ; i < str.length(); i++ )
            {
                char c = str.charAt( i );
                
                if ( ( ( c >= 'a' ) && ( c <= 'z' ) ) || ( ( c >= 'A' ) && ( c <= 'Z' ) ) ||
                    ( ( c >= '0' ) && ( c <= '9' ) ) || ( c == '.' ) || ( c == '-' ) || ( c == '_' ) )
                {
                    continue;
                }
            }
            
            if ( i > pos + 1 )
            {
                // An ObjectClass
                objectClass = str.substring( pos + 1, i );
            }
            
            pos = i;
        }
        
        // Check if we have an AttributeType
        if ( Strings.isCharASCII( str, pos, '/' ) )
        {
            int i = pos + 1;
            
            for ( ; i < str.length(); i++ )
            {
                char c = str.charAt( i );
                
                if ( ( ( c >= 'a' ) && ( c <= 'z' ) ) || ( ( c >= 'A' ) && ( c <= 'Z' ) ) ||
                    ( ( c >= '0' ) && ( c <= '9' ) ) || ( c == '.' ) || ( c == '-' ) || ( c == '_' ) )
                {
                    continue;
                }
            }
            
            if ( i > pos + 1 )
            {
                // An AttributeType
                attributeType = str.substring( pos + 1, i );
            }
            
            pos = i;
        }
        
        
        // The pattern
        return parsePattern( str, pos );
    }

    
    /**
     * Search for a pattern
     */
    private int parsePattern( String str, int pos )
    {
        if ( !Strings.isCharASCII( str, pos, '=' ) )
        {
            return ERROR;
        }
        
        pos++;

        if ( !Strings.isCharASCII( str, pos, '"' ) )
        {
            return ERROR;
        }
        
        pos++;
        
        boolean escapeSeen = false;
        
        for ( int  i = pos; i < str.length(); i++ )
        {
            if ( str.charAt( i ) == '\\' )
            {
                escapeSeen = !escapeSeen;
            }
            else
            {
                if ( str.charAt( i ) == '"' )
                {
                    if ( escapeSeen )
                    {
                        escapeSeen = false;
                    }
                    else
                    {
                        // This is the end of the patter
                        selectorPattern = str.substring( pos, i );
                        return i + 1;
                    }
                }
                else
                {
                    escapeSeen = false;
                }
            }
        }
        
        // The final '"' has not been found, this is an error.
        return ERROR;
    }
    
    
    /**
     * Parses the limit.
     * <pre>
     * limit ::= 'time' time-limit | 'size' size-limit
     * time-limit ::= '.soft=' limit-value| '.hard=' time-hard | '=' limit-value
     * time-hard ::= limit-value | 'soft'
     * size-limit ::= '.soft=' limit-value | '.hard=' size-hard | '.unchecked=' | '=' limit-value
     * size-hard ::= limit-value | 'soft' | 'disable'
     * limit-value ::= INT | 'unlimited' | 'none'
     * </pre>
     */
    private int parseLimit( String str, int pos )
    {
        // Remove spaces
        while ( Strings.isCharASCII( str, pos, ' ' ) )
        {
            pos++;
        }
        
        String limitStr = str.substring( pos );
        
        if ( Strings.isEmpty( limitStr ) )
        {
            return EOL;
        }
        
        int i = 0;
        
        if ( limitStr.startsWith( "time" ) )
        {
            // fetch the time limit (everything that goes up to a space or the end of the string
            for ( ; i < limitStr.length(); i++ )
            {
                if ( limitStr.charAt( i ) == ' ' )
                {
                    break;
                }
            }
            
            if ( i == 0 )
            {
                return ERROR;
            }
            
            TimeLimitWrapper timeLimitWrapper = new TimeLimitWrapper( limitStr.substring( 0, i ) );
            
            if ( timeLimitWrapper.isValid() )
            {
                limits.add( timeLimitWrapper );
                return pos + i; 
            }
            else
            {
                return ERROR;
            }
        }
        else if ( limitStr.startsWith( "size" ) )
        {
            // fetch the size limit (everything that goes up to a space or the end of the string
            for ( ;i < limitStr.length(); i++ )
            {
                if ( limitStr.charAt( i ) == ' ' )
                {
                    break;
                }
            }
            
            if ( i == 0 )
            {
                return ERROR;
            }
            
            SizeLimitWrapper sizeLimitWrapper = new SizeLimitWrapper( limitStr.substring( 0, i ) );
            
            if ( sizeLimitWrapper.isValid() )
            {
                limits.add( sizeLimitWrapper );
                return pos + i; 
            }
            else
            {
                return ERROR;
            }
        }
        else
        {
            return ERROR;
        }
    }
    
    
    /**
     * Sets a new prefix
     * 
     * @param prefix the prefix to set
     */
    public void setPrefix( int prefix )
    {
        this.prefix = prefix;
    }

    
    /**
     * @return the prefix
     */
    public int getPrefix()
    {
        return prefix;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void decrementPrefix()
    {
        prefix--;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void incrementPrefix()
    {
        prefix++;
    }
    
    
    /**
     * @return the selector
     */
    public LimitSelectorEnum getSelector()
    {
        return selector;
    }


    /**
     * @param selector the selector to set
     */
    public void setSelector( LimitSelectorEnum selector )
    {
        this.selector = selector;
    }


    /**
     * @return the selectorPattern
     */
    public String getSelectorPattern()
    {
        return selectorPattern;
    }


    /**
     * @param selectorPattern the selectorPattern to set
     */
    public void setSelectorPattern( String selectorPattern )
    {
        this.selectorPattern = selectorPattern;
    }


    /**
     * @return the dnSpecType
     */
    public DnSpecTypeEnum getDnSpecType()
    {
        return dnSpecType;
    }


    /**
     * @param dnSpecType the dnSpecType to set
     */
    public void setDnSpecType( DnSpecTypeEnum dnSpecType )
    {
        this.dnSpecType = dnSpecType;
    }


    /**
     * @return the dnSpecStyle
     */
    public DnSpecStyleEnum getDnSpecStyle()
    {
        return dnSpecStyle;
    }


    /**
     * @param dnSpecStyle the dnSpecStyle to set
     */
    public void setDnSpecStyle( DnSpecStyleEnum dnSpecStyle )
    {
        this.dnSpecStyle = dnSpecStyle;
    }


    /**
     * @return the objectClass
     */
    public String getObjectClass()
    {
        return objectClass;
    }


    /**
     * @param objectClass the objectClass to set
     */
    public void setObjectClass( String objectClass )
    {
        this.objectClass = objectClass;
    }


    /**
     * @return the attributeType
     */
    public String getAttributeType()
    {
        return attributeType;
    }


    /**
     * @param attributeType the attributeType to set
     */
    public void setAttributeType( String attributeType )
    {
        this.attributeType = attributeType;
    }


    /**
     * @return the limits
     */
    public List<LimitWrapper> getLimits()
    {
        return limits;
    }


    /**
     * @param limits the limits to set
     */
    public void setLimits( List<LimitWrapper> limits )
    {
        this.limits = limits;
    }


    /**
     * Tells if the Limits element is valid or not
     * @return true if the values are correct, false otherwise
     */
    public boolean isValid()
    {
        return isValid;
    }
    
    
    /**
     * Clone the current object
     */
    public LimitsWrapper clone()
    {
        try
        {
            return (LimitsWrapper)super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            return null;
        }
    }
    
    
    /**
     * LimitsWrapper are ordered objects
     * @see Object#equals(Object)
     */
    public boolean equals( Object that )
    {
        // Quick test
        if ( this == that )
        {
            return true;
        }
        
        if ( that instanceof LimitsWrapper )
        {
            LimitsWrapper thatInstance = (LimitsWrapper)that;
            
            // Check the prefix first
            if ( prefix != thatInstance.prefix )
            {
                return false;
            }

            // The selector
            if ( selector != thatInstance.selector )
            {
                return false;
            }
            
            // Same selector. Depending on the type, check the two instance
            switch ( selector )
            {
                case DNSPEC :
                    if ( ( dnSpecStyle != thatInstance.dnSpecStyle ) || ( dnSpecType != thatInstance.dnSpecType ) )
                    {
                        return false;
                    }

                    // Check the pattern
                    if ( selectorPattern != thatInstance.selectorPattern )
                    {
                        return false;
                    }
                    
                    break;
                    
                case GROUP :
                    // If we have an ObjectClass, check it
                    if ( ( objectClass != null ) && ( !objectClass.equals( thatInstance.objectClass ) ) )
                    {
                        return false;
                    }
                    else if ( thatInstance.objectClass != null )
                    {
                        return false;
                    }

                    // If we have an AttributeType, check it
                    if ( ( attributeType != null ) && ( !attributeType.equals( thatInstance.attributeType ) ) )
                    {
                        return false;
                    }
                    else if ( thatInstance.attributeType != null )
                    {
                        return false;
                    }

                    // Check the pattern
                    if ( selectorPattern != thatInstance.selectorPattern )
                    {
                        return false;
                    }
                    
                    break;
                case ANY :
                case ANONYMOUS :
                case USERS :
                    break;
            }
            
            // Check the limits now
            if ( limits.size() != thatInstance.limits.size() )
            {
                return false;
            }
            
            // Iterate on both limits (they are not ordered... This is a O(n2) loop.
            for ( LimitWrapper limit : limits )
            {
                boolean found = false;
                
                for ( LimitWrapper thatLimit : thatInstance.limits )
                {
                    if ( limit.equals( thatLimit ) )
                    {
                        found = true;
                        break;
                    }
                }
                
                if ( !found )
                {
                    return false;
                }
            }
            
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        int h = 37;
        
        h += h*17 + selector.hashCode();
        
        // The selector
        switch ( selector )
        {
            case DNSPEC :
                if ( dnSpecType != null )
                {
                    h += h*17 + dnSpecType.hashCode();
                }
                
                if ( dnSpecStyle != null )
                {
                    h += h*17 + dnSpecStyle.hashCode();
                }
                
                break;
                
            case GROUP :
                if ( selectorPattern != null )
                {
                    h += h*17 + selectorPattern.hashCode();
                }
                
                break;
        }
        
        // The limits
        for ( LimitWrapper limit : limits )
        {
            h += h*17 + limit.hashCode();
        }
        
        return h;
    }


    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( LimitsWrapper that )
    {
        if ( that == null )
        {
            return 1;
        }
        
        // Check the prefix
        if ( prefix < that.prefix )
        {
            return -1;
        }
        else if ( prefix > that.prefix )
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( '{' ).append( prefix ).append( '}' );
        sb.append( selector.getName() );
        
        // The selector
        switch ( selector )
        {
            case ANY :
            case ANONYMOUS :
            case USERS :
                break;
                
            case DNSPEC :
                if ( dnSpecType != null )
                {
                    sb.append( '.' ).append( dnSpecType.getName() );
                }
                
                if ( dnSpecStyle != null )
                {
                    sb.append( '.' ).append( dnSpecStyle.getName() );
                }

                // fall through
                
            case GROUP :
                if ( objectClass != null )
                {
                    sb.append( '/' ).append( objectClass );
                }
                
                if ( attributeType != null )
                {
                    sb.append( '/' ).append( attributeType );
                }
                
                sb.append( "=\"" );
                sb.append( selectorPattern );
                sb.append(  '\"' );
                break;
        }
        
        // The limits
        for ( LimitWrapper limit : limits )
        {
            sb.append( ' ' );
            sb.append( limit );
        }
        
        return sb.toString();
    }
}
