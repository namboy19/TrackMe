package com.namboy.trackme.base

import androidx.fragment.app.FragmentManager

class FragmentBackStack(val fragmentManager: FragmentManager, protected val layoutResId: Int){

    var currentFragment : BaseFragment? = null

    fun addFragment(fragment : BaseFragment){
        val fragmentTransaction = fragmentManager.beginTransaction()
        currentFragment = fragment
        fragmentTransaction.add(layoutResId, fragment, fragment.tagName)
            .addToBackStack(fragment.tagName)
            .commitAllowingStateLoss()
        //after transaction you must call the executePendingTransaction
        fragmentManager.executePendingTransactions()
    }

    fun replaceFragment(fragment : BaseFragment){
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction
            .replace(layoutResId, fragment, fragment.tagName)
            .addToBackStack(fragment.tagName)

        currentFragment?.let {
            fragmentTransaction.remove(it)
            fragmentManager.popBackStack()
        }

            fragmentTransaction.commitAllowingStateLoss()
        currentFragment = fragment

        //after transaction you must call the executePendingTransaction
        fragmentManager.executePendingTransactions()
    }

    fun popFragment(){
        fragmentManager.popBackStackImmediate()
    }

    fun getFragmentCount()=fragmentManager.backStackEntryCount

}