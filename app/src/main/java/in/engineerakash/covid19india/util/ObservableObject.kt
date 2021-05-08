package `in`.engineerakash.covid19india.util

import java.util.*

class ObservableObject private constructor() : Observable() {

    fun updateValue(data: Any?) {
        synchronized(this) {
            setChanged()
            notifyObservers(data)
        }
    }

    companion object {
        @JvmStatic
        val instance = ObservableObject()
    }
}