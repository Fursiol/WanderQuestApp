package com.example.wanderquest

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var actionBarDrawerToggle : ActionBarDrawerToggle

    //Aktualizacja isAdmin po logowaniu i przeładowanie menu żeby pokazał się panel admina
    var isAdmin = false
        set(value){
            field = value
            showAdminNav()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_WanderQuest)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(binding.fragmentContainerView.id) as NavHostFragment
        navController = navHostFragment.findNavController()

        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val logOutText = binding.navigationView.menu.findItem(R.id.nav_log_out)
        val s = SpannableString(logOutText.title)
        s.setSpan(ForegroundColorSpan(Color.Red.toArgb()), 0, s.length, 0)
        logOutText.title = s

        binding.navigationView.setNavigationItemSelectedListener {menuItem ->
            menuItem.isChecked = true

            when (menuItem.itemId)  {

                R.id.nav_main_view -> {
                    navController.navigate(R.id.mainFragment)
                }

                R.id.nav_check_weather -> {
                    navController.navigate(R.id.weatherFragment)
                }
                R.id.nav_city_events -> {
                    navController.navigate(R.id.cityEventsFragment)
                }
                R.id.nav_admin_dashboard -> {
                    navController.navigate(R.id.adminDashboardFragment)
                }
                R.id.nav_log_out -> {
                    navController.navigate(R.id.loginFragment)
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    fun showAdminNav(){
        val adminDashboardText = binding.navigationView.menu.findItem(R.id.nav_admin_dashboard)
        if(isAdmin){
            val s = SpannableString(adminDashboardText.title)
            s.setSpan(ForegroundColorSpan(Color.Green.toArgb()), 0, s.length, 0)
            adminDashboardText.title = s
        } else {
            adminDashboardText.isVisible = false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

