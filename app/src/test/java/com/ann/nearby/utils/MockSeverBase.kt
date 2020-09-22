package com.ann.nearby.utils

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.io.IOException
import java.io.InputStream


open class MockSeverBase {
    private val server: MockWebServer = MockWebServer()

    open fun setup() {
        server.start(MOCK_WEBSERVER_PORT)
    }

    open fun tearDown() {
        server.shutdown()
    }

    fun getUrl(): String = server.url("/").toString()

    fun enqueue(expectResponse: MockResponse) = server.enqueue(expectResponse)

    fun `mock network response with json file`(expectCode: Int, fileName: String) = MockResponse()
        .setResponseCode(expectCode)
        .setBody(FileUtils.readTestResourceFile(fileName))

}

object FileUtils {
    fun readTestResourceFile(fileName: String): String {
        var fileInputStream: InputStream? = null
        try {
            fileInputStream = javaClass.classLoader?.getResourceAsStream(fileName)
            return fileInputStream?.bufferedReader()?.readText() ?: ""
        } catch (e: IOException) {
            throw e
        } finally {
            fileInputStream?.close()
        }
    }
}

const val MOCK_WEBSERVER_PORT = 8000