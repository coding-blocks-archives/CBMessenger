package com.puldroid.cbmessenger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.puldroid.cbmessenger.adapters.ViewPagerAdapter

class MainActivity : AppCompatActivity() {
    val pager by lazy {
        findViewById<ViewPager2>(R.id.viewPager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(
            findViewById(R.id.tabs),
            pager,
            TabLayoutMediator.TabConfigurationStrategy { tab, pos ->
                when (pos) {
                    0 -> tab.text = "Chats"
                    1 -> tab.text = "Users"
                }
            }).attach()
    }
}