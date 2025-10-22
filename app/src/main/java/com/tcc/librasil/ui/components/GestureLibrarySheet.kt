package com.tcc.librasil.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tcc.librasil.R
import com.tcc.librasil.ui.theme.GreenAqua
import com.tcc.librasil.ui.theme.GreenLight
import com.tcc.librasil.ui.theme.PinkPrimary

data class GestureItem(
    val letter: String,
    val imageRes: Int? = null  // Agora é opcional
)

data class GestureCategory(
    val name: String,
    val gestures: List<GestureItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestureLibrarySheet(
    onDismiss: () -> Unit
) {
    val categories = remember {
        listOf(
            GestureCategory(
                name = "Alfabeto",
                gestures = listOf(
                    GestureItem("A", R.drawable.libras_letter_a),
                    GestureItem("B", R.drawable.libras_letter_b),
                    GestureItem("C", R.drawable.libras_letter_c),
                    GestureItem("D", R.drawable.libras_letter_d),
                    GestureItem("E", R.drawable.libras_letter_e),
                    GestureItem("I", R.drawable.libras_letter_i),
                    GestureItem("L", R.drawable.libras_letter_l),
                    GestureItem("M", R.drawable.libras_letter_m),
                    GestureItem("N", R.drawable.libras_letter_n),
                    GestureItem("O", R.drawable.libras_letter_o),
                    GestureItem("R", R.drawable.libras_letter_r),
                    GestureItem("S", R.drawable.libras_letter_s),
                    GestureItem("U", R.drawable.libras_letter_u),
                    GestureItem("V", R.drawable.libras_letter_v),
                    GestureItem("W", R.drawable.libras_letter_w)
                )
            ),
            GestureCategory(
                name = "Hospital",
                gestures = listOf(
                    GestureItem("Médico"),
                    GestureItem("Enfermeiro"),
                    GestureItem("Remédio")
                )
            ),
            GestureCategory(
                name = "Polícia",
                gestures = listOf(
                    GestureItem("Policial"),
                    GestureItem("Delegacia"),
                    GestureItem("Socorro")
                )
            ),
            GestureCategory(
                name = "Restaurante",
                gestures = listOf(
                    GestureItem("Comida"),
                    GestureItem("Água"),
                    GestureItem("Conta")
                )
            )
        )
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Biblioteca de Gestos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PinkPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(category = category)
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: GestureCategory) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rotation"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GreenLight.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Cabeçalho da categoria
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PinkPrimary
                )
                
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Recolher" else "Expandir",
                    tint = PinkPrimary,
                    modifier = Modifier.rotate(rotationAngle)
                )
            }
            
            // Lista de gestos (expansível)
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    category.gestures.forEach { gesture ->
                        GestureLetterItem(gesture = gesture)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun GestureLetterItem(gesture: GestureItem) {
    var showImage by remember { mutableStateOf(false) }
    val hasImage = gesture.imageRes != null
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Linha com letra/palavra (clicável para expandir se tiver imagem)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (hasImage) Modifier.clickable { showImage = !showImage }
                    else Modifier
                ),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = GreenAqua.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (hasImage) "Letra ${gesture.letter}" else gesture.letter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                
                if (hasImage) {
                    Text(
                        text = if (showImage) "Ocultar" else "Ver como fazer",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = GreenAqua
                    )
                }
            }
        }
        
        // Imagem do gesto (expansível, apenas se tiver imagem)
        if (hasImage) {
            AnimatedVisibility(visible = showImage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Como fazer a letra ${gesture.letter}:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PinkPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Image(
                            painter = painterResource(id = gesture.imageRes!!),
                            contentDescription = "Gesto da letra ${gesture.letter}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}
