package com.tdstickets.couponprint.api.service;

import com.google.common.base.Predicate;
import com.tdstickets.couponprint.api.dto.CouponField;

import javax.annotation.Nullable;

public final class CouponFunctions
{
  private CouponFunctions() {}

  public static Predicate<CouponField> isReceipt()
  {
    return CreditCardReceiptPredicate.INSTANCE;
  }

  public static Predicate<CouponField> isTicket()
  {
    return TicketPredicate.INSTANCE;
  }

  public static Predicate<CouponField> isItinerary()
  {
    return ItineraryPredicate.INSTANCE;
  }

  public static Predicate<CouponField> isBagTag()
  {
    return BagTagPredicate.INSTANCE;
  }

  public static Predicate<CouponField> isBigE() {
    return BigEPredicate.INSTANCE;
  }

  private enum BagTagPredicate implements Predicate<CouponField>
  {
    INSTANCE;

    //TODO: change the values that are being searched for to verify type
    @Override
    public boolean apply(@Nullable CouponField couponField)
    {
      return couponField != null && couponField.getValue() != null &&
        (couponField.getValue().contains("BAGGAGE") ||
          couponField.getValue().contains("BAGAGES")); //TODO: FRENCH BAGGAGe
    }
  }

  private enum CreditCardReceiptPredicate implements Predicate<CouponField>
  {
    INSTANCE;

    @Override
    public boolean apply(@Nullable CouponField couponField)
    {
      return (couponField != null && couponField.getValue() != null &&
        (couponField.getValue().contains("CREDIT CARD RECEIPT - AGENT STUB") ||
          couponField.getValue().contains("RECU DE CARTE DE CREDIT - TALON DE L'CLIENT") ||
          couponField.getValue().contains("RECU DE CARTE DE CREDIT - TALON DE L'CLIENT")));
    }
  }

  private enum TicketPredicate implements Predicate<CouponField>
  {
    INSTANCE;

    @Override
    public boolean apply(@Nullable CouponField couponField)
    {
      return (couponField != null && couponField.getValue() != null &&
        couponField.getValue().replaceAll("\\s+|\u0011", "").equals("1234567"));
    }
  }

  private enum ItineraryPredicate implements Predicate<CouponField>
  {
    INSTANCE;

    @Override
    public boolean apply(@Nullable CouponField couponField)
    {
      return (couponField != null && couponField.getValue() != null &&
        (couponField.getValue().replaceAll("\\s+", "").equals("RECEIPT&ITINERARY") ||
          couponField.getValue().replaceAll("\\s+", "").equals("RECUETITINERAIRE") ||
          couponField.getValue().replaceAll("\\s+", "").equals("RECIBO&ITINERARIO")));
    }
  }

  private enum BigEPredicate implements Predicate<CouponField>
  {
    INSTANCE;

    //TODO: change the values that are being searched for to verify type
    @Override
    public boolean apply(@Nullable CouponField couponField)
    {
      return couponField != null && couponField.getValue() != null &&  couponField.getValue().contains("BIG E");
    }
  }
}
