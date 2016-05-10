///@cond DEV
package com.prim8software.app.prim8datalogger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// Part of Prim8 Data Logger
// Copyright 2016, Scott Johnson, Prim8 Software (scott.johnson@prim8software.com)

public class Common {
    public static Integer CastLongToInteger(long longValue) {
        if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE)
            throw new IllegalArgumentException(longValue
                    + " cannot be safely cast to integer!");
        return (int) longValue;
    }

    private static long TimeOffset()
    {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2012);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }

    private static String LongDurationToString(long milliseconds)
    {
        final java.util.Date date = new java.util.Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    private static long StringToLongDuration(String duration)
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            return format.parse(duration).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String LongTimeToString(long milliseconds)
    {
        java.util.Date date = new java.util.Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static String CurrentToDateString()
    {
        long milliseconds = System.currentTimeMillis();
        java.util.Date date = new java.util.Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    private static String LongTimeToShortString(long milliseconds)
    {
        java.util.Date date = new java.util.Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }

    private static long StringToLongTime(String dateString)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(0);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    private static String[] LongTimeToStringArray(long milliseconds)
    {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds);

        String timeString = String.format("%02d:%02d:%02d",
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                c.get(Calendar.SECOND));

        return new String[] {Integer.toString(c.get(Calendar.YEAR)),
                Integer.toString(c.get(Calendar.MONTH) + 1),
                Integer.toString(c.get(Calendar.DAY_OF_MONTH)),
                timeString};
    }

    private static long StringArrayToLongTime(String[] stringArray)
    {
        if(stringArray.length != 4)
            throw new IllegalArgumentException("StringArray length for time must be length 4 (year, month, day, HH:mm:ss)!");

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Integer.parseInt(stringArray[0]));
        c.set(Calendar.MONTH, Integer.parseInt(stringArray[1]) - 1);
        c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(stringArray[2]));
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);

        String regex = "\\:";
        String[] timeArray = stringArray[3].split(regex);
        if(stringArray.length < 1)
            throw new IllegalArgumentException("StringArray length for HH:mm:ss must be at least length 1!");

        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        if(timeArray.length >= 1)
        {
            c.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
            if(timeArray.length >= 2)
            {
                c.set(Calendar.SECOND, Integer.parseInt(timeArray[2]));
            }
        }
        return c.getTimeInMillis();
    }

    public static String[] TimeHeadersForStringArray()
    {
        return new String[] {"Year","Month","Day","Time"};
    }

    public static Integer LongTimeToTime(long time)
    {
        long ts1 = time;
        ts1 -= TimeOffset();
        ts1 /= 1000;
        return CastLongToInteger(ts1);
    }

    public static Integer CurrentTime()
    {
        return LongTimeToTime(System.currentTimeMillis());
    }

    public static long TimeToLongTime(int time)
    {
        long ts1 = time;
        ts1 *= 1000;
        ts1 += TimeOffset();
        return ts1;
    }

    public static int LongDurationToDuration(long duration)
    {
        long dt = duration / 1000;
        return CastLongToInteger(dt);
    }

    public static long DurationToLongDuration(int duration)
    {
        long dt = duration;
        dt *= 1000;
        return dt;
    }

    public static String TimeToString(Integer ts)
    {
        long ts1 = TimeToLongTime(ts);
        return LongTimeToString(ts1);
    }

    public static String TimeToShortString(Integer ts)
    {
        long ts1 = TimeToLongTime(ts);
        return LongTimeToShortString(ts1);
    }

    public static Integer StringToTime(String dateString)
    {
        long ts = StringToLongTime(dateString);
        return LongTimeToTime(ts);
    }

    public static Integer StringArrayToTime(String[] dateString)
    {
        long ts = StringArrayToLongTime(dateString);
        return LongTimeToTime(ts);
    }

    public static String DurationToString(Integer ts)
    {
        long ts1 = DurationToLongDuration(ts);
        return LongDurationToString(ts1);
    }

    public static Integer StringToDuration(String duration)
    {
        long ts = StringToLongDuration(duration);
        return LongDurationToDuration(ts);
    }

    public static String[] TimeToStringArray(Integer ts)
    {
        long ts1 = TimeToLongTime(ts);
        return LongTimeToStringArray(ts1);
    }

    public static String TimeToCSVString(Integer ts)
    {
        String[] string = TimeToStringArray(ts);
        return FormattedArray(string, ",");
    }

    public static String FormattedArray(String[] entry, CharSequence delimiter)
    {
        String write = "";
        for(int i = 0; i < entry.length; i++)
        {
            if(entry[i]==null)
            {
                if(i < entry.length - 1)
                    write += delimiter;
                continue;
            }
            //add quotes?
            {
                Boolean addQuotes = entry[i].contains(delimiter);
                if(addQuotes)
                {
                    write += "\"" + entry[i] + "\"";
                }
                else
                {
                    write += entry[i];
                }
            }
            if(i < entry.length - 1)
            {
                write += delimiter;
            }
        }
        write += "\r\n";
        return write;
    }

    public static String[] ParsedString(String ret, Character delimiter)
    {
        if(ret.contains("\""))
        {
            Boolean literalOn = false;
            int lastIndex = -1;
            ArrayList<String> fields = new ArrayList<String>();
            for(int i = 0; i < ret.length(); i++)
            {
                if(ret.charAt(i)=='\"')
                {
                    literalOn = !literalOn;
                }
                else if(ret.charAt(i) == delimiter && !literalOn)
                {
                    String addMe = ret.substring(lastIndex + 1, i);
                    if(addMe.length() > 2 && addMe.charAt(0)=='\"')
                    {
                        addMe = addMe.substring(1, addMe.length() - 1);
                    }
                    fields.add(addMe);
                    lastIndex = i;
                }
            }
            {
                String addMe = ret.substring(lastIndex + 1);
                if(addMe.length() > 2 && addMe.charAt(0)=='\"')
                {
                    addMe = addMe.substring(1, addMe.length() - 1);
                }
                fields.add(addMe);
            }
            return fields.toArray(new String[fields.size()]);
        }
        else
        {
            String regex = delimiter + "\\s*";
            return ret.split(regex);
        }
    }

    public static Integer IndexOf(String[] fields, String match)
    {
        for(int i = 0; i < fields.length; i++)
        {
            if(fields[i].trim().equalsIgnoreCase(match))
            {
                return i;
            }
        }
        return -1;
    }
}
///@endcond