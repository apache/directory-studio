/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.widgets.composites;


import java.util.List;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclValueWithContext;
import org.apache.directory.studio.openldap.config.acl.model.AclAttribute;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClause;
import org.apache.directory.studio.openldap.config.acl.model.AclWhatClauseAttributes;
import org.apache.directory.studio.openldap.config.acl.widgets.AttributesWidget;


/**
 * The WhatClause Attribute form. It contains only the AttributeWidget :
 * 
 * <pre>
 * ...
 * | .--------------------------------------------------------. |
 * | | Attribute list :                                       | |
 * | | +-------------------------------------------+          | |
 * | | | abc                                       | (Add)    | |
 * | | | !def                                      | (Edit)   | |
 * | | | entry                                     | (Delete) | |
 * | | +-------------------------------------------+          | |
 * | | Val : [ ]  MatchingRule : [ ] Style : [--------------] | |
 * | | Value : [////////////////////////////////////////////] | |
 * | `--------------------------------------------------------' |
 * ...
 * </pre>
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class WhatClauseAttributesComposite extends AbstractClauseComposite
{
    /** The attributes widget */
    private AttributesWidget attributesWidget;

    /**
     * Create a WhatClauseAttributesComposite instance
     * <pre>
     * ...
     * | .--------------------------------------------------------. |
     * | | Attribute list :                                       | |
     * | | +-------------------------------------------+          | |
     * | | | abc                                       | (Add)    | |
     * | | | !def                                      | (Edit)   | |
     * | | | entry                                     | (Delete) | |
     * | | +-------------------------------------------+          | |
     * | | Val : [ ]  MatchingRule : [ ] Style : [--------------] | |
     * | | Value : [////////////////////////////////////////////] | |
     * | `--------------------------------------------------------' |
     * ...
     * </pre>
     * 
     * @param visualEditorComposite The parent composite
     * @param attributesSubComposite The Parent sub-composite
     * @param context The OpenLdapAclValueWithContext instance
     */
    public WhatClauseAttributesComposite( Composite visualEditorComposite, Composite attributesSubComposite, OpenLdapAclValueWithContext context )
    {
        super( context, visualEditorComposite );
        Composite whatComposite = BaseWidgetUtils.createColumnContainer( attributesSubComposite, 2, 1 );
        
        // Create the Attributes clause if it does not already exist
        AclWhatClause aclWhatClause = context.getAclItem().getWhatClause();
        
        if ( aclWhatClause.getAttributesClause() == null )
        {
            aclWhatClause.setAttributesClause( new AclWhatClauseAttributes() );
        }

        // The Attribute widget
        BaseWidgetUtils.createLabel( whatComposite, "", 1 );
        attributesWidget = new AttributesWidget();
        attributesWidget.createWidget( context, whatComposite, connection, aclWhatClause.getAttributesClause() );
    }
}
