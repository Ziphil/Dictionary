package ziphil.module

import groovy.transform.CompileStatic
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalUnit
import java.time.temporal.TemporalField
import java.time.temporal.UnsupportedTemporalTypeException
import java.time.temporal.ValueRange
import ziphilib.transform.Ziphilify


@CompileStatic @Ziphilify
public class HairiaNumberField implements TemporalField {

  public static HairiaNumberField HAIRIA_NUMBER = HairiaNumberField.new()

  private HairiaNumberField() {
  }

  public Long getFrom(TemporalAccessor accessor) {
    if (accessor instanceof HairiaDate) {
      return accessor.getLong(this)
    } else {
      throw UnsupportedTemporalTypeException.new("Unsupported field: " + this)
    }
  }

  public <R extends Temporal> R adjustInto(R temporal, Long newValue) {
    throw UnsupportedTemporalTypeException.new("Unsupported field: " + this)
  }

  public TemporalUnit getBaseUnit() {
    return ChronoUnit.DAYS
  }

  public TemporalUnit getRangeUnit() {
    return ChronoUnit.FOREVER
  }

  public ValueRange range() {
    return ValueRange.of(-365249999634L, 365249999634L)
  }

  public ValueRange rangeRefinedBy(TemporalAccessor accessor) {
    return range()
  }

  public Boolean isDateBased() {
    return true
  }

  public Boolean isTimeBased() {
    return false
  }

  public Boolean isSupportedBy(TemporalAccessor accessor) {
    if (accessor instanceof HairiaDate) {
      return true
    } else {
      return false
    }
  }

}