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

package org.apache.directory.ldapstudio.dsmlv2.reponse;


/**
 * This Class helps to get resultCodeDesc for a ResultCode of a LdapResult.
 */
public class LdapResultEnum
{
    private static String[] resultStrings;

    static
    {
        resultStrings = new String[91];

        resultStrings[0] = "success";
        resultStrings[1] = "operationsError";
        resultStrings[2] = "protocolError";
        resultStrings[3] = "timeLimitExceeded";
        resultStrings[4] = "sizeLimitExceeded";
        resultStrings[5] = "compareFalse";
        resultStrings[6] = "compareTrue";
        resultStrings[7] = "authMethodNotSupported";
        resultStrings[8] = "strongAuthRequired";
        resultStrings[9] = "reserved";
        resultStrings[10] = "referral";
        resultStrings[11] = "adminLimitExceeded";
        resultStrings[12] = "unavailableCriticalExtension";
        resultStrings[13] = "confidentialityRequired";
        resultStrings[14] = "saslBindInProgress";

        resultStrings[15] = "unused";

        resultStrings[16] = "noSuchAttribute";
        resultStrings[17] = "undefinedAttributeType";
        resultStrings[18] = "inappropriateMatching";
        resultStrings[19] = "constraintViolation";
        resultStrings[20] = "attributeOrValueExists";
        resultStrings[21] = "invalidAttributeSyntax";

        resultStrings[22] = "unused";
        resultStrings[23] = "unused";
        resultStrings[24] = "unused";
        resultStrings[25] = "unused";
        resultStrings[26] = "unused";
        resultStrings[27] = "unused";
        resultStrings[28] = "unused";
        resultStrings[29] = "unused";
        resultStrings[30] = "unused";
        resultStrings[31] = "unused";

        resultStrings[32] = "noSuchObject";
        resultStrings[33] = "aliasProblem";
        resultStrings[34] = "invalidDNSyntax";
        resultStrings[35] = "reserved for undefined isLeaf";
        resultStrings[36] = "aliasDereferencingProblem";

        resultStrings[37] = "unused";
        resultStrings[38] = "unused";
        resultStrings[39] = "unused";
        resultStrings[40] = "unused";
        resultStrings[41] = "unused";
        resultStrings[42] = "unused";
        resultStrings[43] = "unused";
        resultStrings[44] = "unused";
        resultStrings[45] = "unused";
        resultStrings[46] = "unused";
        resultStrings[47] = "unused";

        resultStrings[48] = "inappropriateAuthentication";
        resultStrings[49] = "invalidCredentials";
        resultStrings[50] = "insufficientAccessRights";
        resultStrings[51] = "busy";
        resultStrings[52] = "unavailable";
        resultStrings[53] = "unwillingToPerform";
        resultStrings[54] = "loopDetect";

        resultStrings[55] = "unused";
        resultStrings[56] = "unused";
        resultStrings[57] = "unused";
        resultStrings[58] = "unused";
        resultStrings[59] = "unused";
        resultStrings[60] = "unused";
        resultStrings[61] = "unused";
        resultStrings[62] = "unused";
        resultStrings[63] = "unused";

        resultStrings[64] = "namingViolation";
        resultStrings[65] = "objectClassViolation";
        resultStrings[66] = "notAllowedOnNonLeaf";
        resultStrings[67] = "notAllowedOnRDN";
        resultStrings[68] = "entryAlreadyExists";
        resultStrings[69] = "objectClassModsProhibited";

        resultStrings[70] = "reserved for CLDAP";

        resultStrings[71] = "affectsMultipleDSAs";

        resultStrings[72] = "unused";
        resultStrings[73] = "unused";
        resultStrings[74] = "unused";
        resultStrings[75] = "unused";
        resultStrings[76] = "unused";
        resultStrings[77] = "unused";
        resultStrings[78] = "unused";
        resultStrings[79] = "unused";

        resultStrings[80] = "other";

        resultStrings[81] = "reserved for APIs";
        resultStrings[82] = "reserved for APIs";
        resultStrings[83] = "reserved for APIs";
        resultStrings[84] = "reserved for APIs";
        resultStrings[85] = "reserved for APIs";
        resultStrings[86] = "reserved for APIs";
        resultStrings[87] = "reserved for APIs";
        resultStrings[88] = "reserved for APIs";
        resultStrings[89] = "reserved for APIs";
        resultStrings[90] = "reserved for APIs";
    }


    /**
     * Return the String description of a given result code 
     * @param resultCode a result code
     * @return the String description corresponding to the result code
     */
    public static String getResultCodeDescr( int resultCode )
    {
        if ( resultCode > 90 )
        {
            return "unknown";
        }
        else
        {
            return resultStrings[resultCode];
        }
    }
}
