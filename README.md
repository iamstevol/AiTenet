# JAI - Java AI Integration

A Spring Boot application that provides a unified interface for interacting with multiple AI language models, including OpenAI and Ollama. The application leverages Spring AI for seamless integration with different AI engines and Redis for maintaining conversation memory across sessions.

## Features

- **Multi-Model Support**: Easily switch between OpenAI and Ollama models
- **Session-Based Chat**: Maintain conversation context using Redis-backed session memory
- **Spring AI Integration**: Built on Spring AI framework for robust AI model management
- **RESTful API**: Clean REST endpoints for chat interactions
- **Configurable Model Selection**: Choose which AI engine to use per request

## Architecture

### Core Components

- **AIController**: REST endpoint handler for chat requests
- **ChatService**: Service layer that routes requests to appropriate AI engine
- **ModelEngine Interface**: Abstract interface for different AI implementations
    - **OpenAIEngine**: Integration with OpenAI API (GPT-4o-mini)
    - **OllamaEngine**: Integration with Ollama for local/remote models
- **ChatMemoryService**: Manages conversation history using Redis

### Technology Stack

- **Java 21**: Latest Java language features
- **Spring Boot 4.0.5**: Latest Spring Boot framework
- **Spring AI 2.0.0-M3**: AI model integration framework
- **Spring Data Redis**: Session and conversation memory management
- **Lombok**: Reduced boilerplate code
- **Maven**: Build and dependency management

## Prerequisites

- Java 21 or higher
- Maven 3.8.1+
- Redis server running (default: localhost:6371)
- OpenAI API key (for OpenAI model support)
- Ollama server running locally (for Ollama model support)

## Installation

### Setting up Ollama

Ollama is required to use the local model engine. Follow these steps to install and run Ollama:

1. **Download Ollama**:
   - Visit [ollama.ai](https://ollama.ai) and download the installer for your operating system
   - macOS, Linux, and Windows are supported

2. **Install Ollama**:
   ```bash
   # macOS
   # Run the downloaded .dmg file and follow the installation wizard
   
   # Linux
   # Download and run the installation script
   curl https://ollama.ai/install.sh | sh
   
   # Windows
   # Run the downloaded .exe installer
   ```

3. **Start Ollama Server**:
   ```bash
   ollama serve
   ```
   The server will start on `http://localhost:11434` by default

4. **Pull and Run a Model** (in a new terminal):
   ```bash
   # Pull Llama 3 model
   ollama pull llama3
   
   # Verify the model is installed
   ollama list
   ```
   
   The first pull may take a few minutes as it downloads the model (~4-13GB depending on version)

5. **Test Ollama** (optional):
   ```bash
   ollama run llama3
   # Type your prompt and press Enter
   # Type /bye to exit
   ```

### Setting up the JAI Application

1. Clone the repository:
```bash
git clone <repository-url>
cd jai
```

2. Install dependencies:
```bash
mvn clean install
```

3. Set up Redis:
    - Ensure Redis is running on `localhost:6371` (or configure in `application.yaml`)
    - You can start Redis with: `redis-server --port 6371`

4. Configure environment variables:
```bash
export OPENAI_API_KEY=your_openai_api_key_here
```

## Configuration

Edit `src/main/resources/application.yaml` to customize:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6371
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}

server:
  port: 8061
```

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8061`

## API Endpoints

### Chat Endpoint

**POST** `/ai/chat/{sessionId}`

Send a chat message and receive a response from the selected AI model.

**Path Parameters:**
- `sessionId`: Unique identifier for the conversation session

**Request Body:**
```json
{
  "message": "Your question or prompt here",
  "model": "openAI"
}
```

**Supported Models:**
- `openAI`: Uses OpenAI API (GPT-4o-mini model)
- `ollama`: Uses local/remote Ollama instance
- `null` or empty: Defaults to Ollama

**Example Request:**
```bash
curl -X POST http://localhost:8061/ai/chat/session-123 \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hello, how are you?",
    "model": "openAI"
  }'
```

**Response:**
```
AI generated response text
```

## Session Management

- Each chat session maintains a separate conversation history in Redis
- Session data is automatically stored with a 30-minute expiration
- Messages are stored chronologically, allowing the AI to maintain context across multiple turns
- Message format: `USER: <message>` for user inputs and `AI: <response>` for AI responses

## Development

### Project Structure

```
src/main/java/com/example/jai/
├── JaiApplication.java           # Spring Boot entry point
├── controller/
│   └── AIController.java         # REST endpoints
├── service/
│   ├── ChatService.java          # Chat orchestration service
│   ├── ChatMemoryService.java    # Redis-backed session memory
│   └── ModelEngine.java          # AI engine interface
├── engines/
│   ├── OpenAIEngine.java         # OpenAI implementation
│   └── OllamaEngine.java         # Ollama implementation
├── dto/
│   ├── Response.java             # Response DTO
│   └── request/
│       └── PromptRequest.java    # Chat request DTO
└── config/
    └── EnvConfig.java            # Environment configuration
```

### Building

```bash
# Clean build
mvn clean build

# Package as JAR
mvn clean package

# Run tests
mvn test
```

## Extending with New AI Models

To add support for a new AI model:

1. Create a new engine class implementing `ModelEngine`:
```java
@Service
@RequiredArgsConstructor
public class NewModelEngine implements ModelEngine {
    
    @Override
    public boolean supports(String modelName) {
        return "newmodel".equalsIgnoreCase(modelName);
    }
    
    @Override
    public String chat(String prompt, String sessionId) {
        // Implementation here
        return response;
    }
}
```

2. Register it as a Spring Bean (automatic with `@Service` annotation)
3. Use it by specifying the model name in the request

## Troubleshooting

### Redis Connection Issues
- Verify Redis is running: `redis-cli ping`
- Check Redis port in `application.yaml` matches your setup

### Ollama Connection Issues
- Ensure Ollama server is running: `ollama serve` (in a terminal)
- Verify Ollama is accessible: `curl http://localhost:11434`
- Check that a model is installed: `ollama list`
  - If no models are listed, pull one: `ollama pull llama3`
- Verify the endpoint configuration in `OllamaEngine.java` (should be `http://localhost:11434`)
- Check Ollama logs for errors
- Try manually testing with Ollama: `ollama run llama3`

### OpenAI API Errors
- Verify `OPENAI_API_KEY` environment variable is set
- Check API key is valid in OpenAI dashboard
- Ensure you have available credits/quota on your OpenAI account

## Future Enhancements

- [ ] Add support for more AI models (Claude, Cohere, etc.)
- [ ] Implement conversation export/import
- [ ] Add rate limiting and usage analytics
- [ ] Implement user authentication and authorization
- [ ] Add support for different conversation contexts and system prompts
- [ ] Create web UI for chat interactions

## Contributing

Contributions are welcome! Please ensure:
- Code follows project conventions
- Tests pass: `mvn test`
- New features include appropriate documentation

## Contact & Support

For issues, questions, or contributions, please open an issue in the repository.

