package com.ssafy.presentation.loginUI.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.domain.response.ResponseResult
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
        initCollect()
        initCheck()
    }

    private fun initListener() = with(binding) {
        btn.setOnClickListener {
            // TODO 구글 계정 접속 후 사용자 정보 넘기기
            viewModel.signUp("testtest", "테스트임다")
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.signUpEvent.collectLatest { event ->
                when (event) {
                    is ResponseResult.Success -> moveToConnectFragment()
                    is ResponseResult.Error -> showSnackStringBar(event.message)
                }
            }
        }
    }

    private fun initCheck() {
        val uid = getUid()
        if (uid.isNotBlank()) viewModel.checkUser(uid, ::moveToHomeFragment)
    }

    private fun moveToConnectFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToConnectFragment()
        findNavController().navigate(action)
    }

    private fun moveToHomeFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}
