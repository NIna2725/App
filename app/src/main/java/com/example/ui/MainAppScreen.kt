package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Professor
import com.example.data.Recommendation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: AppViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeProfId by viewModel.selectedProfessorId.collectAsState()
    val activeProf by viewModel.activeProfessor.collectAsState()
    val professorsList by viewModel.professors.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "U",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            "Recomienda Profe UPC",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                navigationIcon = {
                    if (currentScreen == Screen.PerfilProfesor) {
                        IconButton(onClick = { viewModel.navigateBack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Regresar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadowUnderline()
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    NavigationItem("Inicio", Icons.Default.Home, Icons.Outlined.Home, Screen.Inicio),
                    NavigationItem("Buscar", Icons.Default.Search, Icons.Outlined.Search, Screen.Buscar),
                    NavigationItem("Recomendar", Icons.Default.Add, Icons.Outlined.Add, Screen.Recomendar),
                    NavigationItem("Sugerir", Icons.Default.Email, Icons.Outlined.Email, Screen.Sugerir)
                )

                items.forEach { item ->
                    val isSelected = currentScreen == item.screen || (item.screen == Screen.Buscar && currentScreen == Screen.PerfilProfesor)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.screen) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.filledIcon else item.outlinedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                item.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            unselectedIconColor = MaterialTheme.colorScheme.secondary,
                            unselectedTextColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Inicio -> ContentInicio(viewModel)
                Screen.Buscar -> ContentBuscar(viewModel)
                Screen.Recomendar -> ContentRecomendar(viewModel)
                Screen.Sugerir -> ContentSugerir(viewModel)
                Screen.PerfilProfesor -> ContentPerfilProfesor(viewModel)
            }
        }
    }
}

private data class NavigationItem(
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val screen: Screen
)

@ModifierRuling
private fun Modifier.shadowUnderline(): Modifier = this.drawBehind {
    drawLine(
        color = Color.LightGray.copy(alpha = 0.5f),
        start = androidx.compose.ui.geometry.Offset(0f, size.height),
        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
        strokeWidth = 2f
    )
}

annotation class ModifierRuling

// ---------------------------------------------------------
// PANTALLA INICIO
// ---------------------------------------------------------
@Composable
fun ContentInicio(viewModel: AppViewModel) {
    var searchInput by remember { mutableStateFlowOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Red UPC stylized card banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Recomienda Profe UPC",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    "Busca profesores, revisa opiniones y comparte tu experiencia de forma anónima sobre docentes de tu carrera.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Search Bar Block
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "¿A qué profesor estás buscando?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                OutlinedTextField(
                    value = searchInput,
                    onValueChange = { searchInput = it },
                    placeholder = { Text("Escribe el nombre del docente o curso...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.searchProfessorFromHome(searchInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buscar profesor", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Quick Access Cards Group
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Accesos Rápidos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickAccessCard(
                    title = "Mejores calificados",
                    subtitle = "Docentes top",
                    icon = Icons.Default.Star,
                    colorAccent = Color(0xFFFFC107),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.filterHighRated() }
                )

                QuickAccessCard(
                    title = "Más comentados",
                    subtitle = "Mayor opinión",
                    icon = Icons.Default.Email,
                    colorAccent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.filterMostCommented() }
                )
            }

            QuickAccessCardWide(
                title = "Sugerir profesor",
                subtitle = "¿Falta un docente en la lista? Regístralo aquí.",
                icon = Icons.Default.AddCircle,
                onClick = { viewModel.navigateTo(Screen.Sugerir) }
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    colorAccent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = colorAccent)
            }
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun QuickAccessCardWide(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}


// ---------------------------------------------------------
// PANTALLA BUSCAR (LISTADO DE PROFESORES)
// ---------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentBuscar(viewModel: AppViewModel) {
    val rawProfessors by viewModel.professors.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val facultyFilter by viewModel.selectedFacultyFilter.collectAsState()
    val ratingFilter by viewModel.selectedRatingFilter.collectAsState()
    val selectedSort by viewModel.sortOrder.collectAsState()

    // Faculties list for filter
    val faculties = listOf("Todas", "Ingeniería de Sistemas", "Ingeniería de Software", "Negocios/Marketing", "Derecho", "Ciencias Básicas", "Comunicaciones")

    // Filter and sort the professors list
    val filteredProfessors = remember(rawProfessors, searchQuery, facultyFilter, ratingFilter, selectedSort) {
        var baseList = rawProfessors.filter { prof ->
            val matchQuery = prof.name.contains(searchQuery, ignoreCase = true) || prof.course.contains(searchQuery, ignoreCase = true)
            val matchFaculty = facultyFilter == "Todas" || prof.faculty == facultyFilter
            val matchRating = when (ratingFilter) {
                4 -> prof.avgRating >= 4.0
                3 -> prof.avgRating >= 3.0
                else -> true
            }
            matchQuery && matchFaculty && matchRating
        }

        baseList = when (selectedSort) {
            SortOrder.Alfabetico -> baseList.sortedBy { it.name }
            SortOrder.MejorCalificado -> baseList.sortedByDescending { it.avgRating }
            SortOrder.MasComentado -> baseList.sortedByDescending { it.commentsCount }
        }

        baseList
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sticky filter panel on top of Search list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Live Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                placeholder = { Text("Buscar por nombre de profe o curso...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            // Horizontal filters scrolling
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sorting Selector Trigger chip
                AssistChip(
                    onClick = {
                        val nextSort = when (selectedSort) {
                            SortOrder.Alfabetico -> SortOrder.MejorCalificado
                            SortOrder.MejorCalificado -> SortOrder.MasComentado
                            SortOrder.MasComentado -> SortOrder.Alfabetico
                        }
                        viewModel.sortOrder.value = nextSort
                    },
                    label = {
                        val sortLabel = when (selectedSort) {
                            SortOrder.Alfabetico -> "Clasificar: A-Z"
                            SortOrder.MejorCalificado -> "Clasificar: Rating ⭐"
                            SortOrder.MasComentado -> "Clasificar: Opiniones 💬"
                        }
                        Text(sortLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )

                // Rating filtering
                FilterChip(
                    selected = ratingFilter == 4,
                    onClick = {
                        viewModel.selectedRatingFilter.value = if (ratingFilter == 4) 0 else 4
                    },
                    label = { Text("Rating 4.0+ ⭐", fontSize = 12.sp) }
                )

                // Quick faculties chips
                faculties.forEach { faculty ->
                    val isSelected = facultyFilter == faculty
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectedFacultyFilter.value = faculty },
                        label = { Text(faculty, fontSize = 12.sp) }
                    )
                }
            }
        }

        // Active filters list details count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Profesores encontrados: ${filteredProfessors.size}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )

            if (facultyFilter != "Todas" || ratingFilter != 0 || searchQuery.isNotEmpty()) {
                TextButton(
                    onClick = {
                        viewModel.searchQuery.value = ""
                        viewModel.selectedFacultyFilter.value = "Todas"
                        viewModel.selectedRatingFilter.value = 0
                        viewModel.sortOrder.value = SortOrder.Alfabetico
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Limpiar filtros", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // List Scroll
        if (filteredProfessors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.LightGray
                    )
                    Text(
                        "No se encontraron profesores",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Prueba cambiando los filtros o sugiere registrar uno nuevo.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProfessors, key = { it.id }) { prof ->
                    ProfessorCard(
                        professor = prof,
                        onClickViewReviews = { viewModel.selectProfessorForProfile(prof.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfessorCard(
    professor: Professor,
    onClickViewReviews: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        professor.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        professor.course,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        professor.faculty,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Average star representation block
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "${professor.avgRating}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        "${professor.commentsCount} opiniones",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Divider(color = Color.LightGray.copy(alpha = 0.3f), thickness = 1.dp)

            Button(
                onClick = onClickViewReviews,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Text("Ver opiniones y recomendaciones", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}


// ---------------------------------------------------------
// PANTALLA DETALLES DEL PROFESOR
// ---------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentPerfilProfesor(viewModel: AppViewModel) {
    val prof by viewModel.activeProfessor.collectAsState()
    val recommendations by viewModel.activeProfessorRecommendations.collectAsState()

    if (prof == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentProf = prof!!

    Column(modifier = Modifier.fillMaxSize()) {
        // Teacher static header card details
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        currentProf.faculty.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }

                // Name
                Text(
                    currentProf.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Subtitle Courses
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                    Text(
                        "Curso: ${currentProf.course}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Giant Rating Star Badge
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFFFC107).copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                        Text(
                            "${currentProf.avgRating}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        "${currentProf.commentsCount} opiniones anónimas",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Divider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                // Primary trigger to recomend this professor
                Button(
                    onClick = {
                        viewModel.setRecommendationProfessor(currentProf.id, currentProf.course)
                        viewModel.navigateTo(Screen.Recomendar)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Create, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Dejar recomendación", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section header of anonymous guidelines recommendations
        Text(
            "RECOMENDACIONES DE ALUMNOS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
        )

        // Scroll of recomendations comments review list
        if (recommendations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.LightGray)
                    Text("Aún no hay recomendaciones", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Text("¡Sé el primero en compartir tu opinión de forma anónima!", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recommendations) { rec ->
                    AnonymousCommentCard(recommendation = rec)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnonymousCommentCard(recommendation: Recommendation) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    Text(
                        "Alumno Anónimo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Miniature Rating stars
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i < recommendation.rating) Color(0xFFFFC107) else Color.LightGray.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            if (recommendation.courseName.isNotBlank()) {
                Text(
                    "Curso dictado: ${recommendation.courseName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "“${recommendation.comment}”",
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Tags chips container
            val tagList = remember(recommendation.tags) {
                if (recommendation.tags.isBlank()) emptyList() else recommendation.tags.split(",")
            }

            if (tagList.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tagList.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(Color.LightGray.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                tag.trim(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                            )
                        }
                    }
                }
            }
        }
    }
}


// ---------------------------------------------------------
// PANTALLA AGREGAR RECOMENDACIÓN (FORMULARIO)
// ---------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContentRecomendar(viewModel: AppViewModel) {
    val professors by viewModel.professors.collectAsState()
    val form by viewModel.recommendationForm.collectAsState()

    var showProfDropdown by remember { mutableStateOf(false) }
    var userSelectedProfName by remember { mutableStateOf("") }

    val tagsAvailable = listOf("Explica claro", "Es exigente", "Recomiendo llevarlo", "Muchos trabajos", "Buena retroalimentación", "Exámenes difíciles", "Toma asistencia")

    // Update the visual name input if selectedProfessorId shifts
    LaunchedEffect(form.selectedProfessorId, professors) {
        val selectedProf = professors.find { it.id == form.selectedProfessorId }
        userSelectedProfName = selectedProf?.name ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Dejar Recomendación Anónima",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            "Tu experiencia es muy valiosa para que otros alumnos lleven cursos con un panorama claro. No guardamos tu identidad.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Divider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)

        // Field 1: Selecting a Teacher
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Profesor", fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showProfDropdown = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userSelectedProfName.ifBlank { "Selecciona un profesor de la lista..." },
                            color = if (userSelectedProfName.isBlank()) Color.Gray else MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp
                        )
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                DropdownMenu(
                    expanded = showProfDropdown,
                    onDismissRequest = { showProfDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    professors.forEach { prof ->
                        DropdownMenuItem(
                            text = { Text(prof.name + " (" + prof.course + ")") },
                            onClick = {
                                viewModel.setRecommendationProfessor(prof.id, prof.course)
                                showProfDropdown = false
                            }
                        )
                    }
                }
            }
        }

        // Field 2: Select Course
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Curso llevado", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = form.selectedCourse,
                onValueChange = { viewModel.updateRecommendationForm(course = it) },
                placeholder = { Text("Ej. Desarrollo de Aplicaciones Móviles") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }

        // Field 3: Star Rating (1 to 5)
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calificación con estrellas", fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                repeat(5) { index ->
                    val starIndex = index + 1
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Calificar $starIndex",
                        tint = if (starIndex <= form.rating) Color(0xFFFFC107) else Color.LightGray,
                        modifier = Modifier
                            .size(34.dp)
                            .clickable { viewModel.updateRecommendationForm(rating = starIndex) }
                    )
                }
            }

            Text(
                text = when (form.rating) {
                    1 -> "Muy malo ❌"
                    2 -> "Malo 😕"
                    3 -> "Regular 😐"
                    4 -> "Muy bueno 👍"
                    5 -> "Excelente, recomendado ⭐"
                    else -> ""
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Field 4: Comentario
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Tu opinión / Comentario", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = form.comment,
                onValueChange = { viewModel.updateRecommendationForm(comment = it) },
                placeholder = { Text("Cuéntale a otros alumnos cómo fue tu experiencia de aprendizaje, rúbricas de evaluación, etc...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                ),
                maxLines = 6
            )
        }

        // Field 5: Quick Tags Selectors
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Etiquetas rápidas (Selecciona las que apliquen)", fontSize = 13.sp, fontWeight = FontWeight.Bold)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tagsAvailable.forEach { tag ->
                    val isChecked = form.selectedTags.contains(tag)
                    FilterChip(
                        selected = isChecked,
                        onClick = { viewModel.toggleTagSelection(tag) },
                        label = { Text(tag, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Submit Button
        Button(
            onClick = { viewModel.publishRecommendation() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = form.selectedProfessorId != null && form.selectedCourse.isNotBlank() && form.comment.isNotBlank()
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Publicar recomendación", fontWeight = FontWeight.Bold)
        }
    }

    // Success Alert Modal Dialog
    if (form.showSuccess) {
        Dialog(onDismissRequest = {
            viewModel.resetRecommendationForm()
            viewModel.navigateTo(Screen.Buscar)
        }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Éxito", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                    }

                    Text(
                        "Publicado con éxito",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        "Tu recomendación fue enviada de forma anónima. ¡Gracias por participar!",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            viewModel.resetRecommendationForm()
                            viewModel.navigateTo(Screen.Buscar)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver opiniones updated", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ---------------------------------------------------------
// PANTALLA SUGERIR PROFESOR
// ---------------------------------------------------------
@Composable
fun ContentSugerir(viewModel: AppViewModel) {
    val form by viewModel.suggestionForm.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Sugerir nuevo profesor",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            "¿Falta algún docente en la plataforma? Envía la sugerencia para evaluarlo y agregarlo a los registros de inmediato.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        Divider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp)

        // Field 1: Name
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Nombre del profesor", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = form.name,
                onValueChange = { viewModel.updateSuggestionForm(name = it) },
                placeholder = { Text("Ej. Dra. María Gonzales Pérez") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        }

        // Field 2: Course
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Curso principal", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = form.course,
                onValueChange = { viewModel.updateSuggestionForm(course = it) },
                placeholder = { Text("Ej. Algonítmica Avanzada") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        }

        // Field 3: Faculty / Career
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Facultad o Carrera", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = form.faculty,
                onValueChange = { viewModel.updateSuggestionForm(faculty = it) },
                placeholder = { Text("Ej. Ingeniería de Software / Diseño") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )
        }

        // Field 4: Optional Comment
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Comentario opcional / Notas", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = form.comment,
                onValueChange = { viewModel.updateSuggestionForm(comment = it) },
                placeholder = { Text("Escribe cualquier detalle adicional si lo deseas...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray
                ),
                maxLines = 4
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Submit button
        Button(
            onClick = { viewModel.submitSuggestion() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            enabled = form.name.isNotBlank() && form.course.isNotBlank() && form.faculty.isNotBlank()
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enviar sugerencia", fontWeight = FontWeight.Bold)
        }
    }

    // Success Modal Dialogue
    if (form.showSuccess) {
        Dialog(onDismissRequest = {
            viewModel.resetSuggestionForm()
            viewModel.navigateTo(Screen.Inicio)
        }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Éxito", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                    }

                    Text(
                        "Sugerencia enviada",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        "Tu sugerencia fue enviada de forma anónima. Evaluaremos los datos para agregar este profesor a la brevedad.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            viewModel.resetSuggestionForm()
                            viewModel.navigateTo(Screen.Inicio)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Regresar al Inicio", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Custom MutableState flow extension helper for Compose
@Composable
fun <T> rememberStateOf(initialValue: T): MutableState<T> = remember { mutableStateOf(initialValue) }
fun <T> mutableStateFlowOf(initialValue: T): MutableState<T> = mutableStateOf(initialValue)
