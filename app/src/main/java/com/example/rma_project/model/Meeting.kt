package com.example.rma_project.model

import java.time.LocalDateTime

data class Meeting(
    val id: String,
    val title: String,
    val description: String,
    val imageId: Int,
    val time: LocalDateTime,
    val owner: User,
    val participants: MutableList<User>
)
