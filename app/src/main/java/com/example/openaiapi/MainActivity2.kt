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

class MainActivity2 : AppCompatActivity() {
    lateinit var queryEdit:EditText
    lateinit var messageRV:RecyclerView
    lateinit var messageRvAdapter: MessageRvAdapter
    lateinit var timeSender: TextView
    lateinit var messageList:ArrayList<MessageRvModel>
    var url = "https://api.openai.com/v1/completions"
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
        //messageRV.updateData()
       // messageRvAdapter.updateData(messageList)
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
                                //  timeSender.text="4"
                                getResponse(queryEdit.text.toString())
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
                    getResponse(queryEdit.text.toString())
                }else{
                    Toast.makeText(this, "please enter query", Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true

            }
            false
        })

    }

    private fun getResponse(query: String) {
        queryEdit.setText("")
        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)
        // creating a json object on below line.
        val jsonObject: JSONObject? = JSONObject()
        // adding params to json object.
        jsonObject?.put("model", "text-davinci-003")
        jsonObject?.put("prompt",query)
        jsonObject?.put("temperature", 0)
        jsonObject?.put("max_tokens", 100)
        jsonObject?.put("top_p", 1)
        jsonObject?.put("frequency_penalty", 0.0)
        jsonObject?.put("presence_penalty", 0.0)
        // on below line making json object request.
        val postRequest: JsonObjectRequest =
            // on below line making json object request.
            @SuppressLint("NotifyDataSetChanged")
            object : JsonObjectRequest(
                Method.POST, url, jsonObject,
                Response.Listener { response ->
                    // on below line getting response message and setting it to text view.
                    val responseMsg: String =
                        response.getJSONArray("choices").getJSONObject(0).getString("text")
                    messageList.add(MessageRvModel(responseMsg,"bot","3.21","4.54"))
                    messageRV.scrollToPosition(messageRvAdapter.itemCount - 1)
                    messageRvAdapter.notifyDataSetChanged()
                },
                // adding on error listener
                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)
                }) {
                override fun getHeaders(): kotlin.collections.MutableMap<kotlin.String, kotlin.String> {
                    val params: MutableMap<String, String> = HashMap()
                    // adding headers on below line.
                    params["Content-Type"] = "application/json"
                    params["Authorization"] =
                        "Bearer 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"
                    return params;
                }
            }

        // on below line adding retry policy for our request.
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // on below line adding our request to queue.
        queue.add(postRequest)
    }
}