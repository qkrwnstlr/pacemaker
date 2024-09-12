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
                            val action =
                                ProfileFragmentDirections.actionProfileFragmentToModifyFragment()
                            findNavController().navigate(action)
                        }

                        R.id.it_can -> {
                            showSnackStringBar("권한설정")
                        }

                        R.id.it_logout -> {
                            showSnackStringBar("로그아웃")
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
}