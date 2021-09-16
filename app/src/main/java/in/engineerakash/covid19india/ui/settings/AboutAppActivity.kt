package `in`.engineerakash.covid19india.ui.settings

import `in`.engineerakash.covid19india.BuildConfig
import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.ActivityAboutAppBinding
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat

class AboutAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initComponent()
    }

    private fun initComponent() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        binding.appDescTv.text = HtmlCompat.fromHtml(
            getString(R.string.app_desc_text), HtmlCompat.FROM_HTML_MODE_COMPACT
        )

        binding.appVersion.text = "App Version ${BuildConfig.VERSION_NAME}"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}