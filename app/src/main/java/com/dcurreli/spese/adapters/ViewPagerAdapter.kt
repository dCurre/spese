package com.dcurreli.spese.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()
    private val mIconList = ArrayList<Int>()

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getPageTitle(position: Int): String {
        return mFragmentTitleList[position]
    }

    fun getIcon(position: Int): Int{
        return mIconList[position]
    }

    fun addFragment(fragment: Fragment, title: String, icSettings: Int){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
        mIconList.add(icSettings)
    }
}