package org.apache.directory.studio.valueeditors.time;


import java.util.ArrayList;
import java.util.List;


/**
 * This enum represents a GeneralizedTime time zones.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public enum GeneralizedTimeTimeZones
{
    UTC_MINUS_12 ( "UTC-12", -1 * ( 12 * 60 * 60 * 1000 ) ),
    UTC_MINUS_11 ( "UTC-11", -1 * ( 11 * 60 * 60 * 1000 ) ),
    UTC_MINUS_10 ( "UTC-10", -1 * ( 10 * 60 * 60 * 1000 ) ),
    UTC_MINUS_9_30 ( "UTC-9:30", -1 * ( ( ( 9 * 60 ) + 30 ) * 60 * 1000 ) ),
    UTC_MINUS_9 ( "UTC-9", -1 * ( 9 * 60 * 60 * 1000 ) ),
    UTC_MINUS_8 ( "UTC-8", -1 * ( 8 * 60 * 60 * 1000 ) ),
    UTC_MINUS_7 ( "UTC-7", -1 * ( 7 * 60 * 60 * 1000 ) ),
    UTC_MINUS_6 ( "UTC-6", -1 * ( 6 * 60 * 60 * 1000 ) ),
    UTC_MINUS_5 ( "UTC-5", -1 * ( 5 * 60 * 60 * 1000 ) ),
    UTC_MINUS_4_30 ( "UTC-4:30", -1 * ( ( ( 4 * 60 ) + 30 ) * 60 * 1000 ) ),
    UTC_MINUS_4 ( "UTC-4", -1 * ( 4 * 60 * 60 * 1000 ) ),
    UTC_MINUS_3_30 ( "UTC-3:30", -1 * ( ( ( 3 * 60 ) + 30 ) * 60 * 1000 ) ),
    UTC_MINUS_3 ( "UTC-3", -1 * ( 3 * 60 * 60 * 1000 ) ),
    UTC_MINUS_2 ( "UTC-2", -1 * ( 2 * 60 * 60 * 1000 ) ),
    UTC_MINUS_1 ( "UTC-1", -1 * ( 1 * 60 * 60 * 1000 ) ), 
    UTC ( "UTC", 0 ),
    UTC_PLUS_1 ( "UTC+1", 1 * 60 * 60 * 1000 ), 
    UTC_PLUS_2 ( "UTC+2", 2 * 60 * 60 * 1000 ), 
    UTC_PLUS_3 ( "UTC+3", 3 * 60 * 60 * 1000 ), 
    UTC_PLUS_3_30 ( "UTC+3:30", ( ( 3 * 60 ) + 30 ) * 60 * 1000 ),
    UTC_PLUS_4 ( "UTC+4", 4 * 60 * 60 * 1000 ), 
    UTC_PLUS_4_30 ( "UTC+4:30", ( ( 4 * 60 ) + 30 ) * 60 * 1000 ),
    UTC_PLUS_5 ( "UTC+5", 5 * 60 * 60 * 1000 ), 
    UTC_PLUS_5_30 ( "UTC+5:30", ( ( 5 * 60 ) + 30 ) * 60 * 1000 ),
    UTC_PLUS_5_45 ( "UTC+5:45", ( ( 5 * 60 ) + 45 ) * 60 * 1000 ),
    UTC_PLUS_6 ( "UTC+6", 6 * 60 * 60 * 1000 ), 
    UTC_PLUS_6_30 ( "UTC+6:30", ( ( 6 * 60 ) + 30 ) * 60 * 1000 ),
    UTC_PLUS_7 ( "UTC+7", 7 * 60 * 60 * 1000 ), 
    UTC_PLUS_8 ( "UTC+8", 8 * 60 * 60 * 1000 ), 
    UTC_PLUS_8_45 ( "UTC+8:45", ( ( 8 * 60 ) + 45 ) * 60 * 1000 ),
    UTC_PLUS_9 ( "UTC+9", 9 * 60 * 60 * 1000 ), 
    UTC_PLUS_9_30 ( "UTC+9:30", ( ( 9 * 60 ) + 30 ) * 60 * 1000 ),
    UTC_PLUS_10 ( "UTC+10", 10 * 60 * 60 * 1000 ), 
    UTC_PLUS_10_30 ( "UTC+10:30", ( ( 10 * 60 ) + 30 ) * 60 * 1000 ),
    UTC_PLUS_11 ( "UTC+11", 11 * 60 * 60 * 1000 ), 
    UTC_PLUS_11_30 ( "UTC+11:30", ( ( 11 * 60 ) + 30 ) * 60 * 1000 ),
    UTC_PLUS_12 ( "UTC+12", 12 * 60 * 60 * 1000 ), 
    UTC_PLUS_12_45 ( "UTC+12:45", ( ( 12 * 60 ) + 45 ) * 60 * 1000 ),
    UTC_PLUS_13 ( "UTC+13", 13 * 60 * 60 * 1000 ), 
    UTC_PLUS_14 ( "UTC+14", 14 * 60 * 60 * 1000 );

    /** The name */
    private String name;

    /** The raw offset */
    private int rawOffset;


    /**
     * Creates a new instance of GeneralizedTimeTimeZones.
     *
     * @param name
     *      the name
     * @param rawOffset
     *      the rawOffset
     */
    GeneralizedTimeTimeZones( String name, int rawOffset )
    {
        this.name = name;
        this.rawOffset = rawOffset;
    }


    /**
     * Gets the name.
     *
     * @return
     *      the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Gets the raw offset.
     *
     * @return
     *      the raw offset
     */
    public int getRawOffset()
    {
        return rawOffset;
    }


    /**
     * Get all the time zones.
     *
     * @return
     *      a {@link List} containing all the time zones
     */
    public static List<GeneralizedTimeTimeZones> getAllTimezones()
    {
        List<GeneralizedTimeTimeZones> timezones = new ArrayList<GeneralizedTimeTimeZones>();

        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_12 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_11 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_10 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_9_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_9 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_8 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_7 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_6 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_5 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_4_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_4 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_3_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_3 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_2 );
        timezones.add( GeneralizedTimeTimeZones.UTC_MINUS_1 );
        timezones.add( GeneralizedTimeTimeZones.UTC );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_1 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_2 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_3 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_3_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_4 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_4_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_5 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_5_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_5_45 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_6 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_6_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_7 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_8 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_8_45 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_9 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_9_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_10 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_10_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_11 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_11_30 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_12 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_12_45 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_13 );
        timezones.add( GeneralizedTimeTimeZones.UTC_PLUS_14 );

        return timezones;
    }
}