package com.shevy.firebasechatapp

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class AwesomeMessageAdapter(
    private val context: Activity,
    resource: Int,
    private val messages: List<AwesomeMessage>
) :
    ArrayAdapter<AwesomeMessage>(context, resource, messages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val layoutInflater: LayoutInflater =
            context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val awesomeMessage: AwesomeMessage? = getItem(position)
        var layoutResource = 1
        val viewType = getItemViewType(position)

        layoutResource = if (viewType == 0) {
            R.layout.my_message_item;
        } else {
            R.layout.your_message_item;
        }

        var view = convertView
        val message: AwesomeMessage? = getItem(position)

        if (convertView != null) {
            viewHolder = view?.tag as ViewHolder
        } else {
            view = layoutInflater.inflate(
                layoutResource, parent, false
            )
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }

        val isText = awesomeMessage?.imageUrl == null

        if (isText) {
            viewHolder.messageTextView.visibility = View.VISIBLE
            viewHolder.photoImageView.visibility = View.GONE
            viewHolder.messageTextView.text = awesomeMessage?.text
            //viewHolder.nameTextView.text = awesomeMessage?.name
        } else {
            //viewHolder.nameTextView.text = awesomeMessage?.name
            viewHolder.messageTextView.visibility = View.GONE
            viewHolder.photoImageView.visibility = View.VISIBLE
            Glide.with(viewHolder.photoImageView.context)
                .load(awesomeMessage?.imageUrl)
                .into(viewHolder.photoImageView)
        }
        viewHolder.nameTextView.text = message?.name

        return view!!






/*        val view: View = convertView ?: LayoutInflater.from(context)
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

        return view*/
    }

    override fun getItemViewType(position: Int): Int {
        val flag: Int
        val awesomeMessage = messages[position]
        flag = if (awesomeMessage.isMine) {
            0
        } else {
            1
        }
        return flag
    }

    private class ViewHolder(view: View) {
        val photoImageView: ImageView
        val messageTextView: TextView
        val nameTextView: TextView

        init {
            photoImageView = view.findViewById(R.id.photoImageView)
            messageTextView = view.findViewById(R.id.messageTextView)
            nameTextView = view.findViewById(R.id.nameTextViewB)
            nameTextView.text = "Fuck"
        }
    }
}