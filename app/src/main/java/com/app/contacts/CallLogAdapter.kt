package com.app.contacts

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.Manifest


class CallLogAdapter(
    private val callLogs: List<CallLogItem>,
    private val context: Context,
) :
    RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    private var originalList: List<CallLogItem> = callLogs.distinctBy {it.name}
    private var filteredList: List<CallLogItem> = originalList
    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { it.name.contains(query) }
        }
        notifyDataSetChanged()
    }

    class CallLogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactName: TextView = view.findViewById(R.id.contactName)
        val phoneNumber: TextView = view.findViewById(R.id.phoneNumber)
        val contactImage: TextView = view.findViewById(R.id.contactImage)
        var point: ImageView = view.findViewById(R.id.point)
        val circleShape: GradientDrawable = ContextCompat.getDrawable(
            view.context,
            R.drawable.circular_background
        ) as GradientDrawable
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_call_log, parent, false)
        return CallLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        val callLog = filteredList[position]
        Log.d("azer", filteredList.size.toString())
        holder.contactName.text = callLog.name
        holder.phoneNumber.text = callLog.phoneNumber
        holder.contactImage.text = if (callLog.name.isNotEmpty()) {
            callLog.name.first().toString()
        } else {
            "N"
        }
        val randomColor = generateRandomColor()
        holder.contactName.setOnClickListener{
            Toast.makeText(context, "item ${callLog.name}", Toast.LENGTH_SHORT).show()
        }
        holder.circleShape.setColor(randomColor)
        holder.contactImage.background = holder.circleShape
        holder.point.setOnClickListener { view ->
            showPopupMenu(view, position)
        }
    }

    override fun getItemCount(): Int = filteredList.size
    fun generateRandomColor(): Int {
        val random = java.util.Random()
        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color.rgb(red, green, blue)
    }

    private fun makeCall(context: Context, phoneNumber: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(callIntent)
        } else {
            Toast.makeText(context, "Permission to make calls is required", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendMessage(context: Context, phoneNumber: String, message: String) {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", message)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
        }
    }

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(context, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.item_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_call -> {
                    makeCall(context, filteredList[position].phoneNumber)
                    true
                }

                R.id.menu_message -> {
                    sendMessage(context, filteredList[position].phoneNumber, "hello from contacts")
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}

