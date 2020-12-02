package edu.sjsu.android.stockmarketviewer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main activity which holds the favorites list and auto complete text form
 */
public class MainActivity extends AppCompatActivity {
    private AutoCompleteTextView inputSymbol;
    private JSONParser jsonParser;
    private AutoCompleteAdapter autoCompleteAdapter;
    private ArrayList<StockInfo> stockInfos = new ArrayList<StockInfo>();
    private Button clearButton;
    private Button getQuoteButton;
    private ArrayList<String> sharedPreferences;
    private RecyclerView.Adapter favoritesAdapter;
    private RecyclerView favoritesRecyclerView;
    private ArrayList<StockFavorite> favoritesDataSet = new ArrayList<StockFavorite>();
    private RecyclerView.LayoutManager favoriteLayoutManager;
    private ImageButton refreshButton;
    private Switch refreshSwitch;
    private Boolean refresh = false;
    Runnable runnable;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        runnable = new Runnable() {
            public void run() {
                new GetFavoritesSymbolsNoBar().execute();
                if (refresh) {
                    Log.d("Background Refresh", "THIS IS RUNNING");
                    mHandler.postDelayed(this, 10000);
                }
            }
        };

        setContentView(R.layout.activity_main);
        jsonParser = new JSONParser();
        inputSymbol = findViewById(R.id.autoCompleteTextView);
        refreshButton = findViewById(R.id.imageButton);
        refreshSwitch = findViewById(R.id.refreshSwitch);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setRepeatCount(Animation.INFINITE);
                rotateAnimation.setRepeatMode(Animation.RESTART);
                rotateAnimation.setDuration(500);
                refreshButton.startAnimation(rotateAnimation);
                new GetFavoritesSymbols().execute();

            }
        });
        autoCompleteAdapter = new AutoCompleteAdapter(getApplicationContext(), R.layout.auto_complete_layout, stockInfos);
        inputSymbol.setAdapter(autoCompleteAdapter);
        inputSymbol.setThreshold(3);
        inputSymbol.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (inputSymbol.isPerformingCompletion()) {
                    return;
                }
                if (charSequence.length() >= 3) {
                    new QuerySymbolTask().execute(charSequence.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputSymbol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView text = view.findViewById(R.id.stockSymbol);
                inputSymbol.setText(text.getText());
            }
        });
        clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSymbol.setText("");
            }
        });
        getQuoteButton = findViewById(R.id.getButton);
        getQuoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputSymbol.getText().toString().length() == 0) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setMessage("Please enter a Stock Name/Symbol");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog.show();
                } else {
                    new GetSymbolInfoTask().execute(inputSymbol.getText().toString());
                }

            }
        });
        sharedPreferences = new ArrayList<String>();
        if (getSharedPreferences() != null) {
            sharedPreferences.addAll(Arrays.asList(getSharedPreferences().split(",")));
        }
        favoriteLayoutManager = new LinearLayoutManager(this);
        favoritesRecyclerView = findViewById(R.id.favoritesList);
        favoritesRecyclerView.setLayoutManager(favoriteLayoutManager);
        favoritesAdapter = new FavoritesAdapter(favoritesDataSet);
        favoritesRecyclerView.setAdapter(favoritesAdapter);
        new GetFavoritesSymbols().execute();
        refreshSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    refresh = true;
                    mHandler.post(runnable);
                } else {
                    mHandler.removeCallbacksAndMessages(null);
                    refresh = false;
                }
            }
        });
    }
    @Override
    protected void onPause(){
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        sharedPreferences.clear();
        if (getSharedPreferences() != null) {
            sharedPreferences.addAll(Arrays.asList(getSharedPreferences().split(",")));
        }
        new GetFavoritesSymbols().execute();
        if(refreshSwitch.isChecked() == true){
            refresh = true;
            mHandler.post(runnable);
        }else{
            refresh = false;
        }
    }

    public String getSharedPreferences() {
        SharedPreferences symbols = getSharedPreferences("symbols", MODE_PRIVATE);
        return symbols.getString("symbols", null);
    }

    private class QuerySymbolTask extends AsyncTask<String, Void, Void> {
        List<StockInfo> stocks = new ArrayList<StockInfo>();

        @Override
        protected Void doInBackground(String... strings) {
            JSONArray result = jsonParser.querySymbol(strings[0]);
            if (result == null) {
                return null;
            }
            for (int i = 0; i < result.length(); i++) {
                try {
                    JSONObject stockResult = result.getJSONObject(i);
                    stocks.add(new StockInfo(stockResult.getString("ticker"), stockResult.getString("name")));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            stockInfos.clear();
            stockInfos.addAll(stocks);
            autoCompleteAdapter.notifyDataSetChanged();
        }
    }

    private class GetSymbolInfoTask extends AsyncTask<String, Void, Void> {
        boolean exists = true;
        RelativeLayout loading = findViewById(R.id.progressBarView);
        JSONArray result;
        JSONArray historicalData;
        JSONObject companyDescription;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getQuoteButton.setEnabled(false);
            clearButton.setEnabled(false);
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            result = jsonParser.getCurrentStock(strings[0]);
            if (result == null) {
                exists = false;
                return null;
            }
            if (result.length() == 0) {
                exists = false;
            } else {
                historicalData = jsonParser.getHistoricalData(strings[0]);
                if (historicalData == null) {
                    exists = false;
                    return null;
                }
                companyDescription = jsonParser.getCompanyInfo(strings[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (exists == false) {
                super.onPostExecute(aVoid);
                getQuoteButton.setEnabled(true);
                clearButton.setEnabled(true);
                loading.setVisibility(View.GONE);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setMessage("Invalid Symbol");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.show();
            } else {
                super.onPostExecute(aVoid);
                loading.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), StockDetailsActivity.class);
                Bundle dataBundle = new Bundle();

                try {
                    dataBundle.putString("stockDetails", result.getJSONObject(0).toString());
                    dataBundle.putString("historicalData", historicalData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    getQuoteButton.setEnabled(true);
                    clearButton.setEnabled(true);
                    return;
                }
                dataBundle.putString("companyInfo", companyDescription.toString());
                intent.putExtras(dataBundle);
                startActivity(intent);
                getQuoteButton.setEnabled(true);
                clearButton.setEnabled(true);


            }
        }
    }

    private class GetFavoritesSymbols extends AsyncTask<Void, Void, Void> {
        JSONObject companyInfo;
        JSONObject currentStock;
        ArrayList<StockFavorite> results = new ArrayList<StockFavorite>();
        RelativeLayout loading = findViewById(R.id.progressBarView);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < sharedPreferences.size(); i++) {
                companyInfo = jsonParser.getCompanyInfo(sharedPreferences.get(i));
                Log.d("Get Favorites Symbol", "Got Company Info");
                try {
                    currentStock = jsonParser.getCurrentStock(sharedPreferences.get(i)).getJSONObject(0);
                    Double last = null;
                    Double prev = null;
                    if(!currentStock.isNull("last")) {
                        last = currentStock.getDouble("last");
                    }
                    if(!currentStock.isNull("prevClose")){
                        prev = currentStock.getDouble("prevClose");
                    }
                    results.add(new StockFavorite(companyInfo.getString("name"), companyInfo.getString("ticker"), jsonParser.getMarketCap(sharedPreferences.get(i)), last , prev));
                    Log.d("Get Favorites Symbol", "Added Favorites");
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            favoritesDataSet.clear();
            favoritesDataSet.addAll(results);
            favoritesAdapter.notifyDataSetChanged();
            loading.setVisibility(View.GONE);
            refreshButton.clearAnimation();

        }
    }

    private class GetFavoritesSymbolsNoBar extends AsyncTask<Void, Void, Void> {
        JSONObject companyInfo;
        JSONObject currentStock;
        ArrayList<StockFavorite> results = new ArrayList<StockFavorite>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < sharedPreferences.size(); i++) {
                companyInfo = jsonParser.getCompanyInfo(sharedPreferences.get(i));
                try {
                    currentStock = jsonParser.getCurrentStock(sharedPreferences.get(i)).getJSONObject(0);
                    Double last = null;
                    Double prev = null;
                    if(!currentStock.isNull("last")) {
                        last = currentStock.getDouble("last");
                    }
                    if(!currentStock.isNull("prevClose")){
                        prev = currentStock.getDouble("prevClose");
                    }
                    results.add(new StockFavorite(companyInfo.getString("name"), companyInfo.getString("ticker"), jsonParser.getMarketCap(sharedPreferences.get(i)), last , prev));
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            favoritesDataSet.clear();
            favoritesDataSet.addAll(results);
            favoritesAdapter.notifyDataSetChanged();
        }
    }

    public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
        private List<StockFavorite> stockFavoriteList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView companySymbol;
            public TextView companyPrice;
            public TextView companyPercentage;
            public TextView companyName;
            public TextView favoriteVolume;
            public View layout;

            public ViewHolder(View v) {
                super(v);
                layout = v;
                companySymbol = (TextView) v.findViewById(R.id.companySymbol);
                companyPrice = (TextView) v.findViewById(R.id.companyPrice);
                companyPercentage = (TextView) v.findViewById(R.id.companyPercentage);
                companyName = (TextView) v.findViewById(R.id.favoriteCompanyName);
                favoriteVolume = (TextView) v.findViewById(R.id.favoriteVolume);
            }
        }

        public FavoritesAdapter(List<StockFavorite> stockFavoriteList) {
            this.stockFavoriteList = stockFavoriteList;
        }

        public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.favorites_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        public void onBindViewHolder(ViewHolder holder, final int position) {
            String companySymbol = stockFavoriteList.get(position).getStockSymbol();
            String marketCap = stockFavoriteList.get(position).getMarketCap();
            String latestPrice = stockFavoriteList.get(position).getLatestPrice();
            String companyPercentage = stockFavoriteList.get(position).getPercentage();
            String companyName = stockFavoriteList.get(position).getStockName();

            holder.companySymbol.setText(companySymbol);
            holder.companyPrice.setText(latestPrice);
            if (companyPercentage.contains("-")) {
                holder.companyPercentage.setBackgroundResource(R.color.light_red);
                holder.companyPercentage.setText(companyPercentage);
            } else {
                if (companyPercentage.equals("Unavailable")) {
                    holder.companyPercentage.setBackgroundResource(R.color.light_yellow);
                    holder.companyPercentage.setText("Unavailable");
                } else {
                    holder.companyPercentage.setBackgroundResource(R.color.light_green);

                    holder.companyPercentage.setText("+" + companyPercentage);
                }
            }
            holder.favoriteVolume.setText("Market Cap: " + marketCap);
            holder.companyName.setText(companyName);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new GetSymbolInfoTask().execute(companySymbol);
                }
            });
        }

        public int getItemCount() {
            return stockFavoriteList.size();
        }
    }

}