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
package org.apache.directory.studio.templateeditor.model.widgets;


/**
 * This enum contains all the possible values for the alignment of a widget.
 * <ul>
 *      <li>NONE, for no alignment (usually used as default value)</li>
 *      <li>BEGINNING, for alignment on the left when used horizontally and 
 *      alignment on the top when used vertically</li>
 *      <li>CENTER, for centered alignment when used horizontally and vertically</li>
 *      <li>END, for alignment on the right when used horizontally and 
 *      alignment on the bottom when used vertically</li>
 *      <li>FILL, for alignment taking the whole space when used horizontally and 
 *      vertically</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum WidgetAlignment
{
    NONE, BEGINNING, CENTER, END, FILL
}
