package com.example.bcon.ui.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.bcon.databinding.LogsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogsFragment : Fragment() {
    private val viewModel by viewModel<LogsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = LogsFragmentBinding.inflate(layoutInflater)
        val adapter = BluetoothMessageAdapter()
        binding.messagesRecyclerView.adapter = adapter
        viewModel.bluetoothMessages.observe(viewLifecycleOwner, Observer { listOfMessages ->
            adapter.submitList(listOfMessages)
            //binding.messagesRecyclerView.scrollToPosition(0)
        })
        return binding.root
    }
}
