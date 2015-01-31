package com.bedatadriven.rebar.time.calendar;

import org.junit.Test;

import java.util.Calendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LocalDateTest {


  @Test
  public void fromDate() {

    Calendar cal = Calendar.getInstance();
    cal.set(2011, Calendar.MARCH, 15);

    LocalDate localDate = new LocalDate(cal.getTime());

    assertThat(localDate.getYear(), equalTo(2011));
    assertThat(localDate.getMonthOfYear(), equalTo(3));
    assertThat(localDate.getDayOfMonth(), equalTo(15));

    cal = Calendar.getInstance();
    cal.setTime(localDate.atMidnightInMyTimezone());

    assertThat(cal.get(Calendar.YEAR), equalTo(2011));
    assertThat(cal.get(Calendar.MONDAY), equalTo(Calendar.MARCH));
    assertThat(cal.get(Calendar.DAY_OF_MONTH), equalTo(15));
    assertThat(cal.get(Calendar.HOUR_OF_DAY), equalTo(0));
    assertThat(cal.get(Calendar.MINUTE), equalTo(0));
    assertThat(cal.get(Calendar.SECOND), equalTo(0));

  }

}
