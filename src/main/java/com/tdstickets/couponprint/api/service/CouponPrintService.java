package com.tdstickets.couponprint.api.service;

import com.tdstickets.couponprint.api.CouponPrinterServiceException;
import com.tdstickets.couponprint.api.impl.CouponPrinter;
import com.tdstickets.couponprint.api.printer.ITicketPrinter;

import javax.ejb.Stateless;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;

@Stateless
public class CouponPrintService {
  private static final String LOGO_DIR = "logo";

  public byte[] getCoupons(String carrier, byte[] data) {
    try {
      Coupons coupons = CouponParser.getCoupons(data);
      return CouponPrinter.print(coupons.getCoupons(), getLogo(carrier));
    }
    catch(Exception e) {
      throw new CouponPrinterServiceException("Unable to create coupon document.", "2319");
    }
  }

  public byte[] getCoupons(String carrier, boolean terms, String locale, byte[] data) {
    try {
      Coupons coupons = CouponParser.getCoupons(data);
      return CouponPrinter.print(coupons.getCoupons(), getLogo(carrier), terms, locale);
    }
    catch (Exception e) {
      throw new CouponPrinterServiceException("Unable to create coupon document.", "2319");
    }
  }

  public byte[] getCoupons(String carrier, boolean terms, String locale, boolean ticketsOnly, ITicketPrinter.Type type, byte[] data) {
    try {
      Coupons coupons = CouponParser.getCoupons(data);
      return CouponPrinter.print(coupons.getCoupons(), ticketsOnly, getLogo(carrier), terms, locale, type);
    }
    catch (Exception e) {
      throw new CouponPrinterServiceException("Unable to create coupon document.", "2319");
    }
  }

  private byte[] getLogo(String carrier) {
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      URL logoUrl = classLoader.getResource(LOGO_DIR + carrier + "jpg");
      if(logoUrl == null) {
        return null;
      }
      else {
        File logo = new File(logoUrl.toURI());
        return Files.readAllBytes(logo.toPath());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
