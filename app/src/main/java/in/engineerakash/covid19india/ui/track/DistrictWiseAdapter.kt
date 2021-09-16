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

private const val TYPE_ITEM = 1
private const val TYPE_EMPTY_VIEW = 2

class DistrictWiseAdapter(var list: ArrayList<District>, var allowScrollAnimation: Boolean = true) :
    RecyclerView.Adapter<DistrictWiseAdapter.DistrictWiseVH>() {

    private var context: Context? = null
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictWiseVH {
        context = parent.context
        val binding = DistrictDataItemBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return DistrictWiseVH(binding)
    }


    override fun onBindViewHolder(holder: DistrictWiseVH, position: Int) {
        holder.bind(position)
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

    inner class DistrictWiseVH(private val itemBinding: DistrictDataItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {

            if (getItemViewType(position) == TYPE_EMPTY_VIEW) {
                // show empty view
                itemBinding.dataContainer.visibility = View.GONE
                itemBinding.emptyView.visibility = View.VISIBLE
                itemBinding.root.setBackgroundColor(
                    ContextCompat.getColor(itemBinding.root.context, R.color.colorOddItem)
                )
            } else {
                val data: District = list[position]

                // show data
                itemBinding.districtNameTv.text = data.name
                itemBinding.districtConfirmedTv.text = (data.total?.confirmed ?: 0).toString()
                itemBinding.districtRecoveredTv.text = (data.total?.recovered ?: 0).toString()
                itemBinding.districtDeathTv.text = (data.total?.deceased ?: 0).toString()

                if (Constant.userSelectedDistrict.trim { it <= ' ' }
                        .equals(data.name.trim { it <= ' ' }, ignoreCase = true) &&
                    Constant.locationIsSelectedByUser
                ) itemBinding.userDistrictTv.visibility =
                    View.VISIBLE else itemBinding.userDistrictTv.visibility = View.GONE
                itemBinding.dataContainer.visibility = View.VISIBLE
                itemBinding.emptyView.visibility = View.GONE
                if (position % 2 == 0) {
                    itemBinding.root.setBackgroundColor(
                        ContextCompat.getColor(itemBinding.root.context, R.color.colorOddItem)
                    )
                } else {
                    itemBinding.root.setBackgroundColor(
                        ContextCompat.getColor(itemBinding.root.context, R.color.colorEvenItem)
                    )
                }

                // Total element
                if (data.name.equals(Constant.TOTAL_ITEM_NAME, ignoreCase = true)) {
                    itemBinding.districtNameTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    itemBinding.districtConfirmedTv.typeface =
                        Typeface.defaultFromStyle(Typeface.BOLD)
                    itemBinding.districtRecoveredTv.typeface =
                        Typeface.defaultFromStyle(Typeface.BOLD)
                    itemBinding.districtDeathTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                } else {
                    itemBinding.districtNameTv.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemBinding.districtConfirmedTv.typeface =
                        Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemBinding.districtRecoveredTv.typeface =
                        Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemBinding.districtDeathTv.typeface =
                        Typeface.defaultFromStyle(Typeface.NORMAL)
                }
            }

            if (allowScrollAnimation) {
                val animation: Animation = AnimationUtils.loadAnimation(
                    context,
                    if (position > lastPosition) R.anim.up_from_bottom else R.anim.down_from_top
                )
                itemView.startAnimation(animation)
                lastPosition = position
            }

        }
    }
}