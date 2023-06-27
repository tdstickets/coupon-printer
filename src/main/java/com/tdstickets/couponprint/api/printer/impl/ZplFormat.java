// File:            ZplFormat.java
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
 * ZplFormat
 */
public class ZplFormat implements ITicketFormat
{
    @Override
    public void printText(PdfTemplate ticket, CouponField field, boolean scaled) throws IOException, DocumentException
    {
        ticket.beginText();
        ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 9.0f);
        ticket.setHorizontalScaling(100.0f);

        if (field.getFont() != null) {
            switch (field.getFont()) {
                case 20:
                    ticket.setHorizontalScaling(100.0f);
                    ticket.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED), 12.0f);
                    break;
                case 19:
                    ticket.setHorizontalScaling(36.0f);
                    ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 16.0f);
                    break;
                case 17:
                    ticket.setHorizontalScaling(80.0f);
                    ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 9.0f);
                    break;
                case 12:
                    ticket.setHorizontalScaling(55.0f);
                    ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 9.0f);
                    break;
                default:
                    ticket.setHorizontalScaling(100.0f);
                    ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 9.0f);
                    break;
            }
        }
        ticket.moveText( (((field.getColumn()-12)/12) * 4), ((((field.getRow()-115)/22) + 5) * 8) );
        ticket.showText(field.getValue());
        ticket.endText();
    }

    @Override
    public int barCodeX(boolean isZebra, FormatType format)
    {
        return 110;
    }

    @Override
    public int barCodeY(boolean isZebra, FormatType format, boolean longBarcode)
    {
        if(longBarcode){
                if (!isZebra) {
                    return 50;
                } else {
                    return 45;
                }
            }else{
                if (!isZebra) {
                    return 100;
                } else {
                    return 90;
                }
            }
    }
}
