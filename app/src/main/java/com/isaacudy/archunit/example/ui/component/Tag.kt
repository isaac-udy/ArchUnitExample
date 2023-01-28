package com.isaacudy.archunit.example.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Tag(
    text: String,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colors.primary
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onPrimary
        )
    }
}