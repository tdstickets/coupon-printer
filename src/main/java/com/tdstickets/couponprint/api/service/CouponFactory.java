package com.tdstickets.couponprint.api.service;

import com.tdstickets.couponprint.api.dto.CouponField;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CouponFactory
{
  enum FontType {
    NORMAL, BIG, BOLD, OCR, ROTATE, SMALL
  }

  private static final CouponFactory instance = new CouponFactory();

  private static final Pattern FO = Pattern.compile("^(\\^FO[0-9]+,[0-9]+).*");
  private static final Pattern FD = Pattern.compile(".(\\^FD.*?)\\^");

  private static CouponFactory getInstance()
  {
    return instance;
  }

  public static List<CouponField> parseCoupon(byte[] data)
  {
    return getInstance()._parseCoupon(data);
  }

  private List<CouponField> _parseCoupon(byte[] data)
  {
    List<CouponField> couponFields = new ArrayList<>();

    try
    {
      if( (char) data[0] == '<')
      {
        couponFields = fglCoupon(data);
      }
      else if( (char) data[0] == '^')
      {
        couponFields = zplCoupon(data);
      }
      else
      {
        couponFields = glCoupon(data);
      }

    }
    catch(Exception e) {
      e.printStackTrace();
    }

    for(CouponField field: couponFields)
    {
      if(field.getValue().contains("DOC"))
      {
        field.setValue(field.getValue().replaceAll("\\d", "X"));
      }
    }

    return couponFields;
  }

  private List<CouponField> fglCoupon(byte[] data) throws IOException
  {
    CouponState state = new CouponState();
    StringBuilder command = new StringBuilder();
    StringBuilder fieldValue = new StringBuilder();

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));

    List<CouponField> fields = new ArrayList<>();

    boolean inCommand = false;

    int i;
    while ((i = bis.read()) > -1)
    {
      if( (char) i == '<')
      {
        inCommand = true;

        if(fieldValue.length() > 0)
        {
          CouponField field = new CouponField();
          field.setColumn(state.getColumn());
          field.setRow((state.getFont() == 19) ? state.getRow() + 21 : state.getRow());
          field.setFont(state.getFont());
          field.setType(state.getType());
          field.setKey(Integer.toString(state.getRow()) + Integer.toString(state.getColumn()));
          field.setValue(fieldValue.toString());
          fields.add(field);
        }
      }

      if( (char) i == '>') {
        inCommand = false;

        fieldValue.delete(0, fieldValue.length());

        String cmd = command.append((char) i).toString();
        if(cmd.matches("<F\\d+>"))
        {
          if(cmd.equals("<F8>"))
          {
            state.setFont(17);
          }

          if(cmd.equals("<F6>"))
          {
            state.setFont(20);
          }
        }

        if(cmd.matches("<HW\\d+,\\d+>"))
        {
          Pattern pattern = Pattern.compile("(\\d+)");
          Matcher matcher = pattern.matcher(cmd);
          if(matcher.find())
          {
            int hw = Integer.parseInt(matcher.group(0));
            state.setFont(state.getFont() + ((hw == 1) ? 0 : hw));
          }
        }

        if(cmd.matches("<RC\\d+,\\d+>"))
        {
          Pattern pattern = Pattern.compile("(\\d+)");
          Matcher matcher = pattern.matcher(cmd);

          boolean isRow = true;
          while(matcher.find())
          {
            if(isRow)
            {
              state.setRow(Integer.parseInt(matcher.group()));
              isRow = false;
            }
            else
            {
              state.setColumn(Integer.parseInt(matcher.group()));
            }
          }

          state.setType(CouponField.Type.TEXT);
        }

        if(cmd.equals("<BI>"))
        {
          state.setType(CouponField.Type.BARCODE);
        }

        command.delete(0, command.length());
        continue;
      }

      if(inCommand)
      {
        command.append((char) i);
      }
      else
      {
        fieldValue.append((char) i);
      }
    }

    return fields;
  }

  private List<CouponField> zplCoupon(byte[] data) throws IOException
  {
    String coupon = new String(data, "UTF-8");

    List<CouponField> fields = new ArrayList<>();
    if(coupon.startsWith("^XA"))
    {
      coupon = coupon.substring(3);
    }

    coupon = coupon.substring(coupon.indexOf("^FO"));

    String[] elements = coupon.split("\\^FO");
    for (String field : elements) {
      fields.add(new ZplField("^FO" + field));
    }

    return fields;
  }

  private List<CouponField> glCoupon(byte[] data) throws IOException
  {
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    List<CouponField> fields = new ArrayList<>();

    int value;
    while ((value = bis.read()) > -1)
    {
      if (value == 133)
      {
        if (baos.size() > 0)
        {
          fields.add(getField(baos.toByteArray()));
          baos.flush();
          baos.reset();
        }
      }
      else
      {
        baos.write(value);
      }
    }

    baos.flush();
    baos.close();

    return fields;
  }

  private CouponField getField(byte[] field)
  {
    CouponField couponField = new CouponField();
    StringBuilder fieldBuffer = new StringBuilder();

    for (int i = 0; i < field.length; i++)
    {
      switch (i)
      {
        case 0:
          if (field[i] == 48)
          {
            couponField.setType(CouponField.Type.BARCODE);
          } else
          {
            couponField.setType(CouponField.Type.TEXT);
          }
          break;
        case 1:
          if (!couponField.getType().equals(CouponField.Type.BARCODE))
          {
            couponField.setRow(((int) field[i] - 64) * 21);
          }
          break;
        case 2:
          if (!couponField.getType().equals(CouponField.Type.BARCODE))
          {
            couponField.setColumn(((int) field[i] - 64) * 20);
          }
          break;
        case 3:
          if (!couponField.getType().equals(CouponField.Type.BARCODE))
          {
            if (field[i] > 31)
            {
              fieldBuffer.append(new String(new byte[]
                { field[i] }));
            } else
            {
              couponField.setFont((int) field[i]);
            }
          } else
          {
            fieldBuffer.append(new String(new byte[]
              { field[i] }));
          }
          break;
        default:
          fieldBuffer.append(new String(new byte[]
            { field[i] }));
          break;
      }
    }

    String fieldValue = fieldBuffer.toString();

    couponField.setValue(fieldValue);
    couponField.setKey(couponField.getColumn() + "" + couponField.getRow());

    return couponField;
  }

  private class CouponState {
    private int font = 17;
    private int row;
    private int column;
    private CouponField.Type type = CouponField.Type.TEXT;

    private int getFont() {
      return font;
    }

    private void setFont(int font) {
      this.font = font;
    }

    private int getRow() {
      return row;
    }

    private void setRow(int row) {
      this.row = row;
    }

    private int getColumn() {
      return column;
    }

    private void setColumn(int column) {
      this.column = column;
    }

    private CouponField.Type getType() {
      return type;
    }

    private void setType(CouponField.Type type) {
      this.type = type;
    }
  }

  private class ZplField extends CouponField {
    private final String field;
    private String[] fieldOrigins;
    private String value = "";

    public ZplField(String field) {
      this.field = field;

      Matcher fo = FO.matcher(field);
      if(fo.find()) {
        fieldOrigins = fo.group(1).substring(3).split(",");
      }

      Matcher fd = FD.matcher(field);
      if(fd.find()) {
        value = fd.group(1).substring(3);
      }
    }

    @Override
    public Integer getFont() {
      switch(getFontType()) {
        case BOLD:
          return 20;
        case BIG:
          return 19;
        default:
          return 17;
      }
    }

    public CouponField.Type getType() {
      if(field.contains("^B3N,N,80,N,N")) {
        return CouponField.Type.BARCODE3OF9;
      }
      else if(field.contains("^B2N,60,N,N,N")) {
        return CouponField.Type.BARCODE;
      }
      else if(field.contains("^ACN,18,10")) {
        return Type.ROTATED_TEXT;
      }
      else {
        return CouponField.Type.TEXT;
      }
    }

    public Integer getRow()
    {
      return fieldOrigins == null ? 0 : Integer.parseInt(fieldOrigins[0]);
    }

    public Integer getColumn()
    {
      return fieldOrigins == null ? 0 : Integer.parseInt(fieldOrigins[1]);
    }

    public String getValue()
    {
      return value;
    }

    public FontType getFontType() {
      if(field.contains("^AC,36,20")) {
        return FontType.BOLD;
      }

      if(field.contains("^AC,36,10")) {
        return FontType.BIG;
      }

      if(field.contains("^AH,15,15")) {
        return FontType.OCR;
      }

      if(field.contains("^AC,18,10")) {
        return FontType.NORMAL;
      }

      if(field.contains("^ACN,18,10")) {
        return FontType.ROTATE;
      }

      if(field.contains("^A0,20,20")) {
        return FontType.SMALL;
      }

      return FontType.NORMAL;
    }
  }
}
