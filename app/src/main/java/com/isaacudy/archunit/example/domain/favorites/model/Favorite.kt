package com.isaacudy.archunit.example.domain.favorites.model

sealed class Favorite {
    data class Integer(
        val id: String
    ) : Favorite()

    data class Real(
        val id: String
    ) : Favorite()
}