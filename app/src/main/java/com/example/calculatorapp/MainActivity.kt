package com.example.calculatorapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculatorapp.components.InputField
import com.example.calculatorapp.ui.theme.CalculatorAppTheme
import com.example.calculatorapp.uitls.calculateTotalPerPerson
import com.example.calculatorapp.uitls.calculateTotalTip
import com.example.calculatorapp.widgets.RoundIconButton
import kotlin.math.log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @OptIn(ExperimentalComposeUiApi::class)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}


@Composable
fun MyApp(content: @Composable () -> Unit) {
    CalculatorAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            // modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 80.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 15.dp)
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(20.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {
    BillForm() { billAmt ->
        println(billAmt)
        Log.d("AMT", "MainContent: $billAmt")
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalculatorAppTheme {
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier, onValChange: (String) -> Unit = {}) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    val splitByState = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    Column {
        TopHeader(totalPerPerson = totalPerPersonState.value)
        Spacer(modifier = Modifier.height(height = 1.dp))
        Surface(
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(all = 5.dp),
                // verticalArrangement = Arrangement.Center
                // horizontalAlignment = Alignment.CenterHorizontally
            ) {

                InputField(valueState = totalBillState,
                    lableId = "Enter Bill",
                    enabled = true,
                    isSingelLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        keyboardController?.hide()
                    })
                if (validState) {
                    Row(
                        modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Split", modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            )
                        )
                        Spacer(modifier = Modifier.width(120.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value - 1 else 1
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage,
                                    splitBy = splitByState.value
                                )
                            })
                            Text(
                                "${splitByState.value}",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 5.dp, end = 5.dp)
                            )
                            RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                                splitByState.value = splitByState.value + 1
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage,
                                    splitBy = splitByState.value
                                )
                            })
                        }

                    }
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Tip", modifier = Modifier.align(
                                alignment = Alignment.CenterVertically
                            )
                        )
                        Spacer(modifier = Modifier.width(150.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "$${tipAmountState.value}", modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    ) {
                        Text(
                            text = "$tipPercentage %"
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Slider(
                            value = sliderPositionState.value,
                            onValueChange = { newVal ->
                                sliderPositionState.value = newVal
                                tipAmountState.value = calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage
                                )
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = tipPercentage,
                                    splitBy = splitByState.value
                                )
                                println(newVal)
                            }, steps = 5, modifier = Modifier.padding(
                                start = 15.dp, end = 15.dp
                            )
                        )
                    }
                }
            }
        }
    }
}


