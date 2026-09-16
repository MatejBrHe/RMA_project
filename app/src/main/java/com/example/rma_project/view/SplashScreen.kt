package com.example.rma_project.view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.rma_project.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current

    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        delay(5000)
        onFinished()
    }

    DisposableEffect(Unit) {
        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val sensor = sensorManager.getDefaultSensor(
            Sensor.TYPE_ROTATION_VECTOR
        )

        val listener = object : SensorEventListener {

            private val rotationMatrix = FloatArray(9)
            private val orientation = FloatArray(3)

            override fun onSensorChanged(event: SensorEvent) {

                SensorManager.getRotationMatrixFromVector(
                    rotationMatrix,
                    event.values
                )

                SensorManager.getOrientation(
                    rotationMatrix,
                    orientation
                )

                tiltX = Math.toDegrees(
                    orientation[1].toDouble()
                ).toFloat()

                tiltY = Math.toDegrees(
                    orientation[2].toDouble()
                ).toFloat()
            }

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) = Unit
        }

        sensor?.let {
            sensorManager.registerListener(
                listener,
                it,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val animatedTiltX by animateFloatAsState(
        targetValue = tiltX * (-0.3f),
        animationSpec = tween(50),
        label = "tiltX"
    )

    val animatedTiltY by animateFloatAsState(
        targetValue = tiltY * (-0.3f),
        animationSpec = tween(50),
        label = "tiltY"
    )


    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                rotationX = animatedTiltX
                rotationY = animatedTiltY
            }
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null
            )
        }
    }
}
