package com.OOP.springboot.mongodb.service.utils;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class UnitConverter {
    private static final Map<String, Double> TonnesToBarrelsRates = new HashMap<String, Double>() {{
        put("Crude petroleum oil", 7.33);
        put("Naphtha", 9.0);
        put("Kerosene", 7.313);
        put("Diesel oil", 7.46);
        put("Natural gases", 6.842);
        put("Gasoline", 6.9);
    }};

    private static final Map<String, Double> KilolitresToBarrelsRates = new HashMap<String, Double>() {{
        put("Crude Oil", 6.2898);
        put("Condensate", 7.542);
        put("Gasoline Oil", 0.843);
    }};

    public String convertToKbd(String qty, int multiplier, String unit, String commodity, String year, String month) {
        double quantity = getQuantity(qty, multiplier);
        double rate = getRate(unit, commodity);
        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        int days = yearMonthObject.lengthOfMonth();
        double kbd = quantity * rate / days / 1000;
        return String.format("%.5f", kbd);
    }

    public double getQuantity(String qty, int multiplier) {
        String quantity = qty.replaceAll(",", "");
        return Double.parseDouble(quantity) * multiplier;
    }

    public double getRate(String unit, String commodity) {
        double rate;
        switch(unit) {
            case "T":
                rate = TonnesToBarrelsRates.get(commodity);
                break;
            case "KL":
                rate = KilolitresToBarrelsRates.get(commodity);
                break;
            default:
                rate = 1.0;
                break;
        }
        return rate;
    }
}
