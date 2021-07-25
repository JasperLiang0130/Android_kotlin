package utas.edu.au.liangyc.assignment2

import androidx.fragment.app.Fragment

interface Communicator {
    fun loadFragment(fragment: Fragment, rmFragment: Fragment, backStack: String?)
}
