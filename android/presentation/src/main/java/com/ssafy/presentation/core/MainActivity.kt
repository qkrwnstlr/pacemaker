package com.ssafy.presentation.core

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.ssafy.presentation.R

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            val navHostFragment = NavHostFragment.create(R.navigation.nav_graph)
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, navHostFragment)
                .setPrimaryNavigationFragment(navHostFragment)
                .commit()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val currentFocus = currentFocus ?: return super.dispatchTouchEvent(ev)
        val rect = Rect()
        currentFocus.getGlobalVisibleRect(rect)
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        if (rect.contains(x, y)) return super.dispatchTouchEvent(ev)

        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.let { imm.hideSoftInputFromWindow(currentFocus.windowToken, 0) }

        currentFocus.clearFocus()
        return super.dispatchTouchEvent(ev)
    }

}