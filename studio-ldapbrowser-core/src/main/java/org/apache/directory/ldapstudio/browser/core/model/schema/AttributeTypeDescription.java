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

package org.apache.directory.ldapstudio.browser.core.model.schema;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;


public class AttributeTypeDescription extends SchemaPart2
{

    private static final long serialVersionUID = 8023296692770420698L;

    public static final String ATTRIBUTE_USAGE_USER_APPLICATIONS = "userApplications";

    public static final String ATTRIBUTE_USAGE_DISTRIBUTED_OPERATION = "distributedOperation";

    public static final String ATTRIBUTE_USAGE_DIRECTORY_OPERATION = "directoryOperation";

    public static final String ATTRIBUTE_USAGE_DSA_OPERATION = "dSAOperation";

    private String superiorAttributeTypeDescriptionName;

    private String equalityMatchingRuleDescriptionOID;

    private String orderingMatchingRuleDescriptionOID;

    private String substringMatchingRuleDescriptionOID;

    private String syntaxDescriptionNumericOIDPlusLength;

    private boolean isSingleValued;

    private boolean isCollective;

    private boolean isNoUserModification;

    private String usage;


    public AttributeTypeDescription()
    {
        super();
        this.superiorAttributeTypeDescriptionName = null;
        this.equalityMatchingRuleDescriptionOID = null;
        this.orderingMatchingRuleDescriptionOID = null;
        this.substringMatchingRuleDescriptionOID = null;
        this.syntaxDescriptionNumericOIDPlusLength = null;
        this.isSingleValued = false;
        this.isCollective = false;
        this.isNoUserModification = false;
        this.usage = ATTRIBUTE_USAGE_USER_APPLICATIONS;
    }


    public int compareTo( Object o )
    {
        if ( o instanceof AttributeTypeDescription )
        {
            return this.toString().compareTo( o.toString() );
        }
        else
        {
            throw new ClassCastException( "Object of type " + this.getClass().getName() + " required." );
        }
    }


    /**
     * 
     * @return the equality matching rule OID, may be null
     */
    public String getEqualityMatchingRuleDescriptionOID()
    {
        return equalityMatchingRuleDescriptionOID;
    }


    public void setEqualityMatchingRuleDescriptionOID( String equalityMatchingRuleDescriptionOID )
    {
        this.equalityMatchingRuleDescriptionOID = equalityMatchingRuleDescriptionOID;
    }


    /**
     * 
     * @return the equality matching rule description OID of this or the
     *         superior attribute type description, may be null
     */
    public String getEqualityMatchingRuleDescriptionOIDTransitive()
    {
        if ( this.equalityMatchingRuleDescriptionOID != null )
        {
            return this.equalityMatchingRuleDescriptionOID;
        }
        else if ( this.getExistingSuperiorAttributeTypeDescription() != null )
        {
            return this.getExistingSuperiorAttributeTypeDescription().getEqualityMatchingRuleDescriptionOIDTransitive();
        }
        else
        {
            return null;
        }
    }


    /**
     * 
     * @return the ordering matching rule OID, may be null
     */
    public String getOrderingMatchingRuleDescriptionOID()
    {
        return orderingMatchingRuleDescriptionOID;
    }


    public void setOrderingMatchingRuleDescriptionOID( String orderingMatchingRuleDescriptionOID )
    {
        this.orderingMatchingRuleDescriptionOID = orderingMatchingRuleDescriptionOID;
    }


    /**
     * 
     * @return the ordering matching rule description OID of this or the
     *         superior attribute type description, may be null
     */
    public String getOrderingMatchingRuleDescriptionOIDTransitive()
    {
        if ( this.orderingMatchingRuleDescriptionOID != null )
        {
            return this.orderingMatchingRuleDescriptionOID;
        }
        else if ( this.getExistingSuperiorAttributeTypeDescription() != null )
        {
            return this.getExistingSuperiorAttributeTypeDescription().getOrderingMatchingRuleDescriptionOIDTransitive();
        }
        else
        {
            return null;
        }
    }


    /**
     * 
     * @return the substring matching rule OID, may be null
     */
    public String getSubstringMatchingRuleDescriptionOID()
    {
        return substringMatchingRuleDescriptionOID;
    }


    public void setSubstringMatchingRuleDescriptionOID( String substringMatchingRuleDescriptionOID )
    {
        this.substringMatchingRuleDescriptionOID = substringMatchingRuleDescriptionOID;
    }


    /**
     * 
     * @return the substring matching rule description OID of this or the
     *         superior attribute type description, may be null
     */
    public String getSubstringMatchingRuleDescriptionOIDTransitive()
    {
        if ( this.substringMatchingRuleDescriptionOID != null )
        {
            return this.substringMatchingRuleDescriptionOID;
        }
        else if ( this.getExistingSuperiorAttributeTypeDescription() != null )
        {
            return this.getExistingSuperiorAttributeTypeDescription()
                .getSubstringMatchingRuleDescriptionOIDTransitive();
        }
        else
        {
            return null;
        }
    }


    /**
     * 
     * @return the name of the superior (parent) attribute type description
     *         or null
     */
    public String getSuperiorAttributeTypeDescriptionName()
    {
        return superiorAttributeTypeDescriptionName;
    }


    public void setSuperiorAttributeTypeDescriptionName( String superiorAttributeTypeDescriptionName )
    {
        this.superiorAttributeTypeDescriptionName = superiorAttributeTypeDescriptionName;
    }


    /**
     * 
     * @return the syntax description OID, may be null
     */
    public String getSyntaxDescriptionNumericOIDPlusLength()
    {
        return syntaxDescriptionNumericOIDPlusLength;
    }


    public void setSyntaxDescriptionNumericOIDPlusLength( String syntaxDescriptionNumericOIDPlusLength )
    {
        this.syntaxDescriptionNumericOIDPlusLength = syntaxDescriptionNumericOIDPlusLength;
    }


    /**
     * 
     * @return the syntax description OID of this or the superior attribute
     *         type description, may be null
     */
    public String getSyntaxDescriptionNumericOIDTransitive()
    {
        if ( this.syntaxDescriptionNumericOIDPlusLength != null )
        {
            if ( this.syntaxDescriptionNumericOIDPlusLength.endsWith( "}" )
                && this.syntaxDescriptionNumericOIDPlusLength.indexOf( "{" ) != -1 )
            {
                String syntaxOid = this.syntaxDescriptionNumericOIDPlusLength.substring( 0,
                    this.syntaxDescriptionNumericOIDPlusLength.indexOf( "{" ) );
                return syntaxOid;
            }
            else
            {
                return this.syntaxDescriptionNumericOIDPlusLength;
            }
        }
        else if ( this.getExistingSuperiorAttributeTypeDescription() != null )
        {
            return this.getExistingSuperiorAttributeTypeDescription().getSyntaxDescriptionNumericOIDTransitive();
        }
        else
        {
            return null;
        }
    }


    /**
     * 
     * @return the syntax length of this or the superior attribute type
     *         description, may be null
     */
    public String getSyntaxDescriptionLengthTransitive()
    {
        if ( this.syntaxDescriptionNumericOIDPlusLength != null
            && this.syntaxDescriptionNumericOIDPlusLength.endsWith( "}" )
            && this.syntaxDescriptionNumericOIDPlusLength.indexOf( "{" ) != -1 )
        {
            String length = this.syntaxDescriptionNumericOIDPlusLength.substring(
                this.syntaxDescriptionNumericOIDPlusLength.indexOf( "{" ) + 1,
                this.syntaxDescriptionNumericOIDPlusLength.indexOf( "}" ) );
            return length;
        }
        else if ( this.getExistingSuperiorAttributeTypeDescription() != null )
        {
            return this.getExistingSuperiorAttributeTypeDescription().getSyntaxDescriptionLengthTransitive();
        }
        else
        {
            return null;
        }
    }


    /**
     * 
     * @return the usage, on of ATTRIBUTE_USAGE_...
     */
    public String getUsage()
    {
        return usage;
    }


    public void setUsage( String usage )
    {
        if ( usage == null )
        {
            this.usage = ATTRIBUTE_USAGE_USER_APPLICATIONS;
        }
        else
        {
            this.usage = usage;
        }
    }


    /**
     * 
     * @return the single-valued flag
     */
    public boolean isSingleValued()
    {
        return isSingleValued;
    }


    public void setSingleValued( boolean isSingleValued )
    {
        this.isSingleValued = isSingleValued;
    }


    /**
     * 
     * @return the collective flag
     */
    public boolean isCollective()
    {
        return isCollective;
    }


    public void setCollective( boolean isCollective )
    {
        this.isCollective = isCollective;
    }


    /**
     * 
     * @return the no-user-modification flag
     */
    public boolean isNoUserModification()
    {
        return isNoUserModification;
    }


    public void setNoUserModification( boolean isNoUserModification )
    {
        this.isNoUserModification = isNoUserModification;
    }


    /**
     * Convenience method to !isBinary().
     * 
     * @return true if this attribute type or its syntax is defined as
     *         string
     */
    public boolean isString()
    {
        return !isBinary();
    }


    /**
     * Checks the pre-defined and user-defined binary attribute types. If
     * this attribute name or OID is defned as binary true is returned. Then
     * the syntax is checked, see LdadSyntaxDescription#isBinary().
     * 
     * @return true if this attribute type or its syntax is defined as
     *         binary
     */
    public boolean isBinary()
    {
        // check user-defined binary attributes
        Set binaryAttributeOidsAndNames = BrowserCorePlugin.getDefault().getCorePreferences()
            .getBinaryAttributeOidsAndNames();
        if ( binaryAttributeOidsAndNames.contains( this.numericOID ) )
        {
            return true;
        }
        for ( int i = 0; i < names.length; i++ )
        {
            if ( binaryAttributeOidsAndNames.contains( names[i] ) )
            {
                return true;
            }
        }

        // check syntax (includes user-defined binary syntaxes)
        return this.getSyntaxDescription().isBinary();
    }


    /**
     * 
     * @return the syntax description of this or the superior attribute type
     *         descripiton, may be the default or a dummy, never null
     */
    public LdapSyntaxDescription getSyntaxDescription()
    {

        String syntaxOid = this.getSyntaxDescriptionNumericOIDTransitive();
        if ( syntaxOid != null )
        {
            return this.getSchema().getLdapSyntaxDescription( syntaxOid );
        }
        else
        {
            return LdapSyntaxDescription.DUMMY;
        }
    }


    private AttributeTypeDescription getExistingSuperiorAttributeTypeDescription()
    {
        if ( this.superiorAttributeTypeDescriptionName != null
            && this.schema.hasAttributeTypeDescription( this.superiorAttributeTypeDescriptionName ) )
        {
            return this.getSchema().getAttributeTypeDescription( this.superiorAttributeTypeDescriptionName );
        }
        else
        {
            return null;
        }
    }


    /**
     * 
     * @return all attribute type description using this attribute type
     *         description as superior
     */
    public AttributeTypeDescription[] getDerivedAttributeTypeDescriptions()
    {
        Set derivedATDSet = new HashSet();
        for ( Iterator it = this.getSchema().getAtdMapByName().values().iterator(); it.hasNext(); )
        {
            AttributeTypeDescription atd = ( AttributeTypeDescription ) it.next();
            String supName = atd.getSuperiorAttributeTypeDescriptionName();
            if ( supName != null && this.getLowerCaseIdentifierSet().contains( supName.toLowerCase() ) )
            {
                derivedATDSet.add( atd );
            }
        }
        AttributeTypeDescription[] derivedAtds = ( AttributeTypeDescription[] ) derivedATDSet
            .toArray( new AttributeTypeDescription[0] );
        Arrays.sort( derivedAtds );
        return derivedAtds;
    }


    /**
     * 
     * @return all object class description using this attribute type
     *         description as must attribute
     */
    public ObjectClassDescription[] getUsedAsMust()
    {
        Set usedAsMustSet = new HashSet();
        for ( Iterator it = this.getSchema().getOcdMapByName().values().iterator(); it.hasNext(); )
        {
            ObjectClassDescription ocd = ( ObjectClassDescription ) it.next();
            Set mustSet = toLowerCaseSet( ocd.getMustAttributeTypeDescriptionNamesTransitive() );
            if ( mustSet.removeAll( this.getLowerCaseIdentifierSet() ) )
            {
                usedAsMustSet.add( ocd );
            }
        }
        ObjectClassDescription[] ocds = ( ObjectClassDescription[] ) usedAsMustSet
            .toArray( new ObjectClassDescription[0] );
        Arrays.sort( ocds );
        return ocds;
    }


    /**
     * 
     * @return all object class description using this attribute type
     *         description as may attribute
     */
    public ObjectClassDescription[] getUsedAsMay()
    {
        Set usedAsMaySet = new HashSet();
        for ( Iterator it = this.getSchema().getOcdMapByName().values().iterator(); it.hasNext(); )
        {
            ObjectClassDescription ocd = ( ObjectClassDescription ) it.next();
            Set maySet = toLowerCaseSet( ocd.getMayAttributeTypeDescriptionNamesTransitive() );
            if ( maySet.removeAll( this.getLowerCaseIdentifierSet() ) )
            {
                usedAsMaySet.add( ocd );
            }
        }
        ObjectClassDescription[] ocds = ( ObjectClassDescription[] ) usedAsMaySet
            .toArray( new ObjectClassDescription[0] );
        Arrays.sort( ocds );
        return ocds;
    }


    /**
     * 
     * @return all matching rule description names this attribute type
     *         description applies to according to the schemas matching rule
     *         use descriptions
     */
    public String[] getOtherMatchingRuleDescriptionNames()
    {
        Set otherMatchingRuleSet = new HashSet();
        for ( Iterator it = this.getSchema().getMrudMapByName().values().iterator(); it.hasNext(); )
        {
            MatchingRuleUseDescription mrud = ( MatchingRuleUseDescription ) it.next();
            Set atdSet = toLowerCaseSet( mrud.getAppliesAttributeTypeDescriptionOIDs() );
            if ( atdSet.removeAll( this.getLowerCaseIdentifierSet() ) )
            {
                otherMatchingRuleSet.addAll( Arrays.asList( mrud.getNames() ) );
            }
        }
        String[] mrds = ( String[] ) otherMatchingRuleSet.toArray( new String[0] );
        Arrays.sort( mrds );
        return mrds;
    }

}
