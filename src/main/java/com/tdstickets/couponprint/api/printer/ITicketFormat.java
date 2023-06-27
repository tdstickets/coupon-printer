// File:            ITicketFormat.java
// Created:         5/5/15
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:khoehn@etranscor.com>">Kurt R. Hoehn</a>
//
// (c) 2015 Transcor, Inc.
package com.tdstickets.couponprint.api.printer;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfTemplate;
import com.tdstickets.couponprint.api.dto.CouponField;

import java.io.IOException;

/**
 * ITicketFormat
 */
public interface ITicketFormat
{
    void printText(PdfTemplate ticket, CouponField field, boolean scaled) throws IOException, DocumentException;
    int barCodeX(boolean isZebra, FormatType format);
    int barCodeY(boolean isZebra, FormatType format, boolean longBarcode);
}
