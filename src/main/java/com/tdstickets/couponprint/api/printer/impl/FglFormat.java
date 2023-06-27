package com.tdstickets.couponprint.api.printer.impl;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfTemplate;
import com.tdstickets.couponprint.api.dto.CouponField;
import com.tdstickets.couponprint.api.printer.FormatType;
import com.tdstickets.couponprint.api.printer.ITicketFormat;

import java.io.IOException;

/**
 * FglFormat
 */
public class FglFormat implements ITicketFormat
{
	@Override
	public void printText(PdfTemplate ticket, CouponField field, boolean scaled) throws IOException, DocumentException
	{
		float row = (float) Math.round(field.getRow() * (.355));
		float col = (float) Math.round(field.getColumn() * (.345));

		ticket.beginText();
		ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 8.0f);
		ticket.setHorizontalScaling((115.0f));

		if (field.getFont() != null) {
			switch (field.getFont()) {
				case 20:
					ticket.setHorizontalScaling((115.0f));
					ticket.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED), 12.0f);
					break;
				case 19:
					ticket.setHorizontalScaling((55.0f));
					ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 16.0f);
					break;
				case 17:
				default:
					ticket.setFontAndSize(BaseFont.createFont(BaseFont.COURIER_BOLD, BaseFont.CP1252, BaseFont.EMBEDDED), 8.0f);
					break;
			}
		}

		ticket.moveText(col, ((242) - row));
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

		if (longBarcode){
			switch(format) {
				case FGL:
					return isZebra ? 20 : 50;
				default:
					return 50;
			}
		}else {
			switch (format) {
				case FGL:
					return isZebra ? 75 : 90;
				default:
					return 125;
			}
		}
	}
}
