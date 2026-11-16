package com.example.goldcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF14120C) // رنگ پس‌زمینه تیره
            ) {
                GoldCalculatorScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldCalculatorScreen() {
    var goldPrice18K by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var karat by remember { mutableStateOf("750") }
    var wagePercent by remember { mutableStateOf("") }
    var profitPercent by remember { mutableStateOf("7") }
    var taxPercent by remember { mutableStateOf("10") }

    var finalPrice by remember { mutableStateOf<Double?>(null) }
    var rawGoldCost by remember { mutableStateOf(0.0) }
    var wageCost by remember { mutableStateOf(0.0) }
    var profitCost by remember { mutableStateOf(0.0) }
    var taxCost by remember { mutableStateOf(0.0) }

    val formatter = DecimalFormat("#,###")

    fun calculate() {
        val price18 = goldPrice18K.replace(",", "").toDoubleOrNull() ?: 0.0
        val w = weight.toDoubleOrNull() ?: 0.0
        val k = karat.toDoubleOrNull() ?: 750.0
        val wage = wagePercent.toDoubleOrNull() ?: 0.0
        val profit = profitPercent.toDoubleOrNull() ?: 0.0
        val tax = taxPercent.toDoubleOrNull() ?: 0.0

        if (price18 <= 0 || w <= 0) return

        // فرمول تبدیل عیار و محاسبه اجرت، سود و مالیات
        val equivalentWeight18 = w * (k / 750.0)
        val raw = equivalentWeight18 * price18
        val wageAmt = raw * (wage / 100.0)
        val profitAmt = (raw + wageAmt) * (profit / 100.0)
        val taxAmt = (wageAmt + profitAmt) * (tax / 100.0)
        val total = raw + wageAmt + profitAmt + taxAmt

        rawGoldCost = raw
        wageCost = wageAmt
        profitCost = profitAmt
        taxCost = taxAmt
        finalPrice = total
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // هدر و عنوان
        Text(
            text = "💎 محاسبه‌گر خرید طلا",
            color = Color(0xFFF5C84C),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // کارت نمایش نتیجه
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B13)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "مبلغ نهایی فاکتور", color = Color(0xFF9A9484), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (finalPrice != null) "${formatter.format(finalPrice)} تومان" else "۰ تومان",
                    color = Color(0xFFF5C84C),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                
                if (finalPrice != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFF332C1D))
                    Spacer(modifier = Modifier.height(8.dp))
                    ResultRow("طلای خام:", formatter.format(rawGoldCost))
                    ResultRow("اجرت ساخت:", formatter.format(wageCost))
                    ResultRow("سود فروشنده:", formatter.format(profitCost))
                    ResultRow("مالیات:", formatter.format(taxCost))
                }
            }
        }

        // بخش ورود اطلاعات
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B13)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "⚖️ مشخصات و نرخ طلا", color = Color(0xFFF5C84C), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = goldPrice18K,
                    onValueChange = { goldPrice18K = it },
                    label = { Text("نرخ هر گرم طلای ۱۸ عیار (تومان)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("وزن (گرم)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = karat,
                        onValueChange = { karat = it },
                        label = { Text("عیار") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        }

        // بخش درصدها
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B13)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "% درصد سود، اجرت و مالیات", color = Color(0xFFF5C84C), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = wagePercent,
                        onValueChange = { wagePercent = it },
                        label = { Text("اجرت (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = profitPercent,
                        onValueChange = { profitPercent = it },
                        label = { Text("سود (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = taxPercent,
                    onValueChange = { taxPercent = it },
                    label = { Text("مالیات بر ارزش افزوده (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // دکمه محاسبه
        Button(
            onClick = { calculate() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5C84C)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(text = "محاسبه فاکتور نهایی", color = Color(0xFF14120C), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun ResultRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, color = Color(0xFF9A9484), fontSize = 12.sp)
        Text(text = "$value تومان", color = Color(0xFFF3EFE4), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
