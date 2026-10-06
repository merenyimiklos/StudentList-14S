package hu.petrik.studentlist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//osztály
data class Student(
    val id: Int,
    val name: String,
    val className: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                StudentListScreen()
            }
        }
    }
}

@Composable
fun StudentListScreen() {


    val context = LocalContext.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    var name: String by remember {
        mutableStateOf("")
    }

    var className: String by remember {
        mutableStateOf("")
    }

    var nextId: Int by remember {
        mutableStateOf(1)
    }

    val students = remember {
        mutableStateListOf<Student>()
    }

    var nameError: Boolean by remember {
        mutableStateOf(false)
    }

    var classNameError: Boolean by remember {
        mutableStateOf(false)
    }

    var searchText: String by remember {
        mutableStateOf("")
    }

    var sortByName: Boolean by remember {
        mutableStateOf(true)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Diáklista",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Diákok felvétele és kezelése",
                style = MaterialTheme.typography.bodyLarge
            )

            OutlinedTextField(
                value = name,
                onValueChange = { it ->
                    name = it
                    nameError = false
                },
                label = {
                    Text("Név")
                },
                supportingText = {
                    //error kezelése
                    if (nameError)
                        Text(
                            "A név megadása kötelező",
                            color = MaterialTheme.colorScheme.error,
                        )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = className,
                onValueChange = { it ->
                    className = it
                    classNameError = false
                },
                label = {
                    Text("Osztály")
                },
                supportingText = {
                    //error kezelése
                    if (classNameError)
                        Text(
                            "Az osztály megadása kötelező",
                            color = MaterialTheme.colorScheme.error
                        )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )


            Button(
                onClick = {
                    nameError = name.isBlank()
                    classNameError = className.isBlank()


                    if (nameError || classNameError) {

                        Toast.makeText(
                            context, "Minden mezőt ki kell tölteni",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val newStudent = Student(
                            id = nextId,
                            name = name.trim(),
                            className = className.trim()
                        )

                        students.add(newStudent)
                        nextId++

                        Toast.makeText(
                            context, "Sikeres felvétel",
                            Toast.LENGTH_SHORT
                        ).show()

                        name = ""
                        className = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hozzáadás")
            }

            HorizontalDivider()

            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                label = {
                    Text(text = "Keresés név vagy osztály alapján")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        sortByName = true
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Név szerint") }

                Button(
                    onClick = {
                        sortByName = false
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Osztály szerint") }
            }

            val filteredStudents = students.filter { student ->
                student.name.contains(
                    searchText,
                    ignoreCase = true
                ) ||
                        student.className.contains(
                            searchText,
                            ignoreCase = true
                        )
            }

            val displayedStudents =
                if (sortByName) {
                    filteredStudents.sortedBy {
                        it.name.lowercase()
                    }
                } else {
                    filteredStudents.sortedBy {
                        it.className.lowercase()
                    }
                }

            if (students.isEmpty()) {
                Text(
                    "Még nincs diák a listában",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else if (displayedStudents.isEmpty()) {
                Text(
                    "Nincsenek az adatai a keresésnek megfelelően"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement
                        .spacedBy(8.dp)
                ) {
                    items(
                        items = displayedStudents,
                        key = { student ->
                            student.id
                        }
                    ) { student ->

                        StudentCard(
                            student = student,
                            onDelete = {
                                students.remove(student)
                            }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun StudentAvatar(name: String) {
    // Teszt Elek
    // TE

    val initials: String = name.split(" ").filter {
        it.isNotBlank()
    }.take(2)
        .mapNotNull {
            it.firstOrNull()
        }
        .joinToString("")
        .uppercase()

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StudentCard(student: Student, onDelete: () -> Unit) {

    var showDeleteDialog: Boolean by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudentAvatar(student.name)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = student.className,
                    style = MaterialTheme.typography.bodyMedium
                )

            }
            IconButton(
                //TODO: szerkesztést megvalósítani
                onClick = {}
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.edit_icon
                    ),
                    contentDescription = "Diák szerkesztése"
                )
            }
            IconButton(
                onClick = {
                    showDeleteDialog = true
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.delete_icon
                    ),
                    contentDescription = "Diák törlése"
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = { Text("Diák törlése") },
            text = { Text("Biztosan törölni szeretnéd ${student.name} nevű diákot") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) { Text("Törlés") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) { Text("Mégse") }
            }
        )
    }
}