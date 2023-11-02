package com.tdstickets.couponprint.api.service;

import com.google.common.collect.Iterators;
import com.tdstickets.couponprint.api.dto.Coupon;
import com.tdstickets.couponprint.api.dto.CouponField;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CouponParser {
  private static final CouponParser instance = new CouponParser();

  public static Coupons getCoupons(byte[] data) {
    return instance._getCoupons(data);
  }

  private Coupons _getCoupons(byte[] data) {
    Coupons coupons = new Coupons();
    coupons.setCoupons(new ArrayList<Coupon>());

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));

    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      StringBuffer buffer = new StringBuffer();

      boolean inCommand = false;
      int couponIndex = 0;
      int value;

      while ((value = bis.read()) > -1)
      {
        if (value == 29 || value == 12) {
          coupons.setPrinterType("GW");
          coupons.getCoupons().add(buildCoupon(couponIndex++, "GW", baos.toByteArray()));

          baos.flush();
          baos.reset();
        }
        else
        {
          baos.write(value);

          if( (char) value == '<' || (char) value == '^')
          {
            if((char) value == '^' && buffer.length() > 0) {
              buffer.delete(0, buffer.length());
            }

            inCommand = true;
          }

          if( (char) value == '>')
          {
            inCommand = false;
            buffer.append((char) value);

            String command = buffer.toString();
            if(command.equals("<q>") || command.equals("<p>")) {
              coupons.setPrinterType("FGL");
              coupons.getCoupons().add(buildCoupon(couponIndex++, "FGL", baos.toByteArray()));

              baos.flush();
              baos.reset();
            }

            buffer.delete(0, buffer.length());
          }

          if(inCommand)
          {
            buffer.append((char) value);

            if(buffer.toString().equals("^XZ")) {
              coupons.setPrinterType("ZPL");
              coupons.getCoupons().add(buildCoupon(couponIndex++, "ZPL", baos.toByteArray()));

              baos.flush();
              baos.reset();
            }
          }
        }
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      try {
        bis.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    return coupons;
  }

  private Coupon buildCoupon(int index, String printer, byte[] b) {
    Coupon coupon = new Coupon();
    List<CouponField> fields = CouponFactory.parseCoupon(b);
    coupon.setCouponIndex(index);
    coupon.setPrinterType(printer);
    coupon.setCouponType(getCouponType(fields));
    coupon.setCoupon(fields);
    return coupon;
  }

  private String getCouponType(List<CouponField> fields) {
    if(Iterators.any(fields.iterator(), CouponFunctions.isReceipt()))
    {
      return "RECEIPT";
    }
    else if(Iterators.any(fields.iterator(), CouponFunctions.isTicket()))
    {
      return "TICKET";
    }
    else if(Iterators.any(fields.iterator(), CouponFunctions.isItinerary()))
    {
      return "ITINERARY";
    }
    else if(Iterators.any(fields.iterator(), CouponFunctions.isBagTag()))
    {
      return "BAG";
    }
    else if(Iterators.any(fields.iterator(), CouponFunctions.isBigE()))
    {
      return "BIG_E";
    }

    return "OTHER";
  }
}
