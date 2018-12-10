package com.cuit.dao;

import org.junit.jupiter.api.Test;

import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class DB2connTest {

    /*public static void main(String[] args) throws Exception {
       // System.out.println(new DB2conn().getColName("SK_WAT_HOUR_SUMDATA_"));
        //System.out.println(new DB2conn().getDistinctValue("YWWATER.SK_WAT_HOUR_SUMDATA_","STATION_CODE_"));
    }*/
    @Test
    void getData() {
        try {
            System.out.println(new DB2conn().getData("YWWATER.SK_WAT_HOUR_SUMDATA_","AVG_VALUE_","成都水六厂","水温"));
        } catch (DataFormatException e) {
            System.out.println(e.getMessage());
        }
    }
}