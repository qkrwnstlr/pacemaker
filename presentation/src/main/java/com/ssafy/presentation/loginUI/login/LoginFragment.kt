package com.ssafy.presentation.loginUI.login

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() = with(binding) {
        btn.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToConnectFragment()
            findNavController().navigate(action)
        }
    }
}