package com.example.openaiapi

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import android.widget.ProgressBar
import java.util.Locale
import java.util.Objects

class MainActivity2 : AppCompatActivity() {
    // on below line we are creating a constant value
    private val REQUEST_CODE_SPEECH_INPUT = 1
    lateinit var queryEdit:EditText
    lateinit var messageRV:RecyclerView
    lateinit var messageRvAdapter: MessageRvAdapter
    private lateinit var progressBar: ProgressBar
    lateinit var messageList:ArrayList<MessageRvModel>

    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        queryEdit=findViewById(R.id.idEdtQuery)
        messageRV=findViewById(R.id.rv)
        messageList= ArrayList()
        messageRvAdapter= MessageRvAdapter(messageList)
        progressBar = findViewById(R.id.progressBar)
        val layoutManager=LinearLayoutManager(applicationContext)
        messageRV.layoutManager=layoutManager
        messageRV.adapter=messageRvAdapter
        queryEdit.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableLeft = queryEdit.compoundDrawablesRelative[0] // Index 0 is for the left drawable
                val drawableRight = queryEdit.compoundDrawablesRelative[2] // Index 2 is for the right drawable

                // Check if click occurred on the left drawable
                if (drawableLeft != null && event.x <= (drawableLeft.bounds.width() + queryEdit.paddingStart)) {
                    Toast.makeText(this, "Clicked on left drawable", Toast.LENGTH_SHORT).show()
                    startVoice()
                    return@setOnTouchListener true
                }

                // Check if click occurred on the right drawable
                if (drawableRight != null && event.x >= (queryEdit.width - queryEdit.paddingEnd - drawableRight.bounds.width())) {
                //add message in array and notify adapter
                            if(queryEdit.text.toString().length>0){
                                messageList.add(MessageRvModel(queryEdit.text.toString(),"user","3.43","4.42"))
                               // messageRvAdapter.updateData(messageList)

                                messageRvAdapter.notifyDataSetChanged()
                                messageRV.scrollToPosition(messageRvAdapter.itemCount - 1)

                                callRetrofit(queryEdit.text.toString())
                                queryEdit.text.clear()

                            }else{
                                Toast.makeText(this, "please enter query", Toast.LENGTH_SHORT).show()
                            }

                    return@setOnTouchListener true
                }
            }
            false
        }
        queryEdit.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if(i==EditorInfo.IME_ACTION_SEND){

                if(queryEdit.text.toString().length>0){
                    messageList.add(MessageRvModel(queryEdit.text.toString(),"user","3.43","4.42"))
                    messageRvAdapter.notifyDataSetChanged()


                    messageRV.scrollToPosition(messageRvAdapter.itemCount - 1)
                    //getResponse(queryEdit.text.toString())

                    callRetrofit(queryEdit.text.toString())
                    queryEdit.text.clear()


                }else{
                    Toast.makeText(this, "please enter query", Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true

            }
            false
        })

    }

    private fun startVoice() {
        // on below line we are calling speech recognizer intent.
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // on below line we are passing language model
        // and model free form in our intent
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        // on below line we are passing our
        // language as a default language.
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        // on below line we are specifying a prompt
        // message as speak to text on below line.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak...")

        // on below line we are specifying a try catch block.
        // in this block we are calling a start activity
        // for result method and passing our result code.
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            // on below line we are displaying error message in toast
            Toast
                .makeText(
                    this@MainActivity2, " " + e.message,
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }


    private fun callRetrofit(query: String) {
        // Show the progress bar while making the API call
        progressBar.visibility = View.VISIBLE
        val request = ChatRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                Message(role = "system", content = "You are a helpful assistant."),
                Message(role = "user", content = "$query")
            )
        )

// Make the API call
        val call: Call<ChatResponse> = RetrofitClient.instance.getChatCompletion(request)

        call.enqueue(object : Callback<ChatResponse> {

            override fun onResponse(call: Call<ChatResponse>, response: retrofit2.Response<ChatResponse>) {
                progressBar.visibility = View.GONE
                finishAPICall()

                if (response.isSuccessful) {
                    val chatResponse: ChatResponse? = response.body()
                    messageList.add(
                        MessageRvModel(
                            chatResponse?.choices?.get(0)?.message?.content.toString(),
                            "bot",
                            "3.21",
                            "4.54"
                        )
                    )
                    messageRV.scrollToPosition(messageRvAdapter.itemCount - 1)
                    messageRvAdapter.notifyDataSetChanged()
                    queryEdit.text.clear()
                } else {
                    Toast.makeText(
                        this@MainActivity2,
                        "Failed to get response. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity2, "Failed to get response. Please try again.", Toast.LENGTH_SHORT)
                    .show()
                progressBar.visibility = View.GONE
                finishAPICall()
            }
        })
    }
    private fun startAPICall() {
        queryEdit.isEnabled = false
        // Disable other UI elements if needed
    }

    private fun finishAPICall() {
        queryEdit.isEnabled = true
        // Enable other UI elements if needed
    }

    // on below line we are calling on activity result method.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // in this method we are checking request
        // code with our result code.
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            // on below line we are checking if result code is ok
            if (resultCode == RESULT_OK && data != null) {
                // in that case we are extracting the
                // data from our array list
                val res: java.util.ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as java.util.ArrayList<String>
                // on below line we are setting data
                // to our output text view.
                Toast.makeText(this, res.toString(), Toast.LENGTH_SHORT).show()
                queryEdit.setText(Objects.requireNonNull(res)[0].toString()
                )

            }
        }
    }
}