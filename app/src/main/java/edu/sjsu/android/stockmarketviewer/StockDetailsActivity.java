package edu.sjsu.android.stockmarketviewer;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;


public class StockDetailsActivity extends AppCompatActivity {
    private List<HistoryData> historyData = new ArrayList<HistoryData>();
    private JSONObject stockDetails;
    private JSONObject companyInfo;
    private JSONArray historicalData;
    private RecyclerView.Adapter stockDetailsAdapater;
    private RecyclerView.LayoutManager stocklayoutManager;
    private RecyclerView stockDetailsRecyclerView;
    private RecyclerView historyDataRecyclerView;
    private RecyclerView.Adapter historyDataAdapter;
    private RecyclerView.LayoutManager historyLayoutManager;
    private CheckBox favoriteButton;
    private String symbol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<StockDetails> currentStock = new ArrayList<StockDetails>();
        setContentView(R.layout.stock_details_activity);
        Intent recievedIntent = getIntent();
        Bundle results = recievedIntent.getExtras();
        stockDetailsRecyclerView = (RecyclerView) findViewById(R.id.currentStock);
        stockDetailsRecyclerView.hasFixedSize();
        stocklayoutManager = new LinearLayoutManager(this);
        stockDetailsRecyclerView.setLayoutManager(stocklayoutManager);
        try {
            stockDetails = new JSONObject(results.getString("stockDetails"));
            companyInfo = new JSONObject(results.getString("companyInfo"));
            historicalData = new JSONArray(results.getString("historicalData"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TextView stockName = findViewById(R.id.companyName);
        try {
            symbol = companyInfo.getString("ticker");
            stockName.setText(companyInfo.getString("name")+" ("+ companyInfo.getString("exchangeCode")+":"+companyInfo.getString("ticker") +")" );
            currentStock.add(new StockDetails("Latest Price", stockDetails.getString("last")));
            currentStock.add(new StockDetails("Previous Close", stockDetails.getString("prevClose")));
            currentStock.add(new StockDetails("High", stockDetails.getString("high")));
            currentStock.add(new StockDetails("Mid", stockDetails.getString("mid")));
            currentStock.add(new StockDetails("Low", stockDetails.getString("low")));
            currentStock.add(new StockDetails("Open", stockDetails.getString("open")));
            currentStock.add(new StockDetails("Last Size", stockDetails.getString("lastSize")));
            currentStock.add(new StockDetails("Bid Price", stockDetails.getString("bidPrice") ));
            currentStock.add(new StockDetails("Bid Size", stockDetails.getString("bidSize")));
            currentStock.add(new StockDetails("Ask Size", stockDetails.getString("askSize")));
            currentStock.add(new StockDetails("Ask Price", stockDetails.getString("askPrice")));
            currentStock.add(new StockDetails("Volume", stockDetails.getString("volume")));
            stockDetailsRecyclerView.addItemDecoration(new DividerItemDecoration(stockDetailsRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
            stockDetailsAdapater = new StockDetailsAdapter(currentStock);
            stockDetailsRecyclerView.setAdapter(stockDetailsAdapater);
            historyDataRecyclerView = findViewById(R.id.historicalData);
            historyDataRecyclerView.hasFixedSize();
            historyLayoutManager = new LinearLayoutManager(this);
            historyDataRecyclerView.setLayoutManager(historyLayoutManager);
            historyDataRecyclerView.addItemDecoration(new DividerItemDecoration(historyDataRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

            if(historicalData.length()==0){
                TextView noDataText = findViewById(R.id.noDataText);
                noDataText.setVisibility(View.VISIBLE);
            }else {
                for (int i = 0; i < historicalData.length(); i++) {
                    JSONObject data = historicalData.getJSONObject(i);
                    historyData.add(new HistoryData(data.getString("close"), data.getString("volume"), data.getString("date").substring(0, 10)));
                }
            }
            historyDataAdapter = new HistoricalDataAdapter(historyData);
            historyDataRecyclerView.setAdapter(historyDataAdapter);
            Calendar calendar = Calendar.getInstance();
            String currentDate = ""+ (calendar.get(Calendar.MONTH)+1) +"/"+calendar.get(Calendar.DAY_OF_MONTH)+ "/"+ calendar.get(Calendar.YEAR);
            calendar.add(Calendar.MONTH,-1);
            String startDate = ""+ (calendar.get(Calendar.MONTH)+1) +"/"+calendar.get(Calendar.DAY_OF_MONTH)+ "/"+ calendar.get(Calendar.YEAR);
            TextView dateRange = findViewById(R.id.historicalDateRange);
            dateRange.setText(startDate+" - " + currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences symbols = getSharedPreferences("symbols", MODE_PRIVATE);
        String symbolData = symbols.getString("symbols",null);
        ArrayList sharedPreferences = new ArrayList<String>();
        if(symbolData != null){
            sharedPreferences.addAll(Arrays.asList(symbolData.split(",")));
        }
        favoriteButton = findViewById(R.id.favoriteButton);
        if (sharedPreferences.contains(symbol)){
            favoriteButton.setChecked(true);
        }
        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    SharedPreferences symbols = getSharedPreferences("symbols", MODE_PRIVATE);
                    SharedPreferences.Editor editor = symbols.edit();
                    sharedPreferences.add(symbol);
                    editor.putString("symbols", TextUtils.join(",",sharedPreferences));
                    editor.commit();
                }else{
                    SharedPreferences symbols = getSharedPreferences("symbols", MODE_PRIVATE);
                    SharedPreferences.Editor editor = symbols.edit();
                    sharedPreferences.remove(symbol);
                    if(TextUtils.join(",",sharedPreferences).equals("")){
                        editor.remove("symbols");
                    }else{
                    editor.putString("symbols", TextUtils.join(",",sharedPreferences));
                    }
                    editor.commit();
                }
            }
        });


    }

}
