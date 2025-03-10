package com.ssafy.presentation.loginUI.login

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.ssafy.presentation.BuildConfig
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.core.MainViewModel
import com.ssafy.presentation.core.SplashActivity
import com.ssafy.presentation.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCheck()
        initListener()
        initFirebase()
    }

    private fun initCheck() {
        val uid = requireActivity().intent.getStringExtra(SplashActivity.UID)
        requireActivity().intent.removeExtra(SplashActivity.UID)
        if (uid.isNullOrBlank()) return

        mainViewModel.updateNewUid(uid)
        if (getUid().isNotBlank()) {
            moveToHomeFragment()
        }
    }

    private fun initListener() = with(binding) {
        btn.setOnClickListener {
            signIn()
        }
    }

    private fun initFirebase() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(BuildConfig.server_client_id)
            .requestIdToken(BuildConfig.server_client_id)
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    override fun onStart() {
        super.onStart()
        if (getUid().isBlank()) {
            googleSignInClient.signOut()
        }
        val currentUser = auth.currentUser

        if (currentUser != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.signUp(
                    currentUser.uid,
                    currentUser.displayName,
                    currentUser.photoUrl.toString(),
                    ::moveToConnectFragment,
                    ::moveToHomeFragment,
                    ::saveUid
                )
            }
        }
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Timber.w(e, "Google 로그인에 실패했습니다.")
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        viewModel.signUp(
                            user.uid,
                            user.displayName,
                            user.photoUrl.toString(),
                            ::moveToConnectFragment,
                            ::moveToHomeFragment,
                            ::saveUid
                        )
                    }
                }
            }
    }


    private fun moveToConnectFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToConnectFragment()
        findNavController().navigate(action)
    }

    private fun moveToHomeFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private suspend fun saveUid(uid: String) {
        mainViewModel.setNewUid(uid)
    }
}