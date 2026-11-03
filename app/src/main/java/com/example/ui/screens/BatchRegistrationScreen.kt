package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VegetableBatch
import com.example.data.model.VegetableType
import com.example.ui.components.QrCodeCard
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchRegistrationScreen(
    onRegisterBatch: (
        vegetableName: String,
        variety: String,
        quantity: Double,
        unit: String,
        farmName: String,
        farmLocation: String,
        harvestDate: String,
        organicCertified: Boolean,
        qualityGrade: String,
        description: String,
        farmerName: String,
        farmerContact: String
    ) -> Unit,
    onNavigateBack: () -> Unit,
    onViewBatchTrack: (String) -> Unit,
    registeredBatchSuccess: VegetableBatch?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedVegetable by remember { mutableStateOf(VegetableType.TOMATO) }
    var vegExpanded by remember { mutableStateOf(false) }

    var variety by remember { mutableStateOf("Roma Plum Organic") }
    var quantityStr by remember { mutableStateOf("1500") }
    var selectedUnit by remember { mutableStateOf("kg") }
    var unitExpanded by remember { mutableStateOf(false) }

    var farmName by remember { mutableStateOf("Green Valley Organic Farm") }
    var farmLocation by remember { mutableStateOf("Salinas Valley, CA") }

    val todayDate = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
    var harvestDate by remember { mutableStateOf(todayDate) }

    var organicCertified by remember { mutableStateOf(true) }
    var qualityGrade by remember { mutableStateOf("Grade A Premium") }
    var description by remember { mutableStateOf("Freshly hand-picked early morning harvest, pre-cooled at 14°C.") }

    var farmerName by remember { mutableStateOf("Rajesh Patel") }
    var farmerContact by remember { mutableStateOf("rajesh@greenvalley.ag") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamWhite)
            .testTag("batch_registration_column")
    ) {
        // Header Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestGreen)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "FARMER REGISTRATION",
                            color = LimeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Register Produce Batch",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        if (registeredBatchSuccess != null) {
            // Success State with Generated QR Code!
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = FreshGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FreshGreen,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Batch Successfully Registered!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = ForestGreen
                    )

                    Text(
                        text = "Permanent QR cryptographic identity generated for batch ${registeredBatchSuccess.batchId}.",
                        fontSize = 13.sp,
                        color = MutedGray,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Card with Print & Download Action
                    QrCodeCard(
                        batch = registeredBatchSuccess,
                        showActions = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onViewBatchTrack(registeredBatchSuccess.batchId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_batch_shopper_portal_button")
                        ) {
                            Text("View Shopper Story", color = CreamWhite, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onNavigateBack,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Farmer Dashboard", color = ForestGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Registration Form
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Produce Specifications",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "Fill in the harvest lot details to mint a verifiable batch identity",
                            fontSize = 11.sp,
                            color = MutedGray
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Vegetable Picker Dropdown
                        Text("Vegetable Crop", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))

                        ExposedDropdownMenuBox(
                            expanded = vegExpanded,
                            onExpandedChange = { vegExpanded = !vegExpanded }
                        ) {
                            OutlinedTextField(
                                value = "${selectedVegetable.emoji}  ${selectedVegetable.commonName}",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vegExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("crop_selector_field"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = vegExpanded,
                                onDismissRequest = { vegExpanded = false }
                            ) {
                                VegetableType.entries.forEach { veg ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(veg.emoji, fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(veg.commonName, fontWeight = FontWeight.Bold)
                                                    Text(veg.scientificName, fontSize = 10.sp, color = MutedGray)
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedVegetable = veg
                                            variety = when (veg) {
                                                VegetableType.TOMATO -> "Roma Plum Vine"
                                                VegetableType.POTATO -> "Russet Golden"
                                                VegetableType.ONION -> "Red Sweet Nashik"
                                                VegetableType.CARROT -> "Nantes Sweet Crunch"
                                                VegetableType.SPINACH -> "Baby Leaf Organic"
                                                VegetableType.BRINJAL -> "Purple Globe"
                                                VegetableType.CABBAGE -> "Crisp Green Savoy"
                                                VegetableType.OTHER -> "Fresh Produce"
                                            }
                                            vegExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Variety Field
                        Text("Variety / Cultivar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = variety,
                            onValueChange = { variety = it },
                            placeholder = { Text("e.g. Roma Plum, Heirloom, Red Sweet") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("variety_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quantity & Unit
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1.5f)) {
                                Text("Quantity", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = quantityStr,
                                    onValueChange = { quantityStr = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    placeholder = { Text("1000") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("quantity_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Unit", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                ExposedDropdownMenuBox(
                                    expanded = unitExpanded,
                                    onExpandedChange = { unitExpanded = !unitExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedUnit,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    ExposedDropdownMenu(
                                        expanded = unitExpanded,
                                        onDismissRequest = { unitExpanded = false }
                                    ) {
                                        listOf("kg", "crates", "quintals", "tons").forEach { u ->
                                            DropdownMenuItem(
                                                text = { Text(u) },
                                                onClick = {
                                                    selectedUnit = u
                                                    unitExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Farm & Origin
                        Text("Farm Name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = farmName,
                            onValueChange = { farmName = it },
                            placeholder = { Text("e.g. Green Valley Organic Farm") },
                            leadingIcon = { Icon(Icons.Default.Agriculture, contentDescription = null, tint = FreshGreen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("farm_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Farm Geographic Location", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = farmLocation,
                            onValueChange = { farmLocation = it },
                            placeholder = { Text("e.g. Salinas Valley, CA") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = FreshGreen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("farm_location_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Harvest Date & Quality Grade
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Harvest Date", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = harvestDate,
                                    onValueChange = { harvestDate = it },
                                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = FreshGreen) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quality Grade", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = qualityGrade,
                                    onValueChange = { qualityGrade = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 100% Organic Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF3F7F1))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = organicCertified,
                                onCheckedChange = { organicCertified = it },
                                colors = CheckboxDefaults.colors(checkedColor = FreshGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("Certified Organic Produce", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ForestGreen)
                                Text("Verified non-GMO, zero synthetic chemical pesticides", fontSize = 10.sp, color = MutedGray)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Description
                        Text("Batch Notes / Quality Description", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Add brix sweetness, soil notes, or cooling steps...") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Farmer Credentials
                        Text("Farmer Contact / Handler", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = farmerName,
                            onValueChange = { farmerName = it },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FreshGreen) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val qty = quantityStr.toDoubleOrNull()
                                if (qty == null || qty <= 0.0) {
                                    Toast.makeText(context, "Please enter a valid positive quantity", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (farmName.isBlank() || farmLocation.isBlank()) {
                                    Toast.makeText(context, "Please enter farm name and location", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                onRegisterBatch(
                                    selectedVegetable.commonName,
                                    variety,
                                    qty,
                                    selectedUnit,
                                    farmName,
                                    farmLocation,
                                    harvestDate,
                                    organicCertified,
                                    qualityGrade,
                                    description,
                                    farmerName,
                                    farmerContact
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("register_batch_submit_button")
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mint Batch & Generate QR Code",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
