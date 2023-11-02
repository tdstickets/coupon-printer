package com.tdstickets.couponprint.api.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.tdstickets.couponprint.api.dto.Coupon;
import com.tdstickets.couponprint.api.dto.CouponField;
import com.tdstickets.couponprint.api.printer.ITicketPrinter;
import com.tdstickets.couponprint.api.printer.FormatType;
import com.tdstickets.couponprint.api.printer.TicketPrinterFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * CouponPrinter
 */
public final class CouponPrinter
{
    private static final CouponPrinter instance = new CouponPrinter();

    private CouponPrinter() {}

    public static byte[] print(List<Coupon> coupons, byte[] logo) throws Exception
    {
        return print(coupons, logo, false, "EN");
    }

    public static byte[] print(List<Coupon> coupons, byte[] logo, boolean printTerms, String locale) throws Exception
    {
        return print(coupons, true, logo, printTerms, locale, ITicketPrinter.Type.STANDARD);
    }

    public static byte[] print(List<Coupon> coupons, boolean ticketsOnly, byte[] logo, boolean printTerms, String locale) throws Exception
    {
        return instance.generatePdf(coupons, ticketsOnly, logo, printTerms, locale, ITicketPrinter.Type.STANDARD, null);
    }

    public static byte[] print(List<Coupon> coupons, boolean ticketsOnly, byte[] logo, boolean printTerms, String locale, ITicketPrinter.Type type) throws Exception
    {
        return instance.generatePdf(coupons, ticketsOnly, logo, printTerms, locale, type, null);
    }

    public static byte[] print(List<Coupon> coupons, boolean ticketsOnly, byte[] logo, boolean printTerms, String locale, ITicketPrinter.Type type, Map<String, String> properties) throws Exception
    {
        return instance.generatePdf(coupons, ticketsOnly, logo, printTerms, locale, type, properties);
    }

    private byte[] generatePdf(List<Coupon> coupons, boolean ticketsOnly, byte[] logo, boolean printTerms, String locale, ITicketPrinter.Type type, Map<String, String> properties) throws DocumentException, IOException
    {
        ITicketPrinter ticketPrinter = TicketPrinterFactory.get(type, getResource(locale), logo, printTerms);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document document = ticketPrinter.getDocument(getPrinterType(coupons));
        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();
        PdfContentByte cb = writer.getDirectContent();

        try
        {
            if(ticketsOnly)
            {
                for (int i=0; i < coupons.size(); i++)
                {
                    Coupon coupon = coupons.get(i);

                    List<CouponField> fields = coupons.get(i).getCoupon();

                    if(coupon.getCouponType().equals("TICKET") || coupon.getCouponType().equals("ITINERARY")) {
                        FormatType format = (coupon.getPrinterType() != null) ? FormatType.valueOf(coupon.getPrinterType()) : FormatType.GW;
                        ticketPrinter.print(format, document, cb, fields, properties);
                    }

                    if (coupon.getCouponType().equals("BIG_E")) {
                        FormatType format = coupon.getPrinterType() != null ? FormatType.valueOf(coupon.getPrinterType()) : FormatType.GW;
                        ticketPrinter.print(format, document, cb, fields, false, true);
                    }
                }
            }
            else
            {
                for (int i=0; i < coupons.size(); i++)
                {
                    Coupon coupon = coupons.get(i);

                    List<CouponField> fields = coupons.get(i).getCoupon();
                    if (coupon.getCouponType().equals("BIG_E"))
                    {
                        FormatType format = coupon.getPrinterType() != null ? FormatType.valueOf(coupon.getPrinterType()) : FormatType.GW;
                        ticketPrinter.print(format, document, cb, fields, coupon.getCouponType().equals("BAG"), true);
                    }else
                    {
                        FormatType format = coupon.getPrinterType() != null ? FormatType.valueOf(coupon.getPrinterType()) : FormatType.GW;
                        ticketPrinter.print(format, document, cb, fields, coupon.getCouponType().equals("BAG"), properties);
                    }
                }
            }

            document.close();

            return baos.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private ResourceBundle getResource(String locale)
    {
        return ResourceBundle.getBundle(getClass().getName(), Locale.forLanguageTag(locale), getClass().getClassLoader());
    }

    private FormatType getPrinterType(List<Coupon> coupons)
    {
        return (coupons.isEmpty()) ? FormatType.GW : (coupons.get(0).getPrinterType()) != null ? FormatType.valueOf(coupons.get(0).getPrinterType()) : FormatType.GW;
    }
}
