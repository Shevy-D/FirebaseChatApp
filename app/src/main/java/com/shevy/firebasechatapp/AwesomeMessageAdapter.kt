package com.shevy.firebasechatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


class AwesomeMessageAdapter(context: Context, resource: Int, messages: List<AwesomeMessage>) :
    ArrayAdapter<AwesomeMessage>(context, resource, messages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.message_item, parent, false)

        val photoImageView: ImageView = view.findViewById(R.id.photoImageView)
        val nameView: TextView = view.findViewById(R.id.nameTextView)
        val textView: TextView = view.findViewById(R.id.textTextView)
        val message: AwesomeMessage? = getItem(position)

        val isText: Boolean = message?.imageUrl == null
        if (isText) {
            photoImageView.visibility = View.GONE
            textView.visibility = View.VISIBLE
            textView.text = message?.text
        } else {
            textView.visibility = View.GONE
            photoImageView.visibility = View.VISIBLE
            Glide
                .with(photoImageView.context)
                .load(message?.imageUrl)
                .into(photoImageView)
        }
        nameView.text = message?.name

        return view
    }
}