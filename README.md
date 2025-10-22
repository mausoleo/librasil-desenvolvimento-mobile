# LiBRASIL: Tradutor de Libras

**Trabalho de Conclusão de Curso (TCC) que visa quebrar barreiras de comunicação entre surdos e ouvintes através de um aplicativo Android para tradução da Língua Brasileira de Sinais (Libras).**

---

## Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Modelo de Machine Learning](#modelo-de-machine-learning)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Como Executar](#como-executar)
- [Como Contribuir](#como-contribuir)
- [Roadmap](#roadmap)
- [Desenvolvedores](#desenvolvedores)
- [Licença](#licença)
- [Agradecimentos](#agradecimentos)

---

## Sobre o Projeto

O **LiBRASIL** nasceu da necessidade de criar pontes entre a comunidade surda e ouvinte no Brasil. Este aplicativo móvel, desenvolvido como um Trabalho de Conclusão de Curso, oferece uma solução de tradução bidirecional, permitindo que a comunicação flua de maneira mais natural e acessível.

Utilizando tecnologias de ponta como **Inteligência Artificial** para reconhecimento de gestos e a integração com o **VLibras**, a suíte de ferramentas de acessibilidade do Governo Federal, o LiBRASIL se posiciona como uma ferramenta poderosa para a inclusão digital e social.

## Funcionalidades

O aplicativo é dividido em duas funcionalidades principais, acessíveis através de uma interface simples e intuitiva.

### 1. Transcrever Libras (Gestos para Texto)

Esta funcionalidade transforma o smartphone em um intérprete de Libras em tempo real. Utilizando a câmera frontal, o aplicativo reconhece as letras do alfabeto manual e as converte para texto.

**Recursos:**
- **Reconhecimento em Tempo Real:** Captura e análise de vídeo contínuas.
- **Modelo TensorFlow Lite:** Um modelo de Machine Learning customizado e otimizado para dispositivos móveis, capaz de reconhecer 15 letras do alfabeto em Libras (A, B, C, D, E, I, L, M, N, O, R, S, U, V, W).
- **Feedback Instantâneo:** Exibição da letra reconhecida e do nível de confiança da predição.


### 2. Transcrever Português (Texto para Libras)

Para a comunicação no sentido inverso, o usuário pode digitar uma palavra ou frase em português e vê-la traduzida para Libras por um avatar 3D.

**Recursos:**
- **Integração com VLibras:** Utiliza o widget oficial do VLibras para garantir traduções precisas e padronizadas.
- **Avatar 3D:** Um intérprete virtual que executa os sinais correspondentes ao texto inserido.
- **Interface Otimizada:** O WebView que carrega o widget é gerenciado de forma inteligente para garantir performance e estabilidade.


### 3. Biblioteca de Gestos

Um guia de referência completo para aprender e consultar os sinais do alfabeto em Libras, além de palavras úteis em diferentes contextos.

**Recursos:**
- **Categorias Organizadas:** Gestos agrupados por categorias como "Alfabeto", "Hospital", "Polícia" e "Restaurante".
- **Imagens Ilustrativas:** Para as letras do alfabeto, imagens claras demonstram a forma correta de executar cada sinal.
- **Navegação Intuitiva:** Um sistema de listas expansíveis (`Accordion`) facilita a exploração do conteúdo.


## Tecnologias Utilizadas

O LiBRASIL foi construído com um conjunto de tecnologias modernas e robustas, focadas em performance e na melhor experiência de usuário no ecossistema Android.

| Categoria | Tecnologia | Versão | Propósito |
| :--- | :--- | :--- | :--- |
| **Linguagem** | Kotlin | 1.9.0 | Linguagem principal de desenvolvimento Android. |
| **UI Framework** | Jetpack Compose | 1.5.4 | Construção de UI declarativa e moderna. |
| **Design System** | Material Design 3 | 1.1.2 | Componentes e diretrizes de design. |
| **Machine Learning**| TensorFlow Lite | 2.14.0 | Inferência de modelos de ML em dispositivos móveis. |
| **Câmera** | CameraX | 1.3.0 | API para captura e processamento de imagem/vídeo. |
| **Navegação** | Navigation Compose | 2.7.5 | Gerenciamento de telas e fluxo de navegação. |
| **Ciclo de Vida** | Android Lifecycle | 2.6.2 | Gerenciamento do ciclo de vida de componentes. |
| **Assincronismo** | Kotlin Coroutines | 1.7.3 | Execução de tarefas em background. |
| **Tradução** | VLibras Widget | - | API do Governo Federal para tradução para Libras. |

## Arquitetura

O projeto adota a arquitetura **MVVM (Model-View-ViewModel)**, um padrão recomendado pelo Google que promove um código mais organizado, testável e de fácil manutenção.

- **View (UI):** Camada de interface, construída com *Jetpack Compose*. É responsável por exibir os dados e capturar as interações do usuário. (Ex: `GestureCaptureScreen.kt`)
- **ViewModel:** Atua como um intermediário, preparando e gerenciando os dados para a *View*. Contém a lógica de UI e expõe estados que a UI observa. (Ex: `GestureCaptureViewModel.kt`)
- **Model:** Camada de dados do aplicativo. Inclui fontes de dados (APIs, banco de dados), repositórios e os próprios modelos de dados. (Ex: `GestureClassifier.kt`)

```mermaid
graph TD
    A[View (Activity/Composable)] --> B(ViewModel);
    B --> C{Model (Repository)};
    C --> D[Fonte de Dados (TFLite)];
    C --> E[Fonte de Dados (API)];
```

## Modelo de Machine Learning

O coração da funcionalidade de tradução de gestos é um modelo de **Rede Neural Convolucional (CNN)**, treinado para classificar imagens de mãos em letras do alfabeto de Libras.

- **Input:** Imagem RGB de `224x224` pixels.
- **Output:** Um vetor de probabilidades com 16 classes (15 letras + 1 classe nula).
- **Performance:** Otimizado para rodar em tempo real, com latência média de 100-200ms por frame em dispositivos intermediários.
- **Dataset:** O modelo foi treinado com um dataset privado e diversificado, contendo milhares de imagens com variações de iluminação, ângulos e fundos, além da aplicação de técnicas de *data augmentation*.

## Estrutura do Projeto

O projeto está organizado em módulos e pacotes que seguem as melhores práticas de desenvolvimento Android.

```
Librasil/
├── app/                                # Módulo principal do aplicativo
│   ├── src/main/
│   │   ├── java/com/tcc/librasil/
│   │   │   ├── MainActivity.kt         # Activity principal do app
│   │   │   ├── ui/                     # Camada de UI (View)
│   │   │   │   ├── screens/            # Telas principais
│   │   │   │   │   ├── SplashScreen.kt
│   │   │   │   │   ├── MainScreen.kt
│   │   │   │   │   ├── InfoScreen.kt
│   │   │   │   │   ├── GestureCaptureScreen.kt
│   │   │   │   │   ├── TextToLibrasScreen.kt
│   │   │   │   │   └── LibraryScreen.kt
│   │   │   │   ├── components/         # Componentes reutilizáveis
│   │   │   │   │   ├── GestureLibrarySheet.kt
│   │   │   │   │   └── CaptureHistorySheet.kt
│   │   │   │   └── theme/              # Tema, cores e tipografia
│   │   │   │       ├── Color.kt
│   │   │   │       ├── Theme.kt
│   │   │   │       └── Type.kt
│   │   │   ├── viewmodel/              # ViewModels (Lógica de UI)
│   │   │   │   └── GestureCaptureViewModel.kt
│   │   │   ├── ml/                     # Lógica de Machine Learning
│   │   │   │   └── GestureClassifier.kt
│   │   │   ├── data/                   # Camada de dados (Model)
│   │   │   │   ├── model/
│   │   │   │   │   └── VLibrasModels.kt
│   │   │   │   ├── api/
│   │   │   │   │   ├── VLibrasApi.kt
│   │   │   │   │   └── RetrofitClient.kt
│   │   │   │   └── repository/
│   │   │   │       └── VLibrasRepository.kt
│   │   │   └── navigation/             # Grafo de navegação
│   │   │       └── NavGraph.kt
│   │   ├── res/                        # Recursos (imagens, fontes, etc.)
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   ├── xml/
│   │   │   │   ├── backup_rules.xml
│   │   │   │   └── data_extraction_rules.xml
│   │   │   ├── drawable/               # Imagens e ícones
│   │   │   └── mipmap-*/               # Ícones do app em diferentes densidades
│   │   ├── assets/                     # Arquivos brutos (modelo .tflite)
│   │   │   └── libras_model.tflite
│   │   └── AndroidManifest.xml         # Manifesto do app
│   ├── build.gradle.kts                # Dependências e configurações do módulo 'app'
│   └── proguard-rules.pro              # Regras de ofuscação de código
├── gradle/                             # Configurações do Gradle Wrapper
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts                    # Configurações do projeto raiz
├── settings.gradle.kts                 # Configurações de módulos do projeto
├── gradlew                             # Script Gradle Wrapper (Linux/Mac)
├── gradlew.bat                         # Script Gradle Wrapper (Windows)
├── gradle.properties                   # Propriedades globais do Gradle
└── README.md                           # Documentação do projeto
```

## Como Executar

Siga os passos abaixo para compilar e executar o projeto localmente.

**Pré-requisitos:**
- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Dispositivo Android físico ou emulador com API 26+

**Passos:**

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/mausoleo/librasil-desenvolvimento-mobile.git
   cd librasil
   ```

2. **Abra no Android Studio:**
   - Vá em `File > Open` e selecione a pasta do projeto clonado.
   - Aguarde o Android Studio sincronizar e indexar o projeto.

3. **Execute o aplicativo:**
   - Selecione um dispositivo (físico ou emulador).
   - Clique no botão **Run 'app'** (▶️) na barra de ferramentas.

## Como Contribuir

Contribuições são bem-vindas! Se você tem ideias para novas funcionalidades, melhorias ou correções de bugs, siga os passos abaixo:

1. **Faça um Fork** do projeto.
2. **Crie uma nova Branch** para sua funcionalidade (`git checkout -b feature/nova-feature`).
3. **Faça o Commit** de suas mudanças (`git commit -m 'Adiciona nova feature'`).
4. **Faça o Push** para a sua Branch (`git push origin feature/nova-feature`).
5. **Abra um Pull Request**.

## Roadmap

O LiBRASIL é um projeto em constante evolução. Abaixo estão algumas das funcionalidades e melhorias planejadas para o futuro:

- [ ] **Expansão do Vocabulário:** Adicionar mais palavras e frases à biblioteca de gestos.
- [ ] **Reconhecimento de Palavras:** Evoluir o modelo de ML para reconhecer palavras e frases completas, não apenas letras.
- [ ] **Modo Offline:** Permitir que a tradução de texto para Libras funcione sem conexão com a internet.
- [ ] **Personalização do Avatar:** Opções para o usuário customizar o avatar do VLibras.
- [ ] **Gamificação:** Adicionar quizzes e jogos para auxiliar no aprendizado de Libras.

## Desenvolvedores

Este projeto foi idealizado e desenvolvido por um time de estudantes de Análise e Desenvolvimento de Sistemas.

- **Leonardo Melo**
- **Gabriel Gomes**
- **Paulo Henrique**

## Licença

Este projeto é distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## Agradecimentos

- Agradecemos à comunidade surda por sua rica cultura e por nos inspirar a desenvolver este projeto.
- Ao **Governo Federal do Brasil** pela suíte de ferramentas VLibras, um recurso inestimável para a acessibilidade digital no país.

---

**Versão:** 1.0.0 | **Status:** Concluído | **Data:** Outubro 2025

