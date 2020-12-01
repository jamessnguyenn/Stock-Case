package edu.sjsu.android.stockmarketviewer;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<StockInfo> {
    private final List<StockInfo> stocks;

    public AutoCompleteAdapter(Context context, int resource, List<StockInfo> stocks) {
        super(context, resource, stocks);
        this.stocks = stocks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StockInfo stock = stocks.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.auto_complete_layout, null);
        }
        TextView stockName = convertView.findViewById(R.id.stockName);
        TextView stockSymbol = convertView.findViewById(R.id.stockSymbol);

        if (stock.getName() == "null") {
            stockName.setText("-");
        } else {
            stockName.setText(stock.getName());
        }
        if (stock.getSymbol() == "null") {
            stockSymbol.setText("-");
        } else {
            stockSymbol.setText(stock.getSymbol());
        }
        return convertView;
    }
    @Override
    public Filter getFilter() {
        return new NoFilter();
    }

    private class NoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            result.values = stocks;
            result.count = stocks.size();
            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notifyDataSetChanged();
        }
    }
}
