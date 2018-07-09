package com.brianroper.andevweekly.home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.brianroper.andevweekly.R
import com.brianroper.andevweekly.about.AboutActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        handleToolbarBehavior(home_toolbar)
        initAdapter()
    }

    private fun initAdapter(){
        home_recycler.layoutManager = LinearLayoutManager(this)
        home_recycler.adapter = HomeScreenAdapter(configHomeItems(), this)
    }

    private fun configHomeItems(): ArrayList<HomeItem> {
        val homeItems: ArrayList<HomeItem> = ArrayList()
        val homeItem = HomeItem("Android Weekly", R.drawable.android_weekly)
        homeItems.add(homeItem)
        return homeItems
    }

    private fun handleToolbarBehavior(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setLogo(R.drawable.toolbarlogo)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                val aboutIntent = Intent(this, AboutActivity::class.java)
                startActivity(aboutIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
