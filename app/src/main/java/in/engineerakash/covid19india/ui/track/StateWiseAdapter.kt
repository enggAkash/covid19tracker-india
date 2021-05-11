package `in`.engineerakash.covid19india.ui.track

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.StateDataItemBinding
import `in`.engineerakash.covid19india.pojo.StateWiseData
import `in`.engineerakash.covid19india.util.Constant
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class StateWiseAdapter(var list: ArrayList<StateWiseData>) :
    RecyclerView.Adapter<StateWiseAdapter.StateWiseVH>() {

    private val TYPE_ITEM = 1
    private val TYPE_EMPTY_VIEW = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateWiseVH {
        val binding = StateDataItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return StateWiseVH(binding)
    }

    override fun onBindViewHolder(holder: StateWiseVH, position: Int) {

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
            holder.binding.dataContainer.visibility = View.GONE
            holder.binding.emptyView.visibility = View.VISIBLE
            holder.binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.colorOddItem
                )
            )
        } else {
            val data: StateWiseData = list[position]

            // show data
            holder.binding.stateNameTv.setText(data.state)
            holder.binding.stateConfirmedTv.setText(data.confirmed)
            holder.binding.stateActiveTv.setText(data.active)
            holder.binding.stateRecoveredTv.setText(data.recovered)
            holder.binding.stateDeathTv.setText(data.deaths)
            if (Constant.userSelectedState.trim { it <= ' ' }
                    .equals(data.state.trim { it <= ' ' }, ignoreCase = true) &&
                Constant.isUserSelected
            ) holder.binding.userStateTv.visibility =
                View.VISIBLE else holder.binding.userStateTv.visibility = View.GONE
            holder.binding.dataContainer.visibility = View.VISIBLE
            holder.binding.emptyView.visibility = View.GONE
            if (position % 2 == 0) {
                holder.binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.colorOddItem
                    )
                )
            } else {
                holder.binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.binding.root.context,
                        R.color.colorEvenItem
                    )
                )
            }

            // Total element
            if (position == itemCount - 1) {
                holder.binding.stateNameTv.setTypeface(
                    holder.binding.stateNameTv.typeface,
                    Typeface.BOLD
                )
                holder.binding.stateConfirmedTv.setTypeface(
                    holder.binding.stateConfirmedTv.typeface,
                    Typeface.BOLD
                )
                holder.binding.stateActiveTv.setTypeface(
                    holder.binding.stateActiveTv.typeface,
                    Typeface.BOLD
                )
                holder.binding.stateRecoveredTv.setTypeface(
                    holder.binding.stateRecoveredTv.typeface,
                    Typeface.BOLD
                )
                holder.binding.stateDeathTv.setTypeface(
                    holder.binding.stateDeathTv.typeface,
                    Typeface.BOLD
                )
            } else {
                holder.binding.stateNameTv.setTypeface(
                    holder.binding.stateNameTv.typeface,
                    Typeface.NORMAL
                )
                holder.binding.stateConfirmedTv.setTypeface(
                    holder.binding.stateConfirmedTv.typeface,
                    Typeface.NORMAL
                )
                holder.binding.stateActiveTv.setTypeface(
                    holder.binding.stateActiveTv.typeface,
                    Typeface.NORMAL
                )
                holder.binding.stateRecoveredTv.setTypeface(
                    holder.binding.stateRecoveredTv.typeface,
                    Typeface.NORMAL
                )
                holder.binding.stateDeathTv.setTypeface(
                    holder.binding.stateDeathTv.typeface,
                    Typeface.NORMAL
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return if (list.isEmpty()) 1 // empty view
        else list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list.isEmpty()) TYPE_EMPTY_VIEW else TYPE_ITEM
    }

    class StateWiseVH(val binding: StateDataItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )

}