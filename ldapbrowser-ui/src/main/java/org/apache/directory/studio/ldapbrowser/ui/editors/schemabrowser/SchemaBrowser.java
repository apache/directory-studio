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


import org.apache.directory.shared.ldap.schema.syntax.AbstractSchemaDescription;
import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.syntax.LdapSyntaxDescription;
import org.apache.directory.shared.ldap.schema.syntax.MatchingRuleDescription;
import org.apache.directory.shared.ldap.schema.syntax.MatchingRuleUseDescription;
import org.apache.directory.shared.ldap.schema.syntax.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;


/**
 * The schema browser editor part.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaBrowser extends EditorPart implements INavigationLocationProvider, IReusableEditor
{

    /** The tab folder with all the schema element tabs */
    private CTabFolder tabFolder;

    /** The object class tab */
    private CTabItem ocdTab;

    /** The object class page */
    private ObjectClassDescriptionPage ocdPage;

    /** The attribute type tab */
    private CTabItem atdTab;

    /** The attribute type page */
    private AttributeTypeDescriptionPage atdPage;

    /** The matching rule tab */
    private CTabItem mrdTab;

    /** The matching rule page */
    private MatchingRuleDescriptionPage mrdPage;

    /** The matching rule use tab */
    private CTabItem mrudTab;

    /** The matching rule use page */
    private MatchingRuleUseDescriptionPage mrudPage;

    /** The syntax tab */
    private CTabItem lsdTab;

    /** The syntax page */
    private LdapSyntaxDescriptionPage lsdPage;


    /**
     * Gets the ID of the schema browser.
     *
     * @return the ID of the schema browser
     */
    public static String getId()
    {
        return BrowserUIConstants.EDITOR_SCHEMA_BROWSER;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        setSite( site );

        // mark dummy location, necessary because the first marked
        // location doesn't appear in history
        setInput( new SchemaBrowserInput( null, null ) );
        getSite().getPage().getNavigationHistory().markLocation( this );

        // set real input
        setInput( input );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        ocdPage.dispose();
        atdPage.dispose();
        mrdPage.dispose();
        mrudPage.dispose();
        lsdPage.dispose();
        tabFolder.dispose();
        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {
        tabFolder = new CTabFolder( parent, SWT.BOTTOM );

        ocdTab = new CTabItem( tabFolder, SWT.NONE );
        ocdTab.setText( "Object Classes" );
        ocdTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_OCD ) );
        ocdPage = new ObjectClassDescriptionPage( this );
        Control ocdPageControl = ocdPage.createControl( tabFolder );
        ocdTab.setControl( ocdPageControl );

        atdTab = new CTabItem( tabFolder, SWT.NONE );
        atdTab.setText( "Attribute Types" );
        atdTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ATD ) );
        atdPage = new AttributeTypeDescriptionPage( this );
        Control atdPageControl = atdPage.createControl( tabFolder );
        atdTab.setControl( atdPageControl );

        mrdTab = new CTabItem( tabFolder, SWT.NONE );
        mrdTab.setText( "Matching Rules" );
        mrdTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_MRD ) );
        mrdPage = new MatchingRuleDescriptionPage( this );
        Control mrdPageControl = mrdPage.createControl( tabFolder );
        mrdTab.setControl( mrdPageControl );

        mrudTab = new CTabItem( tabFolder, SWT.NONE );
        mrudTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_MRUD ) );
        mrudTab.setText( "Matching Rule Use" );
        mrudPage = new MatchingRuleUseDescriptionPage( this );
        Control mrudPageControl = mrudPage.createControl( tabFolder );
        mrudTab.setControl( mrudPageControl );

        lsdTab = new CTabItem( tabFolder, SWT.NONE );
        lsdTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LSD ) );
        lsdTab.setText( "Syntaxes" );
        lsdPage = new LdapSyntaxDescriptionPage( this );
        Control lsdPageControl = lsdPage.createControl( tabFolder );
        lsdTab.setControl( lsdPageControl );

        // set default selection
        tabFolder.setSelection( ocdTab );

        // init help context
        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent,
            BrowserUIConstants.PLUGIN_ID + "." + "tools_schema_browser" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( tabFolder,
            BrowserUIConstants.PLUGIN_ID + "." + "tools_schema_browser" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( ocdPageControl,
            BrowserUIConstants.PLUGIN_ID + "." + "tools_schema_browser" );
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( IEditorInput input )
    {
        super.setInput( input );

        if ( input instanceof SchemaBrowserInput && tabFolder != null )
        {
            SchemaBrowserInput sbi = ( SchemaBrowserInput ) input;

            // set connection;
            IBrowserConnection connection = sbi.getConnection();
            setConnection( connection );

            // set schema element and activate tab
            AbstractSchemaDescription schemaElement = sbi.getSchemaElement();
            if ( schemaElement instanceof ObjectClassDescription )
            {
                ocdPage.select( schemaElement );
                tabFolder.setSelection( ocdTab );
            }
            else if ( schemaElement instanceof AttributeTypeDescription )
            {
                atdPage.select( schemaElement );
                tabFolder.setSelection( atdTab );
            }
            else if ( schemaElement instanceof MatchingRuleDescription )
            {
                mrdPage.select( schemaElement );
                tabFolder.setSelection( mrdTab );
            }
            else if ( schemaElement instanceof MatchingRuleUseDescription )
            {
                mrudPage.select( schemaElement );
                tabFolder.setSelection( mrudTab );
            }
            else if ( schemaElement instanceof LdapSyntaxDescription )
            {
                lsdPage.select( schemaElement );
                tabFolder.setSelection( lsdTab );
            }

            if ( connection != null && schemaElement != null )
            {
                // enable one instance hack before fireing the input change event 
                // otherwise the navigation history is cleared.
                SchemaBrowserInput.enableOneInstanceHack( true );
                firePropertyChange( IEditorPart.PROP_INPUT );

                // disable one instance hack for marking the location
                SchemaBrowserInput.enableOneInstanceHack( false );
                getSite().getPage().getNavigationHistory().markLocation( this );
            }

            // finally enable the one instance hack 
            SchemaBrowserInput.enableOneInstanceHack( true );
        }
    }


    /**
     * Refreshes all pages.
     */
    public void refresh()
    {
        ocdPage.refresh();
        atdPage.refresh();
        mrdPage.refresh();
        mrudPage.refresh();
        lsdPage.refresh();
    }


    /**
     * Sets the show defauls schema flag to all pages.
     *
     * @param b the default schema flag
     */
    public void setShowDefaultSchema( boolean b )
    {
        ocdPage.setShowDefaultSchema( b );
        atdPage.setShowDefaultSchema( b );
        mrdPage.setShowDefaultSchema( b );
        mrudPage.setShowDefaultSchema( b );
        lsdPage.setShowDefaultSchema( b );
    }


    /**
     * Sets the connection.
     * 
     * @param connection the connection
     */
    public void setConnection( IBrowserConnection connection )
    {
        ocdPage.setConnection( connection );
        atdPage.setConnection( connection );
        mrdPage.setConnection( connection );
        mrudPage.setConnection( connection );
        lsdPage.setConnection( connection );
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void doSaveAs()
    {
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public INavigationLocation createEmptyNavigationLocation()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public INavigationLocation createNavigationLocation()
    {
        return new SchemaBrowserNavigationLocation( this );
    }

}
