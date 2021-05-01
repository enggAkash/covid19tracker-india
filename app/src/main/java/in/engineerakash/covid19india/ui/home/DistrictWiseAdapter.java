package in.engineerakash.covid19india.ui.home;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import in.engineerakash.covid19india.R;
import in.engineerakash.covid19india.databinding.DistrictDataItemBinding;
import in.engineerakash.covid19india.pojo.District;
import in.engineerakash.covid19india.util.Constant;

public class DistrictWiseAdapter extends RecyclerView.Adapter<DistrictWiseAdapter.DistrictWiseVH> {

    private ArrayList<District> list;

    private int TYPE_ITEM = 1;
    private int TYPE_EMPTY_VIEW = 2;
    private Context context;

    public DistrictWiseAdapter(ArrayList<District> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DistrictWiseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        DistrictDataItemBinding binding = DistrictDataItemBinding.inflate(LayoutInflater.from(context),
                parent, false);

        return new DistrictWiseVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DistrictWiseVH holder, int position) {

        /*if (position == 0) {
            // show header
            holder.binding.districtNameTv.setText("District");
            holder.binding.districtConfirmedTv.setText("Confirmed");
            holder.binding.districtLastUpdatedTv.setText("Last updated at");

            holder.binding.userDistrictTv.setVisibility(View.GONE);

            holder.binding.dataContainer.setVisibility(View.VISIBLE);
            holder.binding.emptyView.setVisibility(View.GONE);

        } else*/
        if (getItemViewType(position) == TYPE_EMPTY_VIEW) {
            // show empty view

            holder.binding.dataContainer.setVisibility(View.GONE);
            holder.binding.emptyView.setVisibility(View.VISIBLE);

            holder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(context, R.color.colorOddItem));

        } else {

            District data = list.get(position);

            // show data
            holder.binding.districtNameTv.setText(data.getName());
            holder.binding.districtConfirmedTv.setText(String.valueOf(data.getConfirmed()));
            holder.binding.districtLastUpdatedTv.setText(data.getLastUpdateTime().isEmpty() ? "-" : data.getLastUpdateTime());

            if (Constant.userSelectedDistrict.trim().equalsIgnoreCase(data.getName().trim()) &&
                    Constant.isUserSelected)
                holder.binding.userDistrictTv.setVisibility(View.VISIBLE);
            else
                holder.binding.userDistrictTv.setVisibility(View.GONE);

            holder.binding.dataContainer.setVisibility(View.VISIBLE);
            holder.binding.emptyView.setVisibility(View.GONE);

            if (position % 2 == 0) {
                holder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(context, R.color.colorOddItem));
            } else {
                holder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(context, R.color.colorEvenItem));
            }

            // Total element
            if (position == getItemCount() - 1) {
                holder.binding.districtNameTv.setTypeface(holder.binding.districtNameTv.getTypeface(), Typeface.BOLD);
                holder.binding.districtConfirmedTv.setTypeface(holder.binding.districtConfirmedTv.getTypeface(), Typeface.BOLD);
                holder.binding.districtLastUpdatedTv.setTypeface(holder.binding.districtLastUpdatedTv.getTypeface(), Typeface.BOLD);
            } else {
                holder.binding.districtNameTv.setTypeface(holder.binding.districtNameTv.getTypeface(), Typeface.NORMAL);
                holder.binding.districtConfirmedTv.setTypeface(holder.binding.districtConfirmedTv.getTypeface(), Typeface.NORMAL);
                holder.binding.districtLastUpdatedTv.setTypeface(holder.binding.districtLastUpdatedTv.getTypeface(), Typeface.NORMAL);
            }
        }

    }

    @Override
    public int getItemCount() {
        if (list.isEmpty())
            return 1; // empty view
        else
            return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.isEmpty())
            return TYPE_EMPTY_VIEW;
        else
            return TYPE_ITEM;
    }

    static class DistrictWiseVH extends RecyclerView.ViewHolder {
        private DistrictDataItemBinding binding;

        public DistrictWiseVH(@NonNull DistrictDataItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
