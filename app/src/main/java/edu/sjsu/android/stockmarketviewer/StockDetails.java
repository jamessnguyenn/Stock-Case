package edu.sjsu.android.stockmarketviewer;

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
