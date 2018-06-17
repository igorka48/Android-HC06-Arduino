package ua.dp.sq.bluetoothflasher.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import ua.dp.sq.bluetoothflasher.R;
import ua.dp.sq.bluetoothflasher.data.LocationItem;

public class FavViewDataAdapter extends
        RecyclerView.Adapter<FavViewDataAdapter.ViewHolder> {

    private List<LocationItem> locationItems;

    public FavViewDataAdapter(List<LocationItem> locationItems) {
        this.locationItems = locationItems;
    }

    @Override
    public FavViewDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_row, null);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.mOrderNumber.setText(locationItems.get(position).getOrderNumber());
        viewHolder.mTitle.setText(locationItems.get(position).getTitle());
        viewHolder.mGroupName.setText(locationItems.get(position).getGroupName());
        viewHolder.mNaviNumber.setText(locationItems.get(position).getNavigatorNumber());
        viewHolder.chkSelected.setChecked(locationItems.get(position).getChecked());
        viewHolder.chkSelected.setTag(locationItems.get(position));

        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                LocationItem item = (LocationItem) cb.getTag();

                item.setChecked(cb.isChecked());
                locationItems.get(pos).setChecked(cb.isChecked());
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        TextView mOrderNumber;
        TextView mGroupName;
        TextView mNaviNumber;

        CheckBox chkSelected;

        ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            mOrderNumber = (TextView) itemView.findViewById(R.id.tvCellOrderNumber);
            mTitle = (TextView) itemView.findViewById(R.id.tvCellTitle);
            mGroupName = (TextView) itemView.findViewById(R.id.tvCellGroupName);
            mNaviNumber = (TextView) itemView.findViewById(R.id.tvCellNaviNumber);

            chkSelected = (CheckBox) itemLayoutView
                    .findViewById(R.id.chkSelected);
        }

    }

    public List<LocationItem> getLocationItems() {
        return locationItems;
    }

    @Override
    public int getItemCount() {
        return locationItems.size();
    }
}