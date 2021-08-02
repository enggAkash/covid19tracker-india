package `in`.engineerakash.covid19india.util

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.google.android.material.snackbar.Snackbar

object ViewUtil {

    fun Context.getDimension(value: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value, this.resources.displayMetrics
        ).toInt()
    }

    fun bindToast(view: View, text: String?, duration: Int = Toast.LENGTH_SHORT) {
        if (!text.isNullOrEmpty()) {
            Toast.makeText(view.context, text, duration).show()
        }
    }

    fun TextView.setTextWithTag(text: String?, bufferType: TextView.BufferType? = null) {
        this.tag = "PROGRAMMATICALLY_CHANGED"
        if (bufferType == null)
            this.text = text
        else
            this.setText(text, bufferType)
        this.tag = null
    }

    fun View.requestFocusAndScrollToThis(rootLayout: ScrollView) {
        this.requestFocus()
        rootLayout.smoothScrollTo(this.x.toInt(), this.y.toInt())
    }

    fun View.requestFocusAndScrollToThis(rootLayout: NestedScrollView) {
        this.requestFocus()
        rootLayout.smoothScrollTo(this.x.toInt(), this.y.toInt())
    }

    fun View.setViewIsChangingProgrammatically() {
        this.tag = "PROGRAMMATICALLY_CHANGED"
    }

    fun View.removeViewIsChangingProgrammatically() {
        this.tag = null
    }

    fun View.isViewIsChangingProgrammatically(): Boolean {
        return this.tag == "PROGRAMMATICALLY_CHANGED"
    }

    fun NavController.navigateSafe(
        @IdRes resId: Int, @Nullable args: Bundle? = null, @Nullable navOptions: NavOptions? = null,
        @Nullable navExtras: Navigator.Extras? = null
    ) {
        val action = currentDestination?.getAction(resId) ?: graph.getAction(resId)
        if (action != null && currentDestination?.id != action.destinationId) {
            navigate(resId, args, navOptions, navExtras)
        }
    }

    fun NavController.navigateSafe(
        @NonNull directions: NavDirections
    ) {
        currentDestination?.getAction(directions.actionId)?.let {
            navigate(directions)
        }
    }

    fun NavController.navigateSafe(
        @NonNull directions: NavDirections, @Nullable navOptions: NavOptions
    ) {
        currentDestination?.getAction(directions.actionId)?.let {
            navigate(directions, navOptions)
        }
    }

    fun NavController.navigateSafe(
        @NonNull directions: NavDirections, @NonNull navExtras: Navigator.Extras
    ) {
        currentDestination?.getAction(directions.actionId)?.let {
            navigate(directions, navExtras)
        }
    }

    fun Snackbar?.dismissWithDelay(timeInMillis: Long = 1000) {

        Handler(Looper.getMainLooper()).postDelayed({
            this?.dismiss()
        }, timeInMillis)

    }
}