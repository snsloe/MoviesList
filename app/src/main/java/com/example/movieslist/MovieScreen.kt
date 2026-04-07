package com.example.movieslist2

import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

@Composable
fun MovieScreen(db: MovieDatabase) {
    var title by remember { mutableStateOf("") }
    var movies by remember { mutableStateOf(listOf<Movie>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        movies = withContext(Dispatchers.IO) { db.movieDao().getAll() }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Movie Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    scope.launch(Dispatchers.IO) {
                        db.movieDao().insert(Movie(title = title))
                        val updated = db.movieDao().getAll()
                        withContext(Dispatchers.Main) {
                            movies = updated
                            title = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Movie")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(movies) { movie ->
                MovieItem(movie,
                    onToggleWatched = {
                        scope.launch(Dispatchers.IO) {
                            db.movieDao().update(movie.copy(isWatched = !movie.isWatched))
                            val updated = db.movieDao().getAll()
                            withContext(Dispatchers.Main) { movies = updated }
                        }
                    },
                    onDelete = {
                        scope.launch(Dispatchers.IO) {
                            db.movieDao().delete(movie)
                            val updated = db.movieDao().getAll()
                            withContext(Dispatchers.Main) { movies = updated }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onToggleWatched: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(if (movie.isWatched) Color.Green.copy(alpha = 0.3f) else Color.White)
            .padding(8.dp)
    ) {
        Text(
            text = movie.title,
            fontWeight = if (movie.isWatched) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f).clickable { onToggleWatched() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onDelete) { Text("Delete") }
    }
}