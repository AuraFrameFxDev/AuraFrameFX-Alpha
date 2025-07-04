package com.example.app.ipc

// Explicitly import the AIDL interface
import com.example.app.ipc.IAuraDriveService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class OracleDriveServiceConnector(private val context: Context) {
    private var auraDriveService: IAuraDriveService? = null
    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        /**
         * Called when the AuraDrive service is connected.
         *
         * Initializes the AIDL interface for the AuraDrive service and updates the connection status to connected.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            auraDriveService = IAuraDriveService.Companion.Stub.asInterface(service)
            _isServiceConnected.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            auraDriveService = null
            _isServiceConnected.value = false
        }
    }

    fun bindService() {
        val intent = Intent().apply {
            component = ComponentName(
                "com.genesis.ai.app",
                "com.genesis.ai.app.service.AuraDriveServiceImpl"
            )
        }
        try {
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: SecurityException) {
            _isServiceConnected.value = false
        }
    }

    fun unbindService() {
        try {
            context.unbindService(serviceConnection)
        } catch (_: Exception) {
        }
        auraDriveService = null
        _isServiceConnected.value = false
    }

    suspend fun getStatusFromOracleDrive(): String? = withContext(Dispatchers.IO) {
        try {
            auraDriveService?.getOracleDriveStatus()
        } catch (e: RemoteException) {
            null
        }
    }

    /**
         * Toggles the LSPosed module state on the connected AuraDrive service.
         *
         * @param packageName The package name of the module to toggle (currently unused).
         * @param enable Whether to enable or disable the module (currently unused).
         * @return "Success" if the operation succeeds, "Failed" if it does not, or null if the service is unavailable or a remote exception occurs.
         */
        suspend fun toggleModuleOnOracleDrive(packageName: String, enable: Boolean): String? =
        withContext(Dispatchers.IO) {
            try {
                val result = auraDriveService?.toggleLSPosedModule()
                if (result == true) "Success" else "Failed"
            } catch (e: RemoteException) {
                null
            }
        }

    suspend fun getDetailedInternalStatus(): String? = withContext(Dispatchers.IO) {
        try {
            auraDriveService?.getDetailedInternalStatus()
        } catch (e: RemoteException) {
            null
        }
    }

    /**
     * Retrieves the internal diagnostics log from the AuraDrive service as a single string.
     *
     * @return The diagnostics log joined by newlines, or null if the service is unavailable or a remote exception occurs.
     */
    suspend fun getInternalDiagnosticsLog(): String? = withContext(Dispatchers.IO) {
        try {
            val logs = auraDriveService?.getInternalDiagnosticsLog()
            logs?.joinToString("\n")
        } catch (e: RemoteException) {
            null
        }
    }
}
