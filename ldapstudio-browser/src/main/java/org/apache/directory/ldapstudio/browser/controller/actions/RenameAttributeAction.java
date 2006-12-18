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

package org.apache.directory.ldapstudio.browser.controller.actions;


import org.apache.directory.ldapstudio.browser.view.views.AttributesView;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.modify.ModifyResponse;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Rename Attribute Action.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameAttributeAction extends Action
{
    private static final int COLUMN_TO_EDIT = 1;
    private AttributesView view;
    private Table table;
    private TableEditor tableEditor;
    private Text textEditor;
    // A flag to not update twice the server
    private boolean done = false;


    /**
     * Creates a new instance of RenameAttributeAction.
     *
     * @param view the associated view
     * @param text the string used as the text for the action
     */
    public RenameAttributeAction( AttributesView view, String text )
    {
        super( text );
        this.view = view;
//        this.table = view.getViewer().getTable();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        createEditor();
        showEditor();
    }


    /**
     * Creates the Editor cell and registers associated listeners
     */
    private void createEditor()
    {
//        // Creating the Table Editor
//        tableEditor = new TableEditor( table );
//        tableEditor.horizontalAlignment = SWT.LEFT;
//        tableEditor.grabHorizontal = true;
//        tableEditor.minimumWidth = 50;
//
//        // Creating the Text Widget that will be used by the user 
//        // to enter the new value
//        textEditor = new Text( view.getViewer().getTable(), SWT.NONE );
//
//        // Adding Traverse Listener used to handle event when the 'return'
//        // or 'escape' key is pressed
//        textEditor.addListener( SWT.Traverse, new Listener()
//        {
//            public void handleEvent( Event event )
//            {
//                // Workaround for bug 20214 due to extra traverse events
//                switch ( event.detail )
//                {
//                    case SWT.TRAVERSE_ESCAPE: // Escape Key
//                        // Do nothing in this case
//                        disposeEditor();
//                        event.doit = true;
//                        event.detail = SWT.TRAVERSE_NONE;
//                        break;
//                    case SWT.TRAVERSE_RETURN: // Return Key
//                        saveChangesAndDisposeEditor();
//                        event.doit = true;
//                        event.detail = SWT.TRAVERSE_NONE;
//                        break;
//                }
//            }
//        } );
//
//        // Adding Focus Listener used to handle event when the user
//        // clicks on the elsewhere
//        textEditor.addFocusListener( new FocusAdapter()
//        {
//            public void focusLost( FocusEvent fe )
//            {
//                if ( !done )
//                {
//                    saveChangesAndDisposeEditor();
//                }
//            }
//        } );
    }


    /**
     * Shows the editor
     */
    private void showEditor()
    {
//        tableEditor.setEditor( textEditor, view.getViewer().getTable().getSelection()[0], COLUMN_TO_EDIT );
        textEditor.setText( getAttributeValue() );
        textEditor.selectAll();
        textEditor.setFocus();
    }


    /**
     * Saves the changes made in the editor and disposes the editor
     */
    private void saveChangesAndDisposeEditor()
    {
        if ( !getAttributeValue().equals( textEditor.getText()) )
        {
            saveChanges();
        }
        disposeEditor();
    }


    /**
     * Disposes the editor and refreshes the Atttributes View UI
     */
    private void disposeEditor()
    {
        textEditor.dispose();
        textEditor = null;
        tableEditor.setEditor( null, null, COLUMN_TO_EDIT );

        // Resizing Columns and resetting the focus on the Table
        view.resizeColumsToFit();
//        view.getViewer().getTable().setFocus();
    }


    /**
     * Gets the name of the selected attribute
     * @return the name of the selected attribute
     */
    private String getAttributeName()
    {
//        TableItem item = view.getSelectedAttributeTableItem();
//        return item.getText( 0 );
        return "";
    }


    /**
     * Gets the value of the selected attribute
     * @return the value of the selected attribute
     */
    private String getAttributeValue()
    {
//        TableItem item = view.getSelectedAttributeTableItem();
//        return item.getText( 1 );
        return "";
    }


    /**
     * Saves the changes made in the editor on the server
     * @param newText
     */
    private void saveChanges()
    {
        try
        {
            // Getting the Browser View
            BrowserView browserView = ( BrowserView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().findView( BrowserView.ID );

            EntryWrapper entryWrapper = ( EntryWrapper ) ( ( TreeSelection ) browserView.getViewer().getSelection() )
                .getFirstElement();
            SearchResultEntry entry = entryWrapper.getEntry();

            // Initialization of the DSML Engine and the DSML Response Parser
            Dsmlv2Engine engine = entryWrapper.getDsmlv2Engine();
            Dsmlv2ResponseParser parser = new Dsmlv2ResponseParser();

            String request = "<batchRequest>" + "   <modifyRequest dn=\""
                + entry.getObjectName().getNormName().toString() + "\">" + "      <modification name=\""
                + getAttributeName() + "\" operation=\"delete\">" + "         <value>" + getAttributeValue()
                + "</value>" + "      </modification>" + "      <modification name=\"" + getAttributeName()
                + "\" operation=\"add\">" + "         <value>" + textEditor.getText() + "</value>"
                + "      </modification>" + "   </modifyRequest>" + "</batchRequest>";

            parser.setInput( engine.processDSML( request ) );
            parser.parse();

            LdapResponse ldapResponse = parser.getBatchResponse().getCurrentResponse();

            if ( ldapResponse instanceof ModifyResponse )
            {
                ModifyResponse modifyResponse = ( ModifyResponse ) ldapResponse;

                if ( modifyResponse.getLdapResult().getResultCode() == 0 )
                {
                    entry.getPartialAttributeList().get( getAttributeName() ).remove( getAttributeValue() );
                    entry.getPartialAttributeList().get( getAttributeName() ).add( textEditor.getText() );
                    
//                    TableItem item = view.getSelectedAttributeTableItem();
//                    item.setText( 1, textEditor.getText() );
//                    view.getViewer().refresh( item );
                }
                else
                {
                    done = true;
                    // Displaying an error
                    MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Error !", "An error has ocurred.\n" + modifyResponse.getLdapResult().getErrorMessage() );
                }
            }
            else if ( ldapResponse instanceof ErrorResponse )
            {
                ErrorResponse errorResponse = ( ErrorResponse ) ldapResponse;

                done = true;
                // Displaying an error
                MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                    "An error has ocurred.\n" + errorResponse.getMessage() );
            }
        }
        catch ( Exception e )
        {
            done = true;
            // Displaying an error
            MessageDialog.openError( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error !",
                "An error has ocurred.\n" + e.getMessage() );
        }
    }
}
