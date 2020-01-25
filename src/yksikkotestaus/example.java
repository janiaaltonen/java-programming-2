package yksikkotestaus;


import java.time.Month;

public class example {

    public static int dayOfYear(int month, int dayOfMonth, int year) {
        boolean leapYear;
        leapYear = year % 4 == 0;

        Month monthOfYear = Month.of(month);

        int daysBeforeThisMonth = monthOfYear.firstDayOfYear(leapYear) - 1;
        return daysBeforeThisMonth + dayOfMonth;
    }

}
