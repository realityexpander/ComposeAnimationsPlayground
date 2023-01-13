package com.realityexpander.composeanimations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.math.MathUtils.clamp
import com.realityexpander.composeanimations.ui.theme.ComposeAnimationsTheme
import java.lang.Math.abs
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class) // for AnimatedContent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeAnimationsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var isVisible by remember { mutableStateOf(false) }
                    var isRound by remember { mutableStateOf(false) }
                    var buttonLabel by remember { mutableStateOf("") }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Button(
                            onClick = {
                                isVisible = !isVisible
                            }
                        ) {
                            Text(text = "Toggle Visibility")
                        }

                        //////////////////////////// AnimatedVisibility
                        AnimatedVisibility(
                            visible= isVisible,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = MaterialTheme.colors.primary),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(text = "Hello World")
                            }
                        }

                        //////////////////////////// Animated State
                        val borderRadius by animateIntAsState(
                            targetValue = if (isRound) 100 else 0,
//                            animationSpec = keyframes {
//                                durationMillis = 1000
//                                0 at 0 with LinearEasing
//                                100 at 500 with FastOutSlowInEasing // 500ms
//                                0 at 1000 with LinearEasing
//                            }
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioHighBouncy,
                                stiffness = Spring.StiffnessVeryLow,
                                visibilityThreshold = 5
                            )
//                            animationSpec = tween(
//                                durationMillis = 1000,
//                                easing = FastOutSlowInEasing
//                            )
                            ,finishedListener = {
                                buttonLabel = "Finished $it"
                            }
                        )
                        Button(
                            onClick = {
                                isRound = !isRound
                                buttonLabel = "Clicked"
                            }
                        ) {
//                            Text(text = "Toggle Roundness")
                            Text(text = "Toggle Roundness $buttonLabel $borderRadius")
                        }
                        Box(
                            modifier = Modifier
                                .size(100.dp)
//                                .clip(RoundedCornerShape(clamp(borderRadius, 0, 100).dp))
                                .clip(RoundedCornerShape(kotlin.math.abs(borderRadius).dp))
                                .background(color = Color.Red),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Radius",
                                modifier = Modifier
                                    .background(color = MaterialTheme.colors.secondary)
                                    .padding(16.dp)
                            )
                        }

                        //////////////////////////// Animate Transitions (multiple properties)
                        val transition = updateTransition(targetState = isRound, label = "roundness")
                        val borderRadius2 by transition.animateInt(
                            transitionSpec = {
                                if (true isTransitioningTo false) {
                                    keyframes {
                                        durationMillis = 1000
                                        100 at 0 with LinearEasing
                                        0 at 1000 with LinearEasing
                                    }
                                } else {
                                    tween(
                                        durationMillis = 1000,
                                        easing = FastOutSlowInEasing
                                    )
                                }
                            },
                            label = "borderRadius",
                            targetValueByState = { isRound ->
                                if (isRound) 100 else 0
                            },
                        )
                        val color by transition.animateColor(
                            transitionSpec = { tween(1000) },
                            label = "color",
                            targetValueByState = { isRound ->
                                if (isRound) Color.Red else Color.Blue
                            }
                        )
                        val offset by transition.animateFloat(
                            transitionSpec = { tween(1000) },
                            label = "offset",
                            targetValueByState = { isRound ->
                                if (isRound) 100f else 0f
                            }
                        )
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(abs(borderRadius2).dp))
                                .background(color = color),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Transition",
                                modifier = Modifier
                                    .background(color = Color.Blue)
                                    .padding(16.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .offset(x = offset.dp, y = 0.dp)
                                    .clip(RoundedCornerShape(abs(borderRadius2).dp))
                                    .background(color = Color.White)
                            )
                        }

                        //////////////////////////// Infinite Transition
                        val transition2 = rememberInfiniteTransition()
                        val color2 by transition2.animateColor(
                            initialValue = Color.Red,
                            targetValue = Color.Blue,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            ),
                        )
                        val rotate by transition2.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart,
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(abs(borderRadius2).dp))
                                .background(color = color2),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Infinite",
                                modifier = Modifier
                                    .background(color = Color.Blue)
                                    .padding(16.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .offset(x = 0.dp, y = offset.dp)
                                    .rotate(rotate)
                                    .clip(RoundedCornerShape(abs(borderRadius2).dp))
                                    .background(color = Color.White)
                            )
                        }

                        //////////////////////////// Animated Content
                        AnimatedContent(
                            targetState = isRound,
                            content = {isVisible ->
                                if (isVisible) {
                                    Box(modifier = Modifier
                                        .size(200.dp)
                                        .background(color = Color.DarkGray),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(text = "Hello World")
                                            Text(text = "Hello World")
                                            Text(text = "Hello World")

                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .rotate(rotate)
                                                    .offset(y = (sin(-rotate * (3.1415925f /360f))*80f).dp)
                                                    .background(color = Color.Yellow)
                                            ) {
                                                Text(text = "Hello World", color = Color.Black)
                                            }
                                        }
                                    }
                                } else {
                                    Box(modifier = Modifier
                                        .size(200.dp)
                                        .background(color = Color.Cyan),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .offset(x = 120.dp - rotate.dp)
                                                .rotate(-rotate)
                                                .background(color = Color.Blue)
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                                contentDescription = "Logo",
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .rotate(rotate)
                                            )
                                        }
                                    }
                                }
                            },
//                            transitionSpec = { fadeIn(animationSpec = tween(5000)) with fadeOut() }
                            transitionSpec = {
                                slideInHorizontally(animationSpec = tween(2000), initialOffsetX = { it }) with
                                slideOutHorizontally(animationSpec = tween(2000), targetOffsetX = { -it } )
                            },
//                            transitionSpec = {
//                                if (true isTransitioningTo false) {
//                                    slideIntoContainer(animationSpec = tween(1000), towards=AnimatedContentScope.SlideDirection.Up) with fadeOut() using SizeTransform { initialSize, targetSize ->
//                                        keyframes {
//                                            durationMillis = 1000
//                                            initialSize at 0 with LinearEasing
//                                            targetSize at 1000 with LinearEasing
//                                        }
//                                    }
//                                } else {
//                                    fadeIn() with slideOutOfContainer(towards = AnimatedContentScope.SlideDirection.Right) using SizeTransform { initialSize, targetSize ->
//                                        tween(
//                                            durationMillis = 4000,
//                                            easing = FastOutSlowInEasing
//                                        )
//                                    }
//                                }
//                            }
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeAnimationsTheme {
        Greeting("Android")
    }
}