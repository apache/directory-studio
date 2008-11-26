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
package org.apache.directory.studio.schemaeditor.view.widget;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.difference.AliasDifference;
import org.apache.directory.studio.schemaeditor.model.difference.ClassTypeDifference;
import org.apache.directory.studio.schemaeditor.model.difference.CollectiveDifference;
import org.apache.directory.studio.schemaeditor.model.difference.DescriptionDifference;
import org.apache.directory.studio.schemaeditor.model.difference.EqualityDifference;
import org.apache.directory.studio.schemaeditor.model.difference.MandatoryATDifference;
import org.apache.directory.studio.schemaeditor.model.difference.NoUserModificationDifference;
import org.apache.directory.studio.schemaeditor.model.difference.ObsoleteDifference;
import org.apache.directory.studio.schemaeditor.model.difference.OptionalATDifference;
import org.apache.directory.studio.schemaeditor.model.difference.OrderingDifference;
import org.apache.directory.studio.schemaeditor.model.difference.PropertyDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SingleValueDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SubstringDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SuperiorATDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SuperiorOCDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SyntaxDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SyntaxLengthDifference;
import org.apache.directory.studio.schemaeditor.model.difference.UsageDifference;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;


/**
 * This class implements the LabelProvider for the DifferencesWidget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferencesWidgetPropertiesLabelProvider extends LabelProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element )
    {
        if ( element instanceof PropertyDifference )
        {
            PropertyDifference propertyDifference = ( PropertyDifference ) element;
            switch ( propertyDifference.getType() )
            {
                case ADDED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_PROPERTY_ADD );

                case MODIFIED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_PROPERTY_MODIFY );
                case REMOVED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_PROPERTY_REMOVE );
            }
        }

        // Default
        return super.getImage( element );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element )
    {
        if ( element instanceof AliasDifference )
        {
            AliasDifference diff = ( AliasDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddAlias" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.RemoveAlias" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof ClassTypeDifference )
        {
            ClassTypeDifference diff = ( ClassTypeDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedClassType" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof CollectiveDifference )
        {
            CollectiveDifference diff = ( CollectiveDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedCollective" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof DescriptionDifference )
        {
            DescriptionDifference diff = ( DescriptionDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedDescription" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedDescription" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.RemovedDescription" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof EqualityDifference )
        {
            EqualityDifference diff = ( EqualityDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedMatchingRule" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedMatchingRule" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.RemovedMatchingRule" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof MandatoryATDifference )
        {
            MandatoryATDifference diff = ( MandatoryATDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedMandatoryAttributeType" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages
                                .getString( "DifferencesWidgetPropertiesLabelProvider.RemovedMandatoryAttributeType" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof NoUserModificationDifference )
        {
            NoUserModificationDifference diff = ( NoUserModificationDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages
                                .getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedNoUserModificationValue" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof ObsoleteDifference )
        {
            ObsoleteDifference diff = ( ObsoleteDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedObsoleteValue" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof OptionalATDifference )
        {
            OptionalATDifference diff = ( OptionalATDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedOptionalAttributeType" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages
                                .getString( "DifferencesWidgetPropertiesLabelProvider.RemovedOptionalAttributeType" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof OrderingDifference )
        {
            OrderingDifference diff = ( OrderingDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedOrderingMatchingRule" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages
                                .getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedOrderingMatchingRule" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.RemovedOrderingMatchingRule" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof SingleValueDifference )
        {
            SingleValueDifference diff = ( SingleValueDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedSingleValueValue" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof SubstringDifference )
        {
            SubstringDifference diff = ( SubstringDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedSubstringMatchingRule" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages
                                .getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedSubstringMatchingRule" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages
                                .getString( "DifferencesWidgetPropertiesLabelProvider.RemovedSubstringMatchingRule" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof SuperiorATDifference )
        {
            SuperiorATDifference diff = ( SuperiorATDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedSuperior" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedSuperior" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.RemovedSuperior" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof SuperiorOCDifference )
        {
            SuperiorOCDifference diff = ( SuperiorOCDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedSuperior" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.RemovedSuperior" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof SyntaxDifference )
        {
            SyntaxDifference diff = ( SyntaxDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedSyntax" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedSyntax" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.RemovedSyntax" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof SyntaxLengthDifference )
        {
            SyntaxLengthDifference diff = ( SyntaxLengthDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.AddedSyntaxLength" ), new Object[] { diff.getNewValue() } ); //$NON-NLS-1$
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedSyntaxLength" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
                case REMOVED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.RemovedSyntaxLength" ), new Object[] { diff.getOldValue() } ); //$NON-NLS-1$
            }
        }
        else if ( element instanceof UsageDifference )
        {
            UsageDifference diff = ( UsageDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return NLS
                        .bind(
                            Messages.getString( "DifferencesWidgetPropertiesLabelProvider.ModifiedUsage" ), new Object[] { diff.getNewValue(), diff.getOldValue() } ); //$NON-NLS-1$
            }
        }

        // Default
        return super.getText( element );
    }
}
