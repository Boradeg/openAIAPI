package com.example.openaiapi

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MessageRvAdapter(private val messageList:ArrayList<MessageRvModel>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class UserMessageViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView){
        val userMessageTv:TextView=itemView.findViewById(R.id.idTvUser)
        val userMessageTime:TextView=itemView.findViewById(R.id.userTimestampText)

    }
    class BotMessageViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView){
        val botMessageTv:TextView=itemView.findViewById(R.id.idTvBot)
        val botMessageTime:TextView=itemView.findViewById(R.id.botTimestampText)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val view:View
       return if(viewType==0){
           view=LayoutInflater.from(parent.context).inflate(R.layout.user_msg_rv,parent,false)
           UserMessageViewHolder(view)
       }else{
           view=LayoutInflater.from(parent.context).inflate(R.layout.bot_msg_rv,parent,false)
           BotMessageViewHolder(view)
       }
    }

    override fun getItemCount(): Int {
       return messageList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (messageList[position].sender) {



                "bot" -> {
                if (holder is BotMessageViewHolder) {
                    holder.botMessageTv.text = messageList[position].message
                    holder.botMessageTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

                }
            }
            "user" -> {
                if (holder is UserMessageViewHolder) {
                    holder.userMessageTv.text = messageList[position].message
                    //holder.userMessageTime.text = "5.43"
                    holder.userMessageTime.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                }
            }
        }



    }

    override fun getItemViewType(position: Int): Int {
        return when(messageList.get(position).sender){
            "user"->0
            "bot"->1
            else->1
        }
    }


}