package `in`.engineerakash.covid19india.ui.home

import `in`.engineerakash.covid19india.R
import `in`.engineerakash.covid19india.databinding.ActivityMainBinding
import `in`.engineerakash.covid19india.util.Constant
import `in`.engineerakash.covid19india.util.NotificationHelper
import `in`.engineerakash.covid19india.util.ViewUtil.fadeIn
import `in`.engineerakash.covid19india.util.ViewUtil.fadeout
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI

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