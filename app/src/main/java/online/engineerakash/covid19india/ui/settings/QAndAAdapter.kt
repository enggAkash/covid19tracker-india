package online.engineerakash.covid19india.ui.settings

import online.engineerakash.covid19india.R
import online.engineerakash.covid19india.databinding.ItemQAndABinding
import online.engineerakash.covid19india.pojo.QAndA
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class QAndAAdapter(var qAnAList: ArrayList<QAndA>) : RecyclerView.Adapter<QAndAAdapter.QAndAVh>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QAndAVh {
        val itemBinding =
            ItemQAndABinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return QAndAVh(itemBinding)
    }

    override fun onBindViewHolder(holder: QAndAVh, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return qAnAList.size
    }


    inner class QAndAVh(var itemBinding: ItemQAndABinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.questionTv.setOnClickListener {
                if (itemBinding.answerBody.tag == "COLLAPSED") {
                    itemBinding.answerBody.visibility = View.VISIBLE

                    itemBinding.questionTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_minus, 0)
                    itemBinding.answerBody.tag = "EXPANDED"
                } else {
                    itemBinding.answerBody.visibility = View.GONE

                    itemBinding.questionTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_plus, 0)
                    itemBinding.answerBody.tag = "COLLAPSED"
                }
            }
        }

        fun bind(position: Int) {
            itemBinding.questionTv.text = qAnAList[position].question
            itemBinding.answerTv.text = qAnAList[position].answer

            itemBinding.questionTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_plus, 0)
            itemBinding.answerBody.tag = "COLLAPSED"
        }

    }

}