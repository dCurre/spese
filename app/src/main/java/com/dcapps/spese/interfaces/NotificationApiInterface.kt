package com.dcapps.spese.interfaces

import com.dcapps.spese.constants.FirebaseConstants
import com.dcapps.spese.data.dto.notification.NotificationMessage
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApiInterface {

    @Headers("Authorization: key=${FirebaseConstants.FIREBASE_SERVER_KEY}", "Content-Type:${FirebaseConstants.CONTENT_TYPE_APPLICATION_JSON}")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: NotificationMessage
    ): Response<ResponseBody>
}