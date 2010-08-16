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


import org.apache.directory.shared.ldap.schema.parsers.MatchingRuleUseDescription;
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
 * The MatchingRuleUseDescriptionPage displays a list with all
 * matching rule use descriptions and hosts the detail page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MatchingRuleUseDescriptionPage extends SchemaPage
{

    /**
     * 
     * Creates a new instance of MatchingRuleUseDescriptionPage.
     *
     * @param schemaBrowser the schema browser
     */
    public MatchingRuleUseDescriptionPage( SchemaBrowser schemaBrowser )
    {
        super( schemaBrowser );
    }


    /**
     * {@inheritDoc}
     */
    protected String getTitle()
    {
        return Messages.getString( "MatchingRuleUseDescriptionPage.MatchingRule" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected String getFilterDescription()
    {
        return Messages.getString( "MatchingRuleUseDescriptionPage.SelectMatchingRule" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected IStructuredContentProvider getContentProvider()
    {
        return new MRUDContentProvider();
    }


    /**
     * {@inheritDoc}
     */
    protected ITableLabelProvider getLabelProvider()
    {
        return new MRUDLabelProvider();
    }


    /**
     * {@inheritDoc}
     */
    protected ViewerSorter getSorter()
    {
        return new MRUDViewerSorter();
    }


    /**
     * {@inheritDoc}
     */
    protected ViewerFilter getFilter()
    {
        return new MRUDViewerFilter();
    }


    /**
     * {@inheritDoc}
     */
    protected SchemaDetailsPage getDetailsPage()
    {
        return new MatchingRuleUseDescriptionDetailsPage( this, this.toolkit );
    }

    /**
     * The content provider used by the viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    class MRUDContentProvider implements IStructuredContentProvider
    {
        /**
         * {@inheritDoc}
         */
        public Object[] getElements( Object inputElement )
        {
            if ( inputElement instanceof Schema )
            {
                Schema schema = ( Schema ) inputElement;
                if ( schema != null && schema.getMatchingRuleUseDescriptions() != null )
                {
                    return schema.getMatchingRuleUseDescriptions().toArray();
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
     */
    class MRUDLabelProvider extends LabelProvider implements ITableLabelProvider
    {
        /**
         * {@inheritDoc}
         */
        public String getColumnText( Object obj, int index )
        {
            if ( obj instanceof MatchingRuleUseDescription )
            {
                return SchemaUtils.toString( ( MatchingRuleUseDescription ) obj );
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
     */
    class MRUDViewerSorter extends ViewerSorter
    {
        /**
         * {@inheritDoc}
         */
        public int compare( Viewer viewer, Object e1, Object e2 )
        {
            if ( e1 instanceof MatchingRuleUseDescription )
            {
                e1 = SchemaUtils.toString( ( MatchingRuleUseDescription ) e1 );
            }
            if ( e2 instanceof MatchingRuleUseDescription )
            {
                e2 = SchemaUtils.toString( ( MatchingRuleUseDescription ) e2 );
            }
            return e1.toString().compareTo( e2.toString() );
        }
    }

    /**
     * The filter used by the viewer.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    class MRUDViewerFilter extends ViewerFilter
    {
        /**
         * {@inheritDoc}
         */
        public boolean select( Viewer viewer, Object parentElement, Object element )
        {
            if ( element instanceof MatchingRuleUseDescription )
            {
                MatchingRuleUseDescription mrud = ( MatchingRuleUseDescription ) element;
                boolean matched = SchemaUtils.toString( mrud ).toLowerCase().indexOf(
                    filterText.getText().toLowerCase() ) != -1
                    || mrud.getNumericOid().toLowerCase().indexOf( filterText.getText().toLowerCase() ) != -1;
                return matched;
            }
            return false;
        }
    }

}
