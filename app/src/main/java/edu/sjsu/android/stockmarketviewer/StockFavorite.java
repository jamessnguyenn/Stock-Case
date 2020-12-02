package edu.sjsu.android.stockmarketviewer;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Stock Favorites class to hold information about the Stock Favorites
 */
public class StockFavorite {
    private String stockName;
    private String stockSymbol;
    private String marketCap;
    private String latestPrice;
    private String percentage;


    public StockFavorite(String stockName, String stockSymbol, Double marketCap, Double latestPrice, Double prevPrice){
        Log.d("STOCK FAVORITES CALLS", "STARTING" );
        this.stockName = stockName;
        this.stockSymbol = stockSymbol;
        if(marketCap == null){
            this.marketCap = "Not Available";
        }else {
            if(marketCap>1000000000.0){
               this.marketCap = String.format("%.2f",marketCap/1000000000.0) + " Billion";
           }else if(marketCap>1000000.0){
               this.marketCap = String.format("%.2f", marketCap/1000000) + " Million";
           }else{
               this.marketCap = String.format("%.2f", marketCap);
           }
        }
        if(latestPrice == null || prevPrice == null){
            this.latestPrice = "-";
            this.percentage = "Unavailable";
        }else {
            this.latestPrice = "$ " + String.format("%.2f", latestPrice);
            this.percentage = String.format("%.2f", Math.round(((latestPrice - prevPrice) /prevPrice) * 10000.0) / 100.0) + "%";
        }
        Log.d("STOCK FAVORITES CALLS", "ENDING" );
    }

    public String getMarketCap() {
        return marketCap;
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
