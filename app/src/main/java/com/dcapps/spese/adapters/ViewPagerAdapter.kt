package com.dcapps.spese.adapters

import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(activity: FragmentActivity?) : FragmentStateAdapter(activity!!) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()
    private val mIconList = ArrayList<Drawable>()

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    fun getTabTitle(position : Int): String{
        return mFragmentTitleList[position]
    }

    fun getIcon(position: Int): Drawable{
        return mIconList[position]
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    fun addFragment(fragment: Fragment, title: String, icon: Drawable?){
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
        mIconList.add(icon!!)
    }
}