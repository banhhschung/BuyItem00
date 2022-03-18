package com.example.buyitem00.fragment.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.adapter.ListProductAdapter
import com.example.buyitem00.databinding.FragmentFocusSearchBinding
import com.example.buyitem00.model.Product
import com.example.buyitem00.ui.DetailActivity
import com.google.firebase.database.*

class FocusSearchFragment : Fragment(), OnclickItemListener {
    private lateinit var binding: FragmentFocusSearchBinding
    private var mProduct = arrayListOf<Product>()
    private val adapter = ListProductAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFocusSearchBinding.inflate(layoutInflater)



        binding.edtSearch.requestFocus()
        binding.edtSearch.showKeyboard()

        binding.rvProduct.layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvProduct.adapter = adapter



        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                search(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })



        return binding.root
    }

    private fun search(str: String) {
        val query =
            FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("name")
                .startAt(str).endAt(str + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mProduct.clear()
                if (binding.edtSearch.text.toString() != "") {
                    for (data in snapshot.children) {
                        val product = data.getValue(Product::class.java)
                        if (product != null) {
                            mProduct.add(product)
                        }
                    }
                    adapter.reloadData(mProduct)

                } else {
                    adapter.reloadData(mProduct)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onClick(product: Product) {
        val intent = Intent(this.requireContext(), DetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

}