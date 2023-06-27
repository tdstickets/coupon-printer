// File:            CouponPrinterServiceException.java
// Created:         11/15/13
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:ikleynshteyn@etranscor.com">Izabella Kleynshteyn</a>
//
// (c) 2013 Transcor, Inc.
package com.tdstickets.couponprint.api;

/**
 * CouponPrinterServiceException
 */
public class CouponPrinterServiceException extends RuntimeException
{
    private final String code;

    public CouponPrinterServiceException(String message, String code)
    {
        super(message);
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
}
