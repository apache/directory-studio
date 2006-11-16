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

import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditor;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;


/**
 * This class is the model for 'object type' LDAP schema
 * elements it's based on the ASN.1 description.
 *
 */
@SuppressWarnings("unused")//$NON-NLS-1$
public class ObjectClass implements SchemaElement
{
    private static Logger logger = Logger.getLogger( ObjectClass.class );
    private ObjectClassLiteral literal;
    private Schema originatingSchema;
    private ArrayList<SchemaElementListener> listeners;
    private ObjectClassFormEditor editor;


    /******************************************
     *             Constructors               *
     ******************************************/

    /**
     * Default constructor
     * @param literal the literal given by the schema parser that represents this element
     * @param originatingSchema the schema file that defines this element 
     */
    public ObjectClass( ObjectClassLiteral literal, Schema originatingSchema )
    {
        this.literal = literal;
        this.originatingSchema = originatingSchema;
        listeners = new ArrayList<SchemaElementListener>();
    }


    /******************************************
     *               Accessors                *
     ******************************************/

    /**
     * @return the openldap parser literal associated with this objectClass 
     */
    public ObjectClassLiteral getLiteral()
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
     * Returns the editor associated to this objectClass
     * @return the editor
     */
    public ObjectClassFormEditor getEditor()
    {
        return editor;
    }


    /**
     * Sets the editor associated to this objectClass
     * @param editor the associated editor
     */
    public void setEditor( ObjectClassFormEditor editor )
    {
        this.editor = editor;
    }


    /******************************************
     *                 Logic                  *
     ******************************************/

    /**
     * Call this method to remove the objectClass<->Editor association
     * @param editor the associated editor
     */
    public void removeEditor( ObjectClassFormEditor editor )
    {
        if ( this.editor == editor )
            this.editor = null;
    }


    /**
     * Close the editor associated to this objectClass WITHOUT applying the
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
     * Checks if the objectClass has pending modifications in its editor
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
    public String[] getNames()
    {
        return literal.getNames();
    }


    public String getOid()
    {
        return literal.getOid();
    }


    public String getDescription()
    {
        return literal.getDescription();
    }


    public String[] getSuperiors()
    {
        return literal.getSuperiors();
    }


    public ObjectClassTypeEnum getClassType()
    {
        return literal.getClassType();
    }


    public boolean isObsolete()
    {
        return literal.isObsolete();
    }


    public String[] getMay()
    {
        return literal.getMay();
    }


    public String[] getMust()
    {
        return literal.getMust();
    }


    //set

    public void setNames( String[] names )
    {
        literal.setNames( names );
        notifyChanged();
    }


    public void setOid( String oid )
    {
        literal.setOid( oid );
        notifyChanged();
    }


    public void setDescription( String description )
    {
        literal.setDescription( description );
        notifyChanged();
    }


    public void setClassType( ObjectClassTypeEnum type )
    {
        literal.setClassType( type );
        notifyChanged();
    }


    public void setObsolete( boolean bool )
    {
        literal.setObsolete( bool );
        notifyChanged();
    }


    public void setSuperiors( String[] superiors )
    {
        literal.setSuperiors( superiors );
        notifyChanged();
    }


    public void setMay( String[] may )
    {
        literal.setMay( may );
        notifyChanged();
    }


    public void setMust( String[] must )
    {
        literal.setMust( must );
        notifyChanged();
    }


    /******************************************
     *                  I/O                   *
     ******************************************/
    public String write()
    {
        StringBuffer sb = new StringBuffer();

        // Open the definition and OID
        sb.append( "objectclass ( " + literal.getOid() + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$

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
        String[] superiors = literal.getSuperiors();
        if ( superiors.length != 0 )
        {
            sb.append( "\tSUP " + superiors[0] + " \n" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // CLASSTYPE
        ObjectClassTypeEnum classtype = literal.getClassType();
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
        String[] must = literal.getMust();
        if ( must.length != 0 )
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
        String[] may = literal.getMay();
        if ( may.length != 0 )
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
                listener.schemaElementChanged( this, new LDAPModelEvent( LDAPModelEvent.Reason.OCModified, this ) );
            }
            catch ( Exception e )
            {
                logger.debug( "error when notifying " + listener + " of " + this.getNames()[0] + " modification" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
    }
}
