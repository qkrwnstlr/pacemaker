package com.ssafy.presentation.utils

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.safeCast

inline fun <reified T : IBinder, reified S : Service> ActivityRetainedLifecycle.bindService(context: Context): BinderConnection<T> {
    val connection = BinderConnection(context, T::class)
    BinderConnection.bindService(context, S::class, connection)
    addOnClearedListener {
        connection.unbind()
    }
    return connection
}

class BinderConnection<T : IBinder>(
    private val context: Context,
    private val type: KClass<out T>,
) : ServiceConnection {
    private val mutableBinder = MutableStateFlow<T?>(null)
    private val binder = mutableBinder.asStateFlow()

    fun unbind() {
        context.unbindService(this)
    }

    suspend fun <R> runWhenConnected(command: suspend (T) -> R): R =
        command(binder.filterNotNull().first())

    fun <N, V : Flow<N>> flowWhenConnected(property: KProperty1<T, V>): Flow<N> =
        binder.flatMapLatest { it?.let { property.get(it) } ?: emptyFlow() }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        type.safeCast(service)?.also {
            mutableBinder.value = it
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mutableBinder.value = null
    }

    companion object {
        inline fun <reified T : IBinder, reified S : Service> ViewModelLifecycle.bindService(context: Context): BinderConnection<T> {
            val connection = BinderConnection(context, T::class)
            bindService(context, S::class, connection)
            addOnClearedListener {
                connection.unbind()
            }
            return connection
        }

        inline fun <reified T : IBinder, reified S : Service> Lifecycle.bindService(
            context: Context,
        ): BinderConnection<T> {
            val connection = BinderConnection(context, T::class)
            addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    bindService(context, S::class, connection)
                }

                override fun onStop(owner: LifecycleOwner) {
                    connection.unbind()
                }
            })
            return connection
        }

        inline fun <reified T : IBinder, reified S : Service> CoroutineScope.bindService(
            context: Context,
        ): BinderConnection<T> {
            val connection = BinderConnection(context, T::class)
            launch {
                try {
                    bindService(context, S::class, connection)
                    awaitCancellation()
                } finally {
                    connection.unbind()
                }
            }
            return connection
        }

        fun <S : Service> bindService(
            context: Context,
            service: KClass<S>,
            connection: BinderConnection<*>,
        ) {
            Intent(context, service.java).also { intent ->
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }
}