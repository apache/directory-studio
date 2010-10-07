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
package org.apache.directory.studio.schemaeditor.model.io;


import java.util.List;

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * This class is used to export a Schema file into the OpenLDAP Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapSchemaFileExporter
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
            sb.append( "\n" ); //$NON-NLS-1$
        }

        for ( ObjectClassImpl oc : schema.getObjectClasses() )
        {
            sb.append( toSourceCode( oc ) );
            sb.append( "\n" ); //$NON-NLS-1$
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
        List<String> names = at.getNames();
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            sb.append( "\tNAME " ); //$NON-NLS-1$
            if ( names.size() > 1 )
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
                sb.append( "'" + names.get( 0 ) + "' \n" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
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
        if ( ( at.getSubstringOid() != null ) && ( !at.getSubstringOid().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tSUBSTR " + at.getSubstringOid() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // SYNTAX
        if ( ( at.getSyntaxOid() != null ) && ( !at.getSyntaxOid().equals( "" ) ) ) //$NON-NLS-1$
        {
            sb.append( "\tSYNTAX " + at.getSyntaxOid() ); //$NON-NLS-1$
            if ( at.getSyntaxLength() > 0 )
            {
                sb.append( "{" + at.getSyntaxLength() + "}" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            sb.append( " \n" ); //$NON-NLS-1$
        }

        // SINGLE-VALUE
        if ( at.isSingleValued() )
        {
            sb.append( "\tSINGLE-VALUE \n" ); //$NON-NLS-1$
        }

        // COLLECTIVE
        if ( at.isCollective() )
        {
            sb.append( "\tCOLLECTIVE \n" ); //$NON-NLS-1$
        }

        // NO-USER-MODIFICATION
        if ( !at.isUserModifiable() )
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
        List<String> names = oc.getNames();
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            sb.append( "\tNAME " ); //$NON-NLS-1$
            if ( names.size() > 1 )
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
                sb.append( "'" + names.get( 0 ) + "' \n" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
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
        List<String> superiors = oc.getSuperiorOids();
        if ( ( superiors != null ) && ( superiors.size() != 0 ) )
        {
            if ( superiors.size() > 1 )
            {
                sb.append( "\tSUP (" + superiors.get( 0 ) ); //$NON-NLS-1$
                for ( int i = 1; i < superiors.size(); i++ )
                {
                    sb.append( " $ " + superiors.get( i ) ); //$NON-NLS-1$
                }
                sb.append( ") \n" ); //$NON-NLS-1$
            }
            else
            {
                sb.append( "\tSUP " + superiors.get( 0 ) + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
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
        List<String> must = oc.getMustAttributeTypeOids();
        if ( ( must != null ) && ( must.size() != 0 ) )
        {
            sb.append( "\tMUST " ); //$NON-NLS-1$
            if ( must.size() > 1 )
            {
                sb.append( "( " + must.get( 0 ) + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                for ( int i = 1; i < must.size(); i++ )
                {
                    sb.append( "$ " + must.get( i ) + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                sb.append( ") \n" ); //$NON-NLS-1$
            }
            else if ( must.size() == 1 )
            {
                sb.append( must.get( 0 ) + " \n" ); //$NON-NLS-1$
            }
        }

        // MAY
        List<String> may = oc.getMayAttributeTypeOids();
        if ( ( may != null ) && ( may.size() != 0 ) )
        {
            sb.append( "\tMAY " ); //$NON-NLS-1$
            if ( may.size() > 1 )
            {
                sb.append( "( " + may.get( 0 ) + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                for ( int i = 1; i < may.size(); i++ )
                {
                    sb.append( "$ " + may.get( i ) + " " ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                sb.append( ") \n" ); //$NON-NLS-1$
            }
            else if ( may.size() == 1 )
            {
                sb.append( may.get( 0 ) + " \n" ); //$NON-NLS-1$
            }
        }
        // Closing the definition
        sb.append( " )\n" ); //$NON-NLS-1$

        return sb.toString();
    }
}
