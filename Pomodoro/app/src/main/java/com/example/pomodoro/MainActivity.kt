package com.example.pomodoro

import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomodoro.ui.theme.PomodoroTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomodoroTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = { AppTopBar() }
                    ) {
                        PomodoroApp(modifier = Modifier.padding(it))
                    }

                }
            }
        }
    }
}

@Composable
fun AppTopBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pomodoro",
            style = MaterialTheme.typography.displayMedium,
            color = Color.Magenta
        )
    }

}

@Composable
fun PomodoroApp(modifier: Modifier = Modifier) {
    var title by remember {
        mutableStateOf("Timer")
    }
    var titleColor by remember {
        mutableStateOf(Color.Green)
    }
    var timeLeft by remember {
        mutableStateOf(0)
    }
    var reps by remember {
        mutableStateOf(1)
    }
    var reset by remember {
        mutableStateOf(false)
    }
    var checkMark by remember {
        mutableStateOf("")
    }
    var imageResource by remember {
        mutableStateOf(R.drawable.green_tomato)
    }

    var job: Job? by remember { mutableStateOf(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.displaySmall, color = titleColor)
        Spacer(modifier = Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = null,
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
                    .align(Alignment.Center)
            )
            Text(
                text = formatTime(timeLeft),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = {
                reset = false
                job = scope.launch {
                    while (true) {
                        if (reps % 2 == 1) {
                            title = "Work"
                            titleColor = Color.Green
                            imageResource = R.drawable.green_tomato
                            timeLeft = 25 * 60
                        } else if (reps % 8 == 0) {
                            checkMark += "✔ "
                            delay(1000L)
                            title = "Long Break"
                            titleColor = Color.Magenta
                            imageResource = R.drawable.magenta_tomato
                            timeLeft = 15 * 60
                        } else if (reps % 2 == 0) {
                            checkMark += "✔ "
                            delay(1000L)
                            title = "Short Break"
                            titleColor = Color.Red
                            imageResource = R.drawable.red_tomato
                            timeLeft = 5 * 60
                        }
                        while (timeLeft > 0) {
                            delay(1000L)
                            timeLeft--
                        }
                        reps++
                    }
                }

            }) {
                Text(text = "Start")
            }
            Spacer(modifier = Modifier.width(40.dp))
            Button(onClick = {
                job?.cancel()
                reps = 1
                title = "Timer"
                titleColor = Color.Green
                imageResource = R.drawable.green_tomato
                timeLeft = 0
                checkMark = ""
                reset = true
            }) {
                Text(text = "Reset")
            }

            PlayRingtone(context = LocalContext.current, reps = reps, reset)
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = checkMark,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Green
        )
    }
}

@Composable
fun PlayRingtone(context: Context, reps: Int, reset: Boolean) {
    // Remember a ringtone instance
    val ringtone = remember {
        RingtoneManager.getRingtone(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        )
    }

    // Play ringtone when composable is launched
    println("Alex: reset=${reset}--reps=${reps}")
    if (reset || reps % 2 == 1) {
        ringtone.stop()
    } else if (reps % 2 == 0) {
        LaunchedEffect(Unit) {
            ringtone.play()
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PomodoroTheme {
        PomodoroApp()
    }
}