package `in`.engineerakash.covid19india.ui.track

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.DistrictDataItemBinding
import `in`.engineerakash.covid19india.pojo.District
import `in`.engineerakash.covid19india.util.Constant
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DistrictWiseAdapter(var list: ArrayList<District>, var allowScrollAnimation: Boolean = true) :
    RecyclerView.Adapter<DistrictWiseAdapter.DistrictWiseVH>() {

    private val TYPE_ITEM = 1
    private val TYPE_EMPTY_VIEW = 2

    private var context: Context? = null
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictWiseVH {
        context = parent.context
        val binding = DistrictDataItemBinding.inflate(
            LayoutInflater.from(context),
            parent, false
        )
        return DistrictWiseVH(binding)
    }

    override fun onBindViewHolder(holder: DistrictWiseVH, position: Int) {

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
            holder.binding.dataContainer.visibility = View.GONE
            holder.binding.emptyView.visibility = View.VISIBLE
            holder.binding.root.setBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.colorOddItem
                )
            )
        } else {
            val data: District = list[position]

            // show data
            holder.binding.districtNameTv.setText(data.name)
            holder.binding.districtConfirmedTv.setText(data.confirmed.toString())
            holder.binding.districtLastUpdatedTv.text =
                if (data.lastUpdateTime.isEmpty()) "-" else data.lastUpdateTime
            if (Constant.userSelectedDistrict.trim { it <= ' ' }
                    .equals(data.name.trim { it <= ' ' }, ignoreCase = true) &&
                Constant.locationIsSelectedByUser
            ) holder.binding.userDistrictTv.visibility =
                View.VISIBLE else holder.binding.userDistrictTv.visibility = View.GONE
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
                holder.binding.districtNameTv.setTypeface(
                    holder.binding.districtNameTv.typeface,
                    Typeface.BOLD
                )
                holder.binding.districtConfirmedTv.setTypeface(
                    holder.binding.districtConfirmedTv.typeface,
                    Typeface.BOLD
                )
                holder.binding.districtLastUpdatedTv.setTypeface(
                    holder.binding.districtLastUpdatedTv.typeface,
                    Typeface.BOLD
                )
            } else {
                holder.binding.districtNameTv.setTypeface(
                    holder.binding.districtNameTv.typeface,
                    Typeface.NORMAL
                )
                holder.binding.districtConfirmedTv.setTypeface(
                    holder.binding.districtConfirmedTv.typeface,
                    Typeface.NORMAL
                )
                holder.binding.districtLastUpdatedTv.setTypeface(
                    holder.binding.districtLastUpdatedTv.typeface,
                    Typeface.NORMAL
                )
            }
        }

        if (allowScrollAnimation) {
            val animation: Animation = AnimationUtils.loadAnimation(
                context,
                if (position > lastPosition) R.anim.up_from_bottom else R.anim.down_from_top
            )
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun onViewDetachedFromWindow(holder: DistrictWiseVH) {
        super.onViewDetachedFromWindow(holder)
        if (allowScrollAnimation)
            holder.itemView.clearAnimation()
    }


    override fun getItemCount(): Int {
        return if (list.isEmpty()) 1 // empty view
        else list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list.isEmpty()) TYPE_EMPTY_VIEW else TYPE_ITEM
    }

    class DistrictWiseVH(val binding: DistrictDataItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}