package com.dev.unip.weather.ui.screens.weather

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.unip.weather.data.models.ApiWeatherResponse
import com.dev.unip.weather.data.models.ForecastDay
import com.dev.unip.weather.data.models.HistoricalData
import com.dev.unip.weather.data.models.RiskInfo
import com.dev.unip.weather.ui.state.WeatherUiState
import com.dev.unip.weather.ui.theme.WeatherAccentCyan
import com.dev.unip.weather.ui.viewmodels.WeatherViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun formatDatePtBr(dateStr: String?, pattern: String = "dd/MM/yyyy"): String {
    if (dateStr.isNullOrBlank()) return "--"
    return try {
        val clean = dateStr.trim().split("T")[0].split(" ")[0]
        val parts = clean.split("-", "/", ".")
        if (parts.size == 3) {
            val p0 = parts[0].toIntOrNull() ?: 1
            val p1 = parts[1].toIntOrNull() ?: 1
            val p2 = parts[2].toIntOrNull() ?: 1

            val year: Int
            val month: Int
            val day: Int

            if (p0 > 31) {
                year = p0
                if (p1 > 12) {
                    day = p1
                    month = p2
                } else {
                    month = p1
                    day = p2
                }
            } else if (p2 > 31) {
                year = p2
                if (p0 > 12) {
                    day = p0
                    month = p1
                } else {
                    month = p0
                    day = p1
                }
            } else {
                year = if (p0 > 2000) p0 else (2000 + p0)
                month = p1
                day = p2
            }

            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, (month - 1).coerceIn(0, 11))
                set(Calendar.DAY_OF_MONTH, day.coerceIn(1, 31))
            }

            val sdf = SimpleDateFormat(pattern, Locale("pt", "BR"))
            val formatted = sdf.format(cal.time)
            formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() }
        } else {
            dateStr;
        }
    } catch (e: Exception) {
        Log.e("format_date", "${e.message}");
        return dateStr;
    }
}

@Composable
fun WeatherDetailScreen(
    viewModel: WeatherViewModel,
    onNavigateToSearch: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp, bottom = 20.dp, start = 18.dp, end = 18.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Relatório Meteorológico",
                        fontSize = 13.sp,
                        color = Color.LightGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Previsão & Análise",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Button(
                onClick = onNavigateToSearch,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("X", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (val state = uiState) {
            is WeatherUiState.Idle -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhuma consulta realizada",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Acesse a tela de busca para selecionar uma localização e gerar o relatório.",
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateToSearch) {
                            Text("Ir para Busca")
                        }
                    }
                }
            }

            is WeatherUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = WeatherAccentCyan, strokeWidth = 4.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Analisando dados meteorológicos e histórico...",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            is WeatherUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x99FF3333)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Erro ao conectar com a API",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchWeatherReport() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Tentar Novamente", color = Color.Red)
                        }
                    }
                }
            }

            is WeatherUiState.Success -> {
                ReportContent(state.response)
            }
        }
    }
}

@Composable
fun ReportContent(response: ApiWeatherResponse) {
    val weatherData = response.data
    val location = weatherData?.location
    val current = weatherData?.current
    val risk = weatherData?.risk
    val forecast = weatherData?.forecast
    val historical = weatherData?.historical
    val inmet = weatherData?.inmet
    val textReport = response.report

    val heroGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF5C93FF),
            Color(0xFF2854C5)
        )
    )

    val pillGlassBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.25f),
            Color.White.copy(alpha = 0.12f)
        )
    )

    // Hero Weather Display Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(heroGradient)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                val municipalityName = location?.municipality ?: "Localidade"
                val stateName = location?.state
                val locationHeader = if (!stateName.isNullOrEmpty()) "$municipalityName, $stateName" else municipalityName

                Text(
                    text = locationHeader,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                location?.ibgeCode?.let { code ->
                    Text(
                        text = "Código IBGE: $code",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Weather Icon Representation
                Surface(
                    color = Color.White.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.border(1.dp, Color.White.copy(0.4f), CircleShape)
                ) {
                    Text(
                        text = "⛅",
                        fontSize = 58.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Temperature
                Text(
                    text = "${current?.temperature?.let { "%.1f".format(it) } ?: "--"}°C",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Tempo Atual",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 3-Metric Pills (Wind, Humidity, Rain)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(pillGlassBrush)
                        .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(20.dp))
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetricColumn(
                            icon = "🌬️",
                            value = "${current?.windSpeedKmh?.let { "%.1f".format(it) } ?: "--"} km/h",
                            label = "Vento"
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.dp),
                            color = Color.White.copy(0.3f)
                        )
                        MetricColumn(
                            icon = "💧",
                            value = "${current?.humidity?.let { "%.0f".format(it) } ?: "--"}%",
                            label = "Umidade"
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.dp),
                            color = Color.White.copy(0.3f)
                        )
                        MetricColumn(
                            icon = "🌧️",
                            value = "${current?.precipitationMm?.let { "%.1f".format(it) } ?: "0"} mm",
                            label = "Chuva"
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Early Warning / Risk Analysis Card
    risk?.let { RiskCard(risk = it) }

    Spacer(modifier = Modifier.height(18.dp))

    // Text Summary Report
    textReport?.let { report ->
        if (!report.general.isNullOrEmpty() || !report.risk.isNullOrEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, WeatherAccentCyan.copy(alpha = 0.4f), RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Síntese do Agente Meteorológico",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WeatherAccentCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (!report.general.isNullOrEmpty()) {
                        Text(
                            text = report.general,
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (!report.risk.isNullOrEmpty()) {
                        Text(
                            text = "Aviso: ${report.risk}",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }

    // 7-day Forecast Timeline Section
    if (!forecast?.days.isNullOrEmpty()) {
        Text(
            text = "Previsão dos Próximos Dias",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(forecast.days) { day ->
                ForecastDayCard(day)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
    }

    // Historical Disasters Card
    historical?.let { HistoricalDisasterCard(historical = it) }

    // INMET Station Info
    inmet?.station?.let { station ->
        Spacer(modifier = Modifier.height(18.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Estação Meteorológica INMET",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                val periodStartFormatted = formatDatePtBr(inmet.period?.start, "dd/MM/yyyy")
                val periodEndFormatted = formatDatePtBr(inmet.period?.end, "dd/MM/yyyy")
                val periodInfo = if (periodStartFormatted != "--" && periodEndFormatted != "--") {
                    " • Período: $periodStartFormatted até $periodEndFormatted"
                } else ""

                Text(
                    text = "${station.name ?: "--"} (${station.id ?: ""}) • Distância: ${station.distanceKm?.let { "%.1f".format(it) } ?: "--"} km$periodInfo",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MetricColumn(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun RiskCard(risk: RiskInfo) {
    val level = risk.level?.uppercase() ?: "DESCONHECIDO"
    val (backgroundColor, textColor) = when (level) {
        "HIGH", "ALTO", "EXTREMO" -> Color(0xFFDD2C00) to Color.White
        "MEDIUM", "MÉDIO", "MEDIO" -> Color(0xFFFFAB00) to Color.Black
        "LOW", "BAIXO" -> Color(0xFF00C853) to Color.White
        else -> Color.Gray to Color.White
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚠️ ", fontSize = 18.sp)
                    Text(
                        text = "Risco de Desastre",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = level,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            risk.score?.let { score ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Score de Risco: ",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = "%.0f / 100".format(score),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = WeatherAccentCyan
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (risk.factors.isNotEmpty()) {
                Text(
                    text = "Fatores de Risco Detectados:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                risk.factors.forEach { factor ->
                    Surface(
                        color = Color.Black.copy(0.2f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                        Text(
                            text = "• $factor",
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastDayCard(day: ForecastDay) {
    val dateDisplay = formatDatePtBr(day.date, "EEE, dd/MM")

    Card(
        modifier = Modifier
            .width(135.dp)
            .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateDisplay,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${day.temperatureMax?.let { "%.0f".format(it) } ?: "--"}°",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF8A80)
            )
            Text(
                text = "Min: ${day.temperatureMin?.let { "%.0f".format(it) } ?: "--"}°",
                fontSize = 12.sp,
                color = Color(0xFF80D8FF)
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Chuva: ${day.precipitationProbability?.let { "%.0f".format(it) } ?: "0"}%",
                fontSize = 11.sp,
                color = WeatherAccentCyan
            )
            Text(
                text = "${day.precipitationMm?.let { "%.1f".format(it) } ?: "0"} mm",
                fontSize = 11.sp,
                color = Color.LightGray
            )
            Text(
                text = "Vento: ${day.windMaxKmh?.let { "%.0f".format(it) } ?: "0"} km/h",
                fontSize = 11.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun HistoricalDisasterCard(historical: HistoricalData) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Histórico de Impacto do Município",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (!historical.mainDisasterType.isNullOrEmpty()) {
                Text(
                    text = "Principal tipo registrado: ${historical.mainDisasterType}",
                    fontSize = 12.sp,
                    color = WeatherAccentCyan
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total de Eventos: ${historical.totalEvents}", fontSize = 12.sp, color = Color.White)
                    Text("Óbitos: ${historical.deaths}", fontSize = 12.sp, color = Color.White)
                    Text("Feridos: ${historical.injured}", fontSize = 12.sp, color = Color.White)
                }
                Column {
                    Text("Desabrigados: ${historical.homeless}", fontSize = 12.sp, color = Color.White)
                    Text("Desalojados: ${historical.displaced}", fontSize = 12.sp, color = Color.White)
                    Text("Casas Destruídas: ${historical.destroyedHouses}", fontSize = 12.sp, color = Color.White)
                }
            }

            if (historical.totalMaterialDamage > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Danos Materiais: ${currencyFormatter.format(historical.totalMaterialDamage)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFCC80)
                )
            }
        }
    }
}
