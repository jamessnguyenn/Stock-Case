package edu.sjsu.android.stockmarketviewer;

/**
 * Stock details which contains info pertaining to the stock details
 */
public class StockDetails {
    private String detailsTitle;
    private String detailsData;

    public StockDetails(String detailsTitle, String detailsData){
        this.detailsTitle = detailsTitle;
        this.detailsData = detailsData;
    }

    public String getDetailsData() {
        return detailsData;
    }

    public String getDetailsTitle() {
        return detailsTitle;
    }
}
