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
package org.apache.directory.studio.apacheds.schemaeditor.view.widget;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ClassTypeDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.CollectiveDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.DescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.EqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.MandatoryATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.NoUserModificationDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ObsoleteDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.OptionalATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.OrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.PropertyDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.SingleValueDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.SubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.SuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.SuperiorOCDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.SyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.SyntaxLengthDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.UsageDifference;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


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
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_DIFFERENCE_PROPERTY_ADD ).createImage();

                case MODIFIED:
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_DIFFERENCE_PROPERTY_MODIFY ).createImage();
                case REMOVED:
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_DIFFERENCE_PROPERTY_REMOVE ).createImage();
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
                    return "Added alias: '" + diff.getNewValue() + "'.";
                case REMOVED:
                    return "Removed alias: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof ClassTypeDifference )
        {
            ClassTypeDifference diff = ( ClassTypeDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return "Modified class type to : '" + diff.getNewValue() + "' (was '" + diff.getOldValue() + "').";
            }
        }
        else if ( element instanceof CollectiveDifference )
        {
            CollectiveDifference diff = ( CollectiveDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return "Modified 'collective' value to : '" + diff.getNewValue() + "' (was '" + diff.getOldValue()
                        + "').";
            }
        }
        else if ( element instanceof DescriptionDifference )
        {
            DescriptionDifference diff = ( DescriptionDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added description: '" + diff.getNewValue() + "'.";
                case MODIFIED:
                    return "Modified description to : '" + diff.getNewValue() + "' (was '" + diff.getOldValue() + "').";
                case REMOVED:
                    return "Removed description: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof EqualityDifference )
        {
            EqualityDifference diff = ( EqualityDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added equality matching rule: '" + diff.getNewValue() + "'.";
                case MODIFIED:
                    return "Modified equality matching rule to : '" + diff.getNewValue() + "' (was '"
                        + diff.getOldValue() + "').";
                case REMOVED:
                    return "Removed equality matching rule: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof MandatoryATDifference )
        {
            MandatoryATDifference diff = ( MandatoryATDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added mandatory attribute type: '" + diff.getNewValue() + "'.";
                case REMOVED:
                    return "Removed mandatory attribute type: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof NoUserModificationDifference )
        {
            NoUserModificationDifference diff = ( NoUserModificationDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return "Modified 'no user modification' value to : '" + diff.getNewValue() + "' (was '"
                        + diff.getOldValue() + "').";
            }
        }
        else if ( element instanceof ObsoleteDifference )
        {
            ObsoleteDifference diff = ( ObsoleteDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return "Modified 'obsolete' value to : '" + diff.getNewValue() + "' (was '" + diff.getOldValue()
                        + "').";
            }
        }
        else if ( element instanceof OptionalATDifference )
        {
            OptionalATDifference diff = ( OptionalATDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added optional attribute type: '" + diff.getNewValue() + "'.";
                case REMOVED:
                    return "Removed optional attribute type: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof OrderingDifference )
        {
            OrderingDifference diff = ( OrderingDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added ordering matching rule: '" + diff.getNewValue() + "'.";
                case MODIFIED:
                    return "Modified ordering matching rule to : '" + diff.getNewValue() + "' (was '"
                        + diff.getOldValue() + "').";
                case REMOVED:
                    return "Removed ordering matching rule: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof SingleValueDifference )
        {
            SingleValueDifference diff = ( SingleValueDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return "Modified 'single value' value to : '" + diff.getNewValue() + "' (was '"
                        + diff.getOldValue() + "').";
            }
        }
        else if ( element instanceof SubstringDifference )
        {
            SubstringDifference diff = ( SubstringDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added substring matching rule: '" + diff.getNewValue() + "'.";
                case MODIFIED:
                    return "Modified substring matching rule to : '" + diff.getNewValue() + "' (was '"
                        + diff.getOldValue() + "').";
                case REMOVED:
                    return "Removed substring matching rule: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof SuperiorATDifference )
        {
            SuperiorATDifference diff = ( SuperiorATDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added superior: '" + diff.getNewValue() + "'.";
                case MODIFIED:
                    return "Modified superior to: '" + diff.getNewValue() + "' (was '" + diff.getOldValue() + "').";
                case REMOVED:
                    return "Removed superior: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof SuperiorOCDifference )
        {
            SuperiorOCDifference diff = ( SuperiorOCDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added superior: '" + diff.getNewValue() + "'.";
                case REMOVED:
                    return "Removed superior: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof SyntaxDifference )
        {
            SyntaxDifference diff = ( SyntaxDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added syntax: '" + diff.getNewValue() + "'.";
                case MODIFIED:
                    return "Modified syntax to: '" + diff.getNewValue() + "' (was '" + diff.getOldValue() + "').";
                case REMOVED:
                    return "Removed syntax: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof SyntaxLengthDifference )
        {
            SyntaxLengthDifference diff = ( SyntaxLengthDifference ) element;
            switch ( diff.getType() )
            {
                case ADDED:
                    return "Added syntax length: '" + diff.getNewValue() + "'.";
                case MODIFIED:
                    return "Modified syntax length to: '" + diff.getNewValue() + "' (was '" + diff.getOldValue()
                        + "').";
                case REMOVED:
                    return "Removed syntax length: '" + diff.getOldValue() + "'.";
            }
        }
        else if ( element instanceof UsageDifference )
        {
            UsageDifference diff = ( UsageDifference ) element;
            switch ( diff.getType() )
            {
                case MODIFIED:
                    return "Modified usage to: '" + diff.getNewValue() + "' (was '" + diff.getOldValue() + "').";
            }
        }

        // Default
        return super.getText( element );
    }
}
