package com.example.rma_project.model

import com.example.rma_project.R
import kotlin.random.Random

object CoffeeImages {
    val images = listOf(
        R.drawable.coffee1,
        R.drawable.coffee2,
        R.drawable.coffee3,
        R.drawable.coffee4,
        R.drawable.coffee5,
    )

    private val random = Random(System.currentTimeMillis())

    fun getRandomImage(): Int {
        return images.random(random)
    }
}