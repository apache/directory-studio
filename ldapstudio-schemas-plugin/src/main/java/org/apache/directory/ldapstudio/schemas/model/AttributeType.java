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

package org.apache.directory.ldapstudio.schemas.model;


import java.util.ArrayList;

import org.apache.directory.ldapstudio.schemas.controller.SchemasViewController;
import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditor;
import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;


/**
 * This class is the model for 'attribute type' LDAP schema
 * elements. It is modeled after the RFC 2252 recommandation but
 * not all of the properties are implemented in the ApacheDS 
 * Directory Server. 
 *
 */
@SuppressWarnings("unused")//$NON-NLS-1$
public class AttributeType implements SchemaElement
{
    private static Logger logger = Logger.getLogger( AttributeType.class );
    private AttributeTypeLiteral literal;
    private Schema originatingSchema;
    private ArrayList<SchemaElementListener> listeners;
    private AttributeTypeFormEditor editor;


    /******************************************
     *             Constructors               *
     ******************************************/

    /**
     * Default constructor
     * @param literal the literal given by the schema parser that represents this element
     * @param originatingSchema the schema file that defines this element
     */
    public AttributeType( AttributeTypeLiteral literal, Schema originatingSchema )
    {
        this.literal = literal;
        this.originatingSchema = originatingSchema;
        listeners = new ArrayList<SchemaElementListener>();
    }


    /******************************************
     *               Accessors                *
     ******************************************/

    /**
     * @return the openldap parser literal associated with this attributeType 
     */
    public AttributeTypeLiteral getLiteral()
    {
        return literal;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.model.SchemaElement#getOriginatingSchema()
     */
    public Schema getOriginatingSchema()
    {
        return originatingSchema;
    }


    /**
     * Returns the editor associated to this attributeType
     * @return the editor
     */
    public AttributeTypeFormEditor getEditor()
    {
        return editor;
    }


    /**
     * Sets the editor associated to this attributeType
     * @param editor the associated editor
     */
    public void setEditor( AttributeTypeFormEditor editor )
    {
        this.editor = editor;
    }


    /******************************************
     *                 Logic                  *
     ******************************************/

    /**
     * Call this method to remove the attributeType<->Editor association
     * @param editor the associated editor
     */
    public void removeEditor( AttributeTypeFormEditor editor )
    {
        if ( this.editor == editor )
            this.editor = null;
    }


    /**
     * Close the editor associated to this attributeType WITHOUT applying the
     * modifications
     */
    public void closeAssociatedEditor()
    {
        if ( editor != null )
        {
            // This is a trick, so you don't get asked a second time to close and save or not the editor that has already been closed.
            editor.setDirty( false );

            editor.close( false );
            removeEditor( editor );
        }
    }


    /**
     * Checks if the attributeType has pending modifications in its editor
     * @return
     */
    public boolean hasPendingModifications()
    {
        if ( this.editor != null )
        {
            return editor.isDirty();
        }

        return false;
    }


    /**
     * Apply the pending modifications to the model (this instance)
     */
    public void applyPendingModifications()
    {
        if ( hasPendingModifications() )
        {
            editor.doSave( new NullProgressMonitor() );
        }
    }


    /******************************************
     *            Wrapper Methods             *
     ******************************************/

    //get
    public String getDescription()
    {
        return literal.getDescription();
    }


    public String getEquality()
    {
        return literal.getEquality();
    }


    public int getLength()
    {
        return literal.getLength();
    }


    public String[] getNames()
    {
        return literal.getNames();
    }


    public String getOid()
    {
        return literal.getOid();
    }


    public String getOrdering()
    {
        return literal.getOrdering();
    }


    public String getSubstr()
    {
        return literal.getSubstr();
    }


    public String getSuperior()
    {
        return literal.getSuperior();
    }


    public String getSyntax()
    {
        return literal.getSyntax();
    }


    public UsageEnum getUsage()
    {
        return literal.getUsage();
    }


    public void setDescription( String description )
    {
        literal.setDescription( description );
        notifyChanged();
    }


    public void setEquality( String equality )
    {
        literal.setEquality( equality );
        notifyChanged();
    }


    public void setLength( int length )
    {
        literal.setLength( length );
        notifyChanged();
    }


    public void setNames( String[] names )
    {
        literal.setNames( names );
        notifyChanged();
    }


    public void setOrdering( String ordering )
    {
        literal.setOrdering( ordering );
        notifyChanged();
    }


    public void setSubstr( String substr )
    {
        literal.setSubstr( substr );
        notifyChanged();
    }


    public void setSuperior( String superior )
    {
        literal.setSuperior( superior );
        notifyChanged();
    }


    public void setSyntax( String syntax )
    {
        literal.setSyntax( syntax );
        notifyChanged();
    }


    public void setUsage( UsageEnum usage )
    {
        literal.setUsage( usage );
        notifyChanged();
    }

    
    public void setOid( String oid )
    {
        literal.setOid( oid );
        notifyChanged();
    }
    
    
    public void setObsolete( boolean bool )
    {
        literal.setObsolete( bool );
        notifyChanged();
    }


    public void setSingleValue( boolean bool )
    {
        literal.setSingleValue( bool );
        notifyChanged();
    }


    public void setCollective( boolean bool )
    {
        literal.setCollective( bool );
        notifyChanged();
    }


    public void setNoUserModification( boolean bool )
    {
        literal.setNoUserModification( bool );
        notifyChanged();
    }


    public boolean isObsolete()
    {
        return literal.isObsolete();
    }


    public boolean isCollective()
    {
        return literal.isCollective();
    }


    public boolean isNoUserModification()
    {
        return literal.isNoUserModification();
    }


    public boolean isSingleValue()
    {
        return literal.isSingleValue();
    }


    /******************************************
     *                  I/O                   *
     ******************************************/

    public String write()
    {
        StringBuffer sb = new StringBuffer();

        // Open the definition and OID
        sb.append( "attributetype ( " + literal.getOid() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$

        // NAME(S)
        String[] names = literal.getNames();
        sb.append( "\tNAME " ); //$NON-NLS-1$
        if ( names.length > 1 )
        {
            sb.append( "( " ); //$NON-NLS-1$
            for ( int i = 0; i < names.length; i++ )
            {
                sb.append( "'" + names[i] + "' " ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            sb.append( ") \n" ); //$NON-NLS-1$
        }
        else
        {
            sb.append( "'" + names[0] + "' \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // DESC
        if ( ( literal.getDescription() != null ) && ( literal.getDescription().length() != 0 ) )
        {
            sb.append( "\tDESC '" + literal.getDescription() + "' \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // OBSOLETE
        if ( literal.isObsolete() )
        {
            sb.append( "\tOBSOLETE \n" ); //$NON-NLS-1$
        }

        // SUP
        if ( ( literal.getSuperior() != null ) && ( literal.getSuperior().length() != 0 ) )
        {
            sb.append( "\tSUP " + literal.getSuperior() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // EQUALITY
        if ( ( literal.getEquality() != null ) && ( literal.getEquality().length() != 0 ) )
        {
            sb.append( "\tEQUALITY " + literal.getEquality() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // ORDERING
        if ( ( literal.getOrdering() != null ) && ( literal.getOrdering().length() != 0 ) )
        {
            sb.append( "\tORDERING " + literal.getOrdering() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // SUBSTR
        if ( ( literal.getSubstr() != null ) && ( literal.getSubstr().length() != 0 ) )
        {
            sb.append( "\tSUBSTR " + literal.getSubstr() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // SYNTAX
        if ( ( literal.getSyntax() != null ) && ( literal.getSyntax().length() != 0 ) )
        {
            sb.append( "\tSYNTAX " + literal.getSyntax() ); //$NON-NLS-1$
            if ( literal.getLength() > 0 )
            {
                sb.append( "{" + literal.getLength() + "}" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            sb.append( " \n" ); //$NON-NLS-1$
        }

        // SINGLE-VALUE
        if ( literal.isSingleValue() )
        {
            sb.append( "\tSINGLE-VALUE \n" ); //$NON-NLS-1$
        }

        // COLLECTIVE
        if ( literal.isCollective() )
        {
            sb.append( "\tCOLLECTIVE \n" ); //$NON-NLS-1$
        }

        // NO-USER-MODIFICATION
        if ( literal.isNoUserModification() )
        {
            sb.append( "\tNO-USER-MODIFICATION \n" ); //$NON-NLS-1$
        }

        // USAGE
        UsageEnum usage = literal.getUsage();
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

        // Close the definition
        sb.append( " )\n" ); //$NON-NLS-1$

        return sb.toString();
    }


    /******************************************
     *           Object Redefinition          *
     ******************************************/

    public String toString()
    {
        return getNames()[0];
    }


    /******************************************
     *            Events emmiting             *
     ******************************************/

    public void addListener( SchemaElementListener listener )
    {
        if ( !listeners.contains( listener ) )
            listeners.add( listener );
    }


    public void removeListener( SchemaElementListener listener )
    {
        listeners.remove( listener );
    }


    private void notifyChanged()
    {
        for ( SchemaElementListener listener : listeners )
        {
            try
            {
                listener.schemaElementChanged( this, new LDAPModelEvent( LDAPModelEvent.Reason.ATModified, this ) );
            }
            catch ( Exception e )
            {
                logger.debug( "error when notifying listener: " + listener ); //$NON-NLS-1$
            }
        }
    }
}
