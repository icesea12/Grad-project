package com.example.bcon.ui.logs

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bcon.R
import com.example.bcon.databinding.ItemGsmMessageBinding
import com.example.bcon.databinding.ItemRfMessageBinding
import com.example.bcon.domain.BluetoothMessage
import com.example.bcon.domain.BluetoothMessageType
import com.example.bcon.util.setVisibleIf
import java.util.*

private const val ITEM_VIEW_TYPE_GSM = 0
private const val ITEM_VIEW_TYPE_RF = 1

class BluetoothMessageAdapter : ListAdapter<BluetoothMessage, RecyclerView.ViewHolder>(
    BluetoothMessageDiffCallback()
) {
    class BluetoothMessageDiffCallback : DiffUtil.ItemCallback<BluetoothMessage>() {
        override fun areItemsTheSame(
            oldItem: BluetoothMessage,
            newItem: BluetoothMessage
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: BluetoothMessage,
            newItem: BluetoothMessage
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).bluetoothMessageType) {
            is BluetoothMessageType.Gsm -> ITEM_VIEW_TYPE_GSM
            is BluetoothMessageType.RfMeter -> ITEM_VIEW_TYPE_RF
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_GSM -> ItemGsmMessageViewHolder.from(parent)
            ITEM_VIEW_TYPE_RF -> ItemRfMessageViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemGsmMessageViewHolder -> holder.bind(getItem(position))
            is ItemRfMessageViewHolder -> holder.bind(getItem(position))
            else -> throw ClassCastException("Unknown viewHolder $holder")
        }
    }

    class ItemGsmMessageViewHolder(
        private val binding: ItemGsmMessageBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BluetoothMessage) {
            val messageType = item.bluetoothMessageType as BluetoothMessageType.Gsm
            val resources = context.resources
            val isCritical = messageType.hasCriticalSignalStrength()
            binding.apply {
                time.text = resources.getString(R.string.date_text, Date(item.time).toString())
                from.text = resources.getString(R.string.from_device, item.macAdd)
                signalStrength.text = resources.getString(
                    R.string.signal_strength,
                    messageType.getSignalStrength().toString()
                )
                errorRate.text = resources.getString(
                    R.string.error_rate,
                    messageType.getErrorRate().toString()
                )
                criticalStrength.setVisibleIf { isCritical }
                if (isCritical) {
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.secondaryDarkColor)
                    )
                }
            }
        }

        companion object {
            fun from(
                parent: ViewGroup
            ): ItemGsmMessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemGsmMessageBinding.inflate(layoutInflater, parent, false)
                return ItemGsmMessageViewHolder(binding, parent.context)
            }
        }
    }

    class ItemRfMessageViewHolder(
        private val binding: ItemRfMessageBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BluetoothMessage) {
            val messageType = item.bluetoothMessageType as BluetoothMessageType.RfMeter
            val resources = context.resources
            val isCritical = messageType.hasCriticalSignalStrength()
            binding.apply {
                time.text = resources.getString(R.string.date_text, Date(item.time).toString())
                from.text = resources.getString(R.string.from_device, item.macAdd)
                signalStrength.text = resources.getString(
                    R.string.signal_strength,
                    messageType.getSignalStrength().toString()
                )
                criticalStrength.setVisibleIf { isCritical }
                if (isCritical) {
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.secondaryDarkColor)
                    )
                } else {
                    cardView.setCardBackgroundColor(
                            ContextCompat.getColor(context, R.color.secondaryColor)
                    )
                }
            }
        }

        companion object {
            fun from(
                parent: ViewGroup
            ): ItemRfMessageViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRfMessageBinding.inflate(layoutInflater, parent, false)
                return ItemRfMessageViewHolder(binding, parent.context)
            }
        }
    }
}
