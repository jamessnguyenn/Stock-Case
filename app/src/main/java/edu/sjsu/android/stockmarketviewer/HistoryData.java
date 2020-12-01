package edu.sjsu.android.stockmarketviewer;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * History Data class containing information about the history
 */
public class HistoryData {
    private String closePrice;
    private String volume;
    private String date;

    public HistoryData(String closePrice, String volume, String date) {
        this.closePrice = NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(Double.parseDouble(closePrice));
        this.volume = volume;
        this.date = date;

    }

    public String getClosePrice() {
        return closePrice;
    }

    public String getDate() {
        return date;
    }

    public String getVolume() {
        return volume;
    }
}
