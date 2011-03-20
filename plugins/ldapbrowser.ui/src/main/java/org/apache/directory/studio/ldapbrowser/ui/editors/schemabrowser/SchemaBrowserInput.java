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
package org.apache.directory.studio.ldapbrowser.ui.editors.schemabrowser;


import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;


/**
 * The input for the schema browser.
 * 
 * There is a trick to provide a single instance of the schema browser:
 * <ul>
 * <li>If oneInstanceHackEnabled is true the equals method returns always 
 *     true as long as the compared object is also of type SchemaBrowserInput. 
 *     With this trick only one instance of the schema browser is opened
 *     by the eclipse editor framework.
 * <li>If oneInstanceHackEnabled is false the equals method returns 
 *     true only if the wrapped objects (IConnection and SchemaPart) are equal. 
 *     This is necessary for the history navigation because it must be able 
 *     to distinguish the different input objects.
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaBrowserInput implements IEditorInput
{

    /** The connection */
    private IBrowserConnection connection;

    /** The schema element */
    private SchemaObject schemaElement;

    /** One instance hack flag */
    private static boolean oneInstanceHackEnabled = true;


    /**
     * Creates a new instance of SchemaBrowserInput.
     *
     * @param connection the connection
     * @param schemaElement the schema element input
     */
    public SchemaBrowserInput( IBrowserConnection connection, SchemaObject schemaElement )
    {
        this.connection = connection;
        this.schemaElement = schemaElement;
    }


    //    /**
    //     * Creates a new instance of SchemaBrowserInput.
    //     *
    //     *@param connection the connection
    //     * @param schemaElement the schema element input
    //     */
    //    public SchemaBrowserInput( Connection connection, SchemaPart schemaElement )
    //    {
    //        this.connection = BrowserCorePlugin.getDefault().getConnectionManager().getConnection( connection );
    //        this.schemaElement = schemaElement;
    //    }

    /**
     * This implementation always return false because
     * a schema element should not be visible in the 
     * "File Most Recently Used" menu.
     * 
     * {@inheritDoc}
     */
    public boolean exists()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_BROWSER_SCHEMABROWSEREDITOR );
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return Messages.getString( "SchemaBrowserInput.SchemaBrowser" ); //$NON-NLS-1$
    }


    /**
     * This implementation always return null.
     * 
     * {@inheritDoc}
     */
    public IPersistableElement getPersistable()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getToolTipText()
    {
        return ""; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( @SuppressWarnings("rawtypes") Class adapter )
    {
        return null;
    }


    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public IBrowserConnection getConnection()
    {
        return connection;
    }


    /**
    /**
     * Gets the schema element, may be null.
     *
     * @return the schema element or null
     */
    public SchemaObject getSchemaElement()
    {
        return schemaElement;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return getToolTipText().hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {

        boolean equal;

        if ( oneInstanceHackEnabled )
        {
            equal = ( obj instanceof SchemaBrowserInput );
        }
        else
        {
            if ( obj instanceof SchemaBrowserInput )
            {
                SchemaBrowserInput other = ( SchemaBrowserInput ) obj;
                if ( this.connection == null && other.connection == null )
                {
                    return true;
                }
                else if ( this.connection == null || other.connection == null )
                {
                    return false;
                }
                else if ( !this.connection.equals( other.connection ) )
                {
                    return false;
                }
                else if ( this.schemaElement == null && other.schemaElement == null )
                {
                    return true;
                }
                else if ( this.schemaElement == null || other.schemaElement == null )
                {
                    return false;
                }
                else
                {
                    equal = other.schemaElement.equals( this.schemaElement );
                }
            }
            else
            {
                equal = false;
            }
        }

        return equal;
    }


    /**
     * Enables or disabled the one instance hack.
     *
     * @param b 
     *      true to enable the one instance hack,
     *      false to disable the one instance hack
     */
    public static void enableOneInstanceHack( boolean b )
    {
        oneInstanceHackEnabled = b;
    }

}
