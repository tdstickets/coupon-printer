// File:            CouponResponse.java
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
 * CouponResponse
 */
public class CouponResponse implements Serializable
{
    private String agencyCode;
    private String departDate;
    private String confirmationNumber;
    private List<Coupon> tripSummaryCoupons;

    public String getAgencyCode()
    {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode)
    {
        this.agencyCode = agencyCode;
    }

    public String getDepartDate()
    {
        return departDate;
    }

    public void setDepartDate(String departDate)
    {
        this.departDate = departDate;
    }

    public String getConfirmationNumber()
    {
        return confirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber)
    {
        this.confirmationNumber = confirmationNumber;
    }

    public List<Coupon> getTripSummaryCoupons()
    {
        return tripSummaryCoupons;
    }

    public void setTripSummaryCoupons(List<Coupon> tripSummaryCoupons)
    {
        this.tripSummaryCoupons = tripSummaryCoupons;
    }

    @Override
    public String toString()
    {
        return "CouponResponse{" +
                "agencyCode='" + agencyCode + '\'' +
                ", departDate='" + departDate + '\'' +
                ", confirmationNumber='" + confirmationNumber + '\'' +
                ", tripSummaryCoupons=" + tripSummaryCoupons +
                '}';
    }
}
