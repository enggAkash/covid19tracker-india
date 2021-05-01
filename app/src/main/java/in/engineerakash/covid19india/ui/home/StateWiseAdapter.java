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
import in.engineerakash.covid19india.databinding.StateDataItemBinding;
import in.engineerakash.covid19india.pojo.StateWiseData;
import in.engineerakash.covid19india.util.Constant;

public class StateWiseAdapter extends RecyclerView.Adapter<StateWiseAdapter.StateWiseVH> {

    private ArrayList<StateWiseData> list;
    private int TYPE_ITEM = 1;
    private int TYPE_EMPTY_VIEW = 2;
    private Context context;

    public StateWiseAdapter(ArrayList<StateWiseData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public StateWiseVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        StateDataItemBinding binding = StateDataItemBinding.inflate(LayoutInflater.from(context),
                parent, false);

        return new StateWiseVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StateWiseVH holder, int position) {

        /*if (position == 0) {
            // show header
            holder.binding.stateNameTv.setText("State");
            holder.binding.stateConfirmedTv.setText("Confirmed");
            holder.binding.stateActiveTv.setText("Active");
            holder.binding.stateRecoveredTv.setText("Recovered");
            holder.binding.stateDeathTv.setText("Death");

            holder.binding.userStateTv.setVisibility(View.GONE);

            holder.binding.dataContainer.setVisibility(View.VISIBLE);
            holder.binding.emptyView.setVisibility(View.GONE);

        } else*/

        if (getItemViewType(position) == TYPE_EMPTY_VIEW) {
            // show empty view

            holder.binding.dataContainer.setVisibility(View.GONE);
            holder.binding.emptyView.setVisibility(View.VISIBLE);

            holder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(context, R.color.colorOddItem));

        } else {

            StateWiseData data = list.get(position);

            // show data
            holder.binding.stateNameTv.setText(data.getState());
            holder.binding.stateConfirmedTv.setText(data.getConfirmed());
            holder.binding.stateActiveTv.setText(data.getActive());
            holder.binding.stateRecoveredTv.setText(data.getRecovered());
            holder.binding.stateDeathTv.setText(data.getDeaths());

            if (Constant.userSelectedState.trim().equalsIgnoreCase(data.getState().trim()) &&
                    Constant.isUserSelected)
                holder.binding.userStateTv.setVisibility(View.VISIBLE);
            else
                holder.binding.userStateTv.setVisibility(View.GONE);

            holder.binding.dataContainer.setVisibility(View.VISIBLE);
            holder.binding.emptyView.setVisibility(View.GONE);

            if (position % 2 == 0) {
                holder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(context, R.color.colorOddItem));
            } else {
                holder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(context, R.color.colorEvenItem));
            }

            // Total element
            if (position == getItemCount() - 1) {
                holder.binding.stateNameTv.setTypeface(holder.binding.stateNameTv.getTypeface(), Typeface.BOLD);
                holder.binding.stateConfirmedTv.setTypeface(holder.binding.stateConfirmedTv.getTypeface(), Typeface.BOLD);
                holder.binding.stateActiveTv.setTypeface(holder.binding.stateActiveTv.getTypeface(), Typeface.BOLD);
                holder.binding.stateRecoveredTv.setTypeface(holder.binding.stateRecoveredTv.getTypeface(), Typeface.BOLD);
                holder.binding.stateDeathTv.setTypeface(holder.binding.stateDeathTv.getTypeface(), Typeface.BOLD);
            } else {
                holder.binding.stateNameTv.setTypeface(holder.binding.stateNameTv.getTypeface(), Typeface.NORMAL);
                holder.binding.stateConfirmedTv.setTypeface(holder.binding.stateConfirmedTv.getTypeface(), Typeface.NORMAL);
                holder.binding.stateActiveTv.setTypeface(holder.binding.stateActiveTv.getTypeface(), Typeface.NORMAL);
                holder.binding.stateRecoveredTv.setTypeface(holder.binding.stateRecoveredTv.getTypeface(), Typeface.NORMAL);
                holder.binding.stateDeathTv.setTypeface(holder.binding.stateDeathTv.getTypeface(), Typeface.NORMAL);
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

    static class StateWiseVH extends RecyclerView.ViewHolder {
        private StateDataItemBinding binding;

        public StateWiseVH(@NonNull StateDataItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
