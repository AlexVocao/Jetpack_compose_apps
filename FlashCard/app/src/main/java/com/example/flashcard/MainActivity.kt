package com.example.flashcard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashcard.ui.theme.FlashCardTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlashCardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        FlashCardGame(modifier = Modifier.padding(it))
                    }

                }
            }
        }
    }
}

// Define the FlashCard data class
data class FlashCard(val englishWord: String, val vietnameseTranslation: String)
data class FlashCardData(val kitchen: List<FlashCard>, val political: List<FlashCard>, val daily: List<FlashCard>)

fun loadFlashCardsFromJson(context: Context): FlashCardData? {
    return try {
        val jsonString = context.assets.open("flashcards.json").bufferedReader().use { it.readText() }
        Gson().fromJson(jsonString, FlashCardData::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun FlashCardGame(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var flashCards by remember {
        mutableStateOf(loadFlashCardsFromJson(context)?.kitchen?.toMutableList() ?: emptyList<FlashCard>().toMutableList())
    }
    var currentCard by remember { mutableStateOf(flashCards.random()) }
    var isFlipped by remember { mutableStateOf(false) }
    var test = false
    val tts: TextToSpeech? = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                test = true
            } else {
                Log.e("TextToSpeech", "Initialization failed.")
            }
        }
    }
    if (test) {
        val langResult = tts?.setLanguage(java.util.Locale.US)
        if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TextToSpeech", "Language is not supported or missing data.")
        }
    } else {
        Log.e("TextToSpeech", "Initialization failed.")
    }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isFlipped = true
                        }
                    )
                }
                .padding(16.dp)
                .fillMaxWidth()
                .height(300.dp),
            elevation = CardDefaults.cardElevation(8.dp),

            ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                if (isFlipped) {
                    Text(
                        text = currentCard.vietnameseTranslation,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = TextStyle(fontSize = 24.sp)
                    )
                } else {
                    Text(
                        text = currentCard.englishWord,
                        style = TextStyle(fontSize = 24.sp),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
        Row {
            Button(onClick = {
                flashCards.remove(currentCard)
                if(flashCards.isEmpty())
                    flashCards = loadFlashCardsFromJson(context)?.kitchen?.toMutableList() ?: emptyList<FlashCard>().toMutableList()
                currentCard = flashCards.random()
                isFlipped = false
            }, modifier = Modifier.size(width = 100.dp, height = 50.dp)) {
                Text(text = "Next")
            }
            Spacer(modifier = Modifier.width(100.dp))
            Button(onClick = {
                tts?.speak(currentCard.englishWord, TextToSpeech.QUEUE_FLUSH, null, null)
            }, modifier = Modifier.size(width = 100.dp, height = 50.dp)) {
                Text(text = "Speak")
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
        var option by remember {
            mutableStateOf("")
        }
        TextField(
            value = option,
            onValueChange = { option = it },
            label = { Text(text = "Enter option") },
            singleLine = true
        )
        println("option = $option")
        if (option.lowercase() == "kitchen") {
            option = ""
            flashCards = loadFlashCardsFromJson(context)?.kitchen?.toMutableList() ?: emptyList<FlashCard>().toMutableList()
            currentCard = flashCards.random()
            isFlipped = false
        } else if (option.lowercase() == "political") {
            option = ""
            flashCards = loadFlashCardsFromJson(context)?.political?.toMutableList() ?: emptyList<FlashCard>().toMutableList()
            currentCard = flashCards.random()
            isFlipped = false
        } else if (option.lowercase() == "daily") {
            option = ""
            flashCards = loadFlashCardsFromJson(context)?.daily?.toMutableList() ?: emptyList<FlashCard>().toMutableList()
            currentCard = flashCards.random()
            isFlipped = false
        }

    }

}