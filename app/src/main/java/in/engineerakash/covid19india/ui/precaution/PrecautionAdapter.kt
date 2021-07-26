package `in`.engineerakash.covid19india.ui.precaution

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.ItemPrecautionBinding
import `in`.engineerakash.covid19india.databinding.ItemPrecautionGroupSubTitleBinding
import `in`.engineerakash.covid19india.databinding.ItemPrecautionGroupTitleBinding
import `in`.engineerakash.covid19india.pojo.Precaution
import `in`.engineerakash.covid19india.util.Helper
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder


class PrecautionAdapter(val list: ArrayList<Precaution>) :
    RecyclerView.Adapter<ViewHolder>() {

    private var ITEM_TYPE_GROUP_TITLE = 1
    private var ITEM_TYPE_SUB_GROUP_TITLE = 2
    private var ITEM_TYPE_PRECAUTION_TITLE = 3

    private var lastPosition = 0
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)

        return when (viewType) {
            ITEM_TYPE_GROUP_TITLE -> GroupTitleVh(
                ItemPrecautionGroupTitleBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            ITEM_TYPE_SUB_GROUP_TITLE -> GroupSubTitleVh(
                ItemPrecautionGroupSubTitleBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            else -> PrecautionVh(ItemPrecautionBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_GROUP_TITLE -> (holder as GroupTitleVh).bind(position)
            ITEM_TYPE_SUB_GROUP_TITLE -> (holder as GroupSubTitleVh).bind(position)
            else -> (holder as PrecautionVh).bind(position)
        }

        val animation: Animation = AnimationUtils.loadAnimation(
            context,
            if (position > lastPosition) R.anim.up_from_bottom else R.anim.down_from_top
        )
        holder.itemView.startAnimation(animation)
        lastPosition = position
    }

    override fun onViewDetachedFromWindow(@NonNull holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        return when {
            list[position].groupTitle.isNotEmpty() -> ITEM_TYPE_GROUP_TITLE
            list[position].groupSubTitle.isNotEmpty() -> ITEM_TYPE_SUB_GROUP_TITLE
            else -> ITEM_TYPE_PRECAUTION_TITLE
        }
    }


    inner class PrecautionVh(var itemBinding: ItemPrecautionBinding) :
        ViewHolder(itemBinding.root) {

        init {
            itemBinding.affiliateLink1Tv.setOnClickListener {
                if (bindingAdapterPosition >= 0 && list[bindingAdapterPosition].affiliateLink1Url.isNotEmpty())
                    Helper.openUrl(
                        itemBinding.affiliateLink1Tv.context,
                        list[bindingAdapterPosition].affiliateLink1Url
                    )
            }

            itemBinding.affiliateLink2Tv.setOnClickListener {
                if (bindingAdapterPosition >= 0 && list[bindingAdapterPosition].affiliateLink2Url.isNotEmpty())
                    Helper.openUrl(
                        itemBinding.affiliateLink2Tv.context,
                        list[bindingAdapterPosition].affiliateLink2Url
                    )
            }
        }

        fun bind(position: Int) {
            list[position].iconResource?.let { itemBinding.iconIv.setImageResource(it) }
            itemBinding.titleTv.text = list[position].title
            itemBinding.descTv.text = list[position].description

            itemBinding.affiliateLink1Tv.text = list[position].affiliateLink1Text
            itemBinding.affiliateLink2Tv.text = list[position].affiliateLink2Text

            itemBinding.descTv.visibility =
                if (list[position].description.isEmpty()) View.GONE else View.VISIBLE
            itemBinding.affiliateLink1Tv.visibility =
                if (list[position].affiliateLink1Text.isEmpty()) View.GONE else View.VISIBLE
            itemBinding.affiliateLink2Tv.visibility =
                if (list[position].affiliateLink2Text.isEmpty()) View.GONE else View.VISIBLE
        }

    }

    inner class GroupTitleVh(var itemBinding: ItemPrecautionGroupTitleBinding) :
        ViewHolder(itemBinding.root) {

        fun bind(position: Int) {
            itemBinding.groupTitleTv.text = list[position].groupTitle
        }
    }

    inner class GroupSubTitleVh(var itemBinding: ItemPrecautionGroupSubTitleBinding) :
        ViewHolder(itemBinding.root) {

        fun bind(position: Int) {
            itemBinding.groupSubTitleTv.text = list[position].groupSubTitle
        }
    }
}
