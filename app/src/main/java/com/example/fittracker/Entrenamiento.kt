package com.example.fittracker

// guarda los datos de cada entrenamiento que registro
data class Entrenamiento(
    val tipo: String,
    val minutos: Int,
    val intensidad: String,
    val checks: List<String>,
    val estrellas: Int
)