package com.example.bcon.ui.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bcon.databinding.BluetoothDeviceItemBinding

class BluetoothDeviceAdapter(
    private val deviceClickListener: OnDeviceClickListener
) : ListAdapter<BluetoothDevice, BluetoothDeviceAdapter.BluetoothDeviceViewHolder>(
    BluetoothDeviceDiffCallback()
) {
    class BluetoothDeviceDiffCallback : DiffUtil.ItemCallback<BluetoothDevice>() {
        override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
            return oldItem.name == newItem.name
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: BluetoothDevice,
            newItem: BluetoothDevice
        ): Boolean {
            return oldItem === newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        return BluetoothDeviceViewHolder.from(parent, deviceClickListener)
    }

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BluetoothDeviceViewHolder(
        private val binding: BluetoothDeviceItemBinding,
        private val deviceClickListener: OnDeviceClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BluetoothDevice) {
            binding.apply {
                root.setOnClickListener {
                    deviceClickListener.onClick(item)
                }
                textView.text = "${item.name}\n${item.address}\n"
                textView.append(
                    item.uuids?.map { parcelUuid ->
                        parcelUuid.uuid.toString() + "\n"
                    }.toString()
                )
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                deviceClickListener: OnDeviceClickListener
            ): BluetoothDeviceViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BluetoothDeviceItemBinding.inflate(layoutInflater, parent, false)
                return BluetoothDeviceViewHolder(binding, deviceClickListener)
            }
        }
    }
}

class OnDeviceClickListener(
    val clickListener: (bluetoothDevice: BluetoothDevice) -> Unit
) {
    fun onClick(bluetoothDevice: BluetoothDevice) = clickListener(bluetoothDevice)
}