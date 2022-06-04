package com.dcapps.spese.services


import com.dcapps.spese.constants.FirebaseConstants.Companion.BASE_FMC_API_URL
import com.dcapps.spese.interfaces.NotificationApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstanceService {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_FMC_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationApiInterface::class.java)
        }
    }
}