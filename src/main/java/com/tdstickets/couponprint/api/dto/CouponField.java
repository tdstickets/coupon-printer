// File:            CouponField.java
// Created:         11/15/13
// Last Modified:   $Date$
// Revision:        $Rev$
// Author:          <a href="mailto:ikleynshteyn@etranscor.com">Izabella Kleynshteyn</a>
//
// (c) 2013 Transcor, Inc.
package com.tdstickets.couponprint.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * CouponField
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CouponField implements Serializable
{
    private static final long serialVersionUID = -5328319912593849029L;

    public enum Type {
        TEXT,BARCODE,ROTATED_TEXT,BARCODE2OF5,BARCODE3OF9,ROTATED_BARCODE
    }

    private Type type;
    private String key;
    private Integer column;
    private Integer row;
    private Integer font;
    private String value;

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public Integer getColumn()
    {
        return column;
    }

    public void setColumn(Integer column)
    {
        this.column = column;
    }

    public Integer getRow()
    {
        return row;
    }

    public void setRow(Integer row)
    {
        this.row = row;
    }

    public Integer getFont()
    {
        return font;
    }

    public void setFont(Integer font)
    {
        this.font = font;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "CouponField{" +
                "type=" + type +
                ", key='" + key + '\'' +
                ", column=" + column +
                ", row=" + row +
                ", font=" + font +
                ", value='" + value + '\'' +
                '}';
    }
}
