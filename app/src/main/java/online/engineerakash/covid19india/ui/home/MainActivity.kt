package online.engineerakash.covid19india.ui.home

import online.engineerakash.covid19india.BuildConfig
import online.engineerakash.covid19india.R
import online.engineerakash.covid19india.databinding.ActivityMainBinding
import online.engineerakash.covid19india.util.AppStore
import online.engineerakash.covid19india.util.AppUpdateType
import online.engineerakash.covid19india.util.Constant
import online.engineerakash.covid19india.util.Helper
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONObject

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponent()
    }

    private fun initComponent() {
        setupNavigation()

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 43200 // 12 hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated: Boolean = task.result
                    Log.d(TAG, "Config params updated: $updated")

                    // 0->Flexible Update, 1->Immediate Update
                    val updateType = remoteConfig.getLong("update_type").toInt()
                    val latestVersionCode = remoteConfig.getLong("latest_version_code")
                    // -1 -> Every Time User Open app, 0-> Don't Show Update Dialog, 5->Show 5 times
                    val showDialogCount = remoteConfig.getLong("show_dialog_count")

                    if (BuildConfig.VERSION_CODE >= latestVersionCode)
                        return@addOnCompleteListener

                    val appStoreUrlJsonString = remoteConfig.getValue("app_store_url").asString()

                    /*
                    {
                      "apk_pure_url": "https://apkpure.com/p/online.engineerakash.covid19india",
                      "amazon_app_store_url": "http://www.amazon.com/gp/mas/dl/android?p=online.engineerakash.covid19india",
                      "google_play_store_url": "https://play.google.com/store/apps/details?id=online.engineerakash.covid19india",
                      "mi_app_store_url": "mimarket://details?id=online.engineerakash.covid19india"
                    }
                     */

                    val rootJo = JSONObject(appStoreUrlJsonString)
                    val apkPureUrl = rootJo.optString("apk_pure_url")
                    val amazonAppStoreUrl = rootJo.optString("amazon_app_store_url")
                    val miStoreStoreUrl = rootJo.optString("mi_app_store_url")
                    val googlePlayStoreUrl = rootJo.optString("google_play_store_url")


                    val playStoreUrl =
                        if (Constant.THIS_BUILD_IS_FOR == AppStore.AMAZON_APP_STORE) {
                            amazonAppStoreUrl
                        } else if (Constant.THIS_BUILD_IS_FOR == AppStore.APK_PURE) {
                            apkPureUrl
                        } else if (Constant.THIS_BUILD_IS_FOR == AppStore.MI_APP_STORE) {
                            miStoreStoreUrl
                        } else {
                            // google play store
                            googlePlayStoreUrl
                        }


                    val builder = AlertDialog.Builder(this)

                    builder.setTitle(getString(R.string.new_update_available_dialog_title))
                    if (updateType == AppUpdateType.FLEXIBLE) {
                        builder.setMessage(getString(R.string.flexible_update_description))
                        builder.setCancelable(true)

                        builder.setNegativeButton(getString(R.string.close_btn), object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                // which(int) the button that was clicked (ex. DialogInterface#BUTTON_POSITIVE) or the position of the item clicked
                                dialog?.dismiss()
                            }
                        })
                    } else {
                        builder.setMessage(getString(R.string.critical_update_description))
                        builder.setCancelable(false)

                        builder.setNegativeButton(getString(R.string.exit_btn), object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                // which(int) the button that was clicked (ex. DialogInterface#BUTTON_POSITIVE) or the position of the item clicked
                                finishAffinity()
                            }
                        })
                    }

                    builder.setPositiveButton(getString(R.string.update_btn), object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            // which(int) the button that was clicked (ex. DialogInterface#BUTTON_POSITIVE) or the position of the item clicked
                            dialog?.dismiss()
                            Helper.openUrl(this@MainActivity, playStoreUrl)
                        }
                    })

                    builder.show()

                } else {
                    /*Toast.makeText(
                        this, "Fetch failed", Toast.LENGTH_SHORT
                    ).show()*/
                }
                //displayWelcomeMessage()
            }
    }

    private fun setupNavigation() {
        navController = Navigation.findNavController(this, R.id.navHostFragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController!!)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController!!)
        }

        navController?.addOnDestinationChangedListener(object :
            NavController.OnDestinationChangedListener {
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {

                if (destination.id == R.id.chooseLocationFragment && binding.bottomNavigation.isVisible) {
                    //binding.bottomNavigation.fadeout()
                    binding.bottomNavigation.visibility = View.GONE
                }

                if (destination.id != R.id.chooseLocationFragment && !binding.bottomNavigation.isVisible) {
                    //binding.bottomNavigation.fadeIn()
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        })

        // do not remove this, otherwise setOnNavigationItemSelectedListener will be called on reselection
        binding.bottomNavigation.setOnNavigationItemReselectedListener {}

    }

    fun changeThemeColor(defaultColor: Boolean, color: Int) {
        val statusBarColor: Int
        val actionBarColor: Int
        val bottomNavColor: Int

        if (defaultColor) {
            statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
            actionBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
            bottomNavColor = ContextCompat.getColor(this, R.color.colorPrimary)
        } else {
            bottomNavColor = color
            actionBarColor = bottomNavColor
            statusBarColor = actionBarColor
        }

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor

        supportActionBar?.setBackgroundDrawable(ColorDrawable(actionBarColor))
        binding.bottomNavigation.setBackgroundColor(bottomNavColor)
    }

    /*@Override
    public void onNavigated(@NonNull NavController controller,
                            @NonNull NavDestination destination) {
        ActionBar actionBar = getSupportActionBar();
        CharSequence title = destination.getLabel();
        if (!TextUtils.isEmpty(title)) {
            actionBar.setTitle(title);
        }
        boolean isStartDestination =
                controller.getGraph().getStartDestination() == destination.getId();
        actionBar.setDisplayHomeAsUpEnabled(mDrawerLayout != null || !isStartDestination);
        setActionBarUpIndicator(mDrawerLayout != null && isStartDestination);
    }*/

    override fun onSupportNavigateUp(): Boolean {
        return navController!!.navigateUp() || super.onSupportNavigateUp()
    }

/*
    override fun onResume() {
        super.onResume()
        val notificationText = getString(R.string.covid_report, 4003, 80423, 808744)

        NotificationHelper.createNotification(
            this,
            "covid_report_id", "Daily Covid Report",
            notificationText, R.drawable.ic_notification, notificationText
        )
    }
*/
}