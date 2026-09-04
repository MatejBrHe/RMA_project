package com.example.rma_project.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.rma_project.R
import com.example.rma_project.model.Meeting
import com.example.rma_project.model.MockMeetings
import com.example.rma_project.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class MeetingViewModel : ViewModel() {
    private val _meeting = MutableStateFlow<Meeting?>(null)
    val meeting: StateFlow<Meeting?> = _meeting

    private val _meetings = MutableStateFlow<List<Meeting>>(MockMeetings.meetings)
    val meetings: StateFlow<List<Meeting>> = _meetings

    fun getMeeting(id: Int) {
        val meeting = _meetings.value
            .find { it.id == id }

        _meeting.value = meeting
    }

    fun getAllMeetings() {

    }

    fun addMeeting(title: String, time: LocalDateTime, description: String) {
        val newMeeting = Meeting(
            id = (_meetings.value.maxOfOrNull { it.id } ?: 0) + 1,
            title = title,
            description = description,
            imageId = R.drawable.coffee,
            time = time,
            owner = User(name = "Me"),
            participants = emptyList()
        )

        _meetings.value += newMeeting
    }
}