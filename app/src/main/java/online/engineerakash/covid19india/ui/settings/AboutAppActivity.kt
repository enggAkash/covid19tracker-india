package online.engineerakash.covid19india.ui.settings

import online.engineerakash.covid19india.BuildConfig
import online.engineerakash.covid19india.R
import online.engineerakash.covid19india.databinding.ActivityAboutAppBinding
import online.engineerakash.covid19india.util.Constant
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

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

        binding.appVersion.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)

        if (Constant.SHOW_ADS)
            loadAds()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadAds() {
        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

}