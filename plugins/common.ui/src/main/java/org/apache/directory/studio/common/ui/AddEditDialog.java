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
package org.apache.directory.studio.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * An abstract class used as a base class for Dialog asscoiated with the Add or Edit
 * action of a TableWidget
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 *
 * @param <E> The Element type
 */
public abstract class AddEditDialog<E> extends Dialog
{
    /** The edited Element, if any */
    private E editedElement;
    
    /** The table's elements */
    private List<E> elements;
    
    /** The position of the selected element, if we have any */
    private int selectedPosition;

    /** A flag set to true when the dialog is opened using the Add button */
    private boolean isAdd = false;

    /** A flag used to tell if the okButton must be disabled */
    protected boolean okDisabled = false;

    /**
     * Create a new instance of the TableAddEditDialog
     * 
     * @param parentShell The Parent shell
     */
    protected AddEditDialog( Shell parentShell )
    {
        super( parentShell );
    }

    /**
     * Initialize the Dialog with the content of the edited element, if any
     */
    protected void initDialog()
    {
        // Nothing to do
    }
    

    /**
     * Override the createButtonBar method to be able to hide the OK button if needed
     */
    protected Control createButtonBar( Composite parent ) 
    {
        Control buttonBar = super.createButtonBar( parent );
        
        if ( okDisabled )
        {
            Button okButton = getButton( IDialogConstants.OK_ID );
            okButton.setEnabled( false );
        }
        
        return buttonBar;
    }
    
    
    /**
     * Add a new Element that will be edited
     */
    public abstract void addNewElement();
    

    /**
     * Add a new Element that will be edited
     */
    protected void addNewElement( E newElement )
    {
        // Nothing to do
    }
    

    /**
     * @return The edited element 
     */
    public E getEditedElement()
    {
        return editedElement;
    }


    /**
     * Store the Element that will be edited
     * @param editedElement The edited Element 
     */
    public final void setEditedElement( E editedElement )
    {
        this.editedElement = editedElement;
    }

    /**
     * @return the selectedPosition
     */
    public int getSelectedPosition()
    {
        return selectedPosition;
    }
    

    /**
     * @param selectedPosition the selectedPosition to set
     */
    public void setSelectedPosition( int selectedPosition )
    {
        this.selectedPosition = selectedPosition;
    }

    
    /**
     * Stores the TableWidget list of elements
     * @param elements The elements to store
     */
    public void setElements( List<E> elements )
    {
        this.elements = new ArrayList<E>();
        this.elements.addAll( elements );
    }
    
    
    /**
     * @return The list of elements stored in the TableWidget
     */
    protected List<E> getElements()
    {
        return elements;
    }
    
    
    /**
     * Set the isAdd flag to true
     */
    public void setAdd()
    {
        isAdd = true;
    }
    
    
    /**
     * Set the isAdd flag to false
     */
    public void setEdit()
    {
        isAdd = false;
    }
    
    
    /**
     * @return True if the Dialog has been opened using the Add button.
     */
    public boolean isAdd()
    {
        return isAdd;
    }
}
