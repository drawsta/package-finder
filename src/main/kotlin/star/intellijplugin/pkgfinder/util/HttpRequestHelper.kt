package star.intellijplugin.pkgfinder.util

import com.intellij.util.net.HttpConnectionUtils
import java.net.HttpURLConnection

/**
 * @author drawsta
 * @LastModified: 2025-07-16
 * @since 2025-07-12
 */
object HttpRequestHelper {

    private const val CONNECT_TIMEOUT: Int = 10_000

    private const val READ_TIMEOUT: Int = 10_000

    sealed class RequestResult<out T> {
        data class Success<out T>(val data: T) : RequestResult<T>()
        data class Error(val exception: Throwable, val responseCode: Int? = null) : RequestResult<Nothing>()
    }

    private fun createConnection(url: String): HttpURLConnection {
        return HttpConnectionUtils.openHttpConnection(url).apply {
            connectTimeout = CONNECT_TIMEOUT
            readTimeout = READ_TIMEOUT
        }
    }

    private fun <T> executeRequest(
        url: String,
        handleResponse: (HttpURLConnection) -> T
    ): RequestResult<T> {
        return try {
            val connection = createConnection(url)

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                RequestResult.Error(
                    Exception("HTTP error: ${connection.responseCode}"),
                    connection.responseCode
                )
            } else {
                RequestResult.Success(handleResponse(connection))
            }
        } catch (e: Exception) {
            RequestResult.Error(e)
        }
    }

    fun <R> getForObject(
        url: String,
        parser: (String) -> R
    ): RequestResult<R?> {
        return executeRequest(url) { connection ->
            connection.inputStream.bufferedReader().use { reader ->
                val response = reader.readText()
                response.takeUnless { it.isBlank() }?.let(parser)
            }
        }
    }

    fun <T> getForList(
        url: String,
        parser: (String) -> List<T>
    ): RequestResult<List<T>> {
        return executeRequest(url) { connection ->
            connection.inputStream.bufferedReader().use { reader ->
                val response = reader.readText()
                if (response.isBlank()) emptyList() else parser(response)
            }
        }
    }
}
