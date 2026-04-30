package com.yumaoem.core.utils.network_connection

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresPermission
import com.yumaoem.core.utils.context.PlatformContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

interface NetworkStatusProvider {
    val isConnected: Boolean
    val currentConnectionStatus: ConnectionStatus
    val isConnectedState: StateFlow<Boolean>
    val currentConnectionStatusState: StateFlow<ConnectionStatus>
}




@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun NetworkStatusProvider(): NetworkStatusProvider {
    val appContext = PlatformContext.getApplicationContext() as Context
    val connectivityManager: ConnectivityManager =
        appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val konnectivity = NetworkStatusProviderImpl(
        initialConnection = getCurrentNetworkConnection(connectivityManager)
    )
    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        override fun onAvailable(network: Network) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                konnectivity.onNetworkConnectionChanged(
                    getNetworkConnection(connectivityManager, network)
                )
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val connection = getNetworkConnection(networkCapabilities)
            konnectivity.onNetworkConnectionChanged(connection)
        }

        override fun onLost(network: Network) {
            konnectivity.onNetworkConnectionChanged(ConnectionStatus.NONE)
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    } else {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    return konnectivity
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
private fun getCurrentNetworkConnection(connectivityManager: ConnectivityManager): ConnectionStatus =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        postAndroidMNetworkConnection(connectivityManager)
    } else {
        preAndroidMNetworkConnection(connectivityManager)
    }

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
private fun postAndroidMNetworkConnection(connectivityManager: ConnectivityManager): ConnectionStatus {
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return getNetworkConnection(capabilities)
}

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
@Suppress("DEPRECATION")
private fun preAndroidMNetworkConnection(connectivityManager: ConnectivityManager): ConnectionStatus =
    when (connectivityManager.activeNetworkInfo?.type) {
        null -> ConnectionStatus.NONE
        ConnectivityManager.TYPE_WIFI -> ConnectionStatus.WIFI
        else -> ConnectionStatus.CELLULAR
    }

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
private fun getNetworkConnection(
    connectivityManager: ConnectivityManager,
    network: Network
): ConnectionStatus {
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return getNetworkConnection(capabilities)
}

private fun getNetworkConnection(capabilities: NetworkCapabilities?): ConnectionStatus =
    when {
        capabilities == null -> ConnectionStatus.NONE
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                && !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> ConnectionStatus.NONE
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) -> ConnectionStatus.NONE
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionStatus.WIFI
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionStatus.CELLULAR
        else -> ConnectionStatus.NONE
    }


internal class NetworkStatusProviderImpl(
    initialConnection: ConnectionStatus = ConnectionStatus.NONE,
    ioDispatcher: CoroutineDispatcher = Dispatchers.Default
) : NetworkStatusProvider {

    private val scope = CoroutineScope(ioDispatcher)

    private val state = MutableStateFlow<ConnectionStatus>(initialConnection)

    override val isConnected: Boolean
        get() = state.value != ConnectionStatus.NONE

    override val currentConnectionStatus: ConnectionStatus
        get() = state.value

    override val isConnectedState: StateFlow<Boolean> =
        state.asStateFlow()
            .map(scope) { it != ConnectionStatus.NONE }

    override val currentConnectionStatusState: StateFlow<ConnectionStatus> = state.asStateFlow()

    fun onNetworkConnectionChanged(connection: ConnectionStatus) {
        state.value = connection
    }

    private fun <T, M> StateFlow<T>.map(
        coroutineScope : CoroutineScope,
        mapper : (value : T) -> M
    ) : StateFlow<M> = map { mapper(it) }.stateIn(
        coroutineScope,
        SharingStarted.Eagerly,
        mapper(value)
    )
}