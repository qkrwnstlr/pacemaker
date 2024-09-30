package com.ssafy.presentation.core

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.model.Config
import com.lottiefiles.dotlottie.core.util.DotLottieEventListener
import com.lottiefiles.dotlottie.core.util.DotLottieSource
import com.ssafy.presentation.R
import com.ssafy.presentation.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showSplash()
        viewModel.login()
    }

    private fun showSplash() {
        val config = Config.Builder()
            .autoplay(true)
            .playMode(Mode.FORWARD)
            .speed(1f)
            .source(DotLottieSource.Asset("splash.lottie"))
            .useFrameInterpolation(true)
            .build()

        binding.lottieView.apply {
            addEventListener(object : DotLottieEventListener {
                override fun onComplete() {
                    super.onComplete()
                    collectUID()
                    removeEventListener(this)
                }
            })

            load(config)
        }
    }

    private fun collectUID() = lifecycleScope.launch {
        viewModel.uidState.collectLatest { uid ->
            uid?.let { moveToMainActivity(it) }
        }
    }

    private fun moveToMainActivity(uid: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(UID, uid)
        }
        startActivity(intent)
    }

    companion object {
        const val UID = "UID"
    }
}
