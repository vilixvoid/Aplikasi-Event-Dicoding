package com.dicoding.aplikasidicodingevent.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager // Import LinearLayoutManager
import com.dicoding.aplikasidicodingevent.databinding.FragmentFinishedBinding
import com.dicoding.aplikasidicodingevent.ui.ViewModelFactory
import com.dicoding.aplikasidicodingevent.ui.detail.DetailActivity
import com.dicoding.aplikasidicodingevent.viewmodel.EventAdapter
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menggunakan ViewModelFactory untuk inisialisasi MainViewModel
        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        initializeRecyclerView()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            displayLoadingIndicator(loading)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        // Panggil fetchEvents dengan parameter yang sesuai
        viewModel.fetchEvents(1)
        viewModel.fetchEvents(0)
    }

    private fun initializeRecyclerView() {
        adapter = EventAdapter(requireContext()) { selectedEvent ->
            val detailIntent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", selectedEvent)
            }
            startActivity(detailIntent)
        }

        binding.recycleApiFinish.layoutManager = LinearLayoutManager(context)
        binding.recycleApiFinish.adapter = adapter
    }

    private fun displayLoadingIndicator(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
