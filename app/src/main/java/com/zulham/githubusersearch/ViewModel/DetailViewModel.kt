package com.zulham.githubusersearch.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.zulham.githubusersearch.Model.UserDetail
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailViewModel: ViewModel() {

    private val detail = MutableLiveData<UserDetail>()

    private val isError: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    private val errorMessage = MutableLiveData<String>()

    fun setDetail(login: String?){
        val client = AsyncHttpClient()

        val url = "https://api.github.com/users/$login"

        client.addHeader("Authorization", "token 236f36197e2b0f563eb236ed80a67d3edcdc288d")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {

                try {
                    val result = responseBody?.let { String(it) }
                    val res = result?.let { JSONObject(result) }

                    if (res != null) {
                        val user = UserDetail(
                            avatar_url = res.getString("avatar_url"),
                            name = res.getString("name"),
                            location = checkNullToString(res, "location"),
                            company = checkNullToString(res, "company"),
                            repository = res.getInt("public_repos"),
                                id = res.getInt("id")
                        )

                        detail.value = user

                    } else {

                    }


                } catch (e: Exception) {
                    e.message?.let { setError(true, it) }
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                val errorMessages =
                    when (statusCode) {
                        401 -> "$statusCode : Bad Request"
                        403 -> "$statusCode : Forbidden"
                        404 -> "$statusCode : Not Found"
                        else -> "$statusCode : ${error?.message}"
                    }
                isError.value = true
                errorMessage.value = errorMessages
            }
        })
    }

    fun checkNullToString(res:JSONObject, key: String): String {
        return if (res.isNull(key)) "Kosong" else res.getString(key)
    }

    fun getDetail(): LiveData<UserDetail>{
        return detail
    }

    fun setError(error: Boolean, message: String) {
        isError.value = error
        errorMessage.value = message
    }

    fun getIsError(): LiveData<Boolean> {
        return isError
    }

    fun getErrorMessage(): LiveData<String> {
        return errorMessage
    }

}