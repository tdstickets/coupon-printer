// File:            Coupon.java
// Created:         11/15/13
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:ikleynshteyn@etranscor.com">Izabella Kleynshteyn</a>
//
// (c) 2013 Transcor, Inc.
package com.tdstickets.couponprint.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * coupon
 *
 */
public class Coupon implements Serializable
{
    private Integer couponIndex;
    private String couponType;
    private String printerType;
    private List<CouponField> coupon;

    public Integer getCouponIndex()
    {
        return couponIndex;
    }

    public void setCouponIndex(Integer couponIndex)
    {
        this.couponIndex = couponIndex;
    }

    public String getCouponType()
    {
        return couponType;
    }

    public void setCouponType(String couponType)
    {
        this.couponType = couponType;
    }

    public String getPrinterType()
    {
        return printerType;
    }

    public void setPrinterType(String printerType)
    {
        this.printerType = printerType;
    }

    public List<CouponField> getCoupon()
    {
        return coupon;
    }

    public void setCoupon(List<CouponField> coupon)
    {
        this.coupon = coupon;
    }

    @Override
    public String toString()
    {
        return "coupon{" +
                "couponIndex=" + couponIndex +
                ", couponType='" + couponType + '\'' +
                ", printerType='" + printerType + '\'' +
                ", coupon=" + coupon +
                '}';
    }
}
