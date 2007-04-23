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

package org.apache.directory.ldapstudio.schemas.controller.actions;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.view.ViewUtils;
import org.apache.directory.ldapstudio.schemas.view.views.SchemasView;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.SchemaWrapper;
import org.apache.directory.shared.converter.schema.AttributeTypeHolder;
import org.apache.directory.shared.converter.schema.ObjectClassHolder;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for Exporting a schema For ADS.
 */
public class ExportSchemaForADSAction extends Action
{
    private static Logger logger = Logger.getLogger( ExportSchemaForADSAction.class );

    /** The associated view */
    private SchemasView view;


    /**
     * Creates a new instance of ExportSchemaForADSAction.
     *
     * @param view
     *      the associated view
     */
    public ExportSchemaForADSAction( SchemasView view )
    {
        super( "Export For Apache DS..." );
        this.view = view;
        setToolTipText( getText() );
        setId( PluginConstants.CMD_EXPORT_FOR_ADS );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_EXPORT_SCHEMA_FOR_ADS ) );
        setEnabled( true );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        Object selection = ( ( TreeSelection ) view.getViewer().getSelection() ).getFirstElement();

        if ( selection != null )
        {
            if ( selection instanceof SchemaWrapper )
            {
                Schema schema = ( ( SchemaWrapper ) selection ).getMySchema();

                // Opening the FileDialog to let the user choose the destination file
                FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    SWT.SAVE );
                fd.setText( "Select a file" );
                fd.setFilterPath( Activator.getDefault().getPreferenceStore().getString(
                    PluginConstants.PREFS_SAVE_FILE_DIALOG ) );
                fd.setFileName( schema.getName() + ".ldif" ); //$NON-NLS-1$
                fd.setFilterExtensions( new String[]
                    { "*.ldif", "*.*" } ); //$NON-NLS-1$ //$NON-NLS-2$
                fd.setFilterNames( new String[]
                    { "LDIF files", "All_files" } );
                String savePath = fd.open();
                if ( savePath != null )
                {
                    File selectedFile = new File( savePath );
                    if ( selectedFile.exists() )
                    {
                        int response = ViewUtils.displayQuestionMessageBox( SWT.OK | SWT.CANCEL,
                            "Overwrite existing file ?",
                            "The file you have choosen already exists. Do you want to overwrite it?" );
                        if ( response == SWT.CANCEL )
                        {
                            return;
                        }
                    }

                    StringBuffer sb = new StringBuffer();
                    sb.append( "# " + schema.getName() + "\n" );
                    DateFormat format = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.MEDIUM );
                    Date date = new Date();
                    sb.append( "# Generated by LDAP Studio on " + format.format( date ) + "\n" );
                    sb.append( "\n" );

                    // Generation the Schema Node
                    sb.append( "dn: cn=" + schema.getName() + ", ou=schema\n" );
                    sb.append( "objectclass: metaSchema\n" );
                    sb.append( "objectclass: top\n" );
                    sb.append( "cn: " + schema.getName() + "\n" );
                    sb.append( "\n" );

                    try
                    {
                        // Generation the Attribute Types Node
                        sb.append( "dn: ou=attributeTypes, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: attributetypes\n" );
                        sb.append( "\n" );

                        // Generating LDIF for Attributes Types
                        for ( AttributeType at : schema.getAttributeTypesAsArray() )
                        {
                            AttributeTypeHolder holder = new AttributeTypeHolder( at.getOid() );
                            holder.setCollective( at.isCollective() );
                            holder.setDescription( at.getDescription() );
                            holder.setEquality( at.getEquality() );
                            List<String> names = new ArrayList<String>();
                            for ( String name : at.getNames() )
                            {
                                names.add( name );
                            }
                            holder.setNames( names );
                            holder.setNoUserModification( at.isNoUserModification() );
                            holder.setObsolete( at.isObsolete() );
                            holder.setOrdering( at.getOrdering() );
                            holder.setSingleValue( at.isSingleValue() );
                            holder.setSubstr( at.getSubstr() );
                            holder.setSuperior( at.getSuperior() );
                            holder.setSyntax( at.getSyntax() );
                            holder.setOidLen( at.getLength() );
                            holder.setUsage( at.getUsage() );

                            sb.append( holder.toLdif( schema.getName() ) + "\n" );
                        }

                        // Generation the Comparators Node
                        sb.append( "dn: ou=comparators, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: comparators\n" );
                        sb.append( "\n" );

                        // Generation the DIT Content Rules Node
                        sb.append( "dn: ou=ditContentRules, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: ditcontentrules\n" );
                        sb.append( "\n" );

                        // Generation the DIT Structure RulesNode
                        sb.append( "dn: ou=ditStructureRules, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: ditstructurerules\n" );
                        sb.append( "\n" );

                        // Generation the Matching Rules Node
                        sb.append( "dn: ou=matchingRules, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: matchingrules\n" );
                        sb.append( "\n" );

                        // Generation the Matching Rule Use Node
                        sb.append( "dn: ou=matchingRuleUse, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: matchingruleuse\n" );
                        sb.append( "\n" );

                        // Generation the Name Forms Node
                        sb.append( "dn: ou=nameForms, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: nameforms\n" );
                        sb.append( "\n" );

                        // Generation the Normalizers Node
                        sb.append( "dn: ou=normalizers, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: normalizers\n" );
                        sb.append( "\n" );

                        // Generation the Object Classes Node
                        sb.append( "dn: ou=objectClasses, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: objectClasses\n" );
                        sb.append( "\n" );

                        // Generating LDIF for Object Classes
                        for ( ObjectClass oc : schema.getObjectClassesAsArray() )
                        {
                            ObjectClassHolder holder = new ObjectClassHolder( oc.getOid() );
                            holder.setClassType( oc.getClassType() );
                            holder.setDescription( oc.getDescription() );
                            List<String> mayList = new ArrayList<String>();
                            for ( String may : oc.getMay() )
                            {
                                mayList.add( may );
                            }
                            holder.setMay( mayList );
                            List<String> mustList = new ArrayList<String>();
                            for ( String must : oc.getMust() )
                            {
                                mustList.add( must );
                            }
                            holder.setMust( mustList );
                            List<String> names = new ArrayList<String>();
                            for ( String name : oc.getNames() )
                            {
                                names.add( name );
                            }
                            holder.setNames( names );
                            List<String> superiorList = new ArrayList<String>();
                            for ( String superior : oc.getSuperiors() )
                            {
                                superiorList.add( superior );
                            }
                            holder.setSuperiors( superiorList );
                            holder.setObsolete( oc.isObsolete() );

                            sb.append( holder.toLdif( schema.getName() ) + "\n" );
                        }

                        // Generation the Syntax Checkers Node
                        sb.append( "dn: ou=syntaxCheckers, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: syntaxcheckers\n" );
                        sb.append( "\n" );

                        // Generation the Syntaxes Node
                        sb.append( "dn: ou=syntaxes, cn=" + schema.getName() + ", ou=schema\n" );
                        sb.append( "objectclass: organizationalUnit\n" );
                        sb.append( "objectclass: top\n" );
                        sb.append( "ou: syntaxes\n" );
                        sb.append( "\n" );
                    }
                    catch ( NamingException e )
                    {
                        logger.error( "An error occurred when generating the LDIF associated to the schema.", e );
                        ViewUtils
                            .displayErrorMessageBox( "Export Failed!",
                                "The file couldn't be saved. An error occurred when generating the LDIF associated to the schema." );
                        return;
                    }

                    // Writing generated LDIF to the specified file
                    BufferedWriter bufferedWriter;
                    try
                    {
                        bufferedWriter = new BufferedWriter( new FileWriter( savePath ) );
                        bufferedWriter.write( sb.toString() );
                        bufferedWriter.close();
                    }
                    catch ( IOException e )
                    {
                        logger.error( "The file couldn't be saved. An error occurred when writing file to disk.", e );
                        ViewUtils.displayErrorMessageBox( "Export Failed!",
                            "The file couldn't be saved. An error occurred when writing file to disk." );
                        return;
                    }

                    // Export Successful
                    ViewUtils.displayInformationMessageBox( "Export Successful",
                        "The schema has been sucessfully exported." );

                    Activator.getDefault().getPreferenceStore().putValue( PluginConstants.PREFS_SAVE_FILE_DIALOG,
                        selectedFile.getParent() );
                }
            }
        }
    }
}
