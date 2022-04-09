package com.example.buyitem00.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.buyitem00.OnClickNews
import com.example.buyitem00.adapter.NewsAdapter
import com.example.buyitem00.databinding.FragmentSearchBinding
import com.example.buyitem00.model.News
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment(), OnClickNews {
    private lateinit var binding: FragmentSearchBinding
    private val adapter = NewsAdapter(this)
    private var arrNews = arrayListOf<News>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        binding.edtSearch.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToFocusSearchFragment()
            findNavController().navigate(action)
        }
        binding.rvNews.layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        binding.rvNews.adapter = adapter


        getNewsFirebase()

        return binding.root
    }

    private fun getNewsFirebase() {
        FirebaseDatabase.getInstance().getReference("News")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrNews.clear()
                    for (i in snapshot.children) {
                        val news = i.getValue(News::class.java)
                        if (news != null) {
                            arrNews.add(news)
                        }
                    }

                    adapter.reloadData(arrNews)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onclick(news: News) {
        val action = SearchFragmentDirections.actionSearchFragmentToDetailNewsFragment(news)
        findNavController().navigate(action)
    }
}