package com.ann.nearby

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/*Use this in build gradle*/
class TestRunner:AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}