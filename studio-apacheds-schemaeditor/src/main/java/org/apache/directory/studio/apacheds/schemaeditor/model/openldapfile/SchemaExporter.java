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
package org.apache.directory.studio.apacheds.schemaeditor.model.openldapfile;


import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;


/**
 * This class is used to export a Schema file into the OpenLDAP Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaExporter
{
    /**
     * Converts the given schema to its source code representation
     * in OpenLDAP Schema file format.
     *
     * @param schema
     *      the schema to convert
     * @return
     *      the corresponding source code representation
     */
    public static String toSourceCode( Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        for ( AttributeTypeImpl at : schema.getAttributeTypes() )
        {
            sb.append( toSourceCode( at ) );
            sb.append( "\n" );
        }

        for ( ObjectClassImpl oc : schema.getObjectClasses() )
        {
            sb.append( toSourceCode( oc ) );
            sb.append( "\n" );
        }

        return sb.toString();
    }


    /**
     * Converts the given attribute type to its source code representation
     * in OpenLDAP Schema file format.
     *
     * @param at
     *      the attribute type to convert
     * @return
     *      the corresponding source code representation
     */
    public static String toSourceCode( AttributeTypeImpl at )
    {
        StringBuffer sb = new StringBuffer();

        // Opening the definition and OID
        sb.append( "attributetype ( " + at.getOid() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$

        // NAME(S)
        String[] names = at.getNames();
        sb.append( "\tNAME " ); //$NON-NLS-1$
        if ( names.length > 1 )
        {
            sb.append( "( " ); //$NON-NLS-1$
            for ( String name : names )
            {
                sb.append( "'" + name + "' " ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            sb.append( ") \n" ); //$NON-NLS-1$
        }
        else
        {
            sb.append( "'" + names[0] + "' \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // DESC
        if ( ( at.getDescription() != null ) && ( !at.getDescription().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tDESC '" + at.getDescription() + "' \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // OBSOLETE
        if ( at.isObsolete() )
        {
            sb.append( "\tOBSOLETE \n" ); //$NON-NLS-1$
        }

        // SUP
        if ( ( at.getSuperiorName() != null ) && ( !at.getSuperiorName().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tSUP " + at.getSuperiorName() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // EQUALITY
        if ( ( at.getEqualityName() != null ) && ( !at.getEqualityName().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tEQUALITY " + at.getEqualityName() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // ORDERING
        if ( ( at.getOrderingName() != null ) && ( !at.getOrderingName().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tORDERING " + at.getOrderingName() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // SUBSTR
        if ( ( at.getSubstrName() != null ) && ( !at.getSubstrName().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tSUBSTR " + at.getSubstrName() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // SYNTAX
        if ( ( at.getSyntaxOid() != null ) && ( !at.getSyntaxOid().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tSYNTAX " + at.getSyntaxOid() ); //$NON-NLS-1$
            if ( at.getLength() > 0 )
            {
                sb.append( "{" + at.getLength() + "}" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            sb.append( " \n" ); //$NON-NLS-1$
        }

        // SINGLE-VALUE
        if ( at.isSingleValue() )
        {
            sb.append( "\tSINGLE-VALUE \n" ); //$NON-NLS-1$
        }

        // COLLECTIVE
        if ( at.isCollective() )
        {
            sb.append( "\tCOLLECTIVE \n" ); //$NON-NLS-1$
        }

        // NO-USER-MODIFICATION
        if ( !at.isCanUserModify() )
        {
            sb.append( "\tNO-USER-MODIFICATION \n" ); //$NON-NLS-1$
        }

        // USAGE
        UsageEnum usage = at.getUsage();
        if ( usage != null )
        {
            if ( usage == UsageEnum.DIRECTORY_OPERATION )
            {
                sb.append( "\tUSAGE directoryOperation \n" ); //$NON-NLS-1$
            }
            else if ( usage == UsageEnum.DISTRIBUTED_OPERATION )
            {
                sb.append( "\tUSAGE distributedOperation \n" ); //$NON-NLS-1$
            }
            else if ( usage == UsageEnum.DSA_OPERATION )
            {
                sb.append( "\tUSAGE dSAOperation \n" ); //$NON-NLS-1$
            }
            else if ( usage == UsageEnum.USER_APPLICATIONS )
            {
                // There's nothing to write, this is the default option
            }
        }

        // Closing the definition
        sb.append( " )\n" ); //$NON-NLS-1$

        return sb.toString();
    }


    /**
     * Converts the given object class to its source code representation
     * in OpenLDAP Schema file format.
     *
     * @param at
     *      the object class to convert
     * @return
     *      the corresponding source code representation
     */
    public static String toSourceCode( ObjectClassImpl oc )
    {
        StringBuffer sb = new StringBuffer();

        // Opening the definition and OID
        sb.append( "objectclass ( " + oc.getOid() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$

        // NAME(S)
        String[] names = oc.getNames();
        sb.append( "\tNAME " ); //$NON-NLS-1$
        if ( names.length > 1 )
        {
            sb.append( "( " ); //$NON-NLS-1$
            for ( String name : names )
            {
                sb.append( "'" + name + "' " ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            sb.append( ") \n" ); //$NON-NLS-1$
        }
        else
        {
            sb.append( "'" + names[0] + "' \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // DESC
        if ( ( oc.getDescription() != null ) && ( !oc.getDescription().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tDESC '" + oc.getDescription() + "' \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // OBSOLETE
        if ( oc.isObsolete() )
        {
            sb.append( "\tOBSOLETE \n" ); //$NON-NLS-1$
        }

        // SUP
        String[] superiors = oc.getSuperClassesNames();
        if ( ( superiors != null ) && ( superiors.length != 0 ) )
        {
            if ( superiors.length > 1 )
            {
                sb.append( "\tSUP (" + superiors[0] ); //$NON-NLS-1$
                for ( int i = 1; i < superiors.length; i++ )
                {
                    sb.append( " $ " + superiors[i] ); //$NON-NLS-1$
                }
                sb.append( ") \n" ); //$NON-NLS-1$
            }
            else
            {
                sb.append( "\tSUP " + superiors[0] + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        // CLASSTYPE
        ObjectClassTypeEnum classtype = oc.getType();
        if ( classtype == ObjectClassTypeEnum.ABSTRACT )
        {
            sb.append( "\tABSTRACT \n" ); //$NON-NLS-1$
        }
        else if ( classtype == ObjectClassTypeEnum.AUXILIARY )
        {
            sb.append( "\tAUXILIARY \n" ); //$NON-NLS-1$
        }
        else if ( classtype == ObjectClassTypeEnum.STRUCTURAL )
        {
            sb.append( "\tSTRUCTURAL \n" ); //$NON-NLS-1$
        }

        // MUST
        String[] must = oc.getMustNamesList();
        if ( ( must != null ) && ( must.length != 0 ) )
        {
            sb.append( "\tMUST " ); //$NON-NLS-1$
            if ( must.length > 1 )
            {
                sb.append( "( " + must[0] + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                for ( int i = 1; i < must.length; i++ )
                {
                    sb.append( "$ " + must[i] + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                sb.append( ") \n" ); //$NON-NLS-1$
            }
            else if ( must.length == 1 )
            {
                sb.append( must[0] + " \n" ); //$NON-NLS-1$
            }
        }

        // MAY
        String[] may = oc.getMayNamesList();
        if ( ( may != null ) && ( may.length != 0 ) )
        {
            sb.append( "\tMAY " ); //$NON-NLS-1$
            if ( may.length > 1 )
            {
                sb.append( "( " + may[0] + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                for ( int i = 1; i < may.length; i++ )
                {
                    sb.append( "$ " + may[i] + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                sb.append( ") \n" ); //$NON-NLS-1$
            }
            else if ( may.length == 1 )
            {
                sb.append( may[0] + " \n" ); //$NON-NLS-1$
            }
        }
        // Closing the definition
        sb.append( " )\n" ); //$NON-NLS-1$

        return sb.toString();
    }
}
