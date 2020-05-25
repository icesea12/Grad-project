package com.example.bcon.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bcon.R
import com.example.bcon.databinding.BluetoothFragmentBinding
import com.example.bcon.service.BluetoothService
import com.example.bcon.util.Constants
import com.example.bcon.util.TopItemDecoration
import com.example.bcon.util.setVisibleIf
import com.example.bcon.util.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class BluetoothFragment : Fragment() {
    private val viewModel by viewModel<BluetoothViewModel>()
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var binding: BluetoothFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BluetoothFragmentBinding.inflate(layoutInflater)
        setupRecyclerView()
        setupClickListeners()
        setupObservables()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.devicesRecyclerView.apply {
            addItemDecoration(
                TopItemDecoration(resources.getDimension(R.dimen.margin_normal).toInt())
            )
            adapter = BluetoothDeviceAdapter(onDeviceItemClicked)
        }
    }

    private fun setupClickListeners() {
        binding.buttonBluetoothOn.setOnClickListener { bluetoothAdapter.enable() }
        binding.buttonBluetoothOff.setOnClickListener { bluetoothAdapter.disable() }
        binding.buttonShowPairedDevices.setOnClickListener { viewModel.searchForDevices() }
    }

    private fun setupObservables() {
        viewModel.bluetoothState.observe(viewLifecycleOwner, Observer { bluetoothEnabled ->
            binding.buttonBluetoothOn.setVisibleIf { bluetoothEnabled.not() }
            binding.buttonBluetoothOff.setVisibleIf { bluetoothEnabled }
            binding.buttonShowPairedDevices.setVisibleIf { bluetoothEnabled }
            binding.devicesRecyclerView.setVisibleIf { bluetoothEnabled }
            if (bluetoothEnabled) {
                viewModel.searchForDevices()
            }
        })
        viewModel.listOfDevices.observe(viewLifecycleOwner, Observer { listOfDevices ->
            val adapter = binding.devicesRecyclerView.adapter as BluetoothDeviceAdapter
            adapter.submitList(listOfDevices)
            if (listOfDevices.isEmpty()) {
                showToast("No paired devices found. Pair your serial BT device and try again")
            }
        })
    }

    private val onDeviceItemClicked = OnDeviceClickListener { bluetoothDevice ->
        // TODO add a material dialog here to make sure before starting the activity
        showToast("Starting service listening to: $bluetoothDevice")
        startNewBluetoothService(bluetoothDevice)
        findNavController().navigate(
            BluetoothFragmentDirections.actionBluetoothFragmentToLogsFragment()
        )
    }

    private fun startNewBluetoothService(bluetoothDevice: BluetoothDevice) {
        val serviceIntent = Intent(requireContext(), BluetoothService::class.java)
        serviceIntent.putExtra(Constants.DEVICE_EXTRA, bluetoothDevice)
        serviceIntent.putExtra(Constants.DEVICE_UUID, bluetoothDevice.uuids[0].uuid.toString())
        requireContext().startService(serviceIntent)
    }

    private fun stopBluetoothService() {
        // TODO stop the service at some point?
        val serviceIntent = Intent(requireContext(), BluetoothService::class.java)
        requireContext().stopService(serviceIntent)
    }
}
