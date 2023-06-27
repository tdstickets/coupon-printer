// File:            StandardTicketPrinter.java
// Created:         1/13/15
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:khoehn@etranscor.com>">Kurt R. Hoehn</a>
//
// (c) 2015 Transcor, Inc.
package com.tdstickets.couponprint.api.printer.impl;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.tdstickets.couponprint.api.dto.CouponField;
import com.tdstickets.couponprint.api.printer.FormatType;
import com.tdstickets.couponprint.api.printer.ITicketFormat;
import org.apache.commons.lang.math.NumberUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * StandardTicketPrinter
 */
public class StandardTicketPrinter extends AbstractTicketPrinter
{

    private final ResourceBundle resource;
    private byte[] logo;
    private final boolean printTerms;
    private boolean canadaFlag = false;
    private boolean longBarcode = false;
    private String barcode = "";
    private String customTerms;
    private CouponField name = null;
    private CouponField datefield = null;
    private CouponField confirmation = null;
    private CouponField agency = null;
    private boolean soldOut = false;
    private boolean willCall = false;
    private DateFormat dateFormat = new SimpleDateFormat("ddMMMyy");
    Date date = new Date();
    private String today = "/" + dateFormat.format(date);



    public StandardTicketPrinter(ResourceBundle resource, byte[] logo, boolean printTerms)
    {
        this.resource = resource;
        this.logo = logo;
        this.printTerms = printTerms;
    }

    @Override
    public Document getDocument(FormatType format)
    {
        return new Document(PageSize.LETTER, 36, 36, 36, 36);
    }

    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled) throws Exception {
        Map<String, String> properties = null;
        print(format, document, cb, fields, scaled, properties);
    }

    @Override
    public void print(FormatType format, Document document, PdfContentByte cb, List<CouponField> fields, Boolean scaled, Map<String, String> properties) throws Exception
    {

        Boolean customTermsFlag = false;

        if(properties != null && properties.size() > 0){
            if(properties.containsKey("terms") && properties.get("terms") != null && !properties.get("terms").equals("") ){
                setCustomTerms(properties.get("terms"));
                customTermsFlag = true;
            }
        }

        Barcode barcode = null;
        Image qrCode = null;

        PdfTemplate ticket;

        switch (format) {
            case ZPL:
                ticket = cb.createTemplate(560, 316);
                break;
            case FGL:
                ticket = cb.createTemplate(560, 316);
                break;
            default:
                ticket = cb.createTemplate(560, 316);
                break;
        }

        ITicketFormat ticketFormat = getTicketFormat(format);
        for (CouponField field : fields)
        {
            if(field.getValue() != null)
            {
                if (field.getValue().contains("GREYHOUND CANADA TRANS CO") ) {
                    setCanadaFlag(true);
                }

                switch(field.getType())
                {
                    case BARCODE:
                        String code = field.getValue().replaceAll("[^0-9]", "");
                        if (code.length() > 17) {
                            if((code.length() % 2) != 0){
                                code = code.substring(0, code.length() - 1);
                                setLongBarcode(true);
                            }else{
                                setLongBarcode(true);
                            }
                        }else if((code.length() % 2) != 0) {
                            code = code.substring(0, code.length() - 1);
                        }

                        barcode = getBarcode(code);
                        printBarCode(cb, ticket, barcode, ticketFormat, format, isLongBarcode());
                        qrCode = getQrCode(code, 450, 130);
                        if (qrCode != null) {
                            ticket.addImage(qrCode);
                            ticketFormat.printText(ticket, getQrCodeText("Internal Use Only"), false);
                        }
                        break;
                    case TEXT:
                     if (field.getValue().contains("** VOID IF DETACHED **") && !isCanadaFlag() )
                        {
                            field.setRow(110);
                            field.setFont(12);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        } else if((field.getValue().contains("TARIFF:") && field.getValue().contains("ECON"))
                                                                        || field.getValue().contains("REISSUE"))
                        {
                            ticketFormat.printText(ticket, field, false);
                            ticketFormat.printText(ticket, getGeneralBoarding("General Boarding"), false);
                            break;
                        } else if((field.getValue().contains("COUT:") && field.getValue().contains("ECON"))
                                                                       || field.getValue().contains("RÉÉDITER"))
                        {
                            ticketFormat.printText(ticket, field, false);
                            ticketFormat.printText(ticket, getGeneralBoarding("Embarquement Generale"), false);
                            break;
                        } else if((field.getValue().contains("BOARDING #") && 1260 == field.getColumn()
                                                                           && 619 == field.getRow())
                              || (1260 == field.getColumn() && 577 == field.getRow()
                                                          && NumberUtils.isNumber(field.getValue().trim())))
                        {
                             break; //This shouldn't be printed if its present
                        } else if (field.getValue().contains("ROAD REWARD") && !isWillCall() )
                        {
                            field.setRow(140);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }else if (field.getValue().contains("WILL CALL - AGENT") )
                        {
                            setWillCall(true);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }else if (field.getValue().contains("PSGR:") /* && isWillCall()*/)
                        {
                            field.setRow(550);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }else if (field.getValue().contains("1ST TKT#:")  /*&& isWillCall()*/)
                        {
                            field.setRow(550);
                            ticketFormat.printText(ticket, field, true);
                            break;
                        }else
                        {
                            ticketFormat.printText(ticket, field, scaled);
                            break;
                        }
                    default:
                            break;
                }
            }
        }

        switch (format) {
            case ZPL:
                cb.addTemplate(ticket, 36, 500);
                break;
            case FGL:
                cb.addTemplate(ticket, 36, 450);
                break;
            default:
                if(scaled) {
                    cb.addTemplate(ticket, 36, 500);
                } else {
                    cb.addTemplate(ticket, 36, 450);
                }
                break;
        }

        cb.saveState();
        if(barcode != null)
        {
            cb.addTemplate(getWarning(cb, barcode, resource), 36, 410);
        }

        cb.setLineDash(6, 3);
        cb.moveTo(36, 410);
        cb.lineTo(580, 410);
        cb.stroke();
        cb.restoreState();

        Font font = FontFactory.getFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED, 12.0f);

        if(customTermsFlag){
            try {
                cb.addTemplate(getCustomTerms(cb), 36, 36);
            }catch(Exception e){
            }
        }
        else
        {
            cb.addTemplate(getDisclaimer(cb), 36, 220);

            if (logo != null)
            {
                Image logoImage = Image.getInstance(logo);
                if ((document.getPageSize().getWidth() - 36 * 2) < logoImage.getWidth())
                {
                    logoImage.setAbsolutePosition(36, 70);
                    logoImage.scaleAbsoluteWidth(document.getPageSize().getWidth() - 36 * 2);
                }
                else
                {
                    logoImage.setAbsolutePosition(((document.getPageSize().getWidth() - 36 * 2 - logoImage.getWidth()) / 2) + 36, 70);
                }

                logoImage.setAlignment(Image.MIDDLE);
                cb.addImage(logoImage);
            }
        }
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
                        if(field.getValue().length() == 8 && field.getValue().matches("[0-9]{8}"))
                        {
                            setConfirmation(field);
                        }
                        else if(field.getValue().length() == 4 && field.getValue().matches("[0-9]{4}"))
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
        barcode39.setSize(15);
        barcode39.setTextAlignment(Element.ALIGN_MIDDLE);

        return barcode39;
    }


    private PdfTemplate getWarning(PdfContentByte cb, Barcode barcode, ResourceBundle resource)
    {
        PdfTemplate template = cb.createTemplate(540, 75);

        try {
            PdfPTable table = new PdfPTable(3);
            table.setTotalWidth(540);
            table.setLockedWidth(true);
            table.setWidths(new float[]{230f, 110f, 200f});

            Font font = FontFactory.getFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED, 8.0f);

            PdfPCell spacer = new PdfPCell();
            spacer.setBorder(Rectangle.NO_BORDER);
            table.addCell(spacer);

            PdfPCell bc = new PdfPCell(barcode.createImageWithBarcode(cb, null, null));
            bc.setBorder(Rectangle.NO_BORDER);
            bc.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(bc);

            ColumnText column = new ColumnText(template);
            column.setSimpleColumn(0, 0, 540, 75);
            column.addElement(table);
            column.go();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return template;
    }

    private PdfTemplate getDisclaimer(PdfContentByte cb)
    {
        PdfTemplate template = cb.createTemplate(520, 154);

        try
        {
            Font font = FontFactory.getFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED, 6.0f);

            ColumnText column = new ColumnText(template);
            column.setSimpleColumn(0, 0, 520, 154);

            Paragraph p1 = new Paragraph("Passenger agrees baggage is checked subject to governing tariffs or the following contract of carriage. Liability limited to $250 per adult ticket and $125 per child fare ticket, except that a greater value may be declared and purchased on baggage in interstate travel up to a maximum liability of $1,000. Baggage claims must be supported by this check, I.D. check of transportation ticket, and excess value declaration (if any). Late claim of baggage is subject to storage charges. Note: Passenger's ticket receipts must accompany baggage claims. THIS CHECK MUST BE SURRENDERED IN ORDER TO OBTAIN BAGGAGE. \"Under Government Regulations and Carrier's tariffs all baggage must be properly identified.\" YOUR BAGGAGE MUST BE CLAIMED PROMPTLY ON ARRIVAL. STORAGE CHARGES WILL BE ASSESSED AND COLLECTED FOR LATE CLAIM OF BAGGAGE. Note: Passenger's ticket receipts must accompany baggage claims.", font);
            p1.setLeading(0f);
            p1.setMultipliedLeading(0.9f);
            column.addElement(p1);
            column.go();

            Paragraph p2 = new Paragraph("SMOKING PROHIBITED.", font);
            p2.setLeading(0f);
            p2.setMultipliedLeading(0.9f);
            column.addElement(p2);
            column.go();

            Paragraph p3 = new Paragraph("ISSUING CARRIER WILL BE RESPONSIBLE ONLY FOR TRANSPORTATION ON ITS OWN LINES, in accordance with tariff regulations and limitations, AND ASSUMES NO RESPONSIBILITY FOR ANY ACTS OR OMISSIONS OF OTHERS OCCURRING WITHIN OR OUTSIDE THE UNITED STATES, except as imposed by law with respect to baggage. Seating aboard vehicles operated in interstate or foreign commerce is without regard to race, color, creed, or national origin. ONE WAY FARES LIMITED TO 90 DAYS, ROUND TRIP FARES LIMITED TO 1 YEAR, SPECIAL FARES LIMITED AS ENDORSED.NOTICE: INTERSTATE BAGGAGE LIABILITY. Liability for loss of or damage to checked Baggage is limited to actual value not to exceed $250 per Adult Fare or $125 per Child Fare, unless greater value is declared and paid each time baggage is checked. The maximum declared value cannot exceed $1000 per passenger. Excess value purchased does not cover valuable articles and certain articles are not accepted as baggage (ask agent for information). Excess value coverage may be purchased at the ticket counter. Ask agent for information regarding limitations on the value of baggage checked intrastate. Carrier will not accept any liability for unchecked baggage.PLACE YOUR NAME AND ADDRESS IN AND ON YOUR BAGGAGE. Government regulations and Carrier's Tariffs require that all Baggage must be properly identified. Baggage I.D. Tags should clearly show the name and address to which lost baggage should be forwarded. Free Baggage ID tags are available at all Ticket Windows and Baggage Counters. Additionally, the passenger's name and address must be on the inside of all baggage.NOTICE: INTERSTATE EXPRESS LIABILITY (NOT NEGOTIABLE) SUBJECT TO TARIFF REGULATIONS LIABILITY. This carrier will not pay loss or damage claims over $100 per shipment or $50 per package up to the limit allowed, whichever is greater, unless a grater value is declared and charges for such greater value paid. Maximum valuation on any one shipment is limited by tariff (See tariff for intrastate exceptions. In no event shall the Carrier be liable for CONSEQUENTIAL or INCIDENTAL DAMAGES for loss, damage, or delay.PASSENGERS TRAVELING TO CANADA OR MEXICO MUST HAVE PROPER TRAVEL DOCUMENTS WHICH MAY BE CHECKED AT OR PRIOR TO BOARDING A SCHEDULE DEPARTING INTO CANADA OR MEXICO AND WHICH WILL BE REQUIRED FOR ENTRY INTO CANADA OR MEXICO.", font);
            p3.setLeading(0f);
            p3.setMultipliedLeading(0.9f);
            column.addElement(p3);
            column.go();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return template;
    }

    private PdfTemplate getCustomTerms(PdfContentByte cb)
    {
        PdfTemplate template = cb.createTemplate(520, 360);

        try
        {
            Font font = FontFactory.getFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED, 8.0f); //6 before

            Phrase phrase = new Phrase();
            phrase.add(new Chunk(getCustomTerms(),font));


            ColumnText column = new ColumnText(template);
            column.setSimpleColumn(0, 0, 520, 360);
            Paragraph p1 = new Paragraph(phrase);
            p1.setLeading(0f);
            p1.setMultipliedLeading(0.9f);
            column.addElement(p1);
            column.go();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return template;
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

    public String getCustomTerms() {
        return customTerms;
    }

    public void setCustomTerms(String customTerms) {
        this.customTerms = customTerms;
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

    public boolean isCanadaFlag() {
        return canadaFlag;
    }

    public void setCanadaFlag(boolean canadaFlag) {
        this.canadaFlag = canadaFlag;
    }

    public boolean isWillCall() {
        return willCall;
    }

    public void setWillCall(boolean willCall) {
        this.willCall = willCall;
    }
}
