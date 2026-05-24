package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Screen {
    Inicio,
    Buscar,
    Recomendar,
    Sugerir,
    PerfilProfesor
}

enum class SortOrder {
    Alfabetico,
    MejorCalificado,
    MasComentado
}

data class RecommendationForm(
    val selectedProfessorId: Int? = null,
    val selectedCourse: String = "",
    val rating: Int = 5,
    val comment: String = "",
    val selectedTags: Set<String> = emptySet(),
    val showSuccess: Boolean = false
)

data class SuggestionForm(
    val name: String = "",
    val course: String = "",
    val faculty: String = "",
    val comment: String = "",
    val showSuccess: Boolean = false
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ProfessorRepository(database.professorDao())

    // Base flows from database
    val professors: StateFlow<List<Professor>> = repository.allProfessors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suggestions: StateFlow<List<TeacherSuggestion>> = repository.allSuggestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingRecommendations: StateFlow<List<Recommendation>> = repository.pendingRecommendations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation & detail state
    private val _currentScreen = MutableStateFlow(Screen.Inicio)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _previousScreen = MutableStateFlow(Screen.Inicio)
    val previousScreen: StateFlow<Screen> = _previousScreen.asStateFlow()

    private val _selectedProfessorId = MutableStateFlow<Int?>(null)
    val selectedProfessorId: StateFlow<Int?> = _selectedProfessorId.asStateFlow()

    // Filters for Searching
    val searchQuery = MutableStateFlow("")
    val selectedFacultyFilter = MutableStateFlow("Todas")
    val selectedRatingFilter = MutableStateFlow(0) // 0 means any, otherwise 3+, 4+
    val sortOrder = MutableStateFlow(SortOrder.Alfabetico)

    // Forms states
    private val _recommendationForm = MutableStateFlow(RecommendationForm())
    val recommendationForm: StateFlow<RecommendationForm> = _recommendationForm.asStateFlow()

    private val _suggestionForm = MutableStateFlow(SuggestionForm())
    val suggestionForm: StateFlow<SuggestionForm> = _suggestionForm.asStateFlow()

    // Reactive detailed recommendations for the active professor profile
    val activeProfessorRecommendations: StateFlow<List<Recommendation>> = _selectedProfessorId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getRecommendationsForProfessor(id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeProfessor: StateFlow<Professor?> = _selectedProfessorId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getProfessorById(id)
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Initial prepopulate of fictional database
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // Navigation triggers
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != Screen.PerfilProfesor && screen == Screen.PerfilProfesor) {
            _previousScreen.value = _currentScreen.value
        } else if (screen != Screen.PerfilProfesor && _currentScreen.value == Screen.PerfilProfesor) {
            // exiting profile
        } else {
            _previousScreen.value = _currentScreen.value
        }
        _currentScreen.value = screen
    }

    fun navigateBack() {
        _currentScreen.value = _previousScreen.value
    }

    fun selectProfessorForProfile(profId: Int) {
        _selectedProfessorId.value = profId
        navigateTo(Screen.PerfilProfesor)
    }

    // Quick access triggers
    fun filterHighRated() {
        searchQuery.value = ""
        selectedFacultyFilter.value = "Todas"
        selectedRatingFilter.value = 4 // 4+ stars
        sortOrder.value = SortOrder.MejorCalificado
        navigateTo(Screen.Buscar)
    }

    fun filterMostCommented() {
        searchQuery.value = ""
        selectedFacultyFilter.value = "Todas"
        selectedRatingFilter.value = 0
        sortOrder.value = SortOrder.MasComentado
        navigateTo(Screen.Buscar)
    }

    fun searchProfessorFromHome(query: String) {
        searchQuery.value = query
        selectedFacultyFilter.value = "Todas"
        selectedRatingFilter.value = 0
        sortOrder.value = SortOrder.Alfabetico
        navigateTo(Screen.Buscar)
    }

    // Recommendation Form functions
    fun setRecommendationProfessor(profId: Int?, defaultCourse: String = "") {
        _recommendationForm.update {
            it.copy(
                selectedProfessorId = profId,
                selectedCourse = defaultCourse
            )
        }
    }

    fun updateRecommendationForm(
        course: String = _recommendationForm.value.selectedCourse,
        rating: Int = _recommendationForm.value.rating,
        comment: String = _recommendationForm.value.comment,
        tags: Set<String> = _recommendationForm.value.selectedTags
    ) {
        _recommendationForm.update {
            it.copy(
                selectedCourse = course,
                rating = rating,
                comment = comment,
                selectedTags = tags
            )
        }
    }

    fun toggleTagSelection(tag: String) {
        _recommendationForm.update {
            val current = it.selectedTags
            val updated = if (current.contains(tag)) current - tag else current + tag
            it.copy(selectedTags = updated)
        }
    }

    fun publishRecommendation() {
        val form = _recommendationForm.value
        val profId = form.selectedProfessorId ?: return
        if (form.selectedCourse.isBlank() || form.comment.isBlank()) return

        viewModelScope.launch {
            val rec = Recommendation(
                professorId = profId,
                courseName = form.selectedCourse,
                rating = form.rating,
                comment = form.comment,
                tags = form.selectedTags.joinToString(","),
                isApproved = false
            )
            repository.insertRecommendationAndUpdateProfessor(rec)
            
            // Show confirmation
            _recommendationForm.update { it.copy(showSuccess = true) }
        }
    }

    fun approveRecommendation(id: Int) {
        viewModelScope.launch {
            repository.approveRecommendation(id)
        }
    }

    fun rejectRecommendation(id: Int) {
        viewModelScope.launch {
            repository.rejectRecommendation(id)
        }
    }

    fun approveSuggestion(suggestionId: Int) {
        viewModelScope.launch {
            repository.approveSuggestion(suggestionId)
        }
    }

    fun rejectSuggestion(suggestionId: Int) {
        viewModelScope.launch {
            repository.rejectSuggestion(suggestionId)
        }
    }

    fun resetRecommendationForm() {
        _recommendationForm.value = RecommendationForm()
    }

    // Suggestion Form functions
    fun updateSuggestionForm(
        name: String = _suggestionForm.value.name,
        course: String = _suggestionForm.value.course,
        faculty: String = _suggestionForm.value.faculty,
        comment: String = _suggestionForm.value.comment
    ) {
        _suggestionForm.update {
            it.copy(
                name = name,
                course = course,
                faculty = faculty,
                comment = comment
            )
        }
    }

    fun submitSuggestion() {
        val form = _suggestionForm.value
        if (form.name.isBlank() || form.course.isBlank() || form.faculty.isBlank()) return

        viewModelScope.launch {
            val suggestion = TeacherSuggestion(
                professorName = form.name,
                course = form.course,
                faculty = form.faculty,
                comment = form.comment
            )
            repository.insertSuggestion(suggestion)
            _suggestionForm.update { it.copy(showSuccess = true) }
        }
    }

    fun resetSuggestionForm() {
        _suggestionForm.value = SuggestionForm()
    }
}
