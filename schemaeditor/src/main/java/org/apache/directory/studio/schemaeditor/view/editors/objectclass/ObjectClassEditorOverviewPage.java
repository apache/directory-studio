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

package org.apache.directory.studio.schemaeditor.view.editors.objectclass;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;
import org.apache.directory.studio.schemaeditor.model.alias.Alias;
import org.apache.directory.studio.schemaeditor.model.alias.AliasWithError;
import org.apache.directory.studio.schemaeditor.model.alias.AliasesStringParser;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.dialogs.AttributeTypeSelectionDialog;
import org.apache.directory.studio.schemaeditor.view.dialogs.EditAliasesDialog;
import org.apache.directory.studio.schemaeditor.view.dialogs.ObjectClassSelectionDialog;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingAttributeType;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingObjectClass;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditorInput;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;


/**
 * This class is the Overview Page of the Object Class Editor
 */
public class ObjectClassEditorOverviewPage extends FormPage
{
    /** The page ID */
    public static final String ID = ObjectClassEditor.ID + "overviewPage"; //$NON-NLS-1$

    /** The original object class */
    private ObjectClassImpl originalObjectClass;

    /** The modified object class */
    private ObjectClassImpl modifiedObjectClass;

    /** The original schema */
    private Schema originalSchema;

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaHandler Listener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerListener()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeAdded(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeModified(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeModified( AttributeTypeImpl at )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeRemoved(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleAdded(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleAdded( MatchingRuleImpl mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleModified(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleModified( MatchingRuleImpl mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleRemoved(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleRemoved( MatchingRuleImpl mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassAdded(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassAdded( ObjectClassImpl oc )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassModified(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassModified( ObjectClassImpl oc )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassRemoved(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassRemoved( ObjectClassImpl oc )
        {
            if ( !oc.equals( originalObjectClass ) )
            {
                refreshUI();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaAdded(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaAdded( Schema schema )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaRemoved(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaRemoved( Schema schema )
        {
            if ( !schema.equals( originalSchema ) )
            {
                refreshUI();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxAdded(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxAdded( SyntaxImpl syntax )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxModified(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxModified( SyntaxImpl syntax )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxRemoved(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxRemoved( SyntaxImpl syntax )
        {
            refreshUI();
        }
    };

    // UI fields
    private Text aliasesText;
    private Button aliasesButton;
    private Text oidText;
    private Hyperlink schemaLink;
    private Label schemaLabel;
    private Text descriptionText;
    private Table superiorsTable;
    private TableViewer superiorsTableViewer;
    private Button addButtonSuperiorsTable;
    private Button removeButtonSuperiorsTable;
    private Combo classTypeCombo;
    private Button obsoleteCheckbox;
    private Table mandatoryAttributesTable;
    private TableViewer mandatoryAttributesTableViewer;
    private Button addButtonMandatoryTable;
    private Button removeButtonMandatoryTable;
    private Table optionalAttributesTable;
    private TableViewer optionalAttributesTableViewer;
    private Button addButtonOptionalTable;
    private Button removeButtonOptionalTable;

    // Listeners

    /** The listener for the Aliases Text Widget */
    private ModifyListener aliasesTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            AliasesStringParser parser = new AliasesStringParser();
            parser.parse( aliasesText.getText() );
            List<Alias> parsedAliases = parser.getAliases();
            modifiedObjectClass.setNames( new String[0] );
            List<String> aliasesList = new ArrayList<String>();
            for ( Alias parsedAlias : parsedAliases )
            {
                if ( !( parsedAlias instanceof AliasWithError ) )
                {
                    aliasesList.add( parsedAlias.getAlias() );
                }
            }
            modifiedObjectClass.setNames( aliasesList.toArray( new String[0] ) );
            setEditorDirty();
        }
    };

    /** The listener for Aliases Button Widget */
    private SelectionAdapter aliasesButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            EditAliasesDialog editDialog = new EditAliasesDialog( modifiedObjectClass.getNamesRef() );
            if ( editDialog.open() != Window.OK )
            {
                return;
            }
            if ( editDialog.isDirty() )
            {
                modifiedObjectClass.setNames( editDialog.getAliases() );
                if ( ( modifiedObjectClass.getNamesRef() != null ) && ( modifiedObjectClass.getNamesRef().length != 0 ) )
                {
                    aliasesText.setText( ViewUtils.concateAliases( modifiedObjectClass.getNamesRef() ) );
                }
                else
                {
                    aliasesText.setText( "" );
                }
                setEditorDirty();
            }
        }
    };

    /** The Modify listener for the OID Text Widget */
    private ModifyListener oidTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            oidText.setForeground( ViewUtils.COLOR_BLACK );
            oidText.setToolTipText( "" ); //$NON-NLS-1$

            String oid = oidText.getText();

            if ( OID.isOID( oid ) )
            {
                if ( ( originalObjectClass.getOid().equals( oid ) )
                    || !( schemaHandler.isAliasOrOidAlreadyTaken( oid ) ) )
                {
                    modifiedObjectClass.setOid( oid );
                    setEditorDirty();
                }
                else
                {
                    oidText.setForeground( ViewUtils.COLOR_RED );
                    oidText.setToolTipText( Messages.getString( "ObjectClassEditorOverviewPage.ElementOIDExists" ) );
                }
            }
            else
            {
                oidText.setForeground( ViewUtils.COLOR_RED );
                oidText.setToolTipText( Messages.getString( "ObjectClassEditorOverviewPage.MalformedOID" ) );
            }
        }
    };

    /** The Verify listener for the OID Text Widget */
    private VerifyListener oidTextVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "([0-9]*\\.?)*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
        }
    };

    /** The listener for the Schema Hyperlink Widget*/
    private HyperlinkAdapter schemaLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            SchemaEditorInput input = new SchemaEditorInput( schemaHandler.getSchema( modifiedObjectClass.getSchema() ) );
            String editorId = SchemaEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    };

    /** The listener for Description Text Widget */
    private ModifyListener descriptionTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            int caretPosition = descriptionText.getCaretPosition();
            modifiedObjectClass.setDescription( descriptionText.getText() );
            descriptionText.setSelection( caretPosition );
            setEditorDirty();
        }
    };

    /** The listener for Class Type Widget */
    private ModifyListener classTypeListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( classTypeCombo.getSelectionIndex() == 0 )
            {
                modifiedObjectClass.setType( ObjectClassTypeEnum.ABSTRACT );
            }
            else if ( classTypeCombo.getSelectionIndex() == 1 )
            {
                modifiedObjectClass.setType( ObjectClassTypeEnum.AUXILIARY );
            }
            else if ( classTypeCombo.getSelectionIndex() == 2 )
            {
                modifiedObjectClass.setType( ObjectClassTypeEnum.STRUCTURAL );
            }
            setEditorDirty();
        }
    };

    /** The listener for Obsolete Widget */
    private SelectionAdapter obsoleteListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            modifiedObjectClass.setObsolete( obsoleteCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for Mandatory Attributes Table Widget */
    private MouseListener mandatoryAttributesTableListener = new MouseListener()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) mandatoryAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof AttributeTypeImpl )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new AttributeTypeEditorInput( ( AttributeTypeImpl ) selectedElement ),
                            AttributeTypeEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        }


        public void mouseDown( MouseEvent e )
        {
        }


        public void mouseUp( MouseEvent e )
        {
            removeButtonMandatoryTable.setEnabled( mandatoryAttributesTable.getSelection().length != 0 );
        }
    };

    /** The listener for Add Button Widget of the Mandatory Attributes section */
    private SelectionAdapter addButtonMandatoryTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
            List<AttributeTypeImpl> hiddenATs = new ArrayList<AttributeTypeImpl>();
            String[] mustsHidden = modifiedObjectClass.getMustNamesList();
            if ( mustsHidden != null )
            {
                for ( String must : mustsHidden )
                {
                    AttributeTypeImpl at = schemaHandler.getAttributeType( must );
                    if ( at != null )
                    {
                        hiddenATs.add( at );
                    }
                }
            }
            dialog.setHiddenAttributeTypes( hiddenATs.toArray( new AttributeTypeImpl[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            AttributeTypeImpl at = dialog.getSelectedAttributeType();
            if ( at == null )
            {
                return;
            }

            List<String> newMusts = new ArrayList<String>();
            String[] musts = modifiedObjectClass.getMustNamesList();
            if ( musts != null )
            {
                for ( String must : musts )
                {
                    newMusts.add( must );
                }
            }
            String[] names = at.getNamesRef();
            if ( ( names != null ) && ( names.length > 0 ) )
            {
                newMusts.add( names[0] );
            }
            else
            {
                newMusts.add( at.getOid() );
            }
            modifiedObjectClass.setMustNamesList( newMusts.toArray( new String[0] ) );

            fillInMandatoryAttributesTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Mandatory Attributes section */
    private SelectionAdapter removeButtonMandatoryTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) mandatoryAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> newMusts = new ArrayList<String>();
                String[] musts = modifiedObjectClass.getMustNamesList();
                for ( String must : musts )
                {
                    newMusts.add( must );
                }

                if ( selectedElement instanceof AttributeTypeImpl )
                {
                    for ( String name : ( ( AttributeTypeImpl ) selectedElement ).getNamesRef() )
                    {
                        newMusts.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingAttributeType )
                {
                    newMusts.remove( ( ( NonExistingAttributeType ) selectedElement ).getName() );
                }

                modifiedObjectClass.setMustNamesList( newMusts.toArray( new String[0] ) );

                fillInMandatoryAttributesTable();
                addButtonMandatoryTable.setFocus();
                removeButtonMandatoryTable.setEnabled( mandatoryAttributesTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };

    /** The listener for Optional Attributes Table Widget */
    private MouseListener optionalAttributesTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) optionalAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof AttributeTypeImpl )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new AttributeTypeEditorInput( ( AttributeTypeImpl ) selectedElement ),
                            AttributeTypeEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        }


        public void mouseUp( MouseEvent e )
        {
            removeButtonOptionalTable.setEnabled( optionalAttributesTable.getSelection().length != 0 );
        }
    };

    /** The listener for Add Button Widget of the Optional Attributes section */
    private SelectionAdapter addButtonOptionalTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
            List<AttributeTypeImpl> hiddenATs = new ArrayList<AttributeTypeImpl>();
            String[] maysHidden = modifiedObjectClass.getMayNamesList();
            if ( maysHidden != null )
            {
                for ( String may : maysHidden )
                {
                    AttributeTypeImpl at = schemaHandler.getAttributeType( may );
                    if ( at != null )
                    {
                        hiddenATs.add( at );
                    }
                }
            }
            dialog.setHiddenAttributeTypes( hiddenATs.toArray( new AttributeTypeImpl[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            AttributeTypeImpl at = dialog.getSelectedAttributeType();
            if ( at == null )
            {
                return;
            }

            List<String> newMays = new ArrayList<String>();
            String[] mays = modifiedObjectClass.getMayNamesList();
            if ( mays != null )
            {
                for ( String may : mays )
                {
                    newMays.add( may );
                }
            }
            String[] names = at.getNamesRef();
            if ( ( names != null ) && ( names.length > 0 ) )
            {
                newMays.add( names[0] );
            }
            else
            {
                newMays.add( at.getOid() );
            }
            modifiedObjectClass.setMayNamesList( newMays.toArray( new String[0] ) );

            fillInOptionalAttributesTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Optional Attributes section */
    private SelectionAdapter removeButtonOptionalTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) optionalAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> newMays = new ArrayList<String>();
                String[] mays = modifiedObjectClass.getMayNamesList();
                for ( String may : mays )
                {
                    newMays.add( may );
                }

                if ( selectedElement instanceof AttributeTypeImpl )
                {
                    for ( String name : ( ( AttributeTypeImpl ) selectedElement ).getNamesRef() )
                    {
                        newMays.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingAttributeType )
                {
                    newMays.remove( ( ( NonExistingAttributeType ) selectedElement ).getName() );
                }

                modifiedObjectClass.setMayNamesList( newMays.toArray( new String[0] ) );

                fillInOptionalAttributesTable();
                addButtonOptionalTable.setFocus();
                removeButtonOptionalTable.setEnabled( optionalAttributesTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };

    /** The listener for Superiors Table Widget */
    private MouseListener superiorsTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) superiorsTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof ObjectClassImpl )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new ObjectClassEditorInput( ( ObjectClassImpl ) selectedElement ),
                            ObjectClassEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        }


        public void mouseUp( MouseEvent e )
        {
            removeButtonSuperiorsTable.setEnabled( superiorsTable.getSelection().length != 0 );
        }
    };

    /** The listener for Add Button Widget of the Superiors Table */
    private SelectionAdapter addButtonSuperiorsTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ObjectClassSelectionDialog dialog = new ObjectClassSelectionDialog();
            List<ObjectClassImpl> hiddenOCs = new ArrayList<ObjectClassImpl>();
            for ( String sup : modifiedObjectClass.getSuperClassesNames() )
            {
                ObjectClassImpl oc = schemaHandler.getObjectClass( sup );
                if ( oc != null )
                {
                    hiddenOCs.add( oc );
                }
            }
            hiddenOCs.add( originalObjectClass );
            dialog.setHiddenObjectClasses( hiddenOCs.toArray( new ObjectClassImpl[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            ObjectClassImpl oc = dialog.getSelectedObjectClass();
            if ( oc == null )
            {
                return;
            }

            List<String> superiors = new ArrayList<String>();
            String[] sups = modifiedObjectClass.getSuperClassesNames();
            for ( String sup : sups )
            {
                superiors.add( sup );
            }
            String[] names = oc.getNamesRef();
            if ( ( names != null ) && ( names.length > 0 ) )
            {
                superiors.add( names[0] );
            }
            else
            {
                superiors.add( oc.getOid() );
            }
            modifiedObjectClass.setSuperClassesNames( superiors.toArray( new String[0] ) );

            fillInSuperiorsTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Superiors Table */
    private SelectionAdapter removeButtonSuperiorsTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) superiorsTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> superiors = new ArrayList<String>();
                String[] sups = modifiedObjectClass.getSuperClassesNames();
                for ( String sup : sups )
                {
                    superiors.add( sup );
                }

                if ( selectedElement instanceof ObjectClassImpl )
                {
                    for ( String name : ( ( ObjectClassImpl ) selectedElement ).getNamesRef() )
                    {
                        superiors.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingObjectClass )
                {
                    superiors.remove( ( ( NonExistingObjectClass ) selectedElement ).getName() );
                }

                modifiedObjectClass.setSuperClassesNames( superiors.toArray( new String[0] ) );

                fillInSuperiorsTable();
                addButtonSuperiorsTable.setFocus();
                removeButtonSuperiorsTable.setEnabled( superiorsTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };
    
    /** The filter listener for Mouse Wheel events */
    private Listener mouseWheelFilter = new Listener()
    {
    	public void handleEvent( Event event )
    	{
    		// Hiding Mouse Wheel events for Combo widgets
    		if ( event.widget instanceof Combo )
    		{
    			event.doit = false;
    		}
    	}
    };


    /**
     * Default constructor
     * @param editor
     *      the associated editor
     */
    public ObjectClassEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, Messages.getString( "ObjectClassEditorOverviewPage.Overview" ) );
        schemaHandler = Activator.getDefault().getSchemaHandler();
        schemaHandler.addListener( schemaHandlerListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the original and modified object classes
        modifiedObjectClass = ( ( ObjectClassEditor ) getEditor() ).getModifiedObjectClass();
        originalObjectClass = ( ( ObjectClassEditor ) getEditor() ).getOriginalObjectClass();
        originalSchema = schemaHandler.getSchema( originalObjectClass.getSchema() );

        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        form.getBody().setLayout( new GridLayout() );

        // General Information Section
        createGeneralInformationSection( form.getBody(), toolkit );

        Composite bottomComposite = toolkit.createComposite( form.getBody() );
        bottomComposite.setLayout( new GridLayout( 2, true ) );
        bottomComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Mandatory Attributes Section
        createMandatoryAttributesSection( bottomComposite, toolkit );

        // Optionnal Attributes Section
        createOptionalAttributesSection( bottomComposite, toolkit );

        // Enabling or disabling the fields
        //        setFieldsEditableState();

        // Filling the UI with values from the object class
        fillInUiFields();

        // Listeners initialization
        addListeners();

        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( form,
            PluginConstants.PLUGIN_ID + "." + "object_class_editor" ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Creates the General Information Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createGeneralInformationSection( Composite parent, FormToolkit toolkit )
    {
        // General Information Section
        Section section_general_information = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        section_general_information.setDescription( Messages
            .getString( "ObjectClassEditorOverviewPage.SpecifyGeneralInformation" ) );
        section_general_information.setText( Messages.getString( "ObjectClassEditorOverviewPage.GeneralInformation" ) );
        section_general_information.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Creating the layout of the section
        Composite client_general_information = toolkit.createComposite( section_general_information );
        client_general_information.setLayout( new GridLayout( 2, false ) );
        toolkit.paintBordersFor( client_general_information );
        section_general_information.setClient( client_general_information );
        section_general_information.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Adding elements to the section

        // ALIASES Button
        toolkit.createLabel( client_general_information, Messages.getString( "ObjectClassEditorOverviewPage.Aliases" ) );
        Composite aliasComposite = toolkit.createComposite( client_general_information );
        GridLayout aliasCompositeGridLayout = new GridLayout( 2, false );
        toolkit.paintBordersFor( aliasComposite );
        aliasCompositeGridLayout.marginHeight = 1;
        aliasCompositeGridLayout.marginWidth = 1;
        aliasComposite.setLayout( aliasCompositeGridLayout );
        aliasComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        aliasesText = toolkit.createText( aliasComposite, "" ); //$NON-NLS-1$
        aliasesText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        aliasesButton = toolkit.createButton( aliasComposite, Messages
            .getString( "ObjectClassEditorOverviewPage.EditAliases" ), SWT.PUSH );
        aliasesButton.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

        // OID Field
        toolkit.createLabel( client_general_information, Messages.getString( "ObjectClassEditorOverviewPage.OID" ) );
        oidText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oidText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SCHEMA Field
        schemaLink = toolkit.createHyperlink( client_general_information, Messages
            .getString( "ObjectClassEditorOverviewPage.Schema" ), SWT.WRAP );
        schemaLabel = toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        schemaLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassEditorOverviewPage.Description" ) );
        descriptionText = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        descriptionGridData.heightHint = 42;
        descriptionText.setLayoutData( descriptionGridData );

        // SUPERIORS Table
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassEditorOverviewPage.SuperiorClasses" ) );
        Composite superiorsComposite = toolkit.createComposite( client_general_information );
        GridLayout superiorsCompositeGridLayout = new GridLayout( 2, false );
        toolkit.paintBordersFor( superiorsComposite );
        superiorsCompositeGridLayout.marginHeight = 1;
        superiorsCompositeGridLayout.marginWidth = 1;
        superiorsComposite.setLayout( superiorsCompositeGridLayout );
        superiorsComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        superiorsTable = toolkit.createTable( superiorsComposite, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL
            | SWT.V_SCROLL );
        toolkit.paintBordersFor( superiorsTable );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false, 1, 2 );
        gridData.heightHint = 45;
        gridData.minimumHeight = 45;
        superiorsTable.setLayoutData( gridData );
        superiorsTableViewer = new TableViewer( superiorsTable );
        superiorsTableViewer.setContentProvider( new ObjectClassEditorSuperiorsTableContentProvider() );
        superiorsTableViewer.setLabelProvider( new ObjectClassEditorSuperiorsTableLabelProvider() );
        addButtonSuperiorsTable = toolkit.createButton( superiorsComposite, Messages
            .getString( "ObjectClassEditorOverviewPage.Add" ), SWT.PUSH );
        addButtonSuperiorsTable.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        removeButtonSuperiorsTable = toolkit.createButton( superiorsComposite, Messages
            .getString( "ObjectClassEditorOverviewPage.Remove" ), SWT.PUSH );
        removeButtonSuperiorsTable.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );

        // CLASS TYPE Combo
        toolkit
            .createLabel( client_general_information, Messages.getString( "ObjectClassEditorOverviewPage.ClassType" ) );
        classTypeCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        classTypeCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        initClassTypeCombo();

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        obsoleteCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "ObjectClassEditorOverviewPage.Obsolete" ), SWT.CHECK );
        obsoleteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Mandatory Attributes Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createMandatoryAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // MANDATORY ATTRIBUTES Section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText( Messages.getString( "ObjectClassEditorOverviewPage.MandatoryAttributes" ) );
        section.setDescription( Messages.getString( "ObjectClassEditorOverviewPage.SpecifyMandatoryAttributes" ) );
        section.setExpanded( true );
        Composite client = toolkit.createComposite( section );
        section.setClient( client );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        mandatoryAttributesTable = toolkit.createTable( client, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL
            | SWT.V_SCROLL );
        gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.verticalSpan = 2;
        gd.heightHint = 108;
        mandatoryAttributesTable.setLayoutData( gd );
        mandatoryAttributesTableViewer = new TableViewer( mandatoryAttributesTable );
        mandatoryAttributesTableViewer.setContentProvider( new ObjectClassEditorAttributesTableContentProvider() );
        mandatoryAttributesTableViewer.setLabelProvider( new ObjectClassEditorAttributesTableLabelProvider() );

        addButtonMandatoryTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassEditorOverviewPage.Add" ), SWT.PUSH );
        removeButtonMandatoryTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassEditorOverviewPage.Remove" ), SWT.PUSH );
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        addButtonMandatoryTable.setLayoutData( gd );
        removeButtonMandatoryTable.setLayoutData( gd );

        // By default, no element is selected
        removeButtonMandatoryTable.setEnabled( false );
    }


    /**
     * Creates the Optional Attributes Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createOptionalAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // OPTIONAL ATTRIBUTES Section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText( Messages.getString( "ObjectClassEditorOverviewPage.OptionalAttributes" ) );
        section.setDescription( Messages.getString( "ObjectClassEditorOverviewPage.SpecifyOptionalAttributes" ) );
        section.setExpanded( true );
        Composite client = toolkit.createComposite( section );
        section.setClient( client );
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        optionalAttributesTable = toolkit.createTable( client, SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL
            | SWT.V_SCROLL );
        gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.verticalSpan = 2;
        gd.heightHint = 108;
        optionalAttributesTable.setLayoutData( gd );
        optionalAttributesTableViewer = new TableViewer( optionalAttributesTable );
        optionalAttributesTableViewer.setContentProvider( new ObjectClassEditorAttributesTableContentProvider() );
        optionalAttributesTableViewer.setLabelProvider( new ObjectClassEditorAttributesTableLabelProvider() );

        addButtonOptionalTable = toolkit.createButton( client,
            Messages.getString( "ObjectClassEditorOverviewPage.Add" ), SWT.PUSH );
        removeButtonOptionalTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassEditorOverviewPage.Remove" ), SWT.PUSH );
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        addButtonOptionalTable.setLayoutData( gd );
        removeButtonOptionalTable.setLayoutData( gd );

        // By default, no element is selected
        removeButtonOptionalTable.setEnabled( false );
    }


    /**
     * Initializes the UI fields from the input.
     */
    private void fillInUiFields()
    {
        // ALIASES Label
        if ( ( modifiedObjectClass.getNamesRef() != null ) && ( modifiedObjectClass.getNamesRef().length != 0 ) )
        {
            aliasesText.setText( ViewUtils.concateAliases( modifiedObjectClass.getNamesRef() ) );
        }
        else
        {
            aliasesText.setText( "" ); //$NON-NLS-1$
        }

        // OID Field
        if ( modifiedObjectClass.getOid() != null )
        {
            oidText.setText( modifiedObjectClass.getOid() );
        }

        schemaLabel.setText( modifiedObjectClass.getSchema() );

        // DESCRIPTION Field
        if ( modifiedObjectClass.getDescription() != null )
        {
            descriptionText.setText( modifiedObjectClass.getDescription() );
        }

        // SUPERIORS Table
        fillInSuperiorsTable();

        // CLASS TYPE Combo
        fillInClassType();

        // OBSOLETE Checkbox
        obsoleteCheckbox.setSelection( modifiedObjectClass.isObsolete() );

        // MANDATORY ATTRIBUTES Table
        fillInMandatoryAttributesTable();

        // OPTIONNAL ATTRIBUTES Table
        fillInOptionalAttributesTable();
    }


    /**
     * Fills in the Superiors Table.
     */
    private void fillInSuperiorsTable()
    {
        superiorsTableViewer.setInput( modifiedObjectClass.getSuperClassesNames() );
    }


    /**
     * Initializes the Class Type Combo
     */
    private void initClassTypeCombo()
    {
        classTypeCombo.add( Messages.getString( "ObjectClassEditorOverviewPage.Abstract" ), 0 );
        classTypeCombo.add( Messages.getString( "ObjectClassEditorOverviewPage.Auxiliary" ), 1 );
        classTypeCombo.add( Messages.getString( "ObjectClassEditorOverviewPage.Structural" ), 2 );
    }


    /**
     * Fills in the Class Type Combo
     */
    private void fillInClassType()
    {
        if ( modifiedObjectClass.getType() == ObjectClassTypeEnum.ABSTRACT )
        {
            classTypeCombo.select( 0 );
        }
        else if ( modifiedObjectClass.getType() == ObjectClassTypeEnum.AUXILIARY )
        {
            classTypeCombo.select( 1 );
        }
        else if ( modifiedObjectClass.getType() == ObjectClassTypeEnum.STRUCTURAL )
        {
            classTypeCombo.select( 2 );
        }
    }


    /**
     * Initializes the Mandatory Attributes Table
     */
    private void fillInMandatoryAttributesTable()
    {
        mandatoryAttributesTableViewer.setInput( modifiedObjectClass.getMustNamesList() );
    }


    /**
     * Initializes the Optional Attributes Table
     */
    private void fillInOptionalAttributesTable()
    {
        optionalAttributesTableViewer.setInput( modifiedObjectClass.getMayNamesList() );
    }


    /**
     * Adds listeners to UI fields
     */
    private void addListeners()
    {
        aliasesText.addModifyListener( aliasesTextModifyListener );
        aliasesButton.addSelectionListener( aliasesButtonListener );
        oidText.addModifyListener( oidTextModifyListener );
        oidText.addVerifyListener( oidTextVerifyListener );
        descriptionText.addModifyListener( descriptionTextListener );
        addButtonSuperiorsTable.addSelectionListener( addButtonSuperiorsTableListener );
        removeButtonSuperiorsTable.addSelectionListener( removeButtonSuperiorsTableListener );
        classTypeCombo.addModifyListener( classTypeListener );
        obsoleteCheckbox.addSelectionListener( obsoleteListener );
        addButtonMandatoryTable.addSelectionListener( addButtonMandatoryTableListener );
        removeButtonMandatoryTable.addSelectionListener( removeButtonMandatoryTableListener );
        addButtonOptionalTable.addSelectionListener( addButtonOptionalTableListener );
        removeButtonOptionalTable.addSelectionListener( removeButtonOptionalTableListener );
        schemaLink.addHyperlinkListener( schemaLinkListener );
        superiorsTable.addMouseListener( superiorsTableListener );
        mandatoryAttributesTable.addMouseListener( mandatoryAttributesTableListener );
        optionalAttributesTable.addMouseListener( optionalAttributesTableListener );
        
        Display.getCurrent().addFilter( SWT.MouseWheel, mouseWheelFilter );
    }


    /**
     * Removes listeners from UI fields
     */
    private void removeListeners()
    {
        aliasesText.removeModifyListener( aliasesTextModifyListener );
        aliasesButton.removeSelectionListener( aliasesButtonListener );
        oidText.removeModifyListener( oidTextModifyListener );
        oidText.removeVerifyListener( oidTextVerifyListener );
        schemaLink.removeHyperlinkListener( schemaLinkListener );
        descriptionText.removeModifyListener( descriptionTextListener );
        superiorsTable.removeMouseListener( superiorsTableListener );
        addButtonSuperiorsTable.removeSelectionListener( addButtonSuperiorsTableListener );
        removeButtonSuperiorsTable.removeSelectionListener( removeButtonSuperiorsTableListener );
        classTypeCombo.removeModifyListener( classTypeListener );
        obsoleteCheckbox.removeSelectionListener( obsoleteListener );
        mandatoryAttributesTable.removeMouseListener( mandatoryAttributesTableListener );
        addButtonMandatoryTable.removeSelectionListener( addButtonMandatoryTableListener );
        removeButtonMandatoryTable.removeSelectionListener( removeButtonMandatoryTableListener );
        optionalAttributesTable.removeMouseListener( optionalAttributesTableListener );
        addButtonOptionalTable.removeSelectionListener( addButtonOptionalTableListener );
        removeButtonOptionalTable.removeSelectionListener( removeButtonOptionalTableListener );
        
        Display.getCurrent().removeFilter( SWT.MouseWheel, mouseWheelFilter );
    }


    /**
     * Sets the editor as dirty
     */
    private void setEditorDirty()
    {
        ( ( ObjectClassEditor ) getEditor() ).setDirty( true );
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        removeListeners();
        fillInUiFields();
        addListeners();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#dispose()
     */
    public void dispose()
    {
        schemaHandler.removeListener( schemaHandlerListener );

        super.dispose();
    }
}
