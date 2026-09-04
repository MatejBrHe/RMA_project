package com.example.rma_project.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.rma_project.R
import java.time.LocalDateTime

object MockMeetings {

    private val ana = User(
        name = "Ana"
    )

    private val marko = User(
        name = "Marko"
    )

    private val ivan = User(
        name = "Ivan"
    )

    private val lucija = User(
        name = "Lucija"
    )

    private val petra = User(
        name = "Petra"
    )

    private val filip = User(
        name = "Filip"
    )

    private val sara = User(
        name = "Sara"
    )

    private val nikola = User(
        name = "Nikola"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    val meetings = listOf(

        Meeting(
            id = 1,
            title = "Morning Coffee",
            description = "Start your day with a relaxed coffee and a good conversation.",
            imageId = R.drawable.coffee,
            time = LocalDateTime.of(2026, 9, 4, 9, 0),
            owner = ana,
            participants = listOf(
                marko,
                lucija
            )
        ),

        Meeting(
            id = 2,
            title = "Coffee & Coding",
            description = "A casual meetup for developers and anyone interested in programming and technology.",
            imageId = R.drawable.coffee,
            time = LocalDateTime.of(2026, 9, 4, 11, 30),
            owner = marko,
            participants = listOf(
                ivan,
                filip,
                sara
            )
        ),

        Meeting(
            id = 3,
            title = "Afternoon Chat",
            description = "Meet some new people, grab a coffee and spend a relaxing afternoon together.",
            imageId = R.drawable.coffee,
            time = LocalDateTime.of(2026, 9, 4, 14, 0),
            owner = lucija,
            participants = listOf(
                petra
            )
        ),

        Meeting(
            id = 4,
            title = "Evening Coffee",
            description = "Finish the day with a cup of coffee and some great company.",
            imageId = R.drawable.coffee,
            time = LocalDateTime.of(2026, 9, 4, 18, 30),
            owner = ivan,
            participants = listOf(
                ana,
                nikola,
                petra,
                filip
            )
        ),

        Meeting(
            id = 5,
            title = "Weekend Brunch",
            description = "A laid-back weekend meetup over coffee and brunch. Everyone is welcome.",
            imageId = R.drawable.coffee,
            time = LocalDateTime.of(2026, 9, 4, 10, 30),
            owner = petra,
            participants = listOf(
                sara,
                nikola
            )
        ),

        Meeting(
            id = 6,
            title = "Late Night Coffee",
            description = "For those who prefer evening conversations, coffee and meeting new people.",
            imageId = R.drawable.coffee,
            time = LocalDateTime.of(2026, 9, 4, 20, 0),
            owner = filip,
            participants = listOf(
                marko,
                sara,
                nikola
            )
        )
    )
}