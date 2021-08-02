package `in`.engineerakash.covid19india.ui.location

import `in`.engineerakash.covid19india.R
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import java.util.*

class LocationAdapter(context: Context, private var receivedList: ArrayList<String>) :
    ArrayAdapter<String>(context, R.layout.item_location_name, receivedList) {

    private var originalList = ArrayList(receivedList)
    private var filteredList = ArrayList(receivedList)
    private var filterQuery: String? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var currentItemView = convertView

        if (currentItemView == null)
            currentItemView =
                LayoutInflater.from(context)
                    .inflate(R.layout.item_location_name, parent, false)

        val spannableString = SpannableString(filteredList[position])

        if (!filterQuery.isNullOrEmpty()) {

            val startIndex = spannableString.indexOf(filterQuery!!, ignoreCase = true)
            if (startIndex >= 0)
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD), startIndex, startIndex + filterQuery!!.length,
                    SpannableString.SPAN_INCLUSIVE_INCLUSIVE
                )
        }

        currentItemView?.findViewById<TextView>(R.id.name)?.text = spannableString

        return currentItemView!!
    }

    override fun getCount() = filteredList.size

    fun setList(newList: ArrayList<String>) {
        receivedList.clear()
        receivedList.addAll(newList)

        originalList.clear()
        originalList.addAll(newList)

        filteredList.clear()
        filteredList.addAll(newList)

        /*if (!filterQuery.isNullOrEmpty())
            filter.filter(filterQuery)*/

        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return value
    }

    val value = object : Filter() {
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            filteredList = filterResults.values as ArrayList<String>
            if (filterResults.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val queryString = charSequence?.toString()?.trim()
            filterQuery = queryString

            val filterResults = FilterResults()
            filterResults.values = if (queryString == null || queryString.isEmpty())
                originalList
            else
                originalList.filter {
                    it.toLowerCase(Locale.ENGLISH).contains(queryString, true)
                }

            filterResults.count = if (queryString == null || queryString.isEmpty())
                originalList.size
            else
                originalList.filter {
                    it.toLowerCase(Locale.ENGLISH).contains(queryString)
                }.size
            return filterResults
        }
    }

    override fun getItem(position: Int): String {
        return if (position >= 0 && position < filteredList.size)
            filteredList[position]
        else ""
    }

}