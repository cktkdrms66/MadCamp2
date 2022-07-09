package com.chajun.madcamp2.feature.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposableTarget
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.chajun.madcamp2.R
import com.chajun.madcamp2.data.model.MyModel
import com.chajun.madcamp2.data.repository.MyRepositoryImpl
import com.chajun.madcamp2.ui.theme.Mad_camp2Theme

class MainActivity : ComponentActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(MyRepositoryImpl()))
            .get(MainViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mad_camp2Theme {
                val myModels by viewModel.myModels.observeAsState(emptyList())
                val clickedModel =  viewModel.onItemClickEvent.observeAsState().value
                val data by viewModel.text.observeAsState("")
                HomeContent(models = myModels, onItemClick = {viewModel.loadMyModels()})
                Text(text = data)
                if (clickedModel != null) {
                    ComposableToast(message = clickedModel.title)
                }

            }
        }

        viewModel.loadMyModels()
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeContent(models: List<MyModel>, onItemClick: (Int) -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Title") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {onItemClick(1)},
                backgroundColor = Color.Red,
                content = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
        },
        content = {
            MyModelList(models = models, onItemClick = onItemClick)
        }
    )
}


@Composable
fun MyModelList(models: List<MyModel>, onItemClick: (Int) -> Unit = {}) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(models) { index, item ->
            MyModelListItem(model = item)
        }
    }
}

@Preview
@Composable
fun Qwe() {
    FloatingActionButton(onClick = { /*TODO*/ }) {
        Icon(Icons.Filled.Add, "")
    }
}



@Composable
fun MyModelListItem(model: MyModel, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.primary),
        backgroundColor = Color.White,
        contentColor = MaterialTheme.colors.primary,
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Text(
            text = model.title,
            style = MaterialTheme.typography.h3
        )
    }
}

@Composable
fun ComposableToast(message: String) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
