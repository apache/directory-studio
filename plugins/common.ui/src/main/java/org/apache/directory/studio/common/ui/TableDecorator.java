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

import org.eclipse.jface.viewers.LabelProvider;

/**
 * An Class used to store the comparator and labelProvider used by the TableWidget.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class TableDecorator<E> extends LabelProvider
{
    /** The Dialog instance */
    private AddEditDialog<E> dialog;
    
    /**
     * Create a new instance of a TableDecorator
     */
    public TableDecorator()
    {
    }

    
    /**
     * @return the dialog
     */
    public AddEditDialog<E> getDialog()
    {
        return dialog;
    }


    /**
     * @param dialog the dialog to set
     */
    public void setDialog( AddEditDialog<E> dialog )
    {
        this.dialog = dialog;
    }

    
    /**
     * Compare two elements.
     * @param e1 The first element
     * @param e2 The second element
     * @return A negative value when e1 < e2, positive when e1 > e2, and 0 when e1 = e2
     */
    public abstract int compare( E e1, E e2 );
}
