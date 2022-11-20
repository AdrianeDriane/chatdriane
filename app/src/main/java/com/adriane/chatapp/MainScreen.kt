package com.adriane.chatapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adriane.chatapp.ui.MainViewModel
import com.adriane.chatapp.ui.theme.Blue


@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val state = viewModel.state.collectAsState()

    Column {
        TopAppBar(
            title = { Text(text = "ChatDriane") },
            backgroundColor = Color(51, 153, 255),
            elevation = 4.dp
        )
        MessageList(chats = state.value.chatList)
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, chats: List<Chat>) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.9f)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                reverseLayout = true
                /*horizontalAlignment = when { // 2
                    messageItem.isMine -> Alignment.End
                    else -> Alignment.Start*/
            ) {
                items(
                    items = chats,
                    itemContent = {
                        ChatBubble(chat = it)
                    }
                )
            }
            SendMessageBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }
    }
}


@Composable
fun ChatBubble(chat: Chat) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (chat.isMine) {
            Alignment.CenterEnd
        } else {
            Alignment.CenterStart
        }
    ) {
        Column(
            horizontalAlignment = if (chat.isMine) {
                Alignment.End
            } else {
                Alignment.Start
            }
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 2.dp),
                text = chat.username,
                fontSize = 12.sp
            ) //user.name
            Card(
                modifier = Modifier.widthIn(max = 340.dp),
                shape = RoundedCornerShape(16.dp),
                backgroundColor = when {
                    chat.isMine -> Blue
                    else -> Color.Gray
                },
                elevation = 12.dp
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = chat.message
                )
            }
        }
    }
}

@Composable
fun SendMessageBar(modifier: Modifier, viewModel: MainViewModel = viewModel()) {
    var message by rememberSaveable { mutableStateOf("") }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0),
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(start = 14.dp),
                value = message,
                onValueChange = { message = it },
                placeholder = { Text(text = "Enter a message.") })
            IconButton(
                onClick = {
                    viewModel.sendMessage(message)
                    message = ""},
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainScreen()
}