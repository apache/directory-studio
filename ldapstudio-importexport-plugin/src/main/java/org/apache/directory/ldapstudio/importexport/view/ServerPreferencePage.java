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

package org.apache.directory.ldapstudio.importexport.view;

import org.apache.directory.ldapstudio.importexport.Activator;
import org.apache.directory.ldapstudio.importexport.Messages;
import org.apache.directory.ldapstudio.importexport.Plugin;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the preference page for LDAP Server configuration
 * Host, Base DN, Port, User DN and Password are stored into preferences
 */
public class ServerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
    // The logger
    private static Logger logger = LoggerFactory.getLogger( ServerPreferencePage.class );
    
    public static final String HOST = "ldapstudio.apacheds_tools.prefs_host"; //$NON-NLS-1$
    public static final String HOST_DEFAULT = "localhost"; //$NON-NLS-1$
    public static final String PORT = "ldapstudio.apacheds_tools.prefs_port"; //$NON-NLS-1$
    public static final String PORT_DEFAULT = "10389"; //$NON-NLS-1$
    public static final String USER_DN = "ldapstudio.apacheds_tools.prefs_user_dn"; //$NON-NLS-1$
    public static final String USER_DN_DEFAULT = "uid=admin,ou=system"; //$NON-NLS-1$
    public static final String PASSWORD = "ldapstudio.apacheds_tools.prefs_password"; //$NON-NLS-1$
    public static final String PASSWORD_DEFAULT = "secret";  //$NON-NLS-1$
    public static final String BASE_BN = "ldapstudio.apacheds_tools.prefs_base_dn"; //$NON-NLS-1$
    public static final String BASE_BN_DEFAULT = "ou=system";   //$NON-NLS-1$
    
    private StringFieldEditor host_field;
    private StringFieldEditor port_field;
    private StringFieldEditor userDN_field;
    private StringFieldEditor password_field;
    private StringFieldEditor baseDN_field;
    
    public ServerPreferencePage()
    {
        super(GRID);
        setPreferenceStore( new ScopedPreferenceStore( new ConfigurationScope(), Plugin.ID) );
    }

    @Override
    protected void createFieldEditors()
    {
        host_field = new StringFieldEditor(
            HOST,
            Messages.getString("ServerPreferencePage.Host"), //$NON-NLS-1$
            getFieldEditorParent());
        addField(host_field);
        
        port_field = new StringFieldEditor(
                PORT,
                Messages.getString("ServerPreferencePage.Port"), //$NON-NLS-1$
                getFieldEditorParent());
        addField(port_field);
        
        userDN_field = new StringFieldEditor(
                USER_DN,
                Messages.getString("ServerPreferencePage.User_DN"), //$NON-NLS-1$
                getFieldEditorParent());
        addField(userDN_field);
        
        password_field = new StringFieldEditor(
                PASSWORD,
                Messages.getString("ServerPreferencePage.Password"), //$NON-NLS-1$
                getFieldEditorParent());
        addField(password_field);
    
        baseDN_field = new StringFieldEditor(
                BASE_BN,
                Messages.getString("ServerPreferencePage.Base_DN"), //$NON-NLS-1$
                getFieldEditorParent());
        addField(baseDN_field);
    }

    public void init( IWorkbench workbench )
    {
    }
    
    @Override
    public boolean performOk()
    {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        
        // We store values into preferences
        store.setValue( HOST, host_field.getStringValue() );
        store.setValue( PORT, port_field.getStringValue() );
        store.setValue( USER_DN, userDN_field.getStringValue() );
        store.setValue( PASSWORD, password_field.getStringValue() );
        store.setValue( BASE_BN, baseDN_field.getStringValue() );
        
        logger.info( "Saving preferences" ); //$NON-NLS-1$
        logger.info( "Host: " + host_field.getStringValue() ); //$NON-NLS-1$
        logger.info( "Port: " + port_field.getStringValue() ); //$NON-NLS-1$
        logger.info( "User DN: " + userDN_field.getStringValue() ); //$NON-NLS-1$
        logger.info( "Base DN: " + baseDN_field.getStringValue() ); //$NON-NLS-1$
                
        return super.performOk();
    }
}
