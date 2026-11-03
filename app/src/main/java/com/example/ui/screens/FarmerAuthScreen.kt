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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.LimeGreen
import com.example.ui.theme.MutedGray

@Composable
fun FarmerAuthScreen(
    currentFarmerName: String?,
    onLoginSuccess: (name: String, email: String, role: String) -> Unit,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    onContinueToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRegisterMode by remember { mutableStateOf(false) }

    var displayName by remember { mutableStateOf("Rajesh Patel") }
    var email by remember { mutableStateOf("rajesh@greenvalley.ag") }
    var password by remember { mutableStateOf("••••••••") }
    var selectedRole by remember { mutableStateOf("Certified Organic Producer") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CreamWhite)
            .testTag("farmer_auth_column"),
        horizontalAlignment = Alignment.CenterHorizontally
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
                            text = "SECURITY & IDENTITY",
                            color = LimeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (currentFarmerName != null) "Farmer Account" else if (isRegisterMode) "Farmer Registration" else "Farmer Sign In",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        if (currentFarmerName != null) {
            // Already Signed In Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = FreshGreen.copy(alpha = 0.15f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(36.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Active Session Verified",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "Authenticated as $currentFarmerName ($selectedRole)",
                            fontSize = 12.sp,
                            color = MutedGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onContinueToDashboard,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Farmer Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                onLogout()
                                Toast.makeText(context, "Logged out of FarmTrace", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign Out", color = Color(0xFFD32F2F))
                        }
                    }
                }
            }
        } else {
            // Sign In / Register Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2EBE2))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = if (isRegisterMode) "Create Producer Account" else "Farmer & Handler Login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                        Text(
                            text = "Access batch management, QR generation, and cold-chain logging",
                            fontSize = 12.sp,
                            color = MutedGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isRegisterMode) {
                            Text("Full Name / Farm Brand", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = FreshGreen) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Text("Email Address", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = FreshGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Password", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = FreshGreen) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (email.isBlank() || (isRegisterMode && displayName.isBlank())) {
                                    Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                onLoginSuccess(displayName, email, selectedRole)
                                Toast.makeText(context, "Welcome back, $displayName!", Toast.LENGTH_SHORT).show()
                                onContinueToDashboard()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isRegisterMode) "Register Farmer Account" else "Secure Sign In",
                                color = CreamWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // One-Tap Demo Sign-In
                        Button(
                            onClick = {
                                onLoginSuccess("Rajesh Patel", "rajesh@greenvalley.ag", "Certified Organic Producer")
                                Toast.makeText(context, "Demo Farmer signed in: Rajesh Patel", Toast.LENGTH_SHORT).show()
                                onContinueToDashboard()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "One-Tap Demo Farmer Mode",
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Switch Register / Login Mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isRegisterMode) "Already have an account? Sign In" else "New farmer? Register here",
                                color = FreshGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { isRegisterMode = !isRegisterMode }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
