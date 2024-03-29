package com.example.viewmodelbasicsimple.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.viewmodelbasicsimple.model.Product
import com.example.viewmodelbasicsimple.model.ResponseListModelDto
import com.example.viewmodelbasicsimple.service.ProductService
import com.example.viewmodelbasicsimple.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductListViewModel : ViewModel() {
    private val _products: MutableLiveData<List<Product>> by lazy {
        MutableLiveData<List<Product>>().also {
            loadProducts()
        }
    }
    private val retrofit = RetrofitClient.retrofit

    val products: LiveData<List<Product>>
        get() = _products

    private fun loadProducts() {
        // REST API retrofit
        retrofit.create(ProductService::class.java).getProducts(1, 2)
            .enqueue(object : Callback<ResponseListModelDto<Product>> {
                override fun onResponse(
                    call: Call<ResponseListModelDto<Product>>,
                    response: Response<ResponseListModelDto<Product>>,
                ) {
                    if (!response.isSuccessful) {
                        Log.d(TAG, "onResponse: Response Fail")
                        return
                    }

                    response.body()?.let {
                        _products.postValue(it.items)
                        it.items.forEachIndexed { idx, product ->
                            Log.d(TAG, "PRODUCT[$idx]: ${product.name}")
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseListModelDto<Product>>, t: Throwable) {
                    Log.e(TAG, "onFailure: $t")

                }
            })
    }

    fun searchProducts(page: Int, limit: Int, text: String): ArrayList<Product>? {
        var resultProducts : ArrayList<Product>? = null

        retrofit.create(ProductService::class.java).searchProducts(page, limit, text).enqueue(object: Callback<ResponseListModelDto<Product>>{
            override fun onResponse(
                call: Call<ResponseListModelDto<Product>>,
                response: Response<ResponseListModelDto<Product>>
            ) {
                if (!response.isSuccessful){
                    Log.d(TAG, "onResponse: Response Fail")
                    return
                }

                response.body()?.let{
                    _products.postValue(it.items)
                    resultProducts = it.items
                }
            }

            override fun onFailure(call: Call<ResponseListModelDto<Product>>, t: Throwable) {
                Log.e(TAG, "onFailure: $t", )
            }

        })

        return resultProducts
    }

    companion object{
        const val TAG = "Product_LIST_VIEW_MODEL-TAG"
    }
}