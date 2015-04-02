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
package org.apache.directory.studio.openldap.config.acl.widgets;


import java.text.ParseException;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclItem;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClause;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseStar;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClause;
import org.apache.directory.studio.openldap.config.acl.model.AclWhoClauseStar;


/**
 * This composite contains the tabs with visual and source editor.
 * It also manages the synchronization between these two tabs.
 * 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclTabFolderComposite extends Composite
{
    /** The index of the visual tab */
    public static final int VISUAL_TAB_INDEX = 0;

    /** The index of the source tab */
    public static final int SOURCE_TAB_INDEX = 1;

    /** The tab folder */
    private TabFolder tabFolder;

    /** The visual tab */
    private TabItem visualTab;

    /** The inner container of the visual tab */
    private Composite visualContainer;

    /** The visual editor composite */
    private OpenLdapAclVisualEditorComposite visualComposite;

    /** Tehe source tab */
    private TabItem sourceTab;

    /** The inner container of the visual tab */
    private Composite sourceContainer;

    /** The source editor composite */
    private OpenLdapAclSourceEditorComposite sourceComposite;


    /**
     * Creates a new instance of TabFolderComposite.
     *
     * @param parent
     * @param style
     */
    public OpenLdapAclTabFolderComposite( Composite parent, int style )
    {
        super( parent, style );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        setLayout( layout );

        createTabFolder();

        createVisualTab();

        createSourceTab();

        initListeners();
    }


    /**
     * Initializes the listeners.
     *
     */
    private void initListeners()
    {
        tabFolder.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                tabSelected();
            }
        } );
    }


    /**
     * Creates the source tab and configures the source editor.
     *
     */
    private void createSourceTab()
    {
        // create inner container
        sourceContainer = new Composite( tabFolder, SWT.BORDER );
        sourceContainer.setLayout( new FillLayout() );

        // create source editor
        sourceComposite = new OpenLdapAclSourceEditorComposite( sourceContainer, SWT.NONE );

        // create tab
        sourceTab = new TabItem( tabFolder, SWT.NONE, SOURCE_TAB_INDEX );
        sourceTab.setText( "Source" );
        sourceTab.setControl( sourceContainer );
    }


    /**
     * Creates the visual tab and the GUI editor.
     *
     */
    private void createVisualTab()
    {
        // create inner container
        visualContainer = new Composite( tabFolder, SWT.NONE );
        visualContainer.setLayout( new GridLayout() );
        visualContainer.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // create the visual ACIItem composite
        visualComposite = new OpenLdapAclVisualEditorComposite( visualContainer, SWT.NONE );
        visualComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // create tab
        visualTab = new TabItem( tabFolder, SWT.NONE, VISUAL_TAB_INDEX );
        visualTab.setText( "Visual Editor" );
        visualTab.setControl( visualContainer );
    }


    /**
     * Creates the tab folder and the listeners.
     *
     */
    private void createTabFolder()
    {
        tabFolder = new TabFolder( this, SWT.TOP );
        tabFolder.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    }


    /** 
     * Called, when a tab is selected. This method manages the synchronization
     * between visual and source editor.
     */
    private void tabSelected()
    {
        int index = tabFolder.getSelectionIndex();

        if ( index == SOURCE_TAB_INDEX )
        {
            // switched to source tab: serialize visual and set to source
            // on parse error: print message and return to visual tab
            //            try
            //            {
            //                String input = visualComposite.getInput();
            //                sourceComposite.setInput( input );
            //            }
            //            catch ( ParseException pe )
            //            {
            //                IStatus status = new Status( IStatus.ERROR, ACIITemConstants.PLUGIN_ID, 1, Messages
            //                    .getString( "ACIItemTabFolderComposite.error.onVisualEditor" ), pe ); //$NON-NLS-1$
            //                ErrorDialog.openError( getShell(),
            //                    Messages.getString( "ACIItemTabFolderComposite.error.title" ), null, status ); //$NON-NLS-1$
            //                tabFolder.setSelection( VISUAL_TAB_INDEX );
            //            }
        }
        else if ( index == VISUAL_TAB_INDEX )
        {
            // switched to visual tab: parse source and populate to visual
            // on parse error: print message and return to source tab
            //            try
            //            {
            //                String input = sourceComposite.getInput();
            //                visualComposite.setInput( input );
            //            }
            //            catch ( ParseException pe )
            //            {
            //                IStatus status = new Status( IStatus.ERROR, ACIITemConstants.PLUGIN_ID, 1, Messages
            //                    .getString( "ACIItemTabFolderComposite.error.onSourceEditor" ), pe ); //$NON-NLS-1$
            //                ErrorDialog.openError( getShell(),
            //                    Messages.getString( "ACIItemTabFolderComposite.error.title" ), null, status ); //$NON-NLS-1$
            //                tabFolder.setSelection( SOURCE_TAB_INDEX );
            //            }
        }
    }


    /**
     * Sets the input to both the source editor and to the visual editor.
     * If the syntax is invalid the source editor is activated. 
     *
     * @param input The string representation of the ACI item
     */
    public void setInput( String input )
    {
        // Checking if the input is null or empty
        if ( ( input == null ) || ( "".equals( input ) ) )
        {
            // Creating a default ACL instead
            AclItem defaultAcl = new AclItem( new AclWhatClause( new AclWhatClauseStar() ),
                Arrays.asList( new AclWhoClause[]
                    { new AclWhoClauseStar() } ) );

            // Assiging the default ACL as input
            input = defaultAcl.toString();
        }

        // Setting the input to the source editor
        sourceComposite.forceSetInput( input );

        // Setting the input to the visual editor, on parse error switch to source editor
        try
        {
            visualComposite.setInput( input );
        }
        catch ( ParseException e )
        {
            //            IStatus status = new Status( IStatus.ERROR, ACIITemConstants.PLUGIN_ID, 1, Messages
            //                .getString( "ACIItemTabFolderComposite.error.onInput" ), pe ); //$NON-NLS-1$
            //            ErrorDialog.openError( getShell(),
            //                Messages.getString( "ACIItemTabFolderComposite.error.title" ), null, status ); //$NON-NLS-1$
            //
            tabFolder.setSelection( SOURCE_TAB_INDEX );
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Returns the string representation of the ACI item.
     * A syntax check is performed before returning the input, an 
     * invalid syntax causes a ParseException.
     *
     * @return the valid string representation of the ACI item
     * @throws ParseException it the syntax check fails.
     */
    public String getInput() throws ParseException
    {
        int index = tabFolder.getSelectionIndex();
        if ( index == VISUAL_TAB_INDEX )
        {
            String input = visualComposite.getInput();
            return input;
        }
        else
        {
            String input = sourceComposite.getInput();
            return input;
        }
    }


    /**
     * Sets the context.
     * 
     * @param context the context
     */
    public void setContext( OpenLdapAclValueWithContext context )
    {
        sourceComposite.setContext( context );
        visualComposite.setContext( context );
    }


    /**
     * Formats the content.
     */
    public void format()
    {
        if ( tabFolder.getSelectionIndex() == SOURCE_TAB_INDEX )
        {
            sourceComposite.format();
        }
    }


    /**
     * Saves widget settings.
     */
    public void saveWidgetSettings()
    {
        visualComposite.saveWidgetSettings();
    }
}
