package snsystems.obd.classes;

import android.util.Pair;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by snsystem_amol on 3/21/2017.
 */

public class CurrentMonthDates
{
    public static String getDateRange() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


            Calendar calendar = getCalendarForNow();
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);
            //String begining = calendar.getTime().toString();

            String begining = sdf.format(calendar.getTime());



            Calendar calendarqq = getCalendarForNow();
            calendarqq.set(Calendar.DAY_OF_MONTH,
                    calendarqq.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendarqq);

           String end = sdf.format(calendarqq.getTime());

        return begining+"#"+end;
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
}
