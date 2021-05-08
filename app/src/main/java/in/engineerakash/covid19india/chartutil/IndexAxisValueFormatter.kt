package `in`.engineerakash.covid19india.chartutil

import com.github.mikephil.charting.formatter.ValueFormatter

/**
 * This formatter is used for passing an array of x-axis labels, on whole x steps.
 */
class IndexAxisValueFormatter : ValueFormatter {
    private var mValues = arrayOf<String>()
    private var mValueCount = 0

    /**
     * An empty constructor.
     * Use `setValues` to set the axis labels.
     */
    constructor() {}

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    constructor(values: Array<String>?) {
        if (values != null)
            setValues(values)
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    constructor(values: Collection<String>?) {
        if (values != null)
            setValues(values.toTypedArray())
    }

    override fun getFormattedValue(value: Float): String {
        val index = Math.round(value)
        return if (index < 0 || index >= mValueCount || index != value.toInt()) "" else mValues[index]
    }

    fun getValues(): Array<String> {
        return mValues
    }

    fun setValues(values: Array<String>?) {
        var values1 = values
        if (values1 == null) values1 = arrayOf()
        mValues = values1
        mValueCount = values1.size
    }
}