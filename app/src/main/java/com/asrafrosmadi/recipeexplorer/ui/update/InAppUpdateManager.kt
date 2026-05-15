package com.asrafrosmadi.recipeexplorer.ui.update

import android.app.Activity
import android.content.IntentSender
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateManager(
    private val activity: Activity
) {

    companion object {
        const val UPDATE_REQUEST_CODE = 1001
    }

    private val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(activity)

    fun checkForUpdate() {

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            if (
                appUpdateInfo.updateAvailability() ==
                UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                startImmediateUpdate(appUpdateInfo)
            }
        }
    }

    private fun startImmediateUpdate(
        appUpdateInfo: AppUpdateInfo
    ) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                activity,
                UPDATE_REQUEST_CODE
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    fun onResume() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (
                    appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    startImmediateUpdate(appUpdateInfo)
                }
            }
    }
}