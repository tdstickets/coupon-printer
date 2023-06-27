// File:            StandardFormat.java
// Created:         5/5/15
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:khoehn@etranscor.com>">Kurt R. Hoehn</a>
//
// (c) 2015 Transcor, Inc.
package com.tdstickets.couponprint.api.printer.impl;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfTemplate;
import com.tdstickets.couponprint.api.dto.CouponField;
import com.tdstickets.couponprint.api.printer.FormatType;
import com.tdstickets.couponprint.api.printer.ITicketFormat;

import java.io.IOException;

/**
 * StandardFormat
 */
public class StandardFormat implements ITicketFormat
{
    @Override
    public void printText(PdfTemplate ticket, CouponField field, boolean scaled) throws IOException, DocumentException
    {
        float row = (float) Math.round(field.getRow() * ((scaled) ? .355 : .390));
        float col = (float) Math.round(field.getColumn() * ((scaled) ? .345 : .365));

        ticket.beginText();
        ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED), 10.0f);
        ticket.setHorizontalScaling(((scaled) ? 115.0f : 120.0f));

        if (field.getFont() != null) {
            switch (field.getFont()) {
                case 20:
                    ticket.setHorizontalScaling(((scaled) ? 115.0f : 125.0f));
                    ticket.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED), 16.0f);
                    break;
                case 19:
                    ticket.setHorizontalScaling(((scaled) ? 55.0f : 60.0f));
                    ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED), 20.0f);
                    break;
                case 17:
                default:
                    ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED), 10.0f);
                    break;
            }
        }

        ticket.moveText(col, (((scaled) ? 222 : 305) - row));
        ticket.showText(field.getValue());
        ticket.endText();
    }

    @Override
    public int barCodeX(boolean isZebra, FormatType format)
    {
        switch(format) {
            case FGL:
                return isZebra ? 120 : 123;
            default:
                return 123;
        }
    }

    @Override
    public int barCodeY(boolean isZebra, FormatType format, boolean longBarcode)
    {
        if(longBarcode){
            switch (format) {
                case FGL:
                    return isZebra ? 30 : 60;
                default:
                    return 60;
            }
        }else {
            switch (format) {
                case FGL:
                    return isZebra ? 30 : 60;
                default:
                    return 60;
            }
        }
    }
}
