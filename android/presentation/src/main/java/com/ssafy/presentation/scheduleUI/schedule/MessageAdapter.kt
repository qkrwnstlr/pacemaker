package com.ssafy.presentation.scheduleUI.schedule

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ssafy.presentation.R

class MessageAdapter(context: Context, messages: List<String>) :
    ArrayAdapter<String>(context, 0, messages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_coach_message, parent, false)

        val messageTextView: TextView = view.findViewById(R.id.tv_coach_talk)
        messageTextView.text = getItem(position)

        return view
    }
}
