package `in`.engineerakash.covid19india.ui.track

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.StateDataItemBinding
import `in`.engineerakash.covid19india.pojo.StateDistrictWiseResponse
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

class StateWiseAdapter(
    var list: ArrayList<StateDistrictWiseResponse>,
    var allowScrollAnimation: Boolean = true
) :
    RecyclerView.Adapter<StateWiseAdapter.StateWiseVH>() {

    private var lastPosition = 0
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateWiseVH {
        context = parent.context

        val binding = StateDataItemBinding.inflate(
            LayoutInflater.from(context), parent, false
        )
        return StateWiseVH(binding)
    }

    override fun onBindViewHolder(
        holder: StateWiseVH,
        position: Int
    ) {

        holder.bind(position)
    }

    override fun onViewDetachedFromWindow(holder: StateWiseVH) {
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

    inner class StateWiseVH(private val itemBinding: StateDataItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {

            if (getItemViewType(position) == TYPE_EMPTY_VIEW) {
                // show empty view
                itemBinding.dataContainer.visibility = View.GONE
                itemBinding.emptyView.visibility = View.VISIBLE
                itemBinding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        itemBinding.root.context,
                        R.color.colorOddItem
                    )
                )
            } else {

                val data: StateDistrictWiseResponse = list[position]

                // show data
                itemBinding.stateNameTv.setText(data.name)
                itemBinding.stateConfirmedTv.text = (data.total?.confirmed ?: "-").toString()
                itemBinding.stateRecoveredTv.text = (data.total?.recovered ?: "-").toString()
                itemBinding.stateDeathTv.text = (data.total?.deceased ?: "-").toString()
                if (Constant.userSelectedState.trim { it <= ' ' }
                        .equals(data.name.trim { it <= ' ' }, ignoreCase = true) &&
                    Constant.locationIsSelectedByUser
                ) itemBinding.userStateTv.visibility =
                    View.VISIBLE else itemBinding.userStateTv.visibility = View.GONE
                itemBinding.dataContainer.visibility = View.VISIBLE
                itemBinding.emptyView.visibility = View.GONE
                if (position % 2 == 0) {
                    itemBinding.root.setBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.root.context,
                            R.color.colorOddItem
                        )
                    )
                } else {
                    itemBinding.root.setBackgroundColor(
                        ContextCompat.getColor(
                            itemBinding.root.context,
                            R.color.colorEvenItem
                        )
                    )
                }

                // Total element
                if (data.code.equals(Constant.TOTAL_ITEM_CODE, ignoreCase = true)) {
                    itemBinding.stateNameTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    itemBinding.stateConfirmedTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    itemBinding.stateRecoveredTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    itemBinding.stateDeathTv.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                } else {
                    itemBinding.stateNameTv.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemBinding.stateConfirmedTv.typeface =
                        Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemBinding.stateRecoveredTv.typeface =
                        Typeface.defaultFromStyle(Typeface.NORMAL)
                    itemBinding.stateDeathTv.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
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