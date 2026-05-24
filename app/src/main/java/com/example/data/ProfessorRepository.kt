package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfessorRepository(private val professorDao: ProfessorDao) {

    val allProfessors: Flow<List<Professor>> = professorDao.getAllProfessors()
    val allSuggestions: Flow<List<TeacherSuggestion>> = professorDao.getAllSuggestions()

    fun getProfessorById(id: Int): Flow<Professor?> = professorDao.getProfessorById(id)

    fun getRecommendationsForProfessor(professorId: Int): Flow<List<Recommendation>> =
        professorDao.getRecommendationsForProfessor(professorId)

    suspend fun insertSuggestion(suggestion: TeacherSuggestion) = withContext(Dispatchers.IO) {
        professorDao.insertSuggestion(suggestion)
    }

    suspend fun insertRecommendationAndUpdateProfessor(recommendation: Recommendation) = withContext(Dispatchers.IO) {
        // Insert the recommendation
        professorDao.insertRecommendation(recommendation)

        // Fetch all recommendations for this professor to clean & recalculate
        val list = professorDao.getRecommendationsListForProfessor(recommendation.professorId)
        val count = list.size
        val avgRating = if (count > 0) {
            list.map { it.rating }.average()
        } else {
            0.0
        }

        // Get the current professor structure
        val currentProfList = professorDao.getAllProfessors().firstOrNull() ?: emptyList()
        val currentProf = currentProfList.find { it.id == recommendation.professorId }
        if (currentProf != null) {
            val updated = currentProf.copy(
                avgRating = Math.round(avgRating * 10.0) / 10.0,
                commentsCount = count
            )
            professorDao.updateProfessor(updated)
        }
    }

    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        val count = professorDao.getProfessorsCount()
        if (count == 0) {
            val prof1 = Professor(id = 1, name = "Carlos Mendoza Vega", course = "Desarrollo de Aplicaciones Móviles", faculty = "Ingeniería de Sistemas", avgRating = 4.7, commentsCount = 3)
            val prof2 = Professor(id = 2, name = "Ana María Portugal", course = "Fundamentos de Marketing", faculty = "Negocios/Marketing", avgRating = 4.0, commentsCount = 2)
            val prof3 = Professor(id = 3, name = "Juan Francisco Ortiz", course = "Cálculo I", faculty = "Ciencias Básicas", avgRating = 3.5, commentsCount = 2)
            val prof4 = Professor(id = 4, name = "Guillermo Thorne", course = "Introducción al Derecho", faculty = "Derecho", avgRating = 4.0, commentsCount = 1)
            val prof5 = Professor(id = 5, name = "Mariana De Souza", course = "Estructuras de Datos", faculty = "Ingeniería de Software", avgRating = 4.5, commentsCount = 2)
            val prof6 = Professor(id = 6, name = "José Luis Huamán", course = "Formulación de Proyectos", faculty = "Comunicaciones", avgRating = 4.0, commentsCount = 1)

            professorDao.insertProfessor(prof1)
            professorDao.insertProfessor(prof2)
            professorDao.insertProfessor(prof3)
            professorDao.insertProfessor(prof4)
            professorDao.insertProfessor(prof5)
            professorDao.insertProfessor(prof6)

            // Insert recommendations for prof 1 (Mendoza)
            professorDao.insertRecommendation(Recommendation(
                professorId = 1,
                courseName = "Desarrollo de Aplicaciones Móviles",
                rating = 5,
                comment = "Excelente profesor. Explica súper claro, da muchísimas oportunidades si demuestras interés y usa ejemplos reales.",
                tags = "Explica claro,Da feedback,Recomiendo llevarlo"
            ))
            professorDao.insertRecommendation(Recommendation(
                professorId = 1,
                courseName = "Desarrollo de Aplicaciones Móviles",
                rating = 4,
                comment = "Muy buen profesor de Desarrollo Móvil. Explicaciones dinámicas, aunque es bastante exigente con las entregas y las rúbricas.",
                tags = "Explica claro,Es exigente,Da feedback"
            ))
            professorDao.insertRecommendation(Recommendation(
                professorId = 1,
                courseName = "Desarrollo de Aplicaciones Móviles",
                rating = 5,
                comment = "De los mejores profesores de la carrera. Se nota que trabaja en la industria y actualiza su contenido constantemente.",
                tags = "Explica claro,Recomiendo llevarlo"
            ))

            // Insert for prof 2 (Portugal)
            professorDao.insertRecommendation(Recommendation(
                professorId = 2,
                courseName = "Fundamentos de Marketing",
                rating = 4,
                comment = "Buena profesora, muy amable y puntual. El curso es dinámico pero deja muchos trabajos grupales parciales.",
                tags = "Explica claro,Muchos trabajos"
            ))
            professorDao.insertRecommendation(Recommendation(
                professorId = 2,
                courseName = "Fundamentos de Marketing",
                rating = 4,
                comment = "Súper comprensiva con sus alumnos. Da buena retroalimentación pero tienes que participar en clase constantemente.",
                tags = "Da feedback,Recomiendo llevarlo"
            ))

            // Insert for prof 3 (Ortiz)
            professorDao.insertRecommendation(Recommendation(
                professorId = 3,
                courseName = "Cálculo I",
                rating = 3,
                comment = "Explica bien, pero se enfoca demasiado en la teoría. Sus exámenes son bastante difíciles y toma asistencia estrictamente.",
                tags = "Exámenes difíciles,Es exigente,Toma asistencia"
            ))
            professorDao.insertRecommendation(Recommendation(
                professorId = 3,
                courseName = "Cálculo I",
                rating = 4,
                comment = "Si le prestas atención y haces las guías de ejercicios, apruebas. Resuelve bien las dudas cuando le preguntas directamente.",
                tags = "Es exigente,Explica claro"
            ))

            // Insert for prof 4 (Thorne)
            professorDao.insertRecommendation(Recommendation(
                professorId = 4,
                courseName = "Introducción al Derecho",
                rating = 4,
                comment = "Muy analítico y con mucha experiencia en derecho. Exige lecturas semanales indispensables para dar los controles.",
                tags = "Es exigente,Da feedback,Toma asistencia"
            ))

            // Insert for prof 5 (De Souza)
            professorDao.insertRecommendation(Recommendation(
                professorId = 5,
                courseName = "Estructuras de Datos",
                rating = 5,
                comment = "Excelente explicando Estructuras de Datos. Hace que un curso difícil parezca súper simple si asistes a todas sus clases.",
                tags = "Explica claro,Recomiendo llevarlo"
            ))
            professorDao.insertRecommendation(Recommendation(
                professorId = 5,
                courseName = "Estructuras de Datos",
                rating = 4,
                comment = "Buena profesora. Ayuda en las asesorías virtuales y es justa calificando pero el curso de algoritmos demanda estudiar diariamente.",
                tags = "Es exigente,Da feedback"
            ))

            // Insert for prof 6 (Huamán)
            professorDao.insertRecommendation(Recommendation(
                professorId = 6,
                courseName = "Formulación de Proyectos",
                rating = 4,
                comment = "Profesor amigable y con experiencia, el curso de Proyectos es muy útil pero demanda formar un buen equipo desde la primera semana.",
                tags = "Muchos trabajos,Da feedback"
            ))
        }
    }
}
