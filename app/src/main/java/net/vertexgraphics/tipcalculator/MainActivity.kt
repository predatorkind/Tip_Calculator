package net.vertexgraphics.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.vertexgraphics.tipcalculator.ui.theme.TipCalculatorTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TipCalculatorScreen()
                }
            }
        }
    }
}


/**
 * Main screen of the app. Contains text fields for bill and tip amounts, switch enabling rounding
 * the tip and output text providing the user with calculated tip total.
 *  This is just a test... This is just a test... This is just a test... This is just a test...
 */
@Composable
fun TipCalculatorScreen() {
    // holds the state of bill amount
    var amountInput by remember { mutableStateOf("")}
    val amount = amountInput.toDoubleOrNull() ?: 0.0

    // holds the state of the flag signifying whether to round the tip up
    var roundUp by remember { mutableStateOf(false)}

    // holds the state of tip amount
    var tipInput by remember { mutableStateOf("")}
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
    val tip = calculateTip(amount, tipPercent, roundUp)

    val focusManager = LocalFocusManager.current



    // all UI elements stored inside a Column
    Column(modifier = Modifier.padding(32.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)) {

        // app title
        Text(
            text = stringResource(id = R.string.calculate_tip),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        Spacer(modifier = Modifier.height(16.dp))

        // input fields
        EditNumberField(label = R.string.bill_amount,
            keyboardOptions= KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next), keyboardActions=
            KeyboardActions(onNext={focusManager.moveFocus(
                FocusDirection.Down)}), value = amountInput, onValueChange = {amountInput = it})
        EditNumberField(label = R.string.how_was_the_service,
            keyboardOptions= KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done), keyboardActions=
            KeyboardActions(onDone={ focusManager.clearFocus()}),
            value = tipInput, onValueChange = {tipInput = it})

        // tip round up switch
        RoundTheTipRow(roundUp = roundUp, onRoundUpChanged = { roundUp = it})
        Spacer(modifier = Modifier.height(24.dp))

        // result output text box
        Text(text= stringResource(id = R.string.tip_amount, tip),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


/**
 * A labeled switch UI element used to
 *
 * @param roundUp: the state of the switch
 * @param onRoundUpChanged: callback function
 * @param modifier: switch visuals
 */
@Composable
fun RoundTheTipRow(roundUp: Boolean, onRoundUpChanged: (Boolean) -> Unit, modifier: Modifier = Modifier){
    Row(modifier= Modifier
        .fillMaxWidth()
        .size(48.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(id = R.string.round_up_tip))
        Switch(modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End),
            checked = roundUp, onCheckedChange = onRoundUpChanged,
            colors = SwitchDefaults.colors(uncheckedThumbColor = Color.DarkGray))
    }
}


/**
 * Generic text input field used in the UI
 *
 * @param label: the resource ID of  the string used as a label
 * @param keyboardOptions: specific keyboard options for a given use case
 * @param keyboardActions: specific keyboard actions for a given use case
 * @param value: current value stored inside the text field
 * @param onValueChange: callback function
 */
@Composable
fun EditNumberField(@StringRes label: Int, keyboardOptions: KeyboardOptions,
                    keyboardActions: KeyboardActions,
                    value: String, onValueChange: (String) -> Unit){

    TextField(value = value,
        onValueChange = onValueChange,
        label = {Text(stringResource(id = label))},
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalculatorTheme {
        TipCalculatorScreen()
    }
}


/**
 * Calculates the tip amount based on service cost and formats the result as currency
 *
 * @param amount: the base cost of a service
 * @param tipPercent: percentage of the base cost to calculate as tip
 * @param roundUp: flag signifying whether to round the tip up
 * @return: tip amount formatted as currency
 */
@VisibleForTesting
internal fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String{
    var tip = tipPercent / 100 * amount
    if (roundUp)
        tip = kotlin.math.ceil(tip)
    return NumberFormat.getCurrencyInstance().format(tip)
}