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

package org.apache.directory.ldapstudio.browser.ui.editors.schemabrowser;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.LdapSyntaxDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.MatchingRuleUseDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;


public class SchemaBrowser extends EditorPart
{

    private CTabFolder tabFolder;

    private CTabItem ocdTab;

    private ObjectClassDescriptionPage ocdPage;

    private CTabItem atdTab;

    private AttributeTypeDescriptionPage atdPage;

    private CTabItem mrdTab;

    private MatchingRuleDescriptionPage mrdPage;

    private CTabItem mrudTab;

    private MatchingRuleUseDescriptionPage mrudPage;

    private CTabItem lsdTab;

    private LdapSyntaxDescriptionPage lsdPage;

    private HistoryManager historyManager;

    private BackAction backAction;

    private ForwardAction forwardAction;

    private ShowDefaultSchemaAction showDefaultSchemaAction;

    private ReloadSchemaAction reloadSchemaAction;


    public static String getId()
    {
        return SchemaBrowser.class.getName();
    }


    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        setInput( SchemaBrowserInput.getInstance() );
        super.setSite( site );
    }


    public void dispose()
    {
        this.reloadSchemaAction.dispose();
        this.showDefaultSchemaAction.dispose();
        this.ocdPage.dispose();
        this.atdPage.dispose();
        this.mrdPage.dispose();
        this.mrudPage.dispose();
        this.lsdPage.dispose();
        this.tabFolder.dispose();
        super.dispose();
    }


    public void createPartControl( Composite parent )
    {

        this.historyManager = new HistoryManager( this );
        this.backAction = new BackAction( this.historyManager );
        this.forwardAction = new ForwardAction( this.historyManager );
        this.showDefaultSchemaAction = new ShowDefaultSchemaAction( this );
        this.reloadSchemaAction = new ReloadSchemaAction( this );

        this.tabFolder = new CTabFolder( parent, SWT.BOTTOM );

        this.ocdTab = new CTabItem( this.tabFolder, SWT.NONE );
        this.ocdTab.setText( "Object Classes" );
        this.ocdTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_OCD ) );
        this.ocdPage = new ObjectClassDescriptionPage( this );
        Control ocdPageControl = this.ocdPage.createControl( this.tabFolder );
        this.ocdTab.setControl( ocdPageControl );

        this.atdTab = new CTabItem( this.tabFolder, SWT.NONE );
        this.atdTab.setText( "Attribute Types" );
        this.atdTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_ATD ) );
        this.atdPage = new AttributeTypeDescriptionPage( this );
        Control atdPageControl = this.atdPage.createControl( this.tabFolder );
        this.atdTab.setControl( atdPageControl );

        this.mrdTab = new CTabItem( this.tabFolder, SWT.NONE );
        this.mrdTab.setText( "Matching Rules" );
        this.mrdTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_MRD ) );
        this.mrdPage = new MatchingRuleDescriptionPage( this );
        Control mrdPageControl = this.mrdPage.createControl( this.tabFolder );
        this.mrdTab.setControl( mrdPageControl );

        this.mrudTab = new CTabItem( this.tabFolder, SWT.NONE );
        this.mrudTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_MRUD ) );
        this.mrudTab.setText( "Matching Rule Use" );
        this.mrudPage = new MatchingRuleUseDescriptionPage( this );
        Control mrudPageControl = this.mrudPage.createControl( this.tabFolder );
        this.mrudTab.setControl( mrudPageControl );

        this.lsdTab = new CTabItem( this.tabFolder, SWT.NONE );
        this.lsdTab.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_LSD ) );
        this.lsdTab.setText( "Syntaxes" );
        this.lsdPage = new LdapSyntaxDescriptionPage( this );
        Control lsdPageControl = this.lsdPage.createControl( this.tabFolder );
        this.lsdTab.setControl( lsdPageControl );

        this.tabFolder.setSelection( this.ocdTab );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( parent,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_schema_browser" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( tabFolder,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_schema_browser" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( ocdPageControl,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_schema_browser" );

    }


    public static void select( Object obj )
    {

        String targetId = SchemaBrowser.getId();
        IEditorPart target = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(
            SchemaBrowserInput.getInstance() );
        if ( target == null )
        {
            try
            {
                target = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                    SchemaBrowserInput.getInstance(), targetId, true );
            }
            catch ( PartInitException e )
            {
            }
        }
        if ( target != null && target instanceof SchemaBrowser )
        {
            // target.getSite().getPage().activate(target);
            target.getSite().getPage().bringToTop( target );

            SchemaBrowser schemaBrowser = ( ( SchemaBrowser ) target );
            if ( obj instanceof ObjectClassDescription )
            {
                schemaBrowser.ocdPage.select( obj );
                schemaBrowser.tabFolder.setSelection( schemaBrowser.ocdTab );
            }
            else if ( obj instanceof AttributeTypeDescription )
            {
                schemaBrowser.atdPage.select( obj );
                schemaBrowser.tabFolder.setSelection( schemaBrowser.atdTab );
            }
            else if ( obj instanceof MatchingRuleDescription )
            {
                schemaBrowser.mrdPage.select( obj );
                schemaBrowser.tabFolder.setSelection( schemaBrowser.mrdTab );
            }
            else if ( obj instanceof MatchingRuleUseDescription )
            {
                schemaBrowser.mrudPage.select( obj );
                schemaBrowser.tabFolder.setSelection( schemaBrowser.mrudTab );
            }
            else if ( obj instanceof LdapSyntaxDescription )
            {
                schemaBrowser.lsdPage.select( obj );
                schemaBrowser.tabFolder.setSelection( schemaBrowser.lsdTab );
            }
        }

    }


    public BackAction getBackAction()
    {
        return this.backAction;
    }


    public ForwardAction getForwardAction()
    {
        return this.forwardAction;
    }


    public ReloadSchemaAction getReloadSchemaAction()
    {
        return reloadSchemaAction;
    }


    public ShowDefaultSchemaAction getShowDefaultSchemaAction()
    {
        return showDefaultSchemaAction;
    }


    public void refresh()
    {
        this.ocdPage.refresh();
        this.atdPage.refresh();
        this.mrdPage.refresh();
        this.mrudPage.refresh();
        this.lsdPage.refresh();

        this.reloadSchemaAction.updateEnabledState();
    }


    public boolean isShowDefaultSchema()
    {
        return this.showDefaultSchemaAction.isChecked();
    }


    public IConnection getSelectedConnection()
    {
        return this.ocdPage.getSelectedConnection();
    }


    public void setFocus()
    {
    }


    public void doSave( IProgressMonitor monitor )
    {
    }


    public void doSaveAs()
    {
    }


    public boolean isDirty()
    {
        return false;
    }


    public boolean isSaveAsAllowed()
    {
        return false;
    }

}
