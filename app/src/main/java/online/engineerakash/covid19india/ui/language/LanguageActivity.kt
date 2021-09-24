package online.engineerakash.covid19india.ui.language

import online.engineerakash.covid19india.R
import online.engineerakash.covid19india.databinding.ActivityLanguageBinding
import online.engineerakash.covid19india.ui.home.MainActivity
import online.engineerakash.covid19india.util.AppUtil
import online.engineerakash.covid19india.util.Constant
import online.engineerakash.covid19india.util.LanguageSelectionStartedFrom
import online.engineerakash.covid19india.util.ViewUtil.hideKeyboard
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding

    private var prefs: SharedPreferences? = null

    private var languageSelectionStartedFrom: LanguageSelectionStartedFrom? = null
    private var existingLocale: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideKeyboard()

        languageSelectionStartedFrom =
            intent.getSerializableExtra("language_screen_started_from") as LanguageSelectionStartedFrom?

        prefs = getSharedPreferences(Constant.DEFAULT_SP_NAME, Context.MODE_PRIVATE)

        if (prefs?.getBoolean(Constant.IS_LOCALE_NAME_IS_SET_SP_KEY, false) == true &&
            languageSelectionStartedFrom == null
        ) {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(mainActivityIntent)
            finish()
        } else {

            binding = ActivityLanguageBinding.inflate(layoutInflater)
            setContentView(binding.root)

            initComponent()
        }
    }

    private fun initComponent() {

        setClickListeners()

        existingLocale = AppUtil.getCurrentLocale(this)

        if (existingLocale == Constant.LOCAL_EN) {
            binding.localeEn.isChecked = true
        } else if (existingLocale == Constant.LOCAL_HI) {
            binding.localeHi.isChecked = true
        } else if (existingLocale == Constant.LOCAL_ML) {
            binding.localeMl.isChecked = true
        } else if (existingLocale == Constant.LOCAL_MR) {
            binding.localeMr.isChecked = true
        }

        if (languageSelectionStartedFrom == LanguageSelectionStartedFrom.SETTING_FRAG) {
            binding.skipLanguageBtn.visibility = View.VISIBLE
        }
    }

    private fun setClickListeners() {
        binding.localeEn.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.localeHi.isChecked = false
                binding.localeMl.isChecked = false
                binding.localeMr.isChecked = false
                showLocaleTemporarily(Constant.LOCAL_EN)
            }
        }

        binding.localeHi.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.localeEn.isChecked = false
                binding.localeMl.isChecked = false
                binding.localeMr.isChecked = false
                showLocaleTemporarily(Constant.LOCAL_HI)
            }
        }

        binding.localeMl.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.localeEn.isChecked = false
                binding.localeHi.isChecked = false
                binding.localeMr.isChecked = false
                showLocaleTemporarily(Constant.LOCAL_ML)
            }
        }

        binding.localeMr.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.localeEn.isChecked = false
                binding.localeHi.isChecked = false
                binding.localeMl.isChecked = false
                showLocaleTemporarily(Constant.LOCAL_MR)
            }
        }

        binding.skipLanguageBtn.setOnClickListener {
            if (languageSelectionStartedFrom == LanguageSelectionStartedFrom.SETTING_FRAG)
                onBackPressed()
        }

        binding.saveLanguageBtn.setOnClickListener {

            var locale = Constant.LOCAL_EN

            if (binding.localeEn.isChecked) {
                locale = Constant.LOCAL_EN

            } else if (binding.localeHi.isChecked) {
                locale = Constant.LOCAL_HI

            } else if (binding.localeMl.isChecked) {
                locale = Constant.LOCAL_ML

            } else if (binding.localeMr.isChecked) {
                locale = Constant.LOCAL_MR

            } else {
                Toast.makeText(
                    this@LanguageActivity,
                    "Please Select Preferred Language",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
            val isLocaleChanged = existingLocale != locale

            prefs?.edit()?.putString(Constant.LOCALE_NAME_SP_KEY, locale)?.apply()

            AppUtil.setAppLocale(this, locale, false)

            if (!isLocaleChanged && languageSelectionStartedFrom == LanguageSelectionStartedFrom.SETTING_FRAG) {
                onBackPressed()
            } else {
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(mainActivityIntent)
            }
            finish()
        }
    }

    private fun showLocaleTemporarily(locale: String) {

        val logoContentDesc =
            AppUtil.getStringByLocal(this, R.string.logo_content_description, locale)

        val appName =
            AppUtil.getStringByLocal(this, R.string.app_name, locale)

        val welcomeMsg =
            AppUtil.getStringByLocal(this, R.string.welcome_msg, locale)

        val languageTitle =
            AppUtil.getStringByLocal(this, R.string.select_language_title, locale)

        val localeEn =
            AppUtil.getStringByLocal(this, R.string.locale_en, locale)

        val localeHi =
            AppUtil.getStringByLocal(this, R.string.locale_hi, locale)

        val localeMl =
            AppUtil.getStringByLocal(this, R.string.locale_ml, locale)

        val localeMr =
            AppUtil.getStringByLocal(this, R.string.locale_mr, locale)

        val saveBtnText =
            AppUtil.getStringByLocal(this, R.string.continue_btn_text, locale)

        val skipBtnText =
            AppUtil.getStringByLocal(this, R.string.skip, locale)

        val madeInIndiaText =
            AppUtil.getStringByLocal(this, R.string.made_in_india, locale)

        binding.logoIv.contentDescription = logoContentDesc
        binding.welcomeMsgTv.text = welcomeMsg
        binding.languageTitleTv.text = languageTitle
        binding.localeEn.text = localeEn
        binding.localeHi.text = localeHi
        binding.localeMl.text = localeMl
        binding.localeMr.text = localeMr
        binding.skipLanguageBtn.text = skipBtnText
        binding.saveLanguageBtn.text = saveBtnText
        binding.madeInIndia.text = madeInIndiaText

    }
}
