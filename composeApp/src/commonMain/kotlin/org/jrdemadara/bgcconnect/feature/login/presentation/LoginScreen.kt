package org.jrdemadara.bgcconnect.feature.login.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Phone
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jrdemadara.bgcconnect.core.Routes
import org.jrdemadara.bgcconnect.ui.components.PrimaryButton
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.noziroh.rarenet.core.ui.components.CustomPasswordField
import org.noziroh.rarenet.core.ui.components.CustomTextField

@OptIn(KoinExperimentalAPI::class)
@Composable
fun LoginScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsState()

    val phone = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current



    when (state) {
        is LoginState.Success -> {
            val message = (state as LoginState.Success).message
            navController.navigate(Routes.HOME)
            isLoading = false
        }

        is LoginState.Loading -> isLoading = true

        is LoginState.Error -> {
            val error = (state as LoginState.Error).error
            isLoading = false
            println(error)

        }

        LoginState.Idle -> println("Idle")
    }




    Column(modifier = Modifier.fillMaxSize()) {
        Text("Login Screen")

        CustomTextField(
            label = "Phone",
            value = phone.value,
            onValueChange = { phone.value = it },
            modifier = Modifier.focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Phone
            ),
            keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
            enabled = true,
            leadingIcon = Lucide.Phone
        )

        CustomPasswordField(
            label = "Password",
            value = password.value,
            onValueChange = { password.value = it },
            leadingIcon = Lucide.Lock,
            passwordVisibility = passwordVisibility,
            onToggleVisibility = { passwordVisibility = !passwordVisibility },
        )

        PrimaryButton(
            text = "Login",
            onClick = {
                viewModel.login(
                    phone = phone.value,
                    password = password.value,
                )
            },
            isLoading = isLoading,
            isEnabled = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
    }
}

@Composable
@Preview
fun hello(){
    Text("Login Screen")
}