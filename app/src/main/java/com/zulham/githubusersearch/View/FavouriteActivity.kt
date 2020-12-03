package com.zulham.githubusersearch.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import com.zulham.githubusersearch.Adapter.FavouriteAdapter
import com.zulham.githubusersearch.Adapter.ListUserAdapter
import com.zulham.githubusersearch.Database.db.FavHelper
import com.zulham.githubusersearch.Database.entity.FavUser
import com.zulham.githubusersearch.Database.helper.MappingHelper
import com.zulham.githubusersearch.Model.User
import com.zulham.githubusersearch.R
import kotlinx.android.synthetic.main.activity_favourite.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.InternalCoroutinesApi

class FavouriteActivity : AppCompatActivity() {

    private val listFav = ArrayList<FavUser>()
    private lateinit var progressBar: ProgressBar
    private lateinit var favHelper: FavHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        favHelper = FavHelper(applicationContext)

        progressBar = findViewById(R.id.progressBar)

        rv_FavUser.setHasFixedSize(true)

        recycleFavUser()

        setUpToolbar()

    }

    private fun getData(){
        val query = favHelper.queryAll()
        val mapping = MappingHelper.mapCursorToArrayList(query)

        if (query.count > 0){

            progressBar.visibility = View.GONE

            listFav.addAll(mapping)

        }
    }

    private fun recycleFavUser() {

        getData()

        rv_FavUser.layoutManager = LinearLayoutManager(this)

        val favouriteAdapter = FavouriteAdapter(listFav)

        rv_FavUser.adapter = favouriteAdapter

        favouriteAdapter.setOnItemClickCallback(object : FavouriteAdapter.OnItemClickCallback {
            override fun onItemClicked(data: FavUser) {
                val intent = Intent(this@FavouriteActivity, DetailActivity::class.java)
                val user = data
                intent.putExtra("favuser", user)
                startActivity(intent)
            }

        })

    }

    private fun setUpToolbar() {
        supportActionBar?.setTitle("Favourite User")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
}