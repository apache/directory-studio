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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif.text;


import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;


public class LdifPartitionScanner extends RuleBasedPartitionScanner
{

    public final static String LDIF_RECORD = "__ldif_record";


    public LdifPartitionScanner()
    {
        IToken record = new Token( LDIF_RECORD );

        IPredicateRule[] rules = new IPredicateRule[1];
        rules[0] = new LdifRecordRule( record );

        setPredicateRules( rules );
    }


    public int read()
    {
        return super.read();
        // try {
        // return super.read();
        // } finally {
        // fColumn = UNDEFINED;
        // }
    }

}
