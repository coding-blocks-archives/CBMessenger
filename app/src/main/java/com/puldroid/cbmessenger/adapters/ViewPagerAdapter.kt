package com.puldroid.cbmessenger.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.puldroid.cbmessenger.fragments.ChatsFragment
import com.puldroid.cbmessenger.fragments.UsersFragment

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ChatsFragment()
        else -> UsersFragment()
    }

}