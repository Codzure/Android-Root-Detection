package com.codzure.leonard.androidrootdetection

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

object RootChecker {

    fun isRooted(context: Context): Boolean {
        val isTestBuild = isTestBuild()
        val hasSuperuserAPK = hasSuperuserAPK()
        val hasChainfiresupersu = hasChainfiresupersu(context)
        val hasSU = hasSU()
        Log.d(
            "RootChecker",
            "isTestBuild: $isTestBuild hasSuperuserAPK: $hasSuperuserAPK hasChainfiresupersu: $hasChainfiresupersu hasSU: $hasSU"
        )
        return isTestBuild || hasSuperuserAPK || hasChainfiresupersu || hasSU
    }

    /**************************************** Checker methods *************************************/
    private fun isTestBuild(): Boolean {
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun hasSuperuserAPK(): Boolean {
        return try {
            val file = File("/system/app/Superuser.apk")
            file.exists()
        } catch (e: Exception) {
            false
        }
    }

    private fun hasChainfiresupersu(context: Context): Boolean {
        return isPackageInstalled("eu.chainfire.supersu", context)
    }

    private fun hasSU(): Boolean {
        return findBinary("su") || executeCommand(arrayOf("/system/xbin/which", "su")) || executeCommand(arrayOf("which", "su"))
    }

    /**************************************** Helper methods **************************************/
    private fun isPackageInstalled(packagename: String, context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun findBinary(binaryName: String): Boolean {
        val places = arrayOf(
            "/sbin/",
            "/system/bin/",
            "/system/xbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/"
        )
        for (where in places) {
            if (File(where + binaryName).exists()) {
                return true
            }
        }
        return false
    }

    private fun executeCommand(command: Array<String>): Boolean {
        var localProcess: Process? = null
        var inReader: BufferedReader? = null
        return try {
            localProcess = Runtime.getRuntime().exec(command)
            inReader = BufferedReader(InputStreamReader(localProcess.inputStream))
            inReader.readLine() != null
        } catch (e: Exception) {
            false
        } finally {
            localProcess?.destroy()
            try {
                inReader?.close()
            } catch (e: IOException) {
                e.message?.let { Log.e("RootChecker", it) }
                e.printStackTrace()
            }
        }
    }
}
