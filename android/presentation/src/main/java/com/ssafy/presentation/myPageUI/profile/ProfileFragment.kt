package com.ssafy.presentation.myPageUI.profile

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentProfileBinding

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSetting.setOnClickListener {
            PopupMenu(requireContext(), binding.btnSetting).apply {
                menuInflater.inflate(R.menu.profile_menu, this.menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.it_fix -> {
                            goModify()
                        }

                        R.id.it_can -> {
                            goHealthConnect()
                        }

                        R.id.it_logout -> {
                            logout()
                        }
                    }
                    true
                }

            }.show()
        }
        binding.ivProfile.setOnClickListener {
            showSnackStringBar("강사 바꾸는 뷰로 고고")
        }
    }

    private fun goModify() {
        val action =
            ProfileFragmentDirections.actionProfileFragmentToModifyFragment()
        findNavController().navigate(action)
    }

    private fun goHealthConnect() {
        showSnackStringBar("헬커")
    }

    private fun logout() {
        showSnackStringBar("로그아웃")
    }
}