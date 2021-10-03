package dev.mfazio.espnffb.handlers

import dev.mfazio.espnffb.ESPNConfig
import dev.mfazio.espnffb.service.ESPNService
import dev.mfazio.espnffb.types.espn.ESPNScoreboard
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object ESPNServiceHandler {
    private val espnService = Retrofit.Builder()
        .baseUrl(ESPNConfig.baseURL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create())
        .client(
            OkHttpClient.Builder().addInterceptor(
                HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
            ).build()
        )
        .build()
        .create(ESPNService::class.java)

    suspend fun getHistoricalDataJson(year: Int, week: Int): String =
        espnService.getHistoricalESPNWeekJson(year, week)

    suspend fun getModernDataJson(year: Int, week: Int): String =
        espnService.getModernESPNWeekJson(year, week)

    suspend fun getHistoricalData(year: Int, week: Int): ESPNScoreboard =
        espnService.getHistoricalESPNWeek(year, week).first()

    suspend fun getModernData(year: Int, week: Int): ESPNScoreboard =
        espnService.getModernESPNWeek(year, week)
}