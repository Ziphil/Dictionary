package ziphil.module

import groovy.transform.CompileStatic
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalField
import java.time.temporal.UnsupportedTemporalTypeException
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HairiaDate implements TemporalAccessor {

  private static LocalDate GENESIS_DATE = LocalDate.of(2012, 1, 23)

  private Long $hairiaNumber = 0

  private HairiaDate(Long hairiaNumber) {
    $hairiaNumber = hairiaNumber
  }

  private Int getYear() {
    Long basisNumber = $hairiaNumber + 547862
    Long temporaryYear = Math.floorDiv(4 * basisNumber + 3 + 4 * Math.floorDiv(3 * (Math.floorDiv(4 * (basisNumber + 1), 146097) + 1), 4), 1461)
    Long year = temporaryYear + 1
    return (Int)year
  }

  private Int getYearRemainder() {
    Long basisNumber = $hairiaNumber + 547862
    Long temporaryYear = Math.floorDiv(4 * basisNumber + 3 + 4 * Math.floorDiv(3 * (Math.floorDiv(4 * (basisNumber + 1), 146097) + 1), 4), 1461)
    Long yearRemainder = basisNumber - (365 * temporaryYear + Math.floorDiv(temporaryYear, 4) - Math.floorDiv(temporaryYear, 100) + Math.floorDiv(temporaryYear, 400))
    return (Int)yearRemainder
  }

  private Int getMonthOfYear() {
    return (Int)Math.floorDiv(getYearRemainder(), 33) + 1
  }

  private Int getDayOfMonth() {
    return (Int)Math.floorMod(getYearRemainder(), 33) + 1
  }

  public Boolean isSupported(TemporalField field) {
    if (field == ChronoField.YEAR || field == ChronoField.MONTH_OF_YEAR || field == ChronoField.DAY_OF_MONTH) {
      return true
    } else if (field == HairiaNumberField.HAIRIA_NUMBER) {
      return true
    } else {
      return false
    }
  }

  public Long getLong(TemporalField field) {
    if (field == ChronoField.YEAR) {
      return getYear()
    } else if (field == ChronoField.MONTH_OF_YEAR) {
      return getMonthOfYear()
    } else if (field == ChronoField.DAY_OF_MONTH) {
      return getDayOfMonth()
    } else if (field == HairiaNumberField.HAIRIA_NUMBER) {
      return $hairiaNumber
    } else {
      throw UnsupportedTemporalTypeException.new("Unsupported field: " + field)
    }
  }

  public static HairiaDate now() {
    return from(LocalDate.now())
  }

  public static HairiaDate from(TemporalAccessor accessor) {
    return ofEpochDay(accessor.getLong(ChronoField.EPOCH_DAY))
  }

  public static HairiaDate of(Int year, Int month, Int dayOfMonth) {
    Long hairiaNumber = 0
    hairiaNumber += 365 * (year - 1) + Math.floorDiv(year - 1, 4) - Math.floorDiv(year - 1, 100) + Math.floorDiv(year - 1, 400)
    hairiaNumber += (month - 1) * 33 + dayOfMonth
    hairiaNumber -= 547863
    return HairiaDate.new(hairiaNumber)
  }

  public static HairiaDate ofEpochDay(Long epochDay) {
    Long hairiaNumber = epochDay - GENESIS_DATE.toEpochDay() + 1
    return HairiaDate.new(hairiaNumber)
  }

}