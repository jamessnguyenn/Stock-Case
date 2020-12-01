package edu.sjsu.android.stockmarketviewer;

public class StockFavorite {
    private String stockName;
    private String stockSymbol;
    private String volume;
    private String latestPrice;
    private String percentage;


    public StockFavorite(String stockName, String stockSymbol, String volume, String latestPrice, String prevPrice){
        this.stockName = stockName;
        this.stockSymbol = stockSymbol;
        if(volume == "null"){
            this.volume = "-";
        }else {
            this.volume = volume;
        }
        if(latestPrice == "null" || prevPrice == "null"){
            this.latestPrice = "-";
            this.percentage = "Unavailable";
        }else {
            this.latestPrice = "$ " + String.format("%.2f", Double.parseDouble(latestPrice));
            this.percentage = String.format("%.2f", Math.round(((Double.parseDouble(latestPrice) - Double.parseDouble(prevPrice)) / Double.parseDouble(prevPrice)) * 10000.0) / 100.0) + "%";
        }
    }

    public String getVolume() {
        return volume;
    }

    public String getLatestPrice() {
        return latestPrice;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getStockName() {
        return stockName;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }
}
