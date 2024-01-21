package com.example.openaiapi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
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

class MainActivity2 : AppCompatActivity() {
    lateinit var queryEdit:EditText
    lateinit var messageRV:RecyclerView
    lateinit var messageRvAdapter: MessageRvAdapter
    lateinit var messageList:ArrayList<MessageRvModel>
    @SuppressLint("MissingInflatedId", "NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        queryEdit=findViewById(R.id.idEdtQuery)
        messageRV=findViewById(R.id.rv)
        messageList= ArrayList()
        messageRvAdapter= MessageRvAdapter(messageList)
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


    private fun callRetrofit(query: String) {
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
                if (response.isSuccessful) {
                    val chatResponse: ChatResponse? = response.body()
                    // Handle the response here
                    // chatResponse?.choices?.get(0)?.message?.content
                    messageList.add(MessageRvModel(chatResponse?.choices?.get(0)?.message?.content.toString(),"bot","3.21","4.54"))
                    messageRV.scrollToPosition(messageRvAdapter.itemCount - 1)
                    messageRvAdapter.notifyDataSetChanged()
                    queryEdit.text.clear()
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                // Handle failure
            }
        })    }

}