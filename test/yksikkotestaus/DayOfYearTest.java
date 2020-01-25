package yksikkotestaus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DayOfYearTest {

    private DayOfYear day;

    @BeforeEach
    public void setUp(){
        day = new DayOfYear();
    }

    @Test
    public void firstOfMarchInLeapYear() {
        int result = day.dayOfYear(3,1,2020);

        assertEquals(61, result);
    }

    @Test
    public void firstOfMarchInNormalYear() {
        int result = day.dayOfYear(3,1,2021);

        assertEquals(60, result);
    }

    @Test
    public void leapDay() {
        int result = day.dayOfYear(2,29,2020);

        assertEquals(60, result);
    }

    @Test
    public void lastDayOfNormalYear() {
        int result = day.dayOfYear(12,31,2019);

        assertEquals(365, result);
    }

    @Test
    public void lastDayOfLeapYear() {
        int result = day.dayOfYear(12,31,2020);

        assertEquals(366, result);
    }

}