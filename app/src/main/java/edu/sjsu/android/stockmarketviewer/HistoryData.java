package edu.sjsu.android.stockmarketviewer;

import java.math.RoundingMode;
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
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        nf.setRoundingMode(RoundingMode.HALF_UP);
        this.closePrice = nf.format(Double.parseDouble(closePrice));
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
