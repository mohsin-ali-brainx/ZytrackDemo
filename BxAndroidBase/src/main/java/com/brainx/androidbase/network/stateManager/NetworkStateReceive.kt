package com.brainx.androidbase.network.stateManager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.brainx.androidext.ext.isInternetAvailable
import com.brainx.androidbase.Ktx

class NetworkStateReceive : BroadcastReceiver() {

    //region Lifecycle
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.extras == null || context == null) return
        Ktx.isNetWorkConnected = context.isInternetAvailable()
        NetworkStateManager.instance.mNetworkStateCallback.postValue(NetState(Ktx.isNetWorkConnected))
    }
    //endregion
}