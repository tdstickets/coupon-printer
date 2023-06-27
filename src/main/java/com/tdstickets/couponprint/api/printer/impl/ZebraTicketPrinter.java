// File:            ZebraTicketPrinter.java
// Created:         1/13/15
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:khoehn@etranscor.com>">Kurt R. Hoehn</a>
//
// (c) 2015 Transcor, Inc.
package com.tdstickets.couponprint.api.printer.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.tdstickets.couponprint.api.dto.CouponField;
import com.tdstickets.couponprint.api.printer.FormatType;
import com.tdstickets.couponprint.api.printer.ITicketFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ZebraTicketPrinter
 */
public class ZebraTicketPrinter extends AbstractTicketPrinter
{
    private DateFormat dateFormat = new SimpleDateFormat("ddMMMyy");
    private Date date = new Date();
    private String today = "/" + dateFormat.format(date);
    private boolean canadaFlag = false;
    private boolean longBarcode = false;
    private String barcode = "";
    private CouponField name = null;
    private CouponField datefield = null;
    private CouponField confirmation = null;
    private CouponField agency = null;
    private boolean willCall = false;

    @Override
    public Document getDocument(FormatType formatType)
    {
        switch (formatType) {
            case ZPL:
                return new Document(new RectangleReadOnly(522f, 284f), -10f, 0f, -80f, 0f);
            case FGL:
                return new Document(new RectangleReadOnly(522f, 284f), -10f, 0f, -80f, 0f);
            default:
                return new Document(new RectangleReadOnly(522f, 234f), -8f, 0f, -50f, 0f);
        }
    }

    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled) throws Exception {
        Map<String, String> properties = null;
        print(format, document, cb, fields, scaled, properties);
    }

    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled, Map<String, String> properties) throws Exception
    {
        ITicketFormat ticketFormat = getTicketFormat(format);

        PdfTemplate ticket = cb.createTemplate(document.getPageSize().getWidth(), document.getPageSize().getHeight());
        Image qrCode = null;
        for (CouponField field : fields)
        {
            if(field.getValue() != null)
            {
                if(field.getValue().contains("CA/CA") || (field.getValue().contains("GREYHOUND CANADA TRANS CO")))
                    canadaFlag = true;

                switch(field.getType())
                {
                    case BARCODE:
                        String code = field.getValue().replaceAll("[^0-9]", "");
                        if (code.length() > 17) {
                            if((code.length() % 2) != 0) {
                                code = code.substring(0, code.length() - 1);
                            }
                            setLongBarcode(true);
                        }

                        if((code.length() % 2) != 0) {
                            code = code.substring(0, code.length() - 1);
                        }

                        printBarCode(cb, ticket, getBarcode(code), ticketFormat, format, (code.length() > 16));
                        qrCode = getQrCode(code, 450, 130);
                        if (qrCode != null) {
                            ticket.addImage(qrCode);
                            ticketFormat.printText(ticket, getQrCodeText("Internal Use Only"), false);
                        }
                        break;
                    case TEXT:
                        if(field.getValue().contains("HEARING IMPAIRED") || field.getValue().contains("OTHERSPECIALNEED")
                                || field.getValue().contains("WHEELCHAIR")  || field.getValue().contains("SIGHT IMPAIRED")
                                || field.getValue().contains("SERVICE ANIMAL") ) {
                            field.setFont(19);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }else if (field.getValue().contains("** VOID IF DETACHED **") && !canadaFlag ) {
                            field.setRow(110);
                            field.setFont(12);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }else if (field.getValue().contains("WILL CALL - AGENT") )
                        {
                            setWillCall(true);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }else if (field.getValue().contains("PSGR:") )
                        {
                            field.setRow(550);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }else if (field.getValue().contains("1ST TKT#:")  )
                        {
                            field.setRow(550);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        } else if (field.getValue().contains("ROAD REWARD") && !isWillCall())
                        {
                            field.setRow(140);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        } else {
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }
                  /*  case TEXT:
                        if(field.getValue().contains("PTO TICKET - REFUNDABLE ONLY TO PURCHASER")
                                && canadaFlag)
                        {
                            field.setFont(17);
                            field.setRow(275);
                            ticketFormat.printText(ticket, field, true);
                            canadaFlag = false;
                            break;
                        }else if(field.getValue().contains("Agency #"))
                        {
                            field.setFont(17);
                            field.setRow(450);
                            field.setColumn(660);
                            ticketFormat.printText(ticket, field, scaled);
                            break;
                        }
                        else if(field.getValue().contains("PTO TICKET - REFUNDABLE ONLY TO PURCHASER")
                                && !canadaFlag)
                        {
                            field.setRow(460);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }
                        else if(field.getValue().contains("TICKET FOR SCHEDULE")
                                || field.getValue().contains(today)
                                || field.getValue().contains("TICKET VALID ONLY ON SCHEDULE"))
                        {
                            field.setFont(17);
                            ticketFormat.printText(ticket, field, true);

                            break;
                        }
                        else
                        {
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }*/
                    default:
                        break;
                }
            }
        }
        cb.addTemplate(ticket, 3, 0);
        document.newPage();
    }

    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled, Boolean bigE) throws Exception
    {
        for (CouponField field : fields)
        {
            if (field.getValue() != null)
            {
                switch (field.getType())
                {
                    case BARCODE:
                        setBarcode(field.getValue());
                        break;
                    case TEXT:
                        if(field.getValue().length() == 8)
                        {
                            setConfirmation(field);
                        }
                        else if(field.getValue().length() == 4)
                        {
                            setAgency(field);
                        }
                        else if(field.getValue().contains("/"))
                        {
                            setDatefield(field);
                        }else if (field.getValue().matches(".*[a-zA-Z].*"))
                        {
                            setName(field);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        document.newPage();
        bigETicket(format, document, cb, fields, scaled);

    }

    private void bigETicket(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled) throws Exception
    {
        Barcode barcode = null;
        boolean isZebra = true;

        PdfTemplate ticket = cb.createTemplate(document.getPageSize().getWidth(), document.getPageSize().getHeight());
        ITicketFormat ticketFormat = getTicketFormat(format);
        cb.addTemplate(ticket, 100, 75);

        //print name
        Font font = FontFactory.getFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED, 12);
        Phrase phrase = new Phrase();
        phrase.add(new Chunk("Valid for one Admission to The Big E September 16 thru October 2 2016\n"));
        phrase.setFont(font);
        ColumnText column = new ColumnText(cb);
        column.setSimpleColumn(10,0, 600, 250);
        Paragraph p1 = new Paragraph(phrase);
        p1.setLeading(0);
        p1.setMultipliedLeading(0.9f);
        column.addElement(p1);
        column.go();

        //base info
        Font font2 = FontFactory.getFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED, 9);
        Phrase phrase2 = new Phrase();
        phrase2.add(new Chunk("Ticket holder: " + getName().getValue() + "\n"));
        phrase2.add(new Chunk("Confirmation Number: "+getConfirmation().getValue()+ "\n"));
        phrase2.add(new Chunk("Purchase Date:" + getDatefield().getValue()+ "\n"));
        phrase2.add(new Chunk("Agency Number: "+getAgency().getValue()+ "\n"));
        ColumnText column2 = new ColumnText(cb);
        phrase.setFont(font2);
        column2.setSimpleColumn(10,0, 600, 200);
        Paragraph p2 = new Paragraph(phrase2);
        p2.setLeading(0);
        p2.setMultipliedLeading(0.9f);
        column2.addElement(p2);
        column2.go();


        //print bar code
        barcode = getALPBarcode(getBarcode());
        Image image39 = barcode.createImageWithBarcode(cb, null, null);
        image39.setAbsolutePosition(ticketFormat.barCodeX(isZebra, format), ticketFormat.barCodeY(isZebra, format, false));
        ticket.addImage(image39);


        document.newPage();
    }

    protected Barcode getALPBarcode(String code) throws Exception
    {
        Barcode39 barcode39 = new Barcode39();
        barcode39.setCode(code);
        barcode39.setStartStopText(false);
        barcode39.setN(2.75f);
        barcode39.setBaseline(10);
        barcode39.setBarHeight(30);
        barcode39.setFont(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED));
        barcode39.setSize(15.0f);
        barcode39.setTextAlignment(Element.ALIGN_MIDDLE);

        return barcode39;
    }

    public boolean isCanadaFlag() {
        return canadaFlag;
    }

    public void setCanadaFlag(boolean canadaFlag) {
        this.canadaFlag = canadaFlag;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public CouponField getName() {
        return name;
    }

    public void setName(CouponField name) {
        this.name = name;
    }

    public CouponField getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(CouponField confirmation) {
        this.confirmation = confirmation;
    }

    public CouponField getDatefield() {
        return datefield;
    }

    public void setDatefield(CouponField datefield) {
        this.datefield = datefield;
    }

    public CouponField getAgency() {
        return agency;
    }

    public void setAgency(CouponField agency) {
        this.agency = agency;
    }

    public boolean isLongBarcode() {
        return longBarcode;
    }

    public void setLongBarcode(boolean longBarcode) {
        this.longBarcode = longBarcode;
    }

    public boolean isWillCall() {
        return willCall;
    }

    public void setWillCall(boolean willCall) {
        this.willCall = willCall;
    }
}
