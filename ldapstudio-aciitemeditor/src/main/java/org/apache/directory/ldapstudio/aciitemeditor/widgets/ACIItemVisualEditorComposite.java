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
package org.apache.directory.ldapstudio.aciitemeditor.widgets;


import java.text.ParseException;
import java.util.Collection;

import org.apache.directory.ldapstudio.aciitemeditor.ACIItemValueWithContext;
import org.apache.directory.shared.ldap.aci.ACIItem;
import org.apache.directory.shared.ldap.aci.ACIItemParser;
import org.apache.directory.shared.ldap.aci.AuthenticationLevel;
import org.apache.directory.shared.ldap.aci.ItemFirstACIItem;
import org.apache.directory.shared.ldap.aci.UserFirstACIItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


/**
 * This is the main widget of the ACI item visual editor. It manages
 * the lifecyle of all other ACI item widgets. In particular it
 * shows/hides the userFirst and itemFirst widgets depending on
 * the user's selection. 
 * <p>
 * It extends ScrolledComposite.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACIItemVisualEditorComposite extends ScrolledComposite implements WidgetModifyListener
{

    /** The inner composite for all the content */
    private Composite composite = null;

    /** The general composite contains id-tag, precedence, auth-level, userFirst/itemFirst */
    private ACIItemGeneralComposite generalComposite = null;

    /** The user classes composite used for userFirst selection */
    private ACIItemUserClassesComposite userFirstUserClassesComposite = null;

    /** The user permission composite used for userFirst selection */
    private ACIItemUserPermissionsComposite userFirstUserPermissionsComposite = null;

    /** The protected items composite used for itemFirst selection */
    private ACIItemProtectedItemsComposite itemFirstProtectedItemsComposite = null;

    /** The item permission composite used for itemFirst selection */
    private ACIItemItemPermissionsComposite itemFirstItemPermissionsComposite = null;


    /**
     * Creates a new instance of ACIItemComposite.
     *
     * @param parent
     * @param style
     */
    public ACIItemVisualEditorComposite( Composite parent, int style )
    {
        super( parent, style | SWT.H_SCROLL | SWT.V_SCROLL );
        setExpandHorizontal( true );
        setExpandVertical( true );

        createComposite();

        setContent( composite );
        setMinSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    }


    /**
     * This method initializes the inner composite with all contained widgets.
     *
     */
    private void createComposite()
    {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.verticalAlignment = GridData.CENTER;

        composite = new Composite( this, SWT.NONE );
        composite.setLayout( gridLayout );
        composite.setLayoutData( gridData );

        generalComposite = new ACIItemGeneralComposite( composite, SWT.NONE );
        generalComposite.addWidgetModifyListener( this );

        userFirstUserClassesComposite = new ACIItemUserClassesComposite( composite, SWT.NONE );
        userFirstUserPermissionsComposite = new ACIItemUserPermissionsComposite( composite, SWT.NONE );

        itemFirstProtectedItemsComposite = new ACIItemProtectedItemsComposite( composite, SWT.NONE );
        itemFirstItemPermissionsComposite = new ACIItemItemPermissionsComposite( composite, SWT.NONE );

        widgetModified( null );
    }


    /**
     * This method is called from the contained ACIItemXXXComposites
     * when they are modified.
     * 
     * @param event the event
     */
    public void widgetModified( WidgetModifyEvent event )
    {
        // switch userFirst / itemFirst
        if ( generalComposite.isItemFirst() && !generalComposite.isUserFirst()
            && !itemFirstProtectedItemsComposite.isVisible() )
        {
            userFirstUserClassesComposite.setVisible( false );
            userFirstUserPermissionsComposite.setVisible( false );
            itemFirstProtectedItemsComposite.setVisible( true );
            itemFirstItemPermissionsComposite.setVisible( true );

            setMinSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
            layout( true, true );
        }
        else if ( generalComposite.isUserFirst() && !generalComposite.isItemFirst()
            && !userFirstUserClassesComposite.isVisible() )
        {
            userFirstUserClassesComposite.setVisible( true );
            userFirstUserPermissionsComposite.setVisible( true );
            itemFirstProtectedItemsComposite.setVisible( false );
            itemFirstItemPermissionsComposite.setVisible( false );

            setMinSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
            layout( true, true );
        }
        else if ( !generalComposite.isItemFirst() && !generalComposite.isUserFirst() )
        {
            userFirstUserClassesComposite.setVisible( false );
            userFirstUserPermissionsComposite.setVisible( false );
            itemFirstProtectedItemsComposite.setVisible( false );
            itemFirstItemPermissionsComposite.setVisible( false );

            setMinSize( composite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
            layout( true, true );
        }

    }


    /**
     * Sets the input. The given ACI Item string is parsed and
     * populated to the GUI elements.
     * 
     *
     * @param input The string representation of the ACI item
     * @throws ParseException if the syntax is invalid
     */
    public void setInput( String input ) throws ParseException
    {
        ACIItemParser parser = new ACIItemParser( null );
        ACIItem aciItem = parser.parse( input );

        if ( aciItem != null )
        {
            generalComposite.setIdentificationTag( aciItem.getIdentificationTag() );
            generalComposite.setPrecedence( aciItem.getPrecedence() );
            generalComposite.setAuthenticationLevel( aciItem.getAuthenticationLevel() );

            if ( aciItem instanceof ItemFirstACIItem )
            {
                ItemFirstACIItem itemFirstACI = ( ItemFirstACIItem ) aciItem;
                generalComposite.setItemFirst();
                itemFirstProtectedItemsComposite.setProtectedItems( itemFirstACI.getProtectedItems() );
                itemFirstItemPermissionsComposite.setItemPermissions( itemFirstACI.getItemPermissions() );
            }
            else if ( aciItem instanceof UserFirstACIItem )
            {
                UserFirstACIItem userFirstACI = ( UserFirstACIItem ) aciItem;
                generalComposite.setUserFirst();
                userFirstUserClassesComposite.setUserClasses( userFirstACI.getUserClasses() );
                userFirstUserPermissionsComposite.setUserPermissions( userFirstACI.getUserPermission() );
            }
        }

        // force userFirst/itemFirst switch
        widgetModified( null );

    }


    /**
     * Returns the string representation of the ACI item as defined in GUI.
     * 
     *
     * @return the string representation of the ACI item
     * @throws ParseException if the syntax is invalid
     */
    public String getInput() throws ParseException
    {
        String identificationTag = generalComposite.getIdentificationTag();
        int precedence = generalComposite.getPrecedence();
        AuthenticationLevel authenticationLevel = generalComposite.getAuthenticationLevel();

        ACIItem aciItem = null;
        if ( generalComposite.isUserFirst() )
        {
            Collection userClasses = userFirstUserClassesComposite.getUserClasses();
            Collection userPermissions = userFirstUserPermissionsComposite.getUserPermissions();
            aciItem = new UserFirstACIItem( identificationTag, precedence, authenticationLevel, userClasses,
                userPermissions );
        }
        else if ( generalComposite.isItemFirst() )
        {
            Collection protectedItems = itemFirstProtectedItemsComposite.getProtectedItems();
            Collection itemPermissions = itemFirstItemPermissionsComposite.getItemPermissions();
            aciItem = new ItemFirstACIItem( identificationTag, precedence, authenticationLevel, protectedItems,
                itemPermissions );
        }
        else
        {
            aciItem = null;
        }

        StringBuffer buffer = new StringBuffer();
        if ( aciItem != null )
        {
            aciItem.printToBuffer( buffer );
        }
        return buffer.toString();
    }


    /**
     * Sets the context.
     * 
     * @param context the context
     */
    public void setContext( ACIItemValueWithContext context )
    {
        itemFirstProtectedItemsComposite.setContext( context );
        itemFirstItemPermissionsComposite.setContext( context );
        userFirstUserClassesComposite.setContext( context );
        userFirstUserPermissionsComposite.setContext( context );
    }

}
