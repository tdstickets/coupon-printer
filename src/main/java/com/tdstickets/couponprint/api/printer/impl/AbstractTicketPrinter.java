// File:            AbstractTicketPrinter.java
// Created:         5/6/15
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:khoehn@etranscor.com>">Kurt R. Hoehn</a>
//
// (c) 2015 Transcor, Inc.
package com.tdstickets.couponprint.api.printer.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.BarcodeInter25;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.tdstickets.couponprint.api.dto.CouponField;
import com.tdstickets.couponprint.api.printer.FormatType;
import com.tdstickets.couponprint.api.printer.ITicketFormat;
import com.tdstickets.couponprint.api.printer.ITicketPrinter;

import java.util.List;
import java.util.Map;

/**
 * AbstractTicketPrinter
 */
public abstract class AbstractTicketPrinter implements ITicketPrinter
{
    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields) throws Exception
    {
        print(format, document, cb, fields, false);
    }

    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Map<String, String> properties) throws Exception
    {
        print(format, document, cb, fields, false, properties);
    }

    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled, Boolean bigE) throws Exception
    {
        print(format, document, cb, fields, false, bigE);
    }

    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled, Boolean bigE, Map<String, String> properties) throws Exception
    {
        print(format, document, cb, fields, false, bigE, properties);
    }

    protected Barcode getBarcode(String code) throws Exception
    {
        BarcodeInter25 code25 = new BarcodeInter25();
        code25.setCode(code);
        code25.setStartStopText(false);
        code25.setN(2.75f);
        code25.setFont(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED));
        code25.setSize(8.0f);
        code25.setTextAlignment(Element.ALIGN_CENTER);

        return code25;
    }

    protected Image getQrCode(String code, int absoluteX, int absoluteY) throws DocumentException {
        BarcodeQRCode barcodeQRCode = new BarcodeQRCode(code, 80, 80, null);
        Image qrcode = barcodeQRCode.getImage();
        qrcode.setAbsolutePosition(absoluteX, absoluteY);
        return qrcode;
    }

    protected CouponField getQrCodeText(String value) {

        CouponField qrCodeTxt = new CouponField();
        qrCodeTxt.setColumn(1415);
        qrCodeTxt.setRow(400);
        qrCodeTxt.setFont(12);
        qrCodeTxt.setValue(value);
        return qrCodeTxt;
    }

    protected CouponField getGeneralBoarding(String value) {
        CouponField boarding = new CouponField();
        boarding.setColumn(1176);
        boarding.setRow(577);
        boarding.setFont(19);
        boarding.setValue(value);
        return boarding;
    }

    protected void printBarCode(PdfContentByte cb, PdfTemplate template, Barcode barcode, ITicketFormat ticketFormat, FormatType formatType, boolean longBarcode)
    {
        try
        {
            boolean isZebra = (this instanceof ZebraTicketPrinter);
            Image image25 = barcode.createImageWithBarcode(cb, null, null);
            image25.setAbsolutePosition(ticketFormat.barCodeX(isZebra, formatType), ticketFormat.barCodeY(isZebra, formatType, longBarcode));
            image25.setRotationDegrees(90f);
            template.addImage(image25);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected ITicketFormat getTicketFormat(FormatType format)
    {
        switch(format)
        {
            case ZPL:
                return new ZplFormat();
            case GW:
            case FGL:
                return new FglFormat();
            default:
                return new StandardFormat();
        }
    }
}
