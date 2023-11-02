package com.tdstickets.couponprint.api.service;

import com.tdstickets.couponprint.api.dto.Coupon;

import java.util.List;

public class Coupons {
  String printerType;
  List<Coupon> coupons;

  public String getPrinterType() {
    return printerType;
  }

  public void setPrinterType(String printerType) {
    this.printerType = printerType;
  }

  public List<Coupon> getCoupons() {
    return coupons;
  }

  public void setCoupons(List<Coupon> coupons) {
    this.coupons = coupons;
  }
}
