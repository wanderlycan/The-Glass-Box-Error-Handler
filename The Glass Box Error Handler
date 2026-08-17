@Composable
fun DiagnosticDialog(errorMessage: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Technical Support Required") },
        text = {
            SelectionContainer {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Error Log:", fontWeight = FontWeight.Bold)
                    Text(errorMessage, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("COPY & CLOSE") } }
    )
}
