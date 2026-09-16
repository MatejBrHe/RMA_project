package com.example.rma_project.viewModel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.rma_project.AndroidMeetingReminderScheduler
import com.example.rma_project.R
import com.example.rma_project.model.CoffeeImages
import com.example.rma_project.model.Meeting
import com.example.rma_project.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.sql.Date
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class MeetingViewModel(
    private val context: Context
) : ViewModel() {
    private val reminderScheduler = AndroidMeetingReminderScheduler(context.applicationContext)
    private val db = FirebaseFirestore.getInstance()
    private val _meeting = MutableStateFlow<Meeting?>(null)
    val meeting: StateFlow<Meeting?> = _meeting
    private val _meetings = MutableStateFlow<List<Meeting>>(emptyList())
    val meetings: StateFlow<List<Meeting>> = _meetings

    fun convertLocalDateTimeToTimestamp(localTime: LocalDateTime): Timestamp {
        val zonedDateTime = localTime.atZone(ZoneId.systemDefault())
        val instant = zonedDateTime.toInstant()
        return Timestamp(Date(instant.toEpochMilli()))
    }

    fun convertTimestampToLocalDateTime(timestamp: Timestamp?): LocalDateTime {
        val localTime = timestamp?.toDate()
            ?.toInstant()
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDateTime()

        return localTime ?: LocalDateTime.now()
    }

    fun getMeeting(id: String, onComplete: () -> Unit) {
        db.collection("Meetings")
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val title = document.getString("title")
                    val description = document.getString("description")
                    val imageId = document.getLong("imageId")?.toInt()
                    val time = document.getTimestamp("time")
                    val ownerReference = document.getDocumentReference("owner")
                    val participantsReferences = document.get("participants") as? List<DocumentReference>

                    ownerReference?.get()
                        ?.addOnSuccessListener { ownerDocument ->
                            val owner = User(
                                id = ownerDocument.id,
                                name = ownerDocument.getString("name").toString()
                            )

                            val participantTasks = participantsReferences?.map { reference ->
                                reference.get()
                            }

                            Tasks.whenAllSuccess<DocumentSnapshot>(participantTasks)
                                .addOnSuccessListener { snapshots ->

                                    val participants = snapshots.map { userDocument ->
                                        User(
                                            id = userDocument.id,
                                            name = userDocument.getString("name").toString()
                                        )
                                    }

                                    val meeting = Meeting(
                                        id = document.id,
                                        title = title.toString(),
                                        description = description.toString(),
                                        imageId = imageId ?: R.drawable.coffee1,
                                        time = time?.toDate()
                                            ?.toInstant()
                                            ?.atZone(ZoneId.systemDefault())
                                            ?.toLocalDateTime() ?: LocalDateTime.now(),
                                        owner = owner,
                                        participants = participants.toMutableList()
                                    )

                                    _meeting.value = meeting
                                }
                                .addOnFailureListener { exception ->
                                    throw Exception(exception)
                                }
                        }
                        ?.addOnFailureListener {exception ->
                            throw Exception(exception)
                        }
                }
                else {
                    Log.e("FIRESTORE", "Document doesn't exist")
                }
                onComplete()
            }
            .addOnFailureListener {exception ->
                Log.e("FIRESTORE", exception.message, exception)
                onComplete()
            }
    }

    fun getAllMeetings(onComplete: () -> Unit) {
        db.collection("Meetings")
            .whereGreaterThan("time", convertLocalDateTimeToTimestamp(LocalDateTime.now()))
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {
                    _meetings.value = emptyList()
                    return@addOnSuccessListener
                }

                val meetings = mutableListOf<Meeting>()
                var completed = 0

                documents.forEach { meeting ->

                    val ownerReference = meeting.getDocumentReference("owner")
                        ?: throw Exception("Meeting ${meeting.id} has no owner")

                    ownerReference.get()
                        .addOnSuccessListener { ownerDocument ->

                            val owner = User(
                                id = ownerDocument.id,
                                name = ownerDocument.getString("name").orEmpty()
                            )

                            val newMeeting = Meeting(
                                id = meeting.id,
                                title = meeting.getString("title").orEmpty(),
                                description = meeting.getString("description").orEmpty(),
                                imageId = meeting.getLong("imageId")?.toInt()
                                    ?: CoffeeImages.getRandomImage(),
                                time = convertTimestampToLocalDateTime(
                                    meeting.getTimestamp("time")
                                ),
                                owner = owner,
                                participants = emptyList<User>().toMutableList()
                            )

                            meetings.add(newMeeting)
                            completed++

                            if (completed == documents.size()) {
                                _meetings.value = meetings
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "FIRESTORE",
                                "Failed to get owner for meeting ${meeting.id}",
                                exception
                            )
                        }
                    onComplete()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FIRESTORE", exception.message, exception)
                onComplete()
            }
    }

    fun getMyMeetings(userId: String, onComplete: () -> Unit) {
        val userReference = db.collection("Users").document(userId)

        db.collection("Meetings")
            .whereEqualTo("owner", userReference)
            .whereGreaterThan("time", convertLocalDateTimeToTimestamp(LocalDateTime.now()))
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    _meetings.value = emptyList()
                    return@addOnSuccessListener
                }

                val meetings = mutableListOf<Meeting>()
                var completed = 0

                documents.forEach { meeting ->

                    val ownerReference = meeting.getDocumentReference("owner")
                        ?: throw Exception("Meeting ${meeting.id} has no owner")

                    ownerReference.get()
                        .addOnSuccessListener { ownerDocument ->

                            val owner = User(
                                id = ownerDocument.id,
                                name = ownerDocument.getString("name").orEmpty()
                            )

                            val newMeeting = Meeting(
                                id = meeting.id,
                                title = meeting.getString("title").orEmpty(),
                                description = meeting.getString("description").orEmpty(),
                                imageId = meeting.getLong("imageId")?.toInt()
                                    ?: CoffeeImages.getRandomImage(),
                                time = convertTimestampToLocalDateTime(
                                    meeting.getTimestamp("time")
                                ),
                                owner = owner,
                                participants = emptyList<User>().toMutableList()
                            )

                            meetings.add(newMeeting)
                            completed++

                            if (completed == documents.size()) {
                                _meetings.value = meetings
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "FIRESTORE",
                                "Failed to get owner for meeting ${meeting.id}",
                                exception
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FIRESTORE", exception.message, exception)
            }
        onComplete()
    }

    fun getJoinedMeetings(userId: String, onComplete: () -> Unit) {
        val userReference = db.collection("Users").document(userId)

        db.collection("Meetings")
            .whereArrayContains("participants", userReference)
            .whereGreaterThan("time", convertLocalDateTimeToTimestamp(LocalDateTime.now()))
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {
                    _meetings.value = emptyList()
                    return@addOnSuccessListener
                }

                val meetings = mutableListOf<Meeting>()
                var completed = 0

                documents.forEach { meeting ->

                    val ownerReference = meeting.getDocumentReference("owner")
                        ?: throw Exception("Meeting ${meeting.id} has no owner")

                    ownerReference.get()
                        .addOnSuccessListener { ownerDocument ->

                            val owner = User(
                                id = ownerDocument.id,
                                name = ownerDocument.getString("name").orEmpty()
                            )

                            val newMeeting = Meeting(
                                id = meeting.id,
                                title = meeting.getString("title").orEmpty(),
                                description = meeting.getString("description").orEmpty(),
                                imageId = meeting.getLong("imageId")?.toInt()
                                    ?: CoffeeImages.getRandomImage(),
                                time = convertTimestampToLocalDateTime(
                                    meeting.getTimestamp("time")
                                ),
                                owner = owner,
                                participants = emptyList<User>().toMutableList()
                            )

                            meetings.add(newMeeting)
                            completed++

                            if (completed == documents.size()) {
                                _meetings.value = meetings
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e(
                                "FIRESTORE",
                                "Failed to get owner for meeting ${meeting.id}",
                                exception
                            )
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FIRESTORE", exception.message, exception)
            }
        onComplete()
    }

    fun addMeeting(title: String, time: LocalDateTime, description: String, auth: FirebaseAuth) {
        val user = auth.currentUser ?: throw IllegalStateException("User is not authenticated")

        val newMeeting = hashMapOf(
            "title" to title,
            "description" to description,
            "imageId" to CoffeeImages.getRandomImage(),
            "time" to convertLocalDateTimeToTimestamp(time),
            "owner" to db.collection("Users").document(user.uid),
            "participants" to emptyList<DocumentReference>()
        )

        db.collection("Meetings")
            .add(newMeeting)
            .addOnSuccessListener {
                reminderScheduler.scheduleMeetingReminder(title, time, auth.currentUser!!.uid)
            }
            .addOnFailureListener {exception ->
                Log.e("FIRESTORE", exception.message, exception)
            }
    }

    fun deleteMeeting(meeting: Meeting, userId: String) {
        db.collection("Meetings")
            .document(meeting.id)
            .delete()
            .addOnSuccessListener {
                reminderScheduler.cancelMeetingReminder(meeting.title, meeting.time, userId)
            }
            .addOnFailureListener { exception ->
                Log.e("FIRESTORE", exception.message, exception)
            }
    }

    fun joinMeeting(user: User, meeting: Meeting) {
        db.collection("Meetings")
            .document(meeting.id)
            .get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    val participants = document.get("participants") as MutableList<DocumentReference>
                    val newUserReference = db.collection("Users").document(user.id)

                    if (!participants.contains(newUserReference) && participants.count() < 4) {
                        participants.add(newUserReference)
                    }

                    db.collection("Meetings")
                        .document(meeting.id)
                        .update("participants", participants)
                        .addOnSuccessListener {
                            if (!meeting.participants.contains(user) && meeting.participants.count() < 4) {
                                meeting.participants.add(user)
                                _meeting.value = meeting
                                reminderScheduler.scheduleMeetingReminder(meeting.title, meeting.time, user.id)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("FIRESTORE", exception.message, exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FIRESTORE", exception.message, exception)
            }
    }

    fun leaveMeeting(user: User, meeting: Meeting) {
        db.collection("Meetings")
            .document(meeting.id)
            .get()
            .addOnSuccessListener { document ->
                if(document.exists()) {
                    val participants = document.get("participants") as MutableList<DocumentReference>
                    val oldUserReference = db.collection("Users").document(user.id)

                    if (participants.contains(oldUserReference)) {
                        participants.remove(oldUserReference)
                    }

                    db.collection("Meetings")
                        .document(meeting.id)
                        .update("participants", participants)
                        .addOnSuccessListener {
                            if (meeting.participants.contains(user)) {
                                meeting.participants.remove(user)
                                _meeting.value = meeting
                                reminderScheduler.cancelMeetingReminder(meeting.title, meeting.time, user.id)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("FIRESTORE", exception.message, exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FIRESTORE", exception.message, exception)
            }
    }
}