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

public class HistoricalDataAdapter extends RecyclerView.Adapter<HistoricalDataAdapter.ViewHolder> {
    private List<HistoryData> historyDataList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView closeData;
        public TextView volumeData;
        public TextView dateData;
        public View layout;

        public ViewHolder(View v){
            super(v);
            layout = v;
            closeData = (TextView) v.findViewById(R.id.closePrice);
            volumeData = (TextView) v.findViewById(R.id.volume);
            dateData = (TextView) v.findViewById(R.id.date);
        }
    }
    public HistoricalDataAdapter(List<HistoryData> historyDataList){
        this.historyDataList = historyDataList;
    }

    public HistoricalDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.historical_data_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(ViewHolder holder, final int position){
        String close= historyDataList.get(position).getClosePrice();
        String volume = historyDataList.get(position).getVolume();
        String date = historyDataList.get(position).getDate();
        holder.closeData.setText(close);
        holder.volumeData.setText(volume);
        holder.dateData.setText(date);
    }
    public int getItemCount(){
        return historyDataList.size();
    }
}
