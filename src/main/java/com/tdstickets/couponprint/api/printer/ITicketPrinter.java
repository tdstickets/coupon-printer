// File:            TicketPrinter.java
// Created:         1/13/15
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:khoehn@etranscor.com>">Kurt R. Hoehn</a>
//
// (c) 2015 Transcor, Inc.
package com.tdstickets.couponprint.api.printer;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.tdstickets.couponprint.api.dto.CouponField;

import java.util.List;
import java.util.Map;

/**
 * TicketPrinter
 */
public interface ITicketPrinter
{
    enum Type {
        STANDARD, ZEBRA, ZEBRA_APPLET
    }

    Document getDocument(FormatType format);
    void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields) throws Exception;
    void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Map<String, String> properties) throws Exception;
    void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled) throws Exception;
    void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled, Map<String, String> properties) throws Exception;
    void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled, Boolean bigE) throws Exception;
    void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled, Boolean bigE, Map<String, String> properties) throws Exception;
}
