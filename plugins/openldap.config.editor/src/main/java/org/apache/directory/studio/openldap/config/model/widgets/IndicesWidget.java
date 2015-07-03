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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.AbstractWidget;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.editor.dialogs.DbIndexDialog;
import org.apache.directory.studio.openldap.config.editor.wrappers.DbIndexDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.DbIndexWrapper;
import org.apache.directory.studio.openldap.config.model.OlcDbIndex;


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
