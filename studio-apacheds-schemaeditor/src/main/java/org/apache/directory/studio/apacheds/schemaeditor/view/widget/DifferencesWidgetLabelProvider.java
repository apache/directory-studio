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
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractAddDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractModifyDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AbstractRemoveDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddAliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddMandatoryATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddOptionalATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSuperiorOCDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.AddSyntaxLengthDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyClassTypeDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyCollectiveDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyNoUserModificationDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyObsoleteDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySingleValueDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifySyntaxLengthDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.ModifyUsageDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveAliasDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveDescriptionDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveEqualityDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveMandatoryATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveOptionalATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveOrderingDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSubstringDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSuperiorATDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSuperiorOCDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSyntaxDifference;
import org.apache.directory.studio.apacheds.schemaeditor.model.difference.RemoveSyntaxLengthDifference;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the LabelProvider for the DifferencesWidget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferencesWidgetLabelProvider extends LabelProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element )
    {
        if ( element instanceof AbstractAddDifference )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_DIFFERENCE_ADD )
                .createImage();
        }
        else if ( element instanceof AbstractModifyDifference )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                PluginConstants.IMG_DIFFERENCE_MODIFY ).createImage();
        }
        else if ( element instanceof AbstractRemoveDifference )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                PluginConstants.IMG_DIFFERENCE_REMOVE ).createImage();
        }

        // Default
        return super.getImage( element );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element )
    {
        if ( element instanceof AbstractAddDifference )
        {
            if ( element instanceof AddAliasDifference )
            {
                return "Added a new alias: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddDescriptionDifference )
            {
                return "Added a description: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddEqualityDifference )
            {
                return "Added an equality matching rule: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddMandatoryATDifference )
            {
                return "Added a new mandatory attribute type: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddOptionalATDifference )
            {
                return "Added a new optional attribute type: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddOrderingDifference )
            {
                return "Added an ordering matching rule: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddSubstringDifference )
            {
                return "Added a substring matching rule: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddSuperiorATDifference )
            {
                return "Added a superior: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddSuperiorOCDifference )
            {
                return "Added a superior: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddSyntaxDifference )
            {
                return "Added a syntax: " + ( ( AbstractAddDifference ) element ).getValue();
            }
            else if ( element instanceof AddSyntaxLengthDifference )
            {
                return "Added a syntax length: " + ( ( AbstractAddDifference ) element ).getValue();
            }
        }
        else if ( element instanceof AbstractModifyDifference )
        {
            if ( element instanceof ModifyClassTypeDifference )
            {
                return "Modified the class type to : " + ( ( AbstractModifyDifference ) element ).getNewValue()
                    + " (was " + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifyCollectiveDifference )
            {
                return "Modified the 'collective' value to : " + ( ( AbstractModifyDifference ) element ).getNewValue()
                    + " (was " + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifyDescriptionDifference )
            {
                return "Modified the description to : " + ( ( AbstractModifyDifference ) element ).getNewValue()
                    + " (was " + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifyEqualityDifference )
            {
                return "Modified the equality matching rule to : "
                    + ( ( AbstractModifyDifference ) element ).getNewValue() + " (was "
                    + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifyNoUserModificationDifference )
            {
                return "Modified the 'no user modification' value to : "
                    + ( ( AbstractModifyDifference ) element ).getNewValue() + " (was "
                    + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifyObsoleteDifference )
            {
                return "Modified the 'obsolete' value to : " + ( ( AbstractModifyDifference ) element ).getNewValue()
                    + " (was " + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifyOrderingDifference )
            {
                return "Modified the ordering matching rule to : "
                    + ( ( AbstractModifyDifference ) element ).getNewValue() + " (was "
                    + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifySingleValueDifference )
            {
                return "Modified the 'single value' value to : "
                    + ( ( AbstractModifyDifference ) element ).getNewValue() + " (was "
                    + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifySubstringDifference )
            {
                return "Modified the substring matching rule to : "
                    + ( ( AbstractModifyDifference ) element ).getNewValue() + " (was "
                    + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifySuperiorATDifference )
            {
                return "Modified the superior to : " + ( ( AbstractModifyDifference ) element ).getNewValue()
                    + " (was " + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifySyntaxDifference )
            {
                return "Modified the syntax to : " + ( ( AbstractModifyDifference ) element ).getNewValue() + " (was "
                    + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifySyntaxLengthDifference )
            {
                return "Modified the syntax length to : " + ( ( AbstractModifyDifference ) element ).getNewValue()
                    + " (was " + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
            else if ( element instanceof ModifyUsageDifference )
            {
                return "Modified the usage to : " + ( ( AbstractModifyDifference ) element ).getNewValue() + " (was "
                    + ( ( AbstractModifyDifference ) element ).getOldValue() + ")";
            }
        }
        else if ( element instanceof AbstractRemoveDifference )
        {
            if ( element instanceof RemoveAliasDifference )
            {
                return "Removed the alias: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveDescriptionDifference )
            {
                return "Removed the description: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveEqualityDifference )
            {
                return "Removed the equality matching rule: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveMandatoryATDifference )
            {
                return "Removed the mandatory attribute type: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveOptionalATDifference )
            {
                return "Removed the optional attribute type: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveOrderingDifference )
            {
                return "Removed the ordering matching rule: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveSubstringDifference )
            {
                return "Removed the substring matching rule: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveSuperiorATDifference )
            {
                return "Removed the superior: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveSuperiorOCDifference )
            {
                return "Removed the superior: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveSyntaxDifference )
            {
                return "Removed the syntax: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
            else if ( element instanceof RemoveSyntaxLengthDifference )
            {
                return "Removed the syntax length: " + ( ( AbstractRemoveDifference ) element ).getValue();
            }
        }

        // Default
        return super.getText( element );
    }
}
