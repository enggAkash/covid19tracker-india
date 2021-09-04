package `in`.engineerakash.covid19india.ui.settings

import `in`.engineerakash.covid19india.databinding.ItemQAndABinding
import `in`.engineerakash.covid19india.pojo.QAndA
import android.view.LayoutInflater
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


        fun bind(position: Int) {
            itemBinding.questionTv.text = qAnAList[position].question
            itemBinding.answerTv.text = qAnAList[position].answer
        }

    }

}