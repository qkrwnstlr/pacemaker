package com.ssafy.presentation.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun showSnackBar(@StringRes msgResId: Int) {
        val message = getString(msgResId)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackStringBar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    suspend fun showSnackBar(msg: String) = withContext(Dispatchers.Main) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    fun getUid() = viewModel.uid.value
    fun clearUid(goLogin: () -> Unit) = viewLifecycleOwner.lifecycleScope.launch {
        runCatching { viewModel.clearUid() }
            .onSuccess { goLogin() }
    }

    var auth: FirebaseAuth = Firebase.auth

}
