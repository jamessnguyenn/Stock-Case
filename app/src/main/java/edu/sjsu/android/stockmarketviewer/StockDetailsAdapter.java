package edu.sjsu.android.stockmarketviewer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Stock Details Adapter for Stock Details Recycler View
 */
public class StockDetailsAdapter extends RecyclerView.Adapter<StockDetailsAdapter.ViewHolder> {
    private List<StockDetails> stockDetails;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView detailTitle;
        public TextView detailData;
        public View layout;

        public ViewHolder(View v){
            super(v);
            layout = v;
            detailTitle = (TextView) v.findViewById(R.id.detailTitle);
            detailData = (TextView) v.findViewById(R.id.detailData);
        }
    }
    public StockDetailsAdapter(List<StockDetails>stockDetailsDataSet){
        stockDetails = stockDetailsDataSet;
    }

    public StockDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.current_stock_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, final int position){
        String title= stockDetails.get(position).getDetailsTitle();
        String data=stockDetails.get(position).getDetailsData();
        if(data == "null"){
            holder.detailData.setText("-");
        }else {
            if(!(title.equals("Ask Size") || title.equals("Last Size") || title.equals("Volume") ||title.equals("Bid Size"))){
               data =""+ NumberFormat.getCurrencyInstance(new Locale("en", "US")).format(Double.parseDouble(data));
            }
            holder.detailData.setText(data);
        }
        holder.detailTitle.setText(title);
    }
    public int getItemCount(){
        return stockDetails.size();
    }
}
