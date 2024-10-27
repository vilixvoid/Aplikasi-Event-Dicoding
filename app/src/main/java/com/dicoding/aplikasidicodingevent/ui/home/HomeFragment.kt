package com.dicoding.aplikasidicodingevent.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.databinding.FragmentHomeBinding
import com.dicoding.aplikasidicodingevent.ui.ViewModelFactory
import com.dicoding.aplikasidicodingevent.ui.detail.DetailActivity
import com.dicoding.aplikasidicodingevent.viewmodel.EventAdapter
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var ongoingEventAdapter: EventAdapter
    private lateinit var completedEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menggunakan ViewModelFactory untuk inisialisasi MainViewModel
        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        initializeRecyclerViews()

        viewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            ongoingEventAdapter.submitList(events)
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            completedEventAdapter.submitList(events)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            ongoingEventAdapter.submitList(results)
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

    private fun initializeRecyclerViews() {
        ongoingEventAdapter = EventAdapter(requireContext()) { event ->
            val detailIntent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(detailIntent)
        }
        binding.recyclerViewActiveEvents.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewActiveEvents.adapter = ongoingEventAdapter

        completedEventAdapter = EventAdapter(requireContext()) { event ->
            val detailIntent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(detailIntent)
        }
        binding.recyclerViewFinishedEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewFinishedEvents.adapter = completedEventAdapter
    }

    private fun displayLoadingIndicator(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

