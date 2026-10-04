package com.dev.unip.weather.data.repositories

import com.dev.unip.weather.data.models.ApiWeatherResponse
import com.dev.unip.weather.data.models.WeatherRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class WeatherRepository(
    private val service: HttpClient,
    private val baseUrl: String = "http://10.0.2.2:8000"
) {
    suspend fun getWeatherReport(request: WeatherRequest): ApiWeatherResponse {
        val url = "$baseUrl/api/weather/report"
        val response: HttpResponse = service.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else {
            throw Exception("Erro ao consultar a API local (${response.status.value})")
        }
    }
}
