package com.ssafy.presentation.loginUI.connect

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentConnectBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConnectFragment : BaseFragment<FragmentConnectBinding>(FragmentConnectBinding::inflate) {
    private val viewModel: ConnectViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Set<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(viewModel.requestPermissionsActivityContract()) {
            lifecycleScope.launch {
                if(viewModel.hasAllPermissions()) viewModel.syncWithHealthConnect(getUid())
                val action = ConnectFragmentDirections.actionConnectFragmentToStartFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
        checkPermissions()
    }

    private fun initView() = with(binding.baseLayout) {
        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_up)

        tvExplain.apply {
            text = "Health Connect를 연동하세요!"
            startAnimation(slideDown)
        }

        fabBlue.apply {
            text = "권한 설정하기"
            startAnimation(slideUp)
        }

        ivRunner.visibility = View.GONE
        ivHealthConnect.visibility = View.VISIBLE
    }

    private fun initListener() = with(binding.baseLayout) {
        fabBlue.setOnClickListener {
            viewModel.launchPermissionsLauncher(permissionLauncher)
        }
    }

    private fun checkPermissions() {
        lifecycleScope.launch {
            try {
                if (viewModel.hasAllPermissions()) {
                    Toast.makeText(
                        requireContext(),
                        "All permissions granted",
                        Toast.LENGTH_LONG,
                    ).show()
                } else {
                    viewModel.launchPermissionsLauncher(permissionLauncher)
                }
            } catch (exception: Exception) {
                Toast.makeText(requireContext(), "Error: $exception", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}