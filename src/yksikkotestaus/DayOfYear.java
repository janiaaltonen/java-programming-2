package yksikkotestaus;

import java.time.Month;

/**
 * Smelly Example #1
 *
 * https://web.mit.edu/6.005/www/fa16/classes/04-code-review/
 *
 * Collaboratively authored with contributions from: Saman Amarasinghe, Adam
 * Chlipala, Srini Devadas, Michael Ernst, Max Goldman, John Guttag, Daniel
 * Jackson, Rob Miller, Ma
 *
 */
public class DayOfYear {

    public static int dayOfYear(int month, int dayOfMonth, int year) {

        // returns true if year is leap year
        // every other year will have remainder of 1,2 or 3
        boolean leapYear = year % 4 == 0;

        Month monthOfYear = Month.of(month);

        int daysBeforeThisMonth = monthOfYear.firstDayOfYear(leapYear) - 1;
        return daysBeforeThisMonth + dayOfMonth;
    }
}
