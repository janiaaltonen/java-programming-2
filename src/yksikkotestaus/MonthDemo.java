package yksikkotestaus;

import java.time.Month;

public class MonthDemo {
    public static void main(String[] args) {

        Month month = Month.of(12);
        int days = month.firstDayOfYear(true) - 1;
        int dayOfYear = days + 31;
        System.out.println(dayOfYear);

    }
}
