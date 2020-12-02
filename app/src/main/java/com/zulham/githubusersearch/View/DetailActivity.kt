package com.zulham.githubusersearch.View

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zulham.githubusersearch.Adapter.PagerAdapter
import com.zulham.githubusersearch.Database.db.DatabaseContract
import com.zulham.githubusersearch.Database.db.FavHelper
import com.zulham.githubusersearch.R
import com.zulham.githubusersearch.Model.User
import com.zulham.githubusersearch.ViewModel.DetailViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.InternalCoroutinesApi
import com.zulham.githubusersearch.Model.UserDetail as UserDetail

class DetailActivity : AppCompatActivity() {

    private lateinit var userImage: CircleImageView
    private lateinit var userName: TextView
    private lateinit var userLoc: TextView
    private lateinit var userComp: TextView
    private lateinit var userRepos: TextView
    private lateinit var fav: FloatingActionButton
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var favHelper: FavHelper
    private lateinit var contentValues: ContentValues

    private var query = false
    private var statusFavorite = false

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.title = "Detail User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        contentValues = ContentValues()

        userImage = findViewById(R.id.userdetailImg)
        userName = findViewById(R.id.userName)
        userLoc = findViewById(R.id.userLoc)
        userComp = findViewById(R.id.userCompny)
        userRepos = findViewById(R.id.userRepo)
        fav = findViewById(R.id.fab)

        val sectionsPagerAdapter = PagerAdapter(this, supportFragmentManager)
        VPList.adapter = sectionsPagerAdapter
        tabLayout.setupWithViewPager(VPList)

        showLoading(true)

        val user = intent.getParcelableExtra<User>("user")

        detailViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DetailViewModel::class.java)

        user.let {
            sectionsPagerAdapter.username = it?.login
            detailViewModel.setDetail(it?.login)
        }


        detailViewModel.getIsError().observe(this, Observer {
            when (it) {
                true -> showErrorMessage()
                else -> showDetail()
            }
        })

        favHelper = FavHelper.getInstance(applicationContext)

        setStatusFavorite(statusFavorite, false)
        fav.setOnClickListener{
            statusFavorite = !statusFavorite
            contentValues.put(DatabaseContract.FavColumns.IS_FAV, statusFavorite)

            when (query){
                true -> favHelper.update(contentValues.getAsString(DatabaseContract.FavColumns.USER_NAME), contentValues)
                false -> favHelper.insert(contentValues)
            }

            setStatusFavorite(statusFavorite, true)
        }

    }

    private fun setStatusFavorite(statusFavorite: Boolean, withToast: Boolean) {
        if(statusFavorite) {
            if (withToast)Toast.makeText(this, "I Choose You, Senpai", Toast.LENGTH_SHORT).show()
            fav.setImageResource(R.drawable.ic_baseline_favorite_24)
        }
        else {
            if (withToast)Toast.makeText(this, "Thanks, Senpai", Toast.LENGTH_SHORT).show()
            fav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

    private fun showDetail(){

        detailViewModel.getDetail().observe(this, {
            detail(it)

            showLoading(false)
        })

    }

    private fun showErrorMessage() {
        detailViewModel.getErrorMessage().observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })
    }

    private fun detail(user: UserDetail) {
        Glide.with(this@DetailActivity)
            .load(user.avatar_url)
            .apply(RequestOptions().override(110, 110))
            .into(userImage)

        userName.text = user.name
        userLoc.text = user.location
        userComp.text = user.company
        userRepos.text = user.repository.toString()

        val data = favHelper.queryById(user.name)

        query = data.count > 0
        if (query){
            data.moveToFirst()
            statusFavorite =  data.getInt(data.getColumnIndex(DatabaseContract.FavColumns.IS_FAV)) == 1
            setStatusFavorite(statusFavorite, false)
        }

        contentValues.put(DatabaseContract.FavColumns.USER_ID, user.id)
        contentValues.put(DatabaseContract.FavColumns.USER_NAME, user.name)
        contentValues.put(DatabaseContract.FavColumns.IMG_USER, user.avatar_url)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            detailprogressBar.visibility = View.VISIBLE
        } else {
            detailprogressBar.visibility = View.GONE
        }
    }

}