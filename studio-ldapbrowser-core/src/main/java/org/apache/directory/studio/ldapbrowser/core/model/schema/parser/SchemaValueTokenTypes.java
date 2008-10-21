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

// $ANTLR 2.7.5 (20050128): "schemavalue.g" -> "SchemaValueLexer.java"$
package org.apache.directory.studio.ldapbrowser.core.model.schema.parser;




public interface SchemaValueTokenTypes
{
    int EOF = 1;

    int NULL_TREE_LOOKAHEAD = 3;

    int WHSP = 4;

    int LPAR = 5;

    int RPAR = 6;

    int QUOTE = 7;

    int DOLLAR = 8;

    int LBRACKET = 9;

    int RBRACKET = 10;

    int LEN = 11;

    int DIGIT = 12;

    int NUMERICOID = 13;

    int DESCR = 14;
}
