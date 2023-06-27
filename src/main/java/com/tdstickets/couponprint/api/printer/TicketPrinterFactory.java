// File:            TicketPrinterFactory.java
// Created:         1/13/15
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:khoehn@etranscor.com>">Kurt R. Hoehn</a>
//
// (c) 2015 Transcor, Inc.
package com.tdstickets.couponprint.api.printer;

import com.tdstickets.couponprint.api.printer.impl.StandardTicketPrinter;
import com.tdstickets.couponprint.api.printer.impl.ZebraTicketPrinter;

import java.util.ResourceBundle;

/**
 * TicketPrinterFactory
 */
public class TicketPrinterFactory
{
    public static ITicketPrinter get(ITicketPrinter.Type type, ResourceBundle resource, byte[] logo, boolean printTerms)
    {
        switch(type)
        {
            case ZEBRA:
                return new ZebraTicketPrinter();
            case STANDARD:
            default:
                return new StandardTicketPrinter(resource, logo, printTerms);
        }
    }
}
