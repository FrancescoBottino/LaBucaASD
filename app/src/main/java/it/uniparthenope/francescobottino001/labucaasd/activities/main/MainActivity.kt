package it.uniparthenope.francescobottino001.labucaasd.activities.main

import android.os.Bundle
import it.uniparthenope.francescobottino001.labucaasd.BasicActivity
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list.TimersListFragment

class MainActivity : BasicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TimersListFragment.newInstance())
                .commitNow()
        }
    }
}