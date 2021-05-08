package in.engineerakash.covid19india.ui.home;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import in.engineerakash.covid19india.R;
import in.engineerakash.covid19india.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initComponent();

    }

    private void initComponent() {

        fragmentManager = getSupportFragmentManager();

        setupNavigation();
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.navHostFragment);

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                NavigationUI.onNavDestinationSelected(item, navController);
                return true;
            }
        });
    }

    public void changeThemeColor(boolean defaultColor, int color) {

        int statusBarColor;
        int actionBarColor;
        int bottomNavColor;

        if (defaultColor) {
            statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            actionBarColor = ContextCompat.getColor(this, R.color.colorPrimary);
            bottomNavColor = ContextCompat.getColor(this, R.color.colorPrimary);
        } else {
            statusBarColor = actionBarColor = bottomNavColor = color;
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusBarColor);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setBackgroundDrawable(new ColorDrawable(actionBarColor));

        binding.bottomNavigation.setBackgroundColor(bottomNavColor);
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

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
