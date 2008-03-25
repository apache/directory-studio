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


import org.apache.directory.shared.ldap.schema.syntax.LdapSyntaxDescription;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;


/**
 * The LdapSyntaxDescriptionPage displays a list with all
 * syntax descriptions and hosts the detail page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapSyntaxDescriptionPage extends SchemaPage
{

    /**
     * Creates a new instance of LdapSyntaxDescriptionPage.
     *
     * @param schemaBrowser the schema browser
     */
    public LdapSyntaxDescriptionPage( SchemaBrowser schemaBrowser )
    {
        super( schemaBrowser );
    }


    /**
     * {@inheritDoc}
     */
    protected String getTitle()
    {
        return "Syntaxes";
    }


    /**
     * {@inheritDoc}
     */
    protected String getFilterDescription()
    {
        return "Please select a syntax. Enter a filter to restrict the list.";
    }


    /**
     * {@inheritDoc}
     */
    protected IStructuredContentProvider getContentProvider()
    {
        return new LSDContentProvider();
    }


    /**
     * {@inheritDoc}
     */
    protected ITableLabelProvider getLabelProvider()
    {
        return new LSDLabelProvider();
    }


    /**
     * {@inheritDoc}
     */
    protected ViewerSorter getSorter()
    {
        return new LSDViewerSorter();
    }


    /**
     * {@inheritDoc}
     */
    protected ViewerFilter getFilter()
    {
        return new LSDViewerFilter();
    }


    /**
     * {@inheritDoc}
     */
    protected SchemaDetailsPage getDetailsPage()
    {
        return new LdapSyntaxDescriptionDetailsPage( this, this.toolkit );
    }

    /**
     * The content provider used by the viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    class LSDContentProvider implements IStructuredContentProvider
    {
        /**
         * {@inheritDoc}
         */
        public Object[] getElements( Object inputElement )
        {
            if ( inputElement instanceof Schema )
            {
                Schema schema = ( Schema ) inputElement;
                if ( schema != null && schema.getLdapSyntaxDescriptions() != null )
                {
                    return schema.getLdapSyntaxDescriptions().toArray();
                }
            }
            return new Object[0];
        }


        /**
         * {@inheritDoc}
         */
        public void dispose()
        {
        }


        /**
         * {@inheritDoc}
         */
        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    /**
     * The label provider used by the viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    class LSDLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        /**
         * {@inheritDoc}
         */
        public String getColumnText( Object obj, int index )
        {
            if ( obj instanceof LdapSyntaxDescription )
            {
                return SchemaUtils.toString( ( LdapSyntaxDescription ) obj );
            }
            return obj.toString();
        }


        /**
         * {@inheritDoc}
         */
        public Image getColumnImage( Object obj, int index )
        {
            return null;
        }
    }

    /**
     * The sorter used by the viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    class LSDViewerSorter extends ViewerSorter
    {
        /**
         * {@inheritDoc}
         */
        public int compare( Viewer viewer, Object e1, Object e2 )
        {
            if ( e1 instanceof LdapSyntaxDescription )
            {
                e1 = SchemaUtils.toString( ( LdapSyntaxDescription ) e1 );
            }
            if ( e2 instanceof LdapSyntaxDescription )
            {
                e2 = SchemaUtils.toString( ( LdapSyntaxDescription ) e2 );
            }
            return e1.toString().compareTo( e2.toString() );
        }
    }

    /**
     * The filter used by the viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    class LSDViewerFilter extends ViewerFilter
    {
        /**
         * {@inheritDoc}
         */
        public boolean select( Viewer viewer, Object parentElement, Object element )
        {
            if ( element instanceof LdapSyntaxDescription )
            {
                LdapSyntaxDescription lsd = ( LdapSyntaxDescription ) element;
                boolean matched = SchemaUtils.toString( lsd ).toLowerCase()
                    .indexOf( filterText.getText().toLowerCase() ) != -1
                    || lsd.getNumericOid().toLowerCase().indexOf( filterText.getText().toLowerCase() ) != -1;
                return matched;
            }
            return false;
        }
    }

}
