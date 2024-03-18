package com.example.a2048

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.wear.compose.material.swipeable
import com.example.a2048.ui.theme._2048Theme
import java.lang.Math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            _2048Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App("P1")
                }
            }
        }
    }
}

@Composable
fun App(pseudo : String, modifier: Modifier = Modifier) {
    val column_modifier = Modifier
        .background(Color.DarkGray)
        .padding(20.dp)
        .fillMaxSize()
    var score by remember {
        mutableStateOf(0)
    }
    var highScore by remember {
        mutableStateOf(-1)
    }
    // main window
    Column(
        modifier = column_modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        var score by remember {
            mutableStateOf(0)
        }
        var board by remember {
            mutableStateOf(Board(score))
        }
        Header(score)
        Grid(board)
        Text(
            text = stringResource(id = R.string.rules),
            color = Color(211,188,141),
        )
    }
}


@Composable
fun Header(score: Int){
    var app_name by remember {mutableStateOf("")}
    app_name = stringResource(id = R.string.app_name)
    var bgTitle by remember { mutableStateOf(Color.Yellow) }
    //TODO(update value of score)
    //TODO(set comparison to score)
    val highest by remember {
        mutableStateOf(score)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                /*.background(Color.Green)*/
                .padding(5.dp)
                .weight(1F)
        ) {
            Text(
                text = app_name,
                modifier = Modifier
                    .padding(10.dp),
                color = Color(0xffCC6600),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(background = bgTitle)
            )
            Text(
                text = stringResource(id = R.string.goal)

            )
        }
        Column(
            modifier = Modifier
                /*.background(Color.Cyan)*/
                .padding(5.dp)
                .weight(0.8F)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "SCORE\n$score",
                    textAlign = TextAlign.Center)
                Text(text = "Highest\n$highest",
                    textAlign = TextAlign.Center)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(onClick = { /*TODO(go to home)*/ }) {
                    Icon(
                        Icons.Rounded.Home,
                        contentDescription = null
                    )
                }
                var state by remember {
                    mutableStateOf(0)
                }
                when (state){
                    0 -> {
                        IconButton(onClick = { /*TODO(restart)*/
                            state = 1
                        }) {
                            Icon(
                                Icons.Rounded.PlayArrow,
                                contentDescription = null
                            )
                        }
                    }
                    else -> {
                        Text(text = "AHH")
                    }
                }
                
                IconButton(
                    onClick = {
                        println("button clicked")
                        app_name = "Test"
                        bgTitle = Color.Blue
                    },
                    content = {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Grid(board: Board) {
    var directionX  by remember { mutableStateOf(0.0f) }
    var directionY  by remember { mutableStateOf(0.0f) }
    var finalDirection :String by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(0) }
    Box(
        /*modifier = Modifier
            */
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .fillMaxHeight(0.65F)
                .pointerInput(Unit) {
                    detectDragGestures(onDrag = { change: PointerInputChange, dragAmount: Offset ->
                        change.consume()
                        val (x, y) = dragAmount
                        directionX += x
                        directionY += y
                    },
                        onDragEnd = {
                            val direction: String
                            when {
                                directionX > 0 && directionX > abs(directionY) -> {
                                    println("right swipe detected")
                                    direction = "RIGHT"
                                    //board.moveLeft()
                                }

                                directionX < 0 && -directionX > abs(directionY) -> {
                                    println("left swipe detected")
                                    direction = "LEFT"
                                    //board.moveLeft()
                                }

                                directionY > 0 && directionY > abs(directionX) -> {
                                    println("doxn swipe detected")
                                    direction = "DOWN"
                                    //board.moveLeft()
                                }

                                directionY < 0 && -directionY > abs(directionX) -> {
                                    println("up swipe detected")
                                    direction = "UP"
                                    //board.moveLeft()
                                }

                                else -> direction = ""
                            }
                            finalDirection = direction
                            directionX = 0.0f
                            directionY = 0.0f
                        })

                }
            ,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            when(state){
                0 -> {
                    state=
                    when (finalDirection) {
                        "LEFT" -> board.moveLeft()
                        "RIGHT" -> board.moveRight()
                        "UP" -> board.moveUp()
                        "DOWN" -> board.moveDown()
                        else -> 0
                    }
                    finalDirection = ""
                    for (i in (0..board.size - 1)) {
                        Row(
                            modifier = Modifier
                                /*.padding(3.dp)
                                .background(Color.Yellow)*/
                                .fillMaxWidth()
                                .weight(1F),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (j in (0..board.size - 1)) {
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .background(Color(board.grid[i][j].color))
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                        .weight(1F),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = board.grid[i][j].textShown,
                                        modifier = Modifier
                                            .padding(5.dp),
                                        fontSize = 30.sp
                                    )
                                }
                            }
                        }
                    }
                }
                1 -> {/*TODO(has won)*/
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "YOU WON!!!", textAlign = TextAlign.Center, color = Color.Red)
                    }
                }
                -1 -> {/*TODO(has lost)*/
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "YOU LOST!!!", textAlign = TextAlign.Center, color = Color.Red)
                    }
                }
            }
        }
        Text(
            text = finalDirection,
            fontSize = 48.sp,
            lineHeight = 50.sp
        )
    }
}

fun power(value: Int, exponent: Int) :Int = if (exponent==0) 1 else value*power(value, exponent-1)

class Board(score: Int){
    val size : Int = 4
    //val grid = Array(size){ arrayOfNulls<Cell>(size) }
    var grid = Array(size){Array(size) {Cell(0, Position(0, 0))} }
    val score: Int = score


    init {
        restart()
    }
    fun restart(){
        for (i in (0..size-1)){
            for (j in (0..size-1)){
                grid[i][j] = Cell(newVal[(0..newVal.size-1).random()], Position(i, j))
            }
        }
    }

    fun moveLeft() : Int {
        for (i in (0..size-1)) {
            var j = 0
            var jmax = size-1
            while(j <= jmax){
                val currentValue = grid[i][j].value
                //empty cell
                if (currentValue == 0){
                    jmax = cellsLeft(j, i, jmax)
                }
                //merge with previous
                else if (j-1 >= 0 && grid[i][j-1].value==currentValue){
                    grid[i][j-1].merge()
                    jmax = cellsLeft(j, i, jmax)
                    j--
                }
                //merge with next
                else if (j+1 <= size-1 && grid[i][j+1].value==currentValue){
                    grid[i][j].merge()
                    jmax = cellsLeft(j+1, i, jmax)
                }
                else j++
            }
        }
        return boardState()
    }
    private fun cellsLeft(start : Int, i: Int, jmax: Int): Int{
        for(k in (start..jmax-1)){
            grid[i][k] = grid[i][k+1]
        }
        grid[i][jmax] = Cell(0, Position(i, jmax))
        return jmax-1
    }
    fun moveRight() : Int {
        for (i in (0..size-1)) {
            var j = size-1
            var jmax = 0
            while(j >= jmax){
                val currentValue = grid[i][j].value
                //empty cell
                if (currentValue == 0){
                    jmax = cellsRight(j, i, jmax)
                }
                //merge with previous
                else if (j+1 <= size-1 && grid[i][j+1].value==currentValue){
                    grid[i][j+1].merge()
                    jmax = cellsRight(j, i, jmax)
                    j++
                }
                //merge with next
                else if (j-1 >= 0 && grid[i][j-1].value==currentValue){
                    grid[i][j].merge()
                    jmax = cellsRight(j-1, i, jmax)
                }
                else j--
            }
        }
        return boardState()
    }
    private fun cellsRight(start : Int, i: Int, jmax: Int): Int{
        for(k in (start downTo jmax+1)){
            grid[i][k] = grid[i][k-1]
        }
        grid[i][jmax] = Cell(0, Position(i, jmax))
        return jmax+1
    }
    fun moveDown() : Int {
        for (j in (0..size-1)) {
            var i = size-1
            var imax = 0
            while(i >= imax){
                val currentValue = grid[i][j].value
                //empty cell
                if (currentValue == 0){
                    imax = cellsDown(i, j, imax)
                }
                //merge with previous
                else if (i+1 <= size-1 && grid[i+1][j].value==currentValue){
                    grid[i+1][j].merge()
                    imax = cellsDown(i, j, imax)
                    i++
                }
                //merge with next
                else if (i-1 >= 0 && grid[i-1][j].value==currentValue){
                    grid[i][j].merge()
                    imax = cellsDown(i-1, j, imax)
                }
                else i--
            }
        }
        return boardState()
    }
    private fun cellsDown(start : Int, j: Int, imax: Int): Int{
        for(k in (start downTo imax+1)){
            grid[k][j] = grid[k-1][j]
        }
        grid[imax][j] = Cell(0, Position(imax, j))
        return imax+1
    }
    fun moveUp() : Int {
        for (j in (0..size-1)) {
            var i = 0
            var imax = size-1
            while(i <= imax){
                val currentValue = grid[i][j].value
                //empty cell
                if (currentValue == 0){
                    imax = cellsUp(i, j, imax)
                }
                //merge with previous
                else if (i-1 >= 0 && grid[i-1][j].value==currentValue){
                    grid[i-1][j].merge()
                    imax = cellsUp(i, j, imax)
                    i--
                }
                //merge with next
                else if (i+1 <= size-1 && grid[i+1][j].value==currentValue){
                    grid[i][j].merge()
                    imax = cellsUp(i+1, j, imax)
                }
                else i++
            }
        }
        return boardState()
    }
    private fun cellsUp(start : Int, j: Int, imax: Int): Int{
        for(k in (start..imax-1)){
            grid[k][j] = grid[k+1][j]
        }
        grid[imax][j] = Cell(0, Position(imax, j))
        return imax-1
    }

    fun checkWon(): Boolean {
        for (i in (0..size-1)){
            for (j in (0..size-1)){
                if (grid[i][j].value == 2048) return true
            }
        }
        return false
    }

    fun checkFull(): Boolean {
        for (i in (0..size-1)){
            for (j in (0..size-1)){
                if (grid[i][j].value == 0) return false
            }
        }
        return true
    }

    fun addOnEmptyCells() {
        if(!checkFull()) {
            val positions: ArrayList<Position> = ArrayList(0)
            for (i in (0..size - 1)) {
                for (j in (0..size - 1)) {
                    if (grid[i][j].value == 0) positions.add(Position(i, j))
                }
            }
            val (i, j) = positions[(0..positions.size - 1).random()]
            grid[i][j] = Cell(newVal[(0..newVal.size - 1).random()] + 1, Position(i, j))
        }
    }

    fun boardState() : Int {
        val state : Int
        //update score
        //check highscore
        if (checkWon()) state = 1
        else if (checkFull()) state = -1
        else {
            addOnEmptyCells()
            state = 0
        }
        return state
    }
}

class Cell(var value: Int, val position : Position){
    fun merge() {
        value++
        update()
    }

    public var color : Long = 0
    public var textShown : String = ""
    init {
        update()
    }
    fun update(){
        val text = text[value]
        textShown = if(text == null) "" else text
        val col = hue[value]
        color = if (col == null) 0xFFfff9f5 else col
    }
}

class Position(val x : Int, val y : Int) {
    operator fun component1(): Int {
        return x
    }

    operator fun component2(): Int {
        return y
    }
}

val hue = mutableMapOf(
    0 to 0xFFfff9f5,
    1 to 0xFFffeada,
    2 to 0xFFffdac1,
    3 to 0xFFffcba8,
    4 to 0xFFffbb91,
    5 to 0xFFffaa7a,
    6 to 0xFFff9965,
    7 to 0xFFff8651,
    8 to 0xFFff723d,
    9 to 0xFFff5b2a,
    10 to 0xFFff3f16,
    11 to 0xFFff0000
)
val text = mutableMapOf(
    0 to "",
    1 to "2",
    2 to "4",
    3 to "8",
    4 to "16",
    5 to "32",
    6 to "64",
    7 to "128",
    8 to "256",
    9 to "512",
    10 to "1024",
    11 to "2048"
)
val newVal = arrayOf(0,0,0,0,0, 0, 1, 1,2)


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GreetingPreview() {
    _2048Theme {
        App("schnee")
    }
}