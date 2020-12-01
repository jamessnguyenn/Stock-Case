package edu.sjsu.android.stockmarketviewer;

/**
 * Stock Info class which will give details about the stock
 */
public class StockInfo {
    private String stockSymbol;
    private String stockName;

    public StockInfo(String stockSymbol, String stockName){
        this.stockName = stockName;
        this.stockSymbol = stockSymbol;
    }
    public String getSymbol(){
        return stockSymbol;
    }
    public String getName(){
        return stockName;
    }
}
