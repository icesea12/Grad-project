package com.example.bcon.ui.plot

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.androidplot.xy.*
import com.example.bcon.R
import com.example.bcon.databinding.PlotFragmentBinding
import com.example.bcon.domain.PlotBluetoothMessage
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*


class PlotFragment : Fragment() {
    private val viewModel by viewModel<PlotViewModel>()
    val gsmValsX = mutableListOf<Long>()
    val gsmValsY = mutableListOf<Int>()
    val rfValsX = mutableListOf<Long>()
    val rfValsY = mutableListOf<Int>()
    private lateinit var binding: PlotFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PlotFragmentBinding.inflate(layoutInflater)
        viewModel.listOfPlotGsmMessages.observe(viewLifecycleOwner) { gsmPlotMessages ->
            // GSM messages list
            Timber.d("Gsm plot messages:")
            gsmPlotMessages.forEachIndexed { index, message ->
                Timber.d("Entry $index: Value: {time: ${message.time}, signal strength:${message.signalStrength}}")
                //Populate our lists for plotting a series
                populateGSM(message);

            }
            plotXYSimple()
        }

        viewModel.listOfPlotRfMessages.observe(viewLifecycleOwner) { rfPlotMessages ->
            // RF messages list
            Timber.d("Rf plot messages:")
            rfPlotMessages.forEachIndexed { index, message ->
                Timber.d("Entry $index: Value: {time: ${message.time}, signal strength:${message.signalStrength}}")

                //Populate our lists for plotting a series
                populateRF(message);

            }
            plotXYSimple()
        }





        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //binding.plot.clear()
    }

    /**
     * Populate the x and Y lists for GSM module so that we can create a plot
     * @param message The message stored in the database which has two paramters (time, signal strength)
     */
    private fun populateGSM(message : PlotBluetoothMessage) {
        gsmValsX.add(message.time)
        gsmValsY.add(message.signalStrength)
    }
    /**
     * Populate the x and Y lists for RF meter values so that we can create a plot
     * @param message The message stored in the database which has two paramters (time, signal strength)
     */
    private fun populateRF(message : PlotBluetoothMessage) {
        rfValsX.add(message.time)
        rfValsY.add(message.signalStrength)
    }

    /**
     * Plots the XYsimpleseries based on the data in the X and Y values
     * @param message The message stored in the database which has two paramters (time, signal strength)
     */
    private fun plotXYSimple() {

        Timber.d("I am calling from the plot funciton!")

        var plot = binding.plot
        var plot2 = binding.plot2

        var rfSeries = SimpleXYSeries(rfValsX,rfValsY,"RF signal strength readings")
        var gsmSeries = SimpleXYSeries(gsmValsX,gsmValsY, "GSM signal strength readings")

        var rfLPF = LineAndPointFormatter(requireContext(), R.xml.line_point_formatter_with_labels)
        var gsmLPF = LineAndPointFormatter(requireContext(), R.xml.line_point_formatter_with_labels_2)


        if(rfSeries.size() > 3)
        {
            rfLPF.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
            gsmLPF.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
        }

        //Clear old series
        plot.clear()
        plot2.clear()

        plot.addSeries(rfSeries, rfLPF)
        plot2.addSeries(gsmSeries,gsmLPF)

        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {

            private val dateFormat = SimpleDateFormat("MMM dd")
            override fun format(obj: Any?, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
                val timeMillis: Long = (obj as Number).toLong()
                val date: Date = Date(timeMillis)
                Timber.d("formatting date=" + SimpleDateFormat("MMMM dd, yyyy").format(date))
                return dateFormat.format(date, toAppendTo, pos)
            }

            override fun parseObject(source: String?, pos: ParsePosition): Any? {
                return null;
            }

        }

        plot2.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {

            private val dateFormat = SimpleDateFormat("MMM dd")
            override fun format(obj: Any?, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
                val timeMillis: Long = (obj as Number).toLong()
                val date: Date = Date(timeMillis)
                Timber.d("formatting date=" + SimpleDateFormat("MMMM dd, yyyy").format(date))
                return dateFormat.format(date, toAppendTo, pos)
            }

            override fun parseObject(source: String?, pos: ParsePosition): Any? {
                return null;
            }

        }




        plot.redraw()
        plot2.redraw()
    }




}


