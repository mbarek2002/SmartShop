package com.example.smartshop.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.smartshop.model.Product
import com.example.smartshop.viewmodel.ProductViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun ProductFormScreen(
    viewModel: ProductViewModel,
    initial: Product?,
    onDone: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val errorMessage by viewModel.errorMessage.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()

    // Update fields when initial product is loaded
    LaunchedEffect(initial) {
        if (initial != null) {
            name = initial.name
            quantity = initial.quantity.toString()
            price = initial.price.toString()
            
            // Load image from local storage
            try {
                val uri = viewModel.getImageUri(initial.id)
                if (uri != null && uri.isNotBlank()) {
                    imageUri = uri
                }
            } catch (e: Exception) {
                // Image not found or error loading - that's okay
                android.util.Log.d("ProductFormScreen", "Image not found for product ${initial.id}")
            }
        } else {
            // Reset fields for new product
            name = ""
            quantity = ""
            price = ""
            imageUri = ""
        }
    }

    // Show error dialog when error occurs
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showError = true
        }
    }

    // Close on success
    LaunchedEffect(operationSuccess) {
        if (operationSuccess) {
            viewModel.resetSuccess()
            onDone()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { imageUri = it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (initial != null) "Modifier le produit" else "Nouveau produit",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du produit", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2575FC),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantité", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2575FC),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Prix (€)", fontWeight = FontWeight.Medium) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2575FC),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                
                // Image Section
                if(imageUri.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Product Image",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2575FC)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choisir une image", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val q = quantity.toIntOrNull() ?: 0
                        val p = price.toDoubleOrNull() ?: 0.0

                        if (initial != null) {
                            val updatedProduct = initial.copy(name = name, quantity = q, price = p)
                            viewModel.update(updatedProduct, imageUri)
                        } else {
                            viewModel.add(name, q, p, imageUri)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2575FC)
                    )
                ) {
                    Text(
                        text = if (initial != null) "Mettre à jour" else "Ajouter",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Error dialog
    if (showError && errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                showError = false
                viewModel.clearError()
            },
            title = { Text("Erreur", fontWeight = FontWeight.Bold) },
            text = { Text(errorMessage ?: "Une erreur est survenue") },
            confirmButton = {
                Button(
                    onClick = {
                        showError = false
                        viewModel.clearError()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2575FC)
                    )
                ) {
                    Text("OK")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
