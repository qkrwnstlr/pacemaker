package com.ssafy.presentation.myPageUI.profile

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.presentation.R
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentProfileBinding
import com.ssafy.presentation.utils.toAgeString
import com.ssafy.presentation.utils.toCoachIndex
import com.ssafy.presentation.utils.toCount
import com.ssafy.presentation.utils.toDistance
import com.ssafy.presentation.utils.toGenderString
import com.ssafy.presentation.utils.toHeight
import com.ssafy.presentation.utils.toTime
import com.ssafy.presentation.utils.toWeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCollect()
        initListener()
    }

    private fun initView() = with(binding) {
        val slideRight = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_right)
        val slideLeft = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_slide_left)

        ivProfile.startAnimation(slideRight)
        lyRight.startAnimation(slideRight)
        lyLeft.startAnimation(slideLeft)
        btnSetting.startAnimation(slideLeft)
        viewModel.getUserInfo()
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userInfo.collectLatest { user ->
                with(binding) {
                    tvName.text = user.name
                    tvContentTrainCnt.text = user.trainCount.toCount()
                    tvContentTrainTime.text = user.minute.toTime()
                    tvContentTrainKm.text = user.distance.toDistance()
                    etAge.text = user.age.toAgeString()
                    etHeight.text = user.height.toHeight()
                    etWeight.text = user.weight.toWeight()
                    etGender.text = user.gender.toGenderString()

                    val coachIndex = user.coachNumber.toCoachIndex()
                    ivProfile.setImageResource(coachIndex)
                }
            }
        }
    }

    private fun initListener() {
        binding.btnSetting.setOnClickListener {
            PopupMenu(requireContext(), binding.btnSetting).apply {
                menuInflater.inflate(R.menu.profile_menu, this.menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.it_fix -> moveToModifyFragment()
                        R.id.it_can -> moveToHealthConnect()
                        R.id.it_logout -> logout()
                    }
                    true
                }
            }.show()
        }

        binding.ivProfile.setOnClickListener { moveToSelectCoachFragment() }
    }

    private fun moveToSelectCoachFragment() {
        val action = ProfileFragmentDirections.actionProfileFragmentToSelectCoachFragment()
        findNavController().navigate(action)
    }

    private fun moveToModifyFragment() {
        val action = ProfileFragmentDirections.actionProfileFragmentToModifyFragment()
        findNavController().navigate(action)
    }

    private fun moveToLoginFragment() {
        val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun moveToHealthConnect() {
        showSnackStringBar("헬커")
    }

    private fun logout() {
        lifecycleScope.launch {
            auth.signOut()
            clearUid()
            moveToLoginFragment()
        }
    }
}
