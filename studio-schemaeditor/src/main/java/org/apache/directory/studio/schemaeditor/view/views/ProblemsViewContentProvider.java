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
package org.apache.directory.studio.schemaeditor.view.views;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaChecker;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaError;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaWarning;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder.FolderType;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.ProblemsViewRoot;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaErrorWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWarningWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the ContentProvider for the ProblemsView.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProblemsViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        return getChildren( inputElement );
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        // Nothing to do.
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do.
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        List<TreeNode> children = null;

        if ( parentElement instanceof ProblemsViewRoot )
        {
            ProblemsViewRoot root = ( ProblemsViewRoot ) parentElement;

            if ( root.getChildren().isEmpty() )
            {
                SchemaChecker schemaChecker = Activator.getDefault().getSchemaChecker();

                if ( schemaChecker != null )
                {
                    SchemaError[] errors = schemaChecker.getErrors().toArray( new SchemaError[0] );
                    if ( !( errors.length == 0 ) )
                    {
                        Folder errorsFolder = new Folder( FolderType.ERROR, root );
                        root.addChild( errorsFolder );
                        for ( SchemaError error : errors )
                        {
                            errorsFolder.addChild( new SchemaErrorWrapper( error, errorsFolder ) );
                        }
                    }

                    SchemaWarning[] warnings = schemaChecker.getWarnings().toArray( new SchemaWarning[0] );
                    if ( !( warnings.length == 0 ) )
                    {
                        Folder warningsFolder = new Folder( FolderType.WARNING, root );
                        root.addChild( warningsFolder );
                        for ( SchemaWarning warning : warnings )
                        {
                            warningsFolder.addChild( new SchemaWarningWrapper( warning, warningsFolder ) );
                        }
                    }
                }
            }

            children = root.getChildren();
        }
        else if ( parentElement instanceof Folder )
        {
            Folder folder = ( Folder ) parentElement;

            children = folder.getChildren();
        }
        else if ( parentElement instanceof SchemaErrorWrapper )
        {
            children = new ArrayList<TreeNode>();
        }
        else if ( parentElement instanceof SchemaWarningWrapper )
        {
            children = new ArrayList<TreeNode>();
        }

        return children.toArray();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        if ( element instanceof TreeNode )
        {
            return ( ( TreeNode ) element ).getParent();
        }

        // Default
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof TreeNode )
        {
            return ( ( TreeNode ) element ).hasChildren();
        }

        // Default
        return false;
    }
}
