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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class ObjectClassDescription extends SchemaPart2
{

    private static final long serialVersionUID = 2324990817612632432L;

    public static final String EXTENSIBLEOBJECT_OBJECTCLASSNAME = "extensibleObject";

    public static final String OBSOLETE = "Obsolete";

    public static final String ABSTRACT = "Abstract";

    public static final String STRUCTURAL = "Structural";

    public static final String AUXILIARY = "Auxiliary";

    public static final String OC_ALIAS = "alias";

    public static final String OC_REFERRAL = "referral";

    public static final String OC_SUBENTRY = "subentry";

    public static final String OC_COUNTRY = "country";

    public static final String OC_LOCALITY = "locality";

    public static final String OC_DCOBJECT = "dcObject";

    public static final String OC_DOMAIN = "domain";

    public static final String OC_GROUPOFNAMES = "groupOfNames";

    public static final String OC_GROUPOFUNIQUENAMES = "groupOfUniqueNames";

    public static final String OC_POSIXGROUP = "posixGroup";

    public static final String OC_PERSON = "person";

    public static final String OC_ORGANIZATIONALPERSON = "organizationalPerson";

    public static final String OC_INETORGPERSON = "inetOrgPerson";

    public static final String OC_RESIDENTIALPERSON = "residentialPerson";

    public static final String OC_PILOTPERSON = "pilotPerson";

    public static final String OC_NEWPILOTPERSON = "newPilotPerson";

    public static final String OC_ACCOUNT = "account";

    public static final String OC_ORGANIZATIONALROLE = "organizationalRole";

    public static final String OC_ORGANIZATION = "organization";

    public static final String OC_ORGANIZATIONALUNIT = "organizationalUnit";

    public static final String OC_PILOTORGANIZATION = "pilotOrganization";

    public static final String OC_DMD = "dmd";

    public static final String OC_APPLICATIONPROCESS = "applicationProcess";

    public static final String OC_APPLICATIONENTITY = "applicationEntity";

    public static final String OC_ = "organizationalUnit";

    private boolean isAbstract;

    private boolean isStructural;

    private boolean isAuxiliary;

    private String[] superiorObjectClassDescriptionNames;

    private String[] mustAttributeTypeDescriptionNames;

    private String[] mayAttributeTypeDescriptionNames;


    public ObjectClassDescription()
    {
        super();
        this.isAbstract = false;
        this.isStructural = true;
        this.isAuxiliary = false;
        this.superiorObjectClassDescriptionNames = new String[0];
        this.mustAttributeTypeDescriptionNames = new String[0];
        this.mayAttributeTypeDescriptionNames = new String[0];
    }


    public int compareTo( Object o )
    {
        if ( o instanceof ObjectClassDescription )
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
     * @return the abstract flag
     */
    public boolean isAbstract()
    {
        return isAbstract;
    }


    public void setAbstract( boolean isAbstract )
    {
        this.isAbstract = isAbstract;
        this.isAuxiliary = this.isAuxiliary && !this.isAbstract;
        this.isStructural = this.isStructural && !this.isAbstract;
    }


    /**
     * 
     * @return the auxiliary flag
     */
    public boolean isAuxiliary()
    {
        return isAuxiliary;
    }


    public void setAuxiliary( boolean isAuxiliary )
    {
        this.isAuxiliary = isAuxiliary;
        this.isAbstract = this.isAbstract && !this.isAuxiliary;
        this.isStructural = this.isStructural && !this.isAuxiliary;
    }


    /**
     * 
     * @return the structural flag
     */
    public boolean isStructural()
    {
        return isStructural;
    }


    public void setStructural( boolean isStructural )
    {
        this.isStructural = isStructural;
        this.isAbstract = this.isAbstract && !this.isStructural;
        this.isAuxiliary = this.isAuxiliary && !this.isStructural;
    }


    /**
     * 
     * @return the may attribute names, may be an empty array
     */
    public String[] getMayAttributeTypeDescriptionNames()
    {
        return mayAttributeTypeDescriptionNames;
    }


    public void setMayAttributeTypeDescriptionNames( String[] mayAttributeTypeDescriptionNames )
    {
        this.mayAttributeTypeDescriptionNames = mayAttributeTypeDescriptionNames;
    }


    /**
     * 
     * @return the may attribute names of this and all superior object class
     *         definitions
     */
    public String[] getMayAttributeTypeDescriptionNamesTransitive()
    {
        Set maySet = new HashSet();
        for ( int i = 0; i < this.mayAttributeTypeDescriptionNames.length; i++ )
        {
            maySet.add( this.mayAttributeTypeDescriptionNames[i] );
        }
        ObjectClassDescription[] supOCDs = this.getExistingSuperObjectClassDescription();
        if ( supOCDs != null && supOCDs.length > 0 )
        {
            for ( int i = 0; i < supOCDs.length; i++ )
            {
                maySet.addAll( Arrays.asList( supOCDs[i].getMayAttributeTypeDescriptionNamesTransitive() ) );
            }
        }
        String[] mays = ( String[] ) maySet.toArray( new String[maySet.size()] );
        Arrays.sort( mays );
        return mays;
    }


    /**
     * 
     * @return the must attribute names, may be an empty array
     */
    public String[] getMustAttributeTypeDescriptionNames()
    {
        return mustAttributeTypeDescriptionNames;
    }


    public void setMustAttributeTypeDescriptionNames( String[] mustAttributeTypeDescriptionNames )
    {
        this.mustAttributeTypeDescriptionNames = mustAttributeTypeDescriptionNames;
    }


    /**
     * 
     * @return the must attribute names of this and all superior object
     *         class definitions
     */
    public String[] getMustAttributeTypeDescriptionNamesTransitive()
    {
        Set maySet = new HashSet();
        for ( int i = 0; i < this.mustAttributeTypeDescriptionNames.length; i++ )
        {
            maySet.add( this.mustAttributeTypeDescriptionNames[i] );
        }
        ObjectClassDescription[] supOCDs = this.getExistingSuperObjectClassDescription();
        if ( supOCDs != null && supOCDs.length > 0 )
        {
            for ( int i = 0; i < supOCDs.length; i++ )
            {
                maySet.addAll( Arrays.asList( supOCDs[i].getMustAttributeTypeDescriptionNamesTransitive() ) );
            }
        }
        String[] musts = ( String[] ) maySet.toArray( new String[maySet.size()] );
        Arrays.sort( musts );
        return musts;
    }


    /**
     * 
     * @return the names of the superior (parent) object class names, may be
     *         an empty array
     */
    public String[] getSuperiorObjectClassDescriptionNames()
    {
        return superiorObjectClassDescriptionNames;
    }


    public void setSuperiorObjectClassDescriptionNames( String[] superiorObjectClassDescriptionNames )
    {
        this.superiorObjectClassDescriptionNames = superiorObjectClassDescriptionNames;
    }

    
    /**
     * 
     * @return all superior (parent) object class descriptions, may be an empty array
     */
    public ObjectClassDescription[] getSuperiorObjectClassDescriptions()
    {
        String[] names = getSuperiorObjectClassDescriptionNames();
        ObjectClassDescription[] superiorOcds = new ObjectClassDescription[names.length];
        for ( int i = 0; i < superiorOcds.length; i++ )
        {
            superiorOcds[i] = getSchema().getObjectClassDescription( names[i] );
        }
        return superiorOcds;
    }

    /**
     * 
     * @return all object class description using this object class
     *         definition as superior
     */
    public ObjectClassDescription[] getSubObjectClassDescriptions()
    {
        Set subOCDSet = new HashSet();
        for ( Iterator it = this.getSchema().getOcdMapByName().values().iterator(); it.hasNext(); )
        {
            ObjectClassDescription ocd = ( ObjectClassDescription ) it.next();
            Set supNameSet = toLowerCaseSet( ocd.getSuperiorObjectClassDescriptionNames() );
            if ( supNameSet.removeAll( this.getLowerCaseIdentifierSet() ) )
            {
                subOCDSet.add( ocd );
            }
        }
        ObjectClassDescription[] subOcds = ( ObjectClassDescription[] ) subOCDSet
            .toArray( new ObjectClassDescription[0] );
        Arrays.sort( subOcds );
        return subOcds;
    }


    private ObjectClassDescription[] getExistingSuperObjectClassDescription()
    {
        List supList = new ArrayList();
        for ( int i = 0; i < this.superiorObjectClassDescriptionNames.length; i++ )
        {
            if ( this.schema.hasObjectClassDescription( this.superiorObjectClassDescriptionNames[i] ) )
            {
                supList.add( this.schema.getObjectClassDescription( this.superiorObjectClassDescriptionNames[i] ) );
            }
        }
        ObjectClassDescription[] supOcds = ( ObjectClassDescription[] ) supList
            .toArray( new ObjectClassDescription[supList.size()] );
        Arrays.sort( supOcds );
        return supOcds;
    }

}
