package `in`.engineerakash.covid19india.ui.precaution

import `in`.engineerakash.covid19india.databinding.ItemPrecautionBinding
import `in`.engineerakash.covid19india.databinding.ItemPrecautionGroupSubTitleBinding
import `in`.engineerakash.covid19india.databinding.ItemPrecautionGroupTitleBinding
import `in`.engineerakash.covid19india.pojo.Precaution
import `in`.engineerakash.covid19india.util.Helper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PrecautionAdapter(val list: ArrayList<Precaution>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ITEM_TYPE_GROUP_TITLE = 1
    private var ITEM_TYPE_SUB_GROUP_TITLE = 2
    private var ITEM_TYPE_PRECAUTION_TITLE = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_TYPE_GROUP_TITLE -> (holder as GroupTitleVh).bind(position)
            ITEM_TYPE_SUB_GROUP_TITLE -> (holder as GroupSubTitleVh).bind(position)
            else -> (holder as PrecautionVh).bind(position)
        }
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
        RecyclerView.ViewHolder(itemBinding.root) {

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
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {
            itemBinding.groupTitleTv.text = list[position].groupTitle
        }
    }

    inner class GroupSubTitleVh(var itemBinding: ItemPrecautionGroupSubTitleBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {
            itemBinding.groupSubTitleTv.text = list[position].groupSubTitle
        }
    }
}
