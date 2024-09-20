package com.ssafy.presentation.loginUI.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.ssafy.domain.response.ResponseResult
import com.ssafy.presentation.core.BaseFragment
import com.ssafy.presentation.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    private lateinit var signInClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        initCollect()
        initCheck()
        binding.btn.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        signInClient = Identity.getSignInClient(requireActivity())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("202604819546-jqds6rht7f3it6d9hcm9q390966i7mmr.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false) // 모든 계정 표시
                    .build()
            )
            .setAutoSelectEnabled(true) // 자동 선택 활성화
            .build()
        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                resultLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                // 실패 처리
                Log.e(TAG, "One Tap Sign-In failed", e)
            }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        val account = task.getResult(ApiException::class.java)
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account?.id)
                        val idToken = account?.idToken
                        if (idToken != null) {
                            firebaseAuthWithGoogle(idToken)
                        } else {

                        }
                    } catch (e: ApiException) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e)
                    }
                }
            } else {
                Log.w(TAG, "User cancelled sign-in or failed")
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
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
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val directions = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            findNavController().navigate(directions)
        }
    }

    companion object {
        private const val TAG = "GoogleActivity!!!!!!!!!!!"
    }
}
