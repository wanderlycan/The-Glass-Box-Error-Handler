# 🛡️ GlassBox Error Handler

> Pare de adivinhar por que o app falhou. Transforme exceções em diagnósticos úteis.

**GlassBox** é uma base pequena e sem dependências externas para registrar falhas fatais e exceções que seu aplicativo captura e normalmente deixaria passar. Ela cria relatórios locais, tenta copiar o diagnóstico para a área de transferência quando isso é seguro e nunca impede o fluxo normal de encerramento do sistema.

> **Boas-vindas!** Copie apenas o arquivo da sua plataforma, inicialize-o no ponto de entrada do app e você já terá um relatório compartilhável quando algo der errado.

## Palavras-chave para descoberta

`error-handler` · `crash-reporter` · `exception-handler` · `stack-trace` · `debugging` · `diagnostics` · `android-kotlin` · `wpf` · `dotnet` · `linux` · `windows` · `desktop-app` · `mobile-development`

## O que ele cobre

| Plataforma | Arquivo pronto | Captura fatal | Captura silenciosa / assíncrona | Onde salva |
| --- | --- | --- | --- | --- |
| Android | [`src/android/GlassBox.kt`](src/android/GlassBox.kt) | `Thread.UncaughtExceptionHandler` | `GlassBox.report(...)` | armazenamento interno do app |
| Windows (WPF) | [`src/desktop/WindowsGlassBox.cs`](src/desktop/WindowsGlassBox.cs) | AppDomain + Dispatcher | `TaskScheduler` + `Report(...)` | `%LocalAppData%\GlassBox\crashes` |
| Linux (.NET) | [`src/linux/LinuxGlassBox.cs`](src/linux/LinuxGlassBox.cs) | AppDomain | `TaskScheduler` + `Report(...)` | diretório local de dados do usuário |

## Por que é leve e seguro

- Não usa SDK, rede, banco de dados nem telemetria.
- Não cria processos permanentes; no Linux chama uma ferramenta de clipboard somente quando necessário.
- Salva o arquivo antes de tentar exibir uma mensagem ou acessar o clipboard.
- O handler tem proteção própria: se ele falhar, o tratamento padrão do sistema continua.
- Erros de `Task` não observados e exceções capturadas manualmente também podem virar relatório.

## Instalação

### Android

Copie [`GlassBox.kt`](src/android/GlassBox.kt) para seu pacote e inicialize uma única vez:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlassBox.install(this)
    }
}
```

Registre erros que não derrubaram o app:

```kotlin
try {
    syncProfile()
} catch (error: Throwable) {
    GlassBox.report(error, "profile synchronization")
}
```

Declare sua classe `Application` no `AndroidManifest.xml`. Em um crash fatal, o Android ainda encerrará o processo — essa é a decisão mais segura — mas o relatório fica salvo em `files/glassbox-crashes`.

### Windows / WPF

Copie [`WindowsGlassBox.cs`](src/desktop/WindowsGlassBox.cs) e inicialize no começo do app:

```csharp
protected override void OnStartup(StartupEventArgs e)
{
    WindowsGlassBox.Install();
    base.OnStartup(e);
}
```

Para registrar uma exceção tratada:

```csharp
WindowsGlassBox.Report(exception, "Importing CSV");
```

Por padrão, exceções da interface não são escondidas. Use `Install(continueAfterUiException: true)` somente quando você tiver certeza de que o estado do app ainda é seguro após o erro.

### Linux / .NET

Copie [`LinuxGlassBox.cs`](src/linux/LinuxGlassBox.cs) e instale logo no início:

```csharp
LinuxGlassBox.Install();
```

Em sessões gráficas, o GlassBox tenta usar `wl-copy` (Wayland) ou `xclip` (X11). Em servidor/headless, ele apenas registra o arquivo e escreve o caminho em `stderr` — sem falhar por não haver clipboard.

## Limites importantes

Nenhum handler consegue recuperar com segurança de todos os tipos de falha. Ele não substitui tratamento local para entradas inválidas, erros de rede e regras de negócio. Falhas nativas graves, falta extrema de memória, desligamento forçado e alguns crashes do próprio runtime podem impedir qualquer código de rodar; por isso o relatório é gravado o mais cedo possível.

Nunca publique um relatório sem revisar: stack traces podem conter caminhos locais, identificadores ou dados do usuário.

## Estrutura

```text
src/
  android/GlassBox.kt
  desktop/WindowsGlassBox.cs
  linux/LinuxGlassBox.cs
.github/ISSUE_TEMPLATE/bug_report.md
```

Os arquivos históricos na raiz foram mantidos para não quebrar links antigos; os arquivos em `src/` são a fonte recomendada.

## Como contribuir

Encontrou um caso que não foi registrado? Abra uma [issue](../../issues/new?template=bug_report.md) com a plataforma, versão do runtime e um relatório sem dados sensíveis.

## Licença

Distribuído sob a [MIT License](LICENSE).
