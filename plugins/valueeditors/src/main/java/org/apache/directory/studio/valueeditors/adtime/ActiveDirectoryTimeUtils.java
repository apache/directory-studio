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

package org.apache.directory.studio.valueeditors.adtime;


import java.util.Calendar;


/**
 * Helper class to work with Active Directory time values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ActiveDirectoryTimeUtils
{
    /**
     * Converts the given Active Directory time value to an equivalent calendar.
     *
     * @param adTimeValue the Active Directory time value
     * @return the equivalent calendar
     */
    public static Calendar convertToCalendar( long adTimeValue )
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( ( adTimeValue - 116444736000000000L ) / 10000L );
        return calendar;
    }


    /**
     * Converts the given calendar to the equivalent Active Directory time.
     *
     * @param calendar the calendard
     * @return the equivalent Active Directory time
     */
    public static long convertToActiveDirectoryTime( Calendar calendar )
    {
        return ( ( calendar.getTime().getTime() * 10000L ) + 116444736000000000L );
    }
}
