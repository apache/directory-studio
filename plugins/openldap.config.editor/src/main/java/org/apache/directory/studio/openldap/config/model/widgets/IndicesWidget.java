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
package org.apache.directory.studio.openldap.config.model.widgets;



import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.config.editor.wrappers.DbIndexDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.DbIndexWrapper;


/**
 * The IndicesWidget provides a table viewer to add/edit/remove an index :
 * <pre>
 * Attributes
 * +----------------------------+
 * | Index 1                    | (Add...)
 * | Index 2                    | (Edit...)
 * |                            | (Delete)
 * +----------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class IndicesWidget extends TableWidget<DbIndexWrapper>
{
    /**
     * Creates a new instance of IndicesWidget.
     *
     * @param connection the browserConnection
     */
    public IndicesWidget( IBrowserConnection browserConnection )
    {
        super( new DbIndexDecorator( null, browserConnection ) );
    }
}
