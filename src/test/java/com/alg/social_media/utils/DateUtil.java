package com.alg.social_media.utils;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class DateUtil {

  private DateUtil() {}

  public static LocalDate getRandomLocalDate() {

    // Get the current LocalDateTime
    var currentDateTime = LocalDate.now();

    // Use ThreadLocalRandom to generate random values
    ThreadLocalRandom random = ThreadLocalRandom.current();

    // Generate a random number of days, hours, minutes, and seconds
    int randomDays = random.nextInt(365); // 0 to 364 days

    // Add the random values to the current LocalDateTime
    return currentDateTime.plusDays(randomDays);
  }

  public static boolean areDatesInReverseChronologicalOrder(List<LocalDate> localDates) {
    for (int i = 0; i < localDates.size() - 1; i++) {
      if (localDates.get(i).isBefore(localDates.get(i+1))) {
        return false;
      }
    }

    return true;
  }
}
